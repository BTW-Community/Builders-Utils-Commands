package net.dravigen.bu_commands;

import api.AddonHandler;
import api.BTWAddon;
import net.dravigen.bu_commands.command.*;

public class BU_Commands extends BTWAddon {
	private static BU_Commands instance;
	
	public BU_Commands() {
		super();
		instance = this;
	}
	
	@Override
	public void initialize() {
		AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
		registerNewCommands();
	}
	
	private void registerNewCommands() {
		registerAddonCommand(new CommandSummon());
		registerAddonCommand(new CommandNewKill());
		registerAddonCommand(new CommandNewGive());
		registerAddonCommand(new CommandNewEffect());
		registerAddonCommand(new CommandNewEnchant());
	}
}