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
	
	private static String UPDATE = "INSERT INTO `" + Main.getPlugin().getConfig().getString("mysql.prefix") + "xTravel` "
			+ "(uuid, x, z, lvl, exp, cargoupgrades, speedupgrades, crew, shipinventory) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
			+ "x = VALUES(x), z = VALUES(z), lvl = VALUES(lvl), exp = VALUES(exp), cargoupgrades = VALUES(cargoupgrades), "
			+ "speedupgrades = VALUES(speedupgrades), crew = VALUES(crew), shipinventory = VALUES(shipinventory);";
	
	final private UUID uuid;
	private long x = 0;
	private long z = 0;
	private int lvl = 0;
	private double exp = 0;
	private int cargoUpgrades = 0;
	private int speedUpgrades = 0;
	//TODO Add crew
	private ItemStack[] shipInventory = new ItemStack[0];
	
	private boolean loaded = false;
	private boolean autoCommit = true;
	
	private static HashMap<UUID, MySQLPlayerData> loadedPlayers = new HashMap<UUID, MySQLPlayerData>();
	
	private MySQLPlayerData(UUID uuid) {
		this.uuid = uuid;
		loadData();
	}
	
	/**
	 * Gets a {@link MySQLPlayerData} object from a specific player.
	 * 
	 * @param uuid from the player.
	 * @return {@link MySQLPlayerData} of specified player.
	 */
	public static MySQLPlayerData getPlayerData(UUID uuid) {
		
		if (loadedPlayers.containsKey(uuid)) {
			return loadedPlayers.get(uuid);
		} else {
			MySQLPlayerData playerData = new MySQLPlayerData(uuid);
			loadedPlayers.put(uuid, playerData);
			
			return playerData;
		}
		
	}
	
	/**
	 * If the {@link MySQLPlayerData} object is no longer needed it should be closed, to free up resources.
	 * 
	 * @param uuid from the player.
	 */
	public static void closePlayerData(UUID uuid) {
		
		if (loadedPlayers.containsKey(uuid)) {
			loadedPlayers.remove(uuid);
		}
		
	}
	
	/**
	 * This function should be called on startup to ensure that all tables exist in the database.
	 */
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
						
					}
					
					loaded = true;
					
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
	
	/**
	 * Updates the databases records. Should only be called if autoCommit has been disabled.
	 */
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
	
	private void autoCommit() {
		if (autoCommit) {
			updateData();
		}
	}
	
	//GETTER & SETTER
	/**
	 * Disable or enable autoCommit. By the default it is enabled.
	 * 
	 * @param autoCommit true = enabled; false = disabled.
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	
	/**
	 * Returns the x coordinate of the ship.
	 * 
	 * @return X coordinate of the ship.
	 */
	public long getX() {
		return x;
	}
	
	/**
	 * Sets the x coordinate.
	 * 
	 * @param x The new x coordinate.
	 */
	public void setX(long x) {
		this.x = x;
		autoCommit();
	}
	
	/**
	 * Returns the z coordinate of the ship.
	 * 
	 * @return Z coordinate of the ship.
	 */
	public long getZ() {
		return z;
	}
	
	/**
	 * Sets the z coordinate.
	 * 
	 * @param z The new z coordinate.
	 */
	public void setZ(long z) {
		this.z = z;
		autoCommit();
	}
	
	/**
	 * Returns the level of the ship.
	 * 
	 * @return Level of the ship.
	 */
	public int getLvl() {
		return lvl;
	}
	
	/**
	 * Sets the level of the ship.
	 * 
	 * @param lvl The new level of the ship.
	 */
	public void setLvl(int lvl) {
		this.lvl = lvl;
		autoCommit();
	}
	
	/**
	 * Returns the experience of the ship.
	 * 
	 * @return experience of the ship.
	 */
	public double getExp() {
		return exp;
	}
	
	/**
	 * Sets the experience of the ship.
	 * 
	 * @param exp The new experience of the ship.
	 */
	public void setExp(double exp) {
		this.exp = exp;
		autoCommit();
	}
	
	/**
	 * Returns the level of cargo upgrades of the ship.
	 * 
	 * @return level of cargo upgrades the ship.
	 */
	public int getCargoUpgrades() {
		return cargoUpgrades;
	}
	
	/**
	 * Sets the level of cargo upgrades of the ship.
	 * 
	 * @param cargoUpgrades The new level of cargo upgrades of the ship.
	 */
	public void setCargoUpgrades(int cargoUpgrades) {
		this.cargoUpgrades = cargoUpgrades;
		autoCommit();
	}
	
	/**
	 * Returns the level of speed upgrades of the ship.
	 * 
	 * @return level of speed upgrades the ship.
	 */
	public int getSpeedUpgrades() {
		return speedUpgrades;
	}
	
	/**
	 * Sets the level of speed upgrades of the ship.
	 * 
	 * @param speedUpgrades The new level of speed upgrades of the ship.
	 */
	public void setSpeedUpgrades(int speedUpgrades) {
		this.speedUpgrades = speedUpgrades;
		autoCommit();
	}
	
	/**
	 * Returns an array of {@link ItemStack}s which represent the ships inventory.
	 * 
	 * @return ItemStack array of the ships inventory.
	 */
	public ItemStack[] getShipInventory() {
		return shipInventory;
	}
	
	/**
	 * Sets the inventory of the ship.
	 * 
	 * @param shipInventory The new shipInventory as an array of {@link ItemStack}s.
	 */
	public void setShipInventory(ItemStack[] shipInventory) {
		this.shipInventory = shipInventory;
		autoCommit();
	}

	/**
	 * Should be called before getting any data
	 * 
	 * @return True if the data has been loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
}
