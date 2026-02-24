package net.dravigen.bu_commands.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

public class CommandNewEffect extends CommandBase {
	@Override
	public String getCommandName() {
		return "effect";
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "bu.commands.commands.effect.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] strings) {
		if (strings.length < 2) throw new WrongUsageException(this.getCommandUsage(sender));
		
		EntityPlayerMP entityPlayerMP = CommandEffect.getPlayer(sender, strings[0]);
		
		if (strings[1].equals("clear")) {
			if (entityPlayerMP.getActivePotionEffects().isEmpty()) {
				throw new CommandException("commands.effect.failure.notActive.all", entityPlayerMP.getEntityName());
			}
			
			entityPlayerMP.clearActivePotions();
			CommandEffect.notifyAdmins(sender, "commands.effect.success.removed.all", entityPlayerMP.getEntityName());
		}
		else if (strings[1].equalsIgnoreCase("set") && strings.length >= 3) {
			int id = CommandEffect.parseIntWithMin(sender, strings[2], 1);
			int duration = 600;
			int n3 = 30;
			int amplifier = 0;
			
			if (id >= Potion.potionTypes.length || Potion.potionTypes[id] == null) {
				throw new NumberInvalidException("commands.effect.notFound", id);
			}
			
			Potion potion = Potion.potionTypes[id];
			
			if (strings.length >= 4) {
				n3 = CommandEffect.parseIntBounded(sender, strings[3], 0, 1000000);
				duration = potion.isInstant() ? n3 : n3 * 20;
			}
			else if (potion.isInstant()) {
				duration = 1;
			}
			
			if (strings.length >= 5) {
				amplifier = CommandEffect.parseIntBounded(sender, strings[4], 0, 255);
			}
			
			if (n3 == 0) {
				if (!entityPlayerMP.isPotionActive(id)) throw new CommandException("commands.effect.failure.notActive",
																				   ChatMessageComponent.createFromTranslationKey(
																						   Potion.potionTypes[id].getName()),
																				   entityPlayerMP.getEntityName());
				
				entityPlayerMP.removePotionEffect(id);
				CommandEffect.notifyAdmins(sender,
										   "commands.effect.success.removed",
										   ChatMessageComponent.createFromTranslationKey(Potion.potionTypes[id].getName()),
										   entityPlayerMP.getEntityName());
			}
			else {
				PotionEffect currentPotion = entityPlayerMP.getActivePotionEffect(potion);
				
				if (currentPotion != null) {
					entityPlayerMP.removePotionEffect(potion.id);
				}
				
				PotionEffect potionEffect = new PotionEffect(id, duration, amplifier);
				entityPlayerMP.addPotionEffect(potionEffect);
				CommandEffect.notifyAdmins(sender,
										   "commands.effect.success",
										   ChatMessageComponent.createFromTranslationKey(potionEffect.getEffectName()),
										   id,
										   amplifier,
										   entityPlayerMP.getEntityName(),
										   n3);
			}
		}
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
		if (strings.length == 1) {
			return CommandGive.getListOfStringsMatchingLastWord(strings, this.getPlayers());
		}
		else if (strings.length == 2) {
			return CommandGive.getListOfStringsMatchingLastWord(strings, "clear", "set");
		}
		else if (strings.length == 3 && strings[1].equalsIgnoreCase("set")) {
			return Collections.singletonList("effect|" + strings[2]);
		}
		
		return null;
	}
	
	private String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}
}
