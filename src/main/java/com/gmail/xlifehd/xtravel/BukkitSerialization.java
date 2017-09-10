package com.gmail.xlifehd.xtravel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class BukkitSerialization {
	
	//CODE FROM <a href="https://gist.github.com/graywolf336/8153678">Original Source</a>
	/**
	 * A method to serialize a {@link PlayerInventory} to an array of Base64 Strings. First string is the content and second string is the armor and third is the item in the offHand.
	 * 
	 * @param playerInventory to turn into an array of strings.
	 * @return Array of strings: [ main content, armor content, IteminOffhand content ]
	 * @throws IllegalStateException
	 */
	public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
		//get the main content part, this doesn't return the armor
		//String content = toBase64(playerInventory);
		String content = itemStackArrayToBase64(playerInventory.getStorageContents());
		String armor = itemStackArrayToBase64(playerInventory.getArmorContents());
		String offhand = itemStackToBase64(playerInventory.getItemInOffHand());
		
		return new String[] { content, armor, offhand };
	}
	
	/**
	 * 
	 * A method to serialize an {@link ItemStack} array to Base64 String.
	 * 
	 * <p />
	 * 
	 * Based off of {@link #toBase64(Inventory)}.
	 * 
	 * @param items to turn into a Base64 String.
	 * @return Base64 string of the items.
	 * @throws IllegalStateException
	 */
	public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			
			// Write the size of the inventory
			dataOutput.writeInt(items.length);
			
			// Save every element in the list
			for (int i = 0; i < items.length; i++) {
				dataOutput.writeObject(items[i]);
			}
			
			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}
	
	/**
	 * 
	 * A method to serialize an {@link ItemStack} to Base64 String.
	 * 
	 * <p />
	 * 
	 * Based off of {@link #toBase64(Inventory)}.
	 * 
	 * @param item to turn into a Base64 String.
	 * @return Base64 string of the item.
	 * @throws IllegalStateException
	 */
	public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			
			// Save the item
			dataOutput.writeObject(item);
			
			// Serialize that item
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}
	
	/**
	 * A method to serialize an {@link Inventory} to Base64 string.
	 * 
	 * <p />
	 * 
	 * Special thanks to Comphenix in the Bukkit forums or also known
	 * as aadnk on GitHub.
	 * 
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 * 
	 * @param inventory to serialize
	 * @return Base64 string of the provided inventory
	 * @throws IllegalStateException
	 */
	public static String toBase64(Inventory inventory) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			
			// Write the size of the inventory
			dataOutput.writeInt(inventory.getSize());
			
			// Save every element in the list
			for (int i = 0; i < inventory.getSize(); i++) {
				dataOutput.writeObject(inventory.getItem(i));
			}
			
			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}
	
	/**
	 * 
	 * A method to get an {@link Inventory} from an encoded, Base64, string.
	 * 
	 * <p />
	 * 
	 * Special thanks to Comphenix in the Bukkit forums or also known
	 * as aadnk on GitHub.
	 * 
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 * 
	 * @param data Base64 string of data containing an inventory.
	 * @return Inventory created from the Base64 string.
	 * @throws IOException
	 */
	public static Inventory fromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			int itemLength = dataInput.readInt();
			int invSize = itemLength + 9 - (itemLength % 9); //Convert to multiple of 9
			
			if ( itemLength > 36) { itemLength = 36; }
			if ( invSize > 36 ) {
				invSize = 36;
				Bukkit.getLogger().warning("BukkitSerialization: size of the inventory exceeds 36 items, some items may be lost");
			}
			
			Inventory inventory = Bukkit.getServer().createInventory(null, invSize);
	
			// Read the serialized inventory
			for (int i = 0; i < itemLength; i++) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}
			
			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	
	/**
	 * Gets an array of {@link ItemStack}s from Base64 string.
	 * 
	 * <p />
	 * 
	 * Base off of {@link #fromBase64(String)}.
	 * 
	 * @param data Base64 string to convert to ItemStack array.
	 * @return ItemStack array created from the Base64 string.
	 * @throws IOException
	 */
	public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];
	
			// Read the serialized inventory
			for (int i = 0; i < items.length; i++) {
				items[i] = (ItemStack) dataInput.readObject();
			}
			
			dataInput.close();
			return items;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	
	/**
	 * Gets an {@link ItemStack} from a Base64 string.
	 * 
	 * <p />
	 * 
	 * Base off of {@link #itemStackArrayFromBase64(String)}.
	 * 
	 * @param data Base64 string to convert to ItemStack.
	 * @return ItemStack created from the Base64 string.
	 * @throws IOException
	 */
	public static ItemStack itemStackFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			
			ItemStack item = (ItemStack) dataInput.readObject();
			
			dataInput.close();
			return item;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	
	/**
	 * 
	 * A method to serialize {@link AdvancementProgress} to Base64 String.
	 * 
	 * @param player to get advancementProgress from to turn into a Base64 String.
	 * @return Base64 string of the advancementProgress.
	 * @throws IllegalStateException
	 */
	public static String advancementsToBase64(Player player) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			Iterator<Advancement> advIter = Bukkit.advancementIterator();
			int length = 0;
			
			for ( Iterator<Advancement> tempAdvIter = advIter; tempAdvIter.hasNext(); ++length) {
				tempAdvIter.next();
			}
			
			dataOutput.writeInt(length);
			
			for ( Iterator<Advancement> i = advIter; i.hasNext(); ) {
				Advancement adv = i.next();
				dataOutput.writeObject(adv);
				AdvancementProgress advProg = player.getAdvancementProgress(adv);
				dataOutput.writeObject(advProg);
			}
			
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save advancementprogress.", e);
		}
	}
	
	/**
	 * Gets a {@link HashMap<Advancement, AdvancementProgress>} which links advancementProgress of an player to an advancement.
	 * 
	 * @param data Base64 string to convert to HashMap.
	 * @return HashMap created from the Base64 string.
	 * @throws IOException
	 */
	public static HashMap<Advancement, AdvancementProgress> advancementsFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			HashMap<Advancement, AdvancementProgress> hashMap = new HashMap<Advancement, AdvancementProgress>();
			
			int length = dataInput.readInt();
			
			for ( int i = 0; i < length; i++ ) {
				Advancement adv = (Advancement) dataInput.readObject();
				AdvancementProgress advProg = (AdvancementProgress) dataInput.readObject();
				hashMap.put(adv, advProg);
			}
			
			dataInput.close();
			return hashMap;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	
	/**
	 * 
	 * A method to serialize a Collection of {@link PotionEffect}s to a Base64 String.
	 * 
	 * @param  potion effects to turn into a Base64 String.
	 * @return Base64 string of the potion effects.
	 * @throws IllegalStateException
	 */
	public static String potionEffectsToBase64(Collection<PotionEffect> potionEffects) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			
			dataOutput.writeInt(potionEffects.size());
			
			for ( Iterator<PotionEffect> i = potionEffects.iterator(); i.hasNext(); ) {
				dataOutput.writeObject(i.next());
			}
			
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save advancementprogress.", e);
		}
	}
	
	/**
	 * Gets a {@link Collection<PotionEffect>} of potion effects from a Base64 String.
	 * 
	 * @param data Base64 string to convert to Collection.
	 * @return Collection created from the Base64 string.
	 * @throws IOException
	 */
	public static Collection<PotionEffect> potionEffectsFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Collection<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
			int length = dataInput.readInt();
			
			// Read the serialized potion effects.
			for (int i = 0; i < length; i++) {
				potionEffects.add((PotionEffect) dataInput.readObject());
			}
			
			dataInput.close();
			return potionEffects;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	
}
