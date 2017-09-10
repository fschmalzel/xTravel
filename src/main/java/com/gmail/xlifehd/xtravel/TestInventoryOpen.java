package com.gmail.xlifehd.xtravel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestInventoryOpen implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player player = (Player) sender;
			
			//DEBUG
			player.sendMessage("Befehl ausgeführt!");
			
			
			
			
			
		} else {
			
			sender.sendMessage("Du musst ein Spieler sein!");
			
		}
		
		return true;
	}
	
}
