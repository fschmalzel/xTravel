package com.gmail.xlifehd.xtravel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TestInventoryOpen implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player player = (Player) sender;
			player.sendMessage("Befehl ausgeführt!"); //DEBUG
			
			//DEBUG
			player.sendMessage("Befehl ausgeführt!");
			
			MySQLPlayerData Zeug = MySQLPlayerData.getPlayerData(player.getUniqueId());
			ItemStack[] shipInventory = Zeug.getShipInventory();

			Inventory Lager = Bukkit.createInventory(null, 27, "Schiffslager");
			Lager.setStorageContents(shipInventory);
			
			player.openInventory(Lager);
			
			return true;
			
			
		} else {
			
			sender.sendMessage("Du musst ein Spieler sein!");
			
		}
		
		return true;
	}
	
}