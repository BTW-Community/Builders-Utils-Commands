package net.dravigen.bu_commands.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandNewKill extends CommandBase {
	@Override
	public String getCommandName() {
		return "kill";
	}
	
	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return "bu.commands.commands.kill.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] strings) {
		boolean noDrop = false;
		
		for (String string : strings) {
			if (!noDrop) {
				noDrop = string.equalsIgnoreCase("noDrop");
			}
		}
		
		if (strings.length >= 1) {
			if (strings[0].equalsIgnoreCase("all")) {
				int killCount = 0;
				
				for (int i = 0; i < sender.getEntityWorld().loadedEntityList.size(); i++) {
					Entity entity = (Entity) sender.getEntityWorld().loadedEntityList.get(i);
					
					if (!(entity instanceof EntityPlayer)) {
						killCount++;
						
						if (!noDrop) {
							entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
						}
						else entity.setDead();
					}
				}
				
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " + killCount + " entities"));
			}
			else if (strings[0].equalsIgnoreCase("entity")) {
				int killCount = 0;
				int id = Integer.parseInt(strings[1]);
				String name = "entities";
				List<Entity> entitiesToKill = new ArrayList<>();
				
				for (Object object : sender.getEntityWorld().loadedEntityList) {
					Entity entity = (Entity) object;
					
					if (!(entity instanceof EntityPlayer) &&
							!(entity instanceof EntityItem) &&
							(id == -1 || EntityList.getEntityIDFromClass(entity.getClass()) == id)) {
						entitiesToKill.add(entity);
					}
				}
				
				for (Entity entity : entitiesToKill) {
					name = id == -1 ? name : entity.getEntityName();
					if (!noDrop) {
						entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
					}
					else entity.setDead();
					
					killCount++;
				}
				
				ChatMessageComponent kill = ChatMessageComponent.createFromTranslationWithSubstitutions(
						"bu.commands.commands.kill.entity",
						killCount,
						name);
				
				sender.sendChatToPlayer(kill);
				
			}
			else if (strings[0].equalsIgnoreCase("player") && strings.length == 2) {
				EntityPlayerMP var3 = CommandGive.getPlayer(sender, strings[1]);
				
				if (!noDrop) {
					var3.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
				}
				else {
					var3.setHealth(0);
				}
				
				ChatMessageComponent kill = ChatMessageComponent.createFromTranslationWithSubstitutions(
						"bu.commands.commands.kill.player",
						var3.getEntityName());
				var3.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
				sender.sendChatToPlayer(kill);
			}
			else if (strings[0].equalsIgnoreCase("item")) {
				int killCount = 0;
				/*ListsUtils.ItemInfo itemInfo = getItemInfo(strings);
				
				if (itemInfo == null) {
					getPlayer(sender,
							  sender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText(
							"§cWrong name or id"));
					return;
				}
				
				int id = itemInfo.id();
				*/
				for (Object entity : sender.getEntityWorld().loadedEntityList) {
					if (entity instanceof EntityItem item) {
						/*if (id == item.getEntityItem().itemID &&
								itemInfo.meta() == item.getEntityItem().getItemDamage()) {*/
						killCount++;
						item.setDead();
						//}
					}
				}
				
				ChatMessageComponent kill = ChatMessageComponent.createFromTranslationWithSubstitutions(
						"bu.commands.commands.kill.item",
						killCount);
				sender.sendChatToPlayer(kill);
			}
			else throw new WrongUsageException(this.getCommandUsage(sender));
		}
		else throw new WrongUsageException(this.getCommandUsage(sender));
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
		if (strings.length == 1) {
			return getListOfStringsMatchingLastWord(strings, "player", "entity", "item", "all");
		}
		else if (strings.length == 2 && strings[0].equalsIgnoreCase("entity")) {
			return Collections.singletonList("kill.entity|" + strings[1]);
		}
		else if (strings.length == 2 && strings[0].equalsIgnoreCase("player")) {
			return getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
		}
		else if (strings.length == 3 && strings[0].equalsIgnoreCase("all") ||
				(strings.length == 3 &&
						(strings[0].equalsIgnoreCase("player") || strings[0].equalsIgnoreCase("entity")))) {
			return Collections.singletonList("dropLoot");
		}
		
		return null;
	}
	
}
