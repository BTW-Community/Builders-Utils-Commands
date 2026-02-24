package net.dravigen.bu_commands.utils;

import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.src.CommandBase.getPlayer;

public class ListsUtils {
	
	public static Map<Class<?>, String> CLASS_TO_STRING_MAPPING;
	public static Map<String, String> itemMap = new HashMap<>();
	public static Map<String, Short> potionsMap = new TreeMap<>();
	public static Map<String, Integer> entitiesMap = new HashMap<>();
	public static Map<String, Short> enchantMap = new TreeMap<>();
	
	public static void initMaps() {
		initEnchantNameList();
		initEntityList();
		initPotionList();
		initItemsNameList();
	}
	
	public static void initEnchantNameList() {
		enchantMap.clear();
		
		for (Enchantment enchant : Enchantment.enchantmentsList) {
			if (enchant == null) continue;
			String name = enchant.getName();
			enchantMap.put(name, (short) enchant.effectId);
		}
		
		enchantMap = sortByValuesShort(enchantMap);
	}
	
	public static void initItemsNameList() {
		itemMap.clear();
		List<ItemStack> subItems = new ArrayList<>();
		List<ItemStack> subItems2 = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		
		for (Item item : Item.itemsList) {
			if (item == null) continue;
			
			List<ItemStack> sub = new ArrayList<>();
			
			item.getSubItems(item.itemID, null, sub);
			
			if (item.itemID == 74 || item.itemID == 22480) continue;
			
			subItems2.addAll(sub);
			subItems.addAll(sub);
		}
		
		for (ItemStack stack : subItems) {
			if (stack == null || stack.getItem() == null) continue;
			
			String entry = stack.getUnlocalizedName() +
					".name"; //Item.itemsList[stack.itemID].getItemStackDisplayName(stack).replace(" ", "_");
			if (entry.toLowerCase().contains("old") ||
					entry.toLowerCase().contains("null") ||
					entry.toLowerCase().contains("blockcandle")) continue;
			
			String translated1 = stack.getDisplayName(); //formatItemName(I18n.getString(entry));
			
			if (translated1.toLowerCase().contains("old") ||
					translated1.toLowerCase().contains("null") ||
					translated1.toLowerCase().contains("blockcandle")) continue;
			
			for (ItemStack stack2 : subItems2) {
				if (stack2 == null || stack2.getItem() == null || stack == stack2) continue;
				
				String name = stack2.getUnlocalizedName() +
						".name"; //Item.itemsList[stack2.itemID].getItemStackDisplayName(stack2).replace(" ", "_");
				
				if (name.toLowerCase().contains("old") ||
						name.toLowerCase().contains("null") ||
						name.toLowerCase().contains("blockcandle")) continue;
				
				String translated2 = stack2.getDisplayName();// formatItemName(I18n.getString(name));//Item.itemsList[stack2.itemID].getItemStackDisplayName(stack2)); //formatItemName(name);
				
				if (translated2.toLowerCase().contains("old") ||
						translated2.toLowerCase().contains("null") ||
						translated2.toLowerCase().contains("blockcandle")) continue;
				
				if (name.equalsIgnoreCase(entry) || translated2.equalsIgnoreCase(translated1)) {
					if (stack.itemID == stack2.itemID) {
						if (stack.getItemDamage() != stack2.getItemDamage()) {
							entry += (stack.getHasSubtypes() ? "/" + stack.getItemDamage() : "");
							break;
						}
					}
					else {
						entry += "|" + stack.itemID;
						
						if (stack.getHasSubtypes() && stack2.getHasSubtypes()) {
							entry += (stack.getHasSubtypes() ? "/" + stack.getItemDamage() : "");
						}
						
						break;
					}
				}
			}
			
			map.put(entry, stack.itemID + "/" + stack.getItemDamage());
		}
		
		itemMap = sortItemMap(map);
	}
	
	
	public static ItemInfo getItemInfo(String[] strings) {
		int id;
		int meta = 0;
		String[] idMeta = strings[1].split("/");
		
		try {
			id = Integer.parseInt(idMeta[0]);
			
			if (idMeta.length == 4) {
				meta = Integer.parseInt(strings[3]);
			}
		} catch (NumberFormatException ignored) {
			try {
				String[] idMetaF = itemMap.get(strings[1]).split("/");
				id = Integer.parseInt(idMetaF[0]);
				
				meta = Integer.parseInt(idMetaF[1]);
				
				if (new ItemStack(id, 0, 0).getHasSubtypes() && idMeta.length == 2) {
					meta = Integer.parseInt(idMeta[1]);
				}
			} catch (Exception ignored1) {
				return null;
			}
		}
		return new ItemInfo(id, meta, strings[1]);
	}
	
