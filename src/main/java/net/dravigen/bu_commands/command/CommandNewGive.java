package net.dravigen.bu_commands.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

public class CommandNewGive extends CommandBase {
	@Override
	public String getCommandName() {
		return "give";
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "commands.give.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] strings) {
		int meta;
		int count;
		int id;
		EntityPlayerMP player;
		if (strings.length >= 2) {
			player = CommandGive.getPlayer(sender, strings[0]);
			id = CommandGive.parseIntWithMin(sender, strings[1], 1);
			count = 1;
			meta = 0;
			if (Item.itemsList[id] == null) {
				throw new NumberInvalidException("commands.give.notFound", id);
			}
			if (strings.length >= 3) {
				count = CommandGive.parseIntBounded(sender, strings[2], 1, 64);
			}
			if (strings.length >= 4) {
				meta = CommandGive.parseInt(sender, strings[3]);
			}
		}
		else {
			throw new WrongUsageException("commands.give.usage");
		}
		
		ItemStack stack = new ItemStack(id, count, meta);
		stack.getItem().initializeStackOnGiveCommand(player.worldObj.rand, stack);
		EntityItem item = player.dropPlayerItem(stack);
		item.delayBeforeCanPickup = 0;
		
		if (meta > 0) {
			CommandGive.notifyAdmins(sender,
									 "bu.commands.commands.give.successMeta",
									 ChatMessageComponent.createFromTranslationKey(Item.itemsList[id].getItemStackDisplayName(
											 stack)),
									 id,
									 meta,
									 count,
									 player.getEntityName());
		}
		else {
			CommandGive.notifyAdmins(sender,
									 "bu.commands.commands.give.success",
									 ChatMessageComponent.createFromTranslationKey(Item.itemsList[id].getItemStackDisplayName(
											 stack)),
									 id,
									 count,
									 player.getEntityName());
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
		else if (strings.length == 2 && strings[1].split(" ").length == 1) {
			return Collections.singletonList("give|" + strings[1]);
		}
		
		return null;
	}
	
	private String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}
}
