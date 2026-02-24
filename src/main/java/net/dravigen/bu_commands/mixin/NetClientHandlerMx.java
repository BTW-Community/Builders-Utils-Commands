package net.dravigen.bu_commands.mixin;

import net.dravigen.bu_commands.utils.ListsUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static net.dravigen.bu_commands.utils.ListsUtils.formatItemName;

@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMx extends NetHandler {
	
	@Shadow
	private Minecraft mc;
	
	
	@Inject(method = "handleLogin", at = @At("HEAD"))
	private void initList(Packet1Login par1Packet1Login, CallbackInfo ci) {
		ListsUtils.initMaps();
	}
	
	@Inject(method = "handleAutoComplete", at = @At("HEAD"), cancellable = true)
	private void commandCompleteClient(Packet203AutoComplete packet, CallbackInfo ci) {
		ci.cancel();
		String rawText = packet.getText();
		String[] strings = rawText.split("\u0000");
		String[] split = strings[strings.length - 1].split("\\|");
		StringBuilder list = new StringBuilder();
		String value = split.length >= 2 ? split[1] : "";
		
		String identifier = split[0];
		switch (identifier) {
			case "give" -> {
				for (Map.Entry<String, String> entry : ListsUtils.itemMap.entrySet()) {
					String key = entry.getKey();
					String[] split1 = key.split("\\|");
					String[] split2 = key.split("/");
					
					String[] idMeta = entry.getValue().split("/");
					String translated = formatItemName((new ItemStack(Integer.parseInt(idMeta[0]),
																	  1,
																	  Integer.parseInt(idMeta[1]))).getDisplayName()); //formatItemName(I18n.getString(key));
					
					if (split1.length == 2) {
						translated += "|" + idMeta[0];
					}
					if (split2.length == 2) {
						translated += "/" + idMeta[1];
					}
					
					if (CommandBase.doesStringStartWith(value, translated)) {
						if (!list.isEmpty()) {
							list.append("\u0000");
						}
						
						list.append(translated);
					}
				}
				
				for (Map.Entry<String, String> entry : ListsUtils.itemMap.entrySet()) {
					String key = entry.getKey();
					String[] split1 = key.split("\\|");
					String[] split2 = key.split("/");
					
					String[] idMeta = entry.getValue().split("/");
					String translated = formatItemName((new ItemStack(Integer.parseInt(idMeta[0]),
																	  1,
																	  Integer.parseInt(idMeta[1]))).getDisplayName());
					
					if (split1.length == 2) {
						translated += "|" + idMeta[0];
					}
					if (split2.length == 2) {
						translated += "/" + idMeta[1];
					}
					
					if (list.toString().contains(translated)) continue;
					
					if (translated.toLowerCase().contains(value.toLowerCase())) {
						if (!list.isEmpty()) {
							list.append("\u0000");
						}
						
						list.append(translated);
					}
				}
				
				strings = list.toString().split("\u0000");
			}
			case "enchant" -> {
				for (String s : ListsUtils.enchantMap.keySet()) {
					String translated = I18n.getString(s).replace(" ", "_");
					
					if (CommandBase.doesStringStartWith(value, translated)) {
						if (!list.isEmpty()) {
							list.append("\u0000");
						}
						
						list.append(translated);
					}
				}
				
				strings = list.toString().split("\u0000");
			}
			case "effect" -> {
				for (String s : ListsUtils.potionsMap.keySet()) {
					String translated = I18n.getString(s).replace(" ", "_");
					
					if (CommandBase.doesStringStartWith(value, translated)) {
						if (!list.isEmpty()) {
							list.append("\u0000");
						}
						
						list.append(translated);
					}
				}
				
				strings = list.toString().split("\u0000");
			}
			case "summon" -> {
				String[] ids = value.split(":");
				String var2 = ":";
				boolean afterColon = var2.regionMatches(true, 0, value, value.length() - 1, 1);
				StringBuilder previous = new StringBuilder();
				
				if (afterColon) {
					previous.append(value);
					value = "";
				}
				else if (ids.length > 1) {
					value = ids[ids.length - 1];
					
					for (int i = 0; i < ids.length - 1; i++) {
						previous.append(ids[i]).append(":");
					}
				}
				
				for (String s : ListsUtils.entitiesMap.keySet()) {
					String translated = I18n.getString("entity." + s + ".name")
							.replace("Entity", "")
							.replace("addon", "")
							.replace("fc", "")
							.replace(I18n.getString("entity.villager.name"), "")
							.replace("DireWolf", "The_Beast")
							.replace("JungleSpider", "Jungle_Spider")
							.replace("arrow", "Arrow")
							.replace(" ", "_")
							.replace("entity.", "")
							.replace(".name", "");
					
					if (CommandBase.doesStringStartWith(value, translated)) {
						if (!list.isEmpty()) {
							list.append("\u0000");
						}
						
						if (!previous.isEmpty()) {
							list.append(previous);
						}
						
						list.append(translated);
					}
				}
				
				strings = list.toString().split("\u0000");
			}
			case "kill.entity" -> {
				for (String s : ListsUtils.entitiesMap.keySet()) {
					String translated = I18n.getString("entity." + s + ".name")
							.replace("Entity", "")
							.replace("addon", "")
							.replace("fc", "")
							.replace(I18n.getString("entity.villager.name"), "")
							.replace("DireWolf", "The_Beast")
							.replace("JungleSpider", "Jungle_Spider")
							.replace("arrow", "Arrow")
							.replace(" ", "_")
							.replace("entity.", "")
							.replace(".name", "");
					
					if (CommandBase.doesStringStartWith(value, translated)) {
						if (!list.isEmpty()) {
							list.append("\u0000");
						}
						
						list.append(translated);
					}
				}
				
				strings = list.toString().split("\u0000");
			}
		}
		
		if (this.mc.currentScreen instanceof GuiChat var3) {
			var3.func_73894_a(strings);
		}
	}
}