	public static void initEntityList() {
		entitiesMap.clear();
		
		if (CLASS_TO_STRING_MAPPING != null) {
			for (Map.Entry<Class<?>, String> entry : CLASS_TO_STRING_MAPPING.entrySet()) {
				Class<?> entityClass = entry.getKey();
				String base = CLASS_TO_STRING_MAPPING.get(entityClass);
				
				
				entitiesMap.put(base, EntityList.getEntityIDFromClass(entityClass));
			}
			
			entitiesMap.put("bu.commands.list.lightning", -2);
		}
		
		entitiesMap = sortByValuesInteger(entitiesMap);
	}
	
	public static void initPotionList() {
		for (Potion potion : Potion.potionTypes) {
			if (potion == null) continue;
			
			String name = potion.getName();
			potionsMap.put(name, (short) potion.id);
		}
		
		potionsMap = sortByValuesShort(potionsMap);
	}
	
	private static <T> @NotNull LinkedHashMap<T, Integer> sortByValuesInteger(Map<T, Integer> map) {
		return map.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey,
										  Map.Entry::getValue,
										  (oldValue, newValue) -> oldValue,
										  LinkedHashMap::new));
	}
	
	private static <T> @NotNull LinkedHashMap<T, Short> sortByValuesShort(Map<T, Short> map) {
		return map.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey,
										  Map.Entry::getValue,
										  (oldValue, newValue) -> oldValue,
										  LinkedHashMap::new));
	}
	
	public static Map<String, String> sortItemMap(Map<String, String> unsortedMap) {
		return unsortedMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue((v1, v2) -> {
					String[] parts1 = v1.split("/");
					String[] parts2 = v2.split("/");
					
					int id1 = Integer.parseInt(parts1[0]);
					int id2 = Integer.parseInt(parts2[0]);
					
					if (id1 != id2) {
						return Integer.compare(id1, id2);
					}
					
					int meta1 = Integer.parseInt(parts1[1]);
					int meta2 = Integer.parseInt(parts2[1]);
					return Integer.compare(meta1, meta2);
				}))
				.collect(Collectors.toMap(Map.Entry::getKey,
										  Map.Entry::getValue,
										  (oldValue, newValue) -> oldValue,
										  LinkedHashMap::new));
	}
	
	public static @NotNull String formatItemName(String name) {
		return name.replace(" ", "")
				.replace("tile.", "")
				.replace("fc", "")
				.replace(".name", "")
				.replace("item.", "")
				.replace("btw:", "")
				.replace(".siding", "")
				.replace(".corner", "")
				.replace(".moulding", "");
	}
	
	public static MovingObjectPosition getBlockPlayerIsLooking(ICommandSender sender) {
		EntityPlayer player = getPlayer(sender, sender.getCommandSenderName());
		Vec3 var3 = player.getPosition(1);
		var3.yCoord += player.getEyeHeight();
		Vec3 var4 = player.getLookVec();
		int reach = 256;
		Vec3 var5 = var3.addVector(var4.xCoord * reach, var4.yCoord * reach, var4.zCoord * reach);
		return player.worldObj.clip(var3, var5);
	}
	
	public record ItemInfo(int id, int meta, String itemName) {}
}

