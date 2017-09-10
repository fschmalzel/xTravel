package com.gmail.xlifehd.xtravel.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.xlifehd.xtravel.MySQLPlayerData;

public class OnQuit implements Listener {
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		MySQLPlayerData.closePlayerData(player.getUniqueId());
		
	}
	
}
