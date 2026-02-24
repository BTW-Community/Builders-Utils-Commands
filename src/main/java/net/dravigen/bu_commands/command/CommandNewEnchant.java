package net.dravigen.bu_commands.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

public class CommandNewEnchant extends CommandBase {
	@Override
	public String getCommandName() {
		return "enchant";
	}
	
	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return "commands.enchant.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] strings) {
		if (strings.length >= 2) {
			NBTTagList nBTTagList;
			EntityPlayerMP entityPlayerMP = CommandEnchant.getPlayer(sender, strings[0]);
			int id = CommandEnchant.parseIntBounded(sender, strings[1], 0, Enchantment.enchantmentsList.length - 1);
			int level = 1;
			ItemStack itemStack = entityPlayerMP.getCurrentEquippedItem();
			
			if (itemStack == null) {
				throw new CommandException("commands.enchant.noItem");
			}
			
			Enchantment enchantment = Enchantment.enchantmentsList[id];
			
			if (enchantment == null) {
				throw new NumberInvalidException("commands.enchant.notFound", id);
			}
			
			if (strings.length >= 3) {
				level = CommandEnchant.parseIntBounded(sender, strings[2], enchantment.getMinLevel(), Byte.MAX_VALUE);
			}
			
			if (itemStack.hasTagCompound() && (nBTTagList = itemStack.getEnchantmentTagList()) != null) {
				for (int i = 0; i < nBTTagList.tagCount(); ++i) {
					short s = ((NBTTagCompound) nBTTagList.tagAt(i)).getShort("id");
					
					if (s == enchantment.effectId || Enchantment.enchantmentsList[s] == null) {
						if (s == enchantment.effectId) {
							nBTTagList.removeTag(i);
						}
					}
				}
			}
			
			itemStack.addEnchantment(enchantment, level);
			CommandEnchant.notifyAdmins(sender, "commands.enchant.success");
			
			return;
		}
		
		throw new WrongUsageException("commands.enchant.usage");
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
		if (strings.length == 1) {
			return CommandEnchant.getListOfStringsMatchingLastWord(strings, this.getListOfPlayers());
		}
		else if (strings.length == 2) {
			return Collections.singletonList("enchant|" + strings[1]);
		}
		
		return null;
	}
	
	protected String[] getListOfPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}
	
}
