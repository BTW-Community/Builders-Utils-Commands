package net.dravigen.bu_commands.command;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.dravigen.bu_commands.utils.ListsUtils.getBlockPlayerIsLooking;

public class CommandSummon extends CommandBase {
	@Override
	public String getCommandName() {
		return "summon";
	}
	
	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return "bu.commands.commands.summon.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] strings) {
		if (strings.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		
		int entityNum = strings.length == 3 ? Integer.parseInt(strings[2]) : 1;
		
		EntityPlayer player = getPlayer(sender, sender.getCommandSenderName());
		
		double x = player.posX;
		double y = player.posY;
		double z = player.posZ;
		
		if (strings.length >= 2) {
			String[] pos = strings[1].split("/");
			
			x = getCoord(sender, x, pos[0]) + 0.5;
			y = getCoord(sender, y, pos[1]);
			z = getCoord(sender, z, pos[2]) + 0.5;
		}
		
		for (int i = 0; i < entityNum; i++) {
			String[] entitiesID = strings[0].split(":");
			List<Entity> entities = new ArrayList<>();
			
			for (String sID : entitiesID) {
				if (sID.equals("-2")) {
					player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, x, y, z));
				}
				else {
					entities.add(EntityList.createEntityByID(Integer.parseInt(sID), sender.getEntityWorld()));
				}
			}
			
			if (!entities.isEmpty()) {
				for (int j = 0; j < entities.size(); j++) {
					Entity entity2 = entities.get(j);
					entity2.setPosition(x, y, z);
					sender.getEntityWorld().spawnEntityInWorld(entity2);
					
					if (j > 0) {
						entity2.mountEntity(entities.get(j - 1));
					}
				}
			}
		}
		
		notifyAdmins(sender, "bu.commands.commands.summon.success");
	}
	
	@Unique
	private double getCoord(ICommandSender sender, double pos, String string) {
		try {
			return CommandBase.func_110666_a(sender, MathHelper.floor_double(pos), string);
		} catch (Exception ignored) {
			return Integer.parseInt(string);
		}
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
		if (strings.length == 1) {
			return Collections.singletonList("summon|" + strings[0]);
		}
		
		MovingObjectPosition blockCoord = getBlockPlayerIsLooking(sender);
		
		if (strings.length == 2) {
			if (blockCoord != null) {
				int x = blockCoord.blockX;
				int y = blockCoord.blockY;
				int z = blockCoord.blockZ;
				
				return getListOfStringsMatchingLastWord(strings, x + "/" + y + "/" + z);
			}
			else return null;
		}
		
		return null;
	}
}
