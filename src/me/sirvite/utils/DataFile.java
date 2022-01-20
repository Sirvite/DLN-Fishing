package me.sirvite.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DataFile {
  private File file;
  
  private YamlConfiguration configuration;
  
  public DataFile(JavaPlugin plugin, String name) {
    this.file = new File(plugin.getDataFolder(), name + ".yml");
    if (!this.file.getParentFile().exists())
      this.file.getParentFile().mkdir(); 
    plugin.saveResource(name + ".yml", false);
    this.configuration = YamlConfiguration.loadConfiguration(this.file);
  }
  
  public void load() {
    this.configuration = YamlConfiguration.loadConfiguration(this.file);
  }
  
  public void save() {
    try {
      this.configuration.save(this.file);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public YamlConfiguration getConfiguration() {
    return this.configuration;
  }
  
  public double getDouble(String path) {
    if (this.configuration.contains(path))
      return this.configuration.getDouble(path); 
    return 0.0D;
  }
  
  public int getInt(String path) {
    if (this.configuration.contains(path))
      return this.configuration.getInt(path); 
    return 0;
  }
  
  public Set<String> getConfigurationSection(String path, boolean deep) {
    if (this.configuration.contains(path))
      return this.configuration.getConfigurationSection(path).getKeys(deep); 
    return null;
  }
  
  public boolean getBoolean(String path) {
    return (this.configuration.contains(path) && this.configuration.getBoolean(path));
  }
  
  public String getString(String path) {
    if (this.configuration.contains(path))
      return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)); 
    return "";
  }
  
  public List<String> getStringList(String path) {
    if (this.configuration.contains(path)) {
      List<String> strings = new ArrayList<>();
      this.configuration.getStringList(path).forEach(string -> strings.add(ChatColor.translateAlternateColorCodes('&', string)));
      return strings;
    } 
    return Collections.singletonList("Invalid path.");
  }
}
