package com.gmail.xlifehd.xtravel;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.xlifehd.xtravel.listener.OnQuit;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private static HikariDataSource hikari;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		setupConfig();
		setupMySQL();
		
		//Registering commands
		this.getCommand("openinv").setExecutor(new InventoryOpen());
		
		//Registering listeners
		this.getServer().getPluginManager().registerEvents(new OnQuit(), this);
		
	}
	
	@Override
	public void onDisable() {
		
		hikari.close();
		
	}
	
	//SETUP
	private void setupConfig() {
		
		FileConfiguration config = getConfig();
		
		config.options().header("xTravel Config");
		config.addDefault("CfgVersion", 1);
		
		config.addDefault("mysql.host", "127.0.0.1");
		config.addDefault("mysql.port", 3306);
		config.addDefault("mysql.database", "minecraft");
		config.addDefault("mysql.username", "admin");
		config.addDefault("mysql.password", "foobar");
		config.addDefault("mysql.prefix", "xbr_");
		
		config.options().copyHeader(true);
		config.options().copyDefaults(true);
		saveConfig();
		
	}
	
	private void setupMySQL() {
		
		FileConfiguration config = getConfig();
		
		String host		= config.getString("mysql.host");
		int port		= config.getInt("mysql.port");
		String database	= config.getString("mysql.database");
		String username	= config.getString("mysql.username");
		String password	= config.getString("mysql.password");
		
		HikariConfig dsConfig = new HikariConfig();
		dsConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
		dsConfig.setUsername(username);
		dsConfig.setPassword(password);
		dsConfig.addDataSourceProperty("cachePrepStmts", "true");
		dsConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		dsConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		
		hikari = new HikariDataSource(dsConfig);
		
		MySQLPlayerData.createTables();
		
	}
	
	//GETTER
	public static Main getPlugin() {
		return instance;
	}
	
	public static Connection getConnection() throws SQLException {
		return hikari.getConnection();
	}
	
}
