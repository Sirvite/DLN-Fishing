package me.sirvite.commands;

import java.util.ArrayList;
import java.util.List;
import me.sirvite.FishingCore;
import me.sirvite.listeners.FishingListener;
import me.sirvite.utils.Glow;
import me.sirvite.utils.ItemBuilder;
import me.sirvite.utils.command.Command;
import me.sirvite.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdFishing {
  @Command(name = "astrorod.reload", description = "The reload command for Astro Fishing", usage = "/<command>", permission = "astrorod.reload")
  public void onCommandReload(CommandArgs args) {
    Player player = (Player)args.getSender();
    FishingCore.getInstance().getConfigFile().load();
    player.sendMessage(ChatColor.GREEN + "Reloaded!");
  }
  
  @Command(name = "astrorod", description = "The main command for AstroFishing", aliases = {"fishing.rewards", "astrorod.rewards"}, usage = "/<command>", permission = "astrorod.rewards")
  public void onCommand(CommandArgs args) {
    Player player = (Player)args.getSender();
    List<String> rewards = FishingCore.getInstance().getConfigFile().getStringList("rewards-cmd-msg");
    for (String string : rewards)
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', string)); 
  }
  
  @Command(name = "giverod", description = "The give command for Astro Fishing", usage = "/<command>", permission = "astrorod.give")
  public void onCommandGive(CommandArgs args) {
    int level, amount;
    Player player = (Player)args.getSender();
    if (args.length() != 4) {
      player.sendMessage(ChatColor.RED + "Usage: /giverod give <player> <level> <amount>");
      return;
    } 
    Player target = Bukkit.getPlayer(args.getArgs(0));
    try {
      level = Integer.parseInt(args.getArgs(1));
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "Please specify a valid level!");
      return;
    } 
    if (level > 11) {
      player.sendMessage(ChatColor.RED + "Please specify a level 10 or less!");
      return;
    } 
    if (level <= 0) {
      player.sendMessage(ChatColor.RED + "Please specify a level greater than 0!");
      return;
    } 
    try {
      amount = Integer.parseInt(args.getArgs(2));
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "Please specify a valid amount!");
      return;
    } 
    List<String> list = new ArrayList<>();
    list.add(ChatColor.GRAY + "Leveling this rod allows you to catch");
    list.add(ChatColor.GRAY + "many different types of rewards. The more");
    list.add(ChatColor.GRAY + "you increase your level, the better the loot.");
    list.add(ChatColor.GRAY + "Cast your rod to begin acquiring your riches.");
    list.add(ChatColor.GRAY + " ");
    list.add(ChatColor.LIGHT_PURPLE + "Information: ");
    list.add(ChatColor.GRAY + "Caught: " + ChatColor.LIGHT_PURPLE + "0");
    list.add(ChatColor.GRAY + "Progress: " + FishingListener.getProgressBar(0, 100, 30, "|", "&d", "&7"));
    if (target != null) {
      if (target.getInventory().firstEmpty() == -1) {
        target.getWorld().dropItemNaturally(target.getLocation(), (new ItemBuilder(Material.FISHING_ROD)).amount(amount).enchantment(Glow.getGlow(), 1).name(ChatColor.LIGHT_PURPLE + "Ranked Rod " + ChatColor.GRAY + "[" + level + "]").lore(list).build());
      } else {
        target.getInventory().addItem(new ItemStack[] { (new ItemBuilder(Material.FISHING_ROD)).amount(amount).enchantment(Glow.getGlow(), 1).name(ChatColor.LIGHT_PURPLE + "Ranked Rod " + ChatColor.GRAY + "[" + level + "]").lore(list).build() });
      } 
      target.sendMessage(ChatColor.GREEN + "You have received " + amount + " level " + level + " rod(s).");
      player.sendMessage(ChatColor.GREEN + "You gave " + amount + " level " + level + " rod(s) to " + target.getName() + ".");
    } else {
      player.sendMessage(ChatColor.RED + "The specified player could not be found.");
    } 
  }
}
