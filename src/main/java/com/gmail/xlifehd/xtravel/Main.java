package com.gmail.xlifehd.xtravel;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	@Override
	public void onEnable() {
		instance = this;
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public Main getPlugin() {
		return instance;
	}
	
}
