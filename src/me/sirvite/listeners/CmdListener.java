package me.sirvite.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CmdListener implements Listener {
  @EventHandler(ignoreCancelled = true)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    String message = event.getMessage().toLowerCase();
    if (message.startsWith("/fishing"))
      event.setMessage(message.replace("/fishing", "/af")); 
  }
}
