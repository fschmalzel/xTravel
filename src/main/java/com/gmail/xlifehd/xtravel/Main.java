package com.gmail.xlifehd.xtravel;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		//Registering commands
		this.getCommand("openinv").setExecutor(new TestInventoryOpen());
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static Main getPlugin() {
		return instance;
	}
	
}
