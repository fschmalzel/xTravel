package com.gmail.xlifehd.xtravel;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class MySQLPlayerData {
	
	private static String CREATE = "CREATE TABLE IF NOT EXISTS `" + Main.getPlugin().getConfig().getString("mysql.prefix") + "xTravel` (" + 
			"  `uuid` varchar(36) NOT NULL," + 
			"  `x` long NOT NULL," +
			"  `z` long NOT NULL," +
			"  `lvl` int NOT NULL," +
			"  `exp` double NOT NULL," +
			"  `cargoupgrades` int NOT NULL," +
			"  `speedupgrades` int NOT NULL," +
			"  `crew` mediumtext NOT NULL," +
			"  `shipinventory` longtext NOT NULL," +
			"  PRIMARY KEY (`uuid`)" +
			")";
	
	private static String SELECT = "SELECT * FROM `" + Main.getPlugin().getConfig().getString("mysql.prefix") + "xTravel` WHERE uuid = ?";
	
	private static String UPDATE = "INSERT INTO `" + Main.getPlugin().getConfig().getString("mysql.prefix") + "` "
			+ "(uuid, x, z, lvl, exp, cargoupgrades, speedupgrades, crew, shipinventory) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
			+ "x = VALUES(x), z = VALUES(z), lvl = VALUES(lvl), exp = VALUES(exp), cargoupgrades = VALUES(cargoupgrades), "
			+ "speedupgrades = VALUES(speedupgrades), crew = VALUES(crew), shipinventory = VALUES(shipinventory);";
	
	final private UUID uuid;
	private long x;
	private long z;
	private int lvl;
	private double exp;
	private int cargoUpgrades;
	private int speedUpgrades;
	//TODO Add crew
	private ItemStack[] shipInventory;
	
	private boolean loaded = false;
	
	private static HashMap<UUID, MySQLPlayerData> loadedPlayers = new HashMap<UUID, MySQLPlayerData>();
	
	private MySQLPlayerData(UUID uuid) {
		this.uuid = uuid;
		loadData();
	}
	
	public static MySQLPlayerData getPlayerData(UUID uuid) {
		
		if (loadedPlayers.containsKey(uuid)) {
			return loadedPlayers.get(uuid);
		} else {
			MySQLPlayerData playerData = new MySQLPlayerData(uuid);
			loadedPlayers.put(uuid, playerData);
			
			return playerData;
		}
		
	}
	
	public static void closePlayerData(UUID uuid) {
		
		if (loadedPlayers.containsKey(uuid)) {
			loadedPlayers.remove(uuid);
		}
		
	}
	
	public static void createTables() {
		
		Runnable r = new Runnable() {
			
			public void run() {
				
				try {
					
					Statement statement = Main.getConnection().createStatement();
					
					statement.executeUpdate(CREATE);
					
					statement.close();
					
				} catch (SQLException e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		};
		
		Main.getPlugin().getServer().getScheduler().runTaskAsynchronously(Main.getPlugin(), r);
		
	}
	
	private void loadData() {
		
		Runnable r = new Runnable() {
			
			public void run() {
				
				PreparedStatement preparedStatement;
				
				try {
					
					preparedStatement = Main.getConnection().prepareStatement(SELECT);
					preparedStatement.setString(1, uuid.toString());
					
					ResultSet rs = preparedStatement.executeQuery();
					
					if ( rs.next() ) {
						
						x =					rs.getLong("x");
						z =					rs.getLong("z");
						lvl =				rs.getInt("lvl");
						exp =				rs.getDouble("exp");
						cargoUpgrades =		rs.getInt("cargoUpgrades");
						speedUpgrades =		rs.getInt("speedUpgrades");
						//String crewString =	rs.getString("crew");
						String shipInv =	rs.getString("shipinventory");
						
						//crew = BukkitSerialization.
						shipInventory = BukkitSerialization.itemStackArrayFromBase64(shipInv);
						
						loaded = true;
						
					}
					
					preparedStatement.close();
					
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		};
		
		Main.getPlugin().getServer().getScheduler().runTaskAsynchronously(Main.getPlugin(), r);
		
	}
	
	public void updateData() {
		
		Runnable r = new Runnable() {
			
			public void run() {
				
				try {
					
					//String crewString = BukkitSerialization.
					String crewString = "";
					String shipInv = BukkitSerialization.itemStackArrayToBase64(shipInventory);
					
					PreparedStatement preparedStatement = Main.getConnection().prepareStatement(UPDATE);
					
					preparedStatement.setString(1, uuid.toString());
					preparedStatement.setLong(2, x);
					preparedStatement.setLong(3, z);
					preparedStatement.setInt(4, lvl);
					preparedStatement.setDouble(5, exp);
					preparedStatement.setInt(6, cargoUpgrades);
					preparedStatement.setInt(7, speedUpgrades);
					preparedStatement.setString(8, crewString);
					preparedStatement.setString(9, shipInv);
					
					preparedStatement.executeUpdate();
					preparedStatement.close();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
		};
		
		Main.getPlugin().getServer().getScheduler().runTaskAsynchronously(Main.getPlugin(), r);
		
	}
	
	//GETTER & SETTER
	public long getX() {
		return x;
	}
	
	public void setX(long x) {
		this.x = x;
	}
	
	public long getZ() {
		return z;
	}
	
	public void setZ(long z) {
		this.z = z;
	}
	
	public int getLvl() {
		return lvl;
	}
	
	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	
	public double getExp() {
		return exp;
	}
	
	public void setExp(double exp) {
		this.exp = exp;
	}
	
	public int getCargoUpgrades() {
		return cargoUpgrades;
	}
	
	public void setCargoUpgrades(int cargoUpgrades) {
		this.cargoUpgrades = cargoUpgrades;
	}
	
	public int getSpeedUpgrades() {
		return speedUpgrades;
	}
	
	public void setSpeedUpgrades(int speedUpgrades) {
		this.speedUpgrades = speedUpgrades;
	}
	
	public ItemStack[] getShipInventory() {
		return shipInventory;
	}
	
	public void setShipInventory(ItemStack[] shipInventory) {
		this.shipInventory = shipInventory;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
}
