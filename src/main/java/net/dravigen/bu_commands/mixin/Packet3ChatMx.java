package net.dravigen.bu_commands.mixin;

import net.minecraft.src.I18n;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet3Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static net.dravigen.bu_commands.utils.ListsUtils.*;

@Mixin(Packet3Chat.class)
public abstract class Packet3ChatMx extends Packet {
	@Shadow
	public String message;
	
	@Inject(method = "<init>(Ljava/lang/String;Z)V", at = @At("TAIL"))
	private void handleCustomCommand(String bl, boolean par2, CallbackInfo ci) {
		String msg = this.message;
		
		if (msg.startsWith("/")) {
			if (msg.startsWith("/give")) {
				String[] strings = msg.substring(6).split(" ");
				int id = 0;
				int count = strings.length >= 3 ? Integer.parseInt(strings[2]) : 1;
				int meta = strings.length == 4 ? Integer.parseInt(strings[3]) : 0;
				
				try {
					id = Integer.parseInt(strings[1]);
				} catch (NumberFormatException e) {
					for (Map.Entry<String, String> entry : itemMap.entrySet()) {
						String s = entry.getKey();
						String[] split1 = s.split("\\|");
						String[] split2 = s.split("/");
						
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
						
						if (strings[1].equalsIgnoreCase(translated)) {
							id = Integer.parseInt(idMeta[0]);
							meta = Integer.parseInt(idMeta[1]);
							
							break;
						}
					}
				}
				
				this.message = "/give " + strings[0] + " " + id + " " + count + " " + meta;
			}
			else if (msg.startsWith("/enchant")) {
				String[] strings = msg.substring(9).split(" ");
				int id = 0;
				int level = strings.length == 3 ? Integer.parseInt(strings[2]) : 1;
				
				try {
					id = Integer.parseInt(strings[1]);
				} catch (NumberFormatException e) {
					for (String s : enchantMap.keySet()) {
						if (strings[1].equalsIgnoreCase(I18n.getString(s).replace(" ", "_"))) {
							id = enchantMap.get(s);
							
							break;
						}
					}
				}
				
				this.message = "/enchant " + strings[0] + " " + id + " " + level;
			}
			else if (msg.startsWith("/effect")) {
				String[] strings = msg.substring(8).split(" ");
				int id = 0;
				int duration = strings.length >= 4 ? Integer.parseInt(strings[3]) : 30;
				int level = strings.length == 5 ? Integer.parseInt(strings[4]) : 0;
				
				if (strings.length >= 3) {
					try {
						id = Integer.parseInt(strings[2]);
					} catch (NumberFormatException e) {
						for (String s : potionsMap.keySet()) {
							if (strings[2].equalsIgnoreCase(I18n.getString(s).replace(" ", "_"))) {
								id = potionsMap.get(s);
								
								break;
							}
						}
					}
				}
				
				String string = strings.length >= 2 ? strings[1] : "set";
				
				this.message = "/effect " + strings[0] + " " + string + " " + id + " " + duration + " " + level;
			}
			else if (msg.startsWith("/summon")) {
				String[] strings = msg.substring(8).split(" ");
				String[] names = strings[0].split(":");
				String pos = strings.length >= 2 ? " " + strings[1] : "";
				StringBuilder ids = new StringBuilder();
				String num = strings.length == 3 ? " " + strings[2] : "";
				//String nbt = strings.length == 3 ? strings[2] : "{}";
				
				
				for (String name : names) {
					for (String s : entitiesMap.keySet()) {
						if (name.equalsIgnoreCase(I18n.getString("entity." + s + ".name")
														  .replace("Entity", "")
														  .replace("addon", "")
														  .replace("fc", "")
														  .replace(I18n.getString("entity.villager.name"), "")
														  .replace("DireWolf", "The_Beast")
														  .replace("JungleSpider", "Jungle_Spider")
														  .replace("arrow", "Arrow")
														  .replace(" ", "_")
														  .replace("entity.", "")
														  .replace(".name", ""))) {
							
							if (!ids.isEmpty()) {
								ids.append(":");
							}
							
							ids.append(entitiesMap.get(s));
						}
					}
				}
				
				this.message = "/summon " + ids + pos + num;
			}
			else if (msg.startsWith("/kill entity")) {
				int id = 0;
				
				if (msg.length() == 12) {
					id = -1;
				}
				else {
					String name = msg.substring(13);
					for (String s : entitiesMap.keySet()) {
						if (name.equalsIgnoreCase(I18n.getString("entity." + s + ".name")
														  .replace("Entity", "")
														  .replace("addon", "")
														  .replace("fc", "")
														  .replace(I18n.getString("entity.villager.name"), "")
														  .replace("DireWolf", "The_Beast")
														  .replace("JungleSpider", "Jungle_Spider")
														  .replace("arrow", "Arrow")
														  .replace(" ", "_")
														  .replace("entity.", "")
														  .replace(".name", ""))) {
							
							id = entitiesMap.get(s);
						}
					}
				}
				
				this.message = "/kill entity " + id;
			}
		}
	}
}
