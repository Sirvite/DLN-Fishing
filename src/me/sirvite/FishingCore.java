package me.sirvite;

import me.sirvite.commands.CmdFishing;
import me.sirvite.listeners.CmdListener;
import me.sirvite.listeners.FishingListener;
import me.sirvite.utils.DataFile;
import me.sirvite.utils.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FishingCore extends JavaPlugin {
  private static FishingCore instance;
  
  private DataFile configFile;
  
  private CommandFramework commandFramework;
  
  public static FishingCore getInstance() {
    return instance;
  }
  
  public DataFile getConfigFile() {
    return this.configFile;
  }
  
  public void onEnable() {
    instance = this;
    this.configFile = new DataFile(this, "config");
    this.commandFramework = new CommandFramework((Plugin)this);
    registerCommands();
    registerListeners();
  }
  
  public void onDisable() {
    instance = null;
    this.commandFramework = null;
  }
  
  private void registerCommands() {
    this.commandFramework.registerCommands(new CmdFishing());
    this.commandFramework.registerHelp();
  }
  
  private void registerListeners() {
    PluginManager pluginManager = Bukkit.getPluginManager();
    pluginManager.registerEvents((Listener)new FishingListener(), (Plugin)this);
    pluginManager.registerEvents((Listener)new CmdListener(), (Plugin)this);
  }
}
