package me.sirvite.listeners;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sirvite.FishingCore;
import me.sirvite.utils.Glow;
import me.sirvite.utils.RandomCollection;

public class FishingListener implements Listener {
  private DecimalFormat df = new DecimalFormat("###");
  
  public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
    float percent = (float) current / max;
    int progressBars = (int) (totalBars * percent);
    int leftOver = totalBars - progressBars;
    
    
    StringBuilder sb = new StringBuilder();
    sb.append(ChatColor.translateAlternateColorCodes('&', completedColor));
    int i;
    for (i = 0; i < progressBars; i++)
      sb.append(symbol); 
    sb.append(ChatColor.translateAlternateColorCodes('&', notCompletedColor));
    for (i = 0; i < leftOver; i++)
      sb.append(symbol); 
    return ChatColor.DARK_GRAY + "[" + sb.toString() + ChatColor.DARK_GRAY + "]";
  }
  
  private String getReward(int level) {
    RandomCollection<String> rewards = new RandomCollection();
    for (String string : FishingCore.getInstance().getConfigFile().getStringList("level." + level + ".rewards"))
      rewards.add(Double.parseDouble(StringUtils.split(string, "|")[1]), StringUtils.split(string, "|")[0]); 
    return (String)rewards.next();
  }
  
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerFish(PlayerFishEvent event) {
    if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
      return; 
    Player player = event.getPlayer();
    ItemStack rod = player.getItemInHand();
    if (rod.hasItemMeta() && rod.getItemMeta().hasDisplayName()) {
      String displayName = rod.getItemMeta().getDisplayName();
      if (displayName.startsWith(ChatColor.LIGHT_PURPLE + "Ranked Rod ")) {
        int level = Integer.parseInt(StringUtils.substringBetween(displayName, "[", "]"));
        updateRod(player, rod, level, FishingCore.getInstance().getConfigFile().getInt("level." + level + ".fishrequired"));
        Item fishingCatch = (Item)event.getCaught();
        if (fishingCatch != null)
          fishingCatch.remove(); 
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), getReward(level).replace("%player%", player.getName()));
      } else {
        registerRod(player, rod);
      } 
    } else {
      registerRod(player, rod);
    } 
  }
  
  private void registerRod(Player player, ItemStack itemStack) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ranked Rod " + ChatColor.GRAY + "[1]");
    if (itemMeta.hasLore()) {
      itemMeta.getLore().add(ChatColor.GRAY + "Leveling this rod allows you to catch");
      itemMeta.getLore().add(ChatColor.GRAY + "many different types of rewards. The more");
      itemMeta.getLore().add(ChatColor.GRAY + "you increase your level, the better the loot.");
      itemMeta.getLore().add(ChatColor.GRAY + "Cast your rod to begin acquiring your riches.");
      itemMeta.getLore().add(ChatColor.GRAY + " ");
      itemMeta.getLore().add(ChatColor.LIGHT_PURPLE + "Information: ");
      itemMeta.getLore().add(ChatColor.GRAY + "Caught: " + ChatColor.LIGHT_PURPLE + "0");
      itemMeta.getLore().add(ChatColor.GRAY + "Progress: " + getProgressBar(0, 100, 40, "|", "&d", "&7"));
    } else {
      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.GRAY + "Leveling this rod allows you to catch");
      lore.add(ChatColor.GRAY + "many different types of rewards. The more");
      lore.add(ChatColor.GRAY + "you increase your level, the better the loot.");
      lore.add(ChatColor.GRAY + "Cast your rod to begin acquiring your riches.");
      lore.add(ChatColor.GRAY + " ");
      lore.add(ChatColor.LIGHT_PURPLE + "Information: ");
      lore.add(ChatColor.GRAY + "Caught: " + ChatColor.LIGHT_PURPLE + "0");
      lore.add(ChatColor.GRAY + "Progress: " + getProgressBar(0, 100, 40, "|", "&d", "&7"));
      itemMeta.setLore(lore);
    } 
    itemMeta.addEnchant(Glow.getGlow(), 1, true);
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
    itemMeta.spigot().setUnbreakable(true);
    itemStack.setItemMeta(itemMeta);
    player.updateInventory();
  }
  
  private void updateRod(Player player, ItemStack itemStack, int level, int fishRequired) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta.hasLore()) {
      List<String> lore = new ArrayList<>();
      int currentFish = 1;
      for (String string : itemMeta.getLore()) {
        if (string.startsWith(ChatColor.GRAY + "Progress: "))
          lore.remove(string); 
        if (string.startsWith(ChatColor.GRAY + "Caught: ")) {
          currentFish += Integer.parseInt(ChatColor.stripColor(StringUtils.split(string, " ")[1]));
          lore.remove(string);
        } 
      }
      //lore.add(ChatColor.GRAY + "Caught: " + ChatColor.LIGHT_PURPLE + currentFish);
      double progress = (currentFish / fishRequired * 100);
      if (progress >= 100.0D) {
        if (level != 11) {
          itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ranked Rod " + ChatColor.GRAY + "[" + ++level + "]");
          lore.add(ChatColor.GRAY + "Progress: " + getProgressBar(currentFish, fishRequired, 40, "|", "&d", "&7"));
          for (String string : FishingCore.getInstance().getConfigFile().getStringList("level." + level + ".commands"))
            Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), string.replace("%player%", player.getName())); 
          Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FishingCore.getInstance().getConfigFile().getString("broadcast-rankup").replace("%player%", player.getName()).replace("%level%", String.valueOf(level))));
        } else {
          itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ranked Rod " + ChatColor.GRAY + "[" + '\n' + "]");
          lore.add(ChatColor.GRAY + "Leveling this rod allows you to catch");
          lore.add(ChatColor.GRAY + "many different types of rewards. The more");
          lore.add(ChatColor.GRAY + "you increase your level, the better the loot.");
          lore.add(ChatColor.GRAY + "Cast your rod to begin acquiring your riches.");
          lore.add(ChatColor.GRAY + " ");
          lore.add(ChatColor.LIGHT_PURPLE + "Information: ");
          lore.add(ChatColor.LIGHT_PURPLE + "Level Completed, catch another to proceed!");
          lore.add(ChatColor.GRAY + "Progress: " + getProgressBar(100, 100, 40, "|", "&d", "&7"));
        } 
      } else {
    	  lore.add(ChatColor.GRAY + "Leveling this rod allows you to catch");
          lore.add(ChatColor.GRAY + "many different types of rewards. The more");
          lore.add(ChatColor.GRAY + "you increase your level, the better the loot.");
          lore.add(ChatColor.GRAY + "Cast your rod to begin acquiring your riches.");
          lore.add(ChatColor.GRAY + " ");
          lore.add(ChatColor.LIGHT_PURPLE + "Information: ");
          lore.add(ChatColor.GRAY + "Caught: " + ChatColor.LIGHT_PURPLE + currentFish);
        lore.add(ChatColor.GRAY + "Progress: " + getProgressBar(currentFish, fishRequired, 40, "|", "&d", "&7"));
      } 
      itemMeta.setLore(lore);
    } else {
      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.GRAY + "Leveling this rod allows you to catch");
      lore.add(ChatColor.GRAY + "many different types of rewards. The more");
      lore.add(ChatColor.GRAY + "you increase your level, the better the loot.");
      lore.add(ChatColor.GRAY + "Cast your rod to begin acquiring your riches.");
      lore.add(ChatColor.GRAY + " ");
      lore.add(ChatColor.LIGHT_PURPLE + "Information: ");
      lore.add(ChatColor.GRAY + "Caught: " + ChatColor.LIGHT_PURPLE + "0");
      lore.add(ChatColor.GRAY + "Progress: " + getProgressBar(0, 100, 30, "|", "&d", "&7"));
      itemMeta.setLore(lore);
    } 
    itemMeta.addEnchant(Glow.getGlow(), 1, true);
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
    itemMeta.spigot().setUnbreakable(true);
    itemStack.setItemMeta(itemMeta);
    player.updateInventory();
  }
}
