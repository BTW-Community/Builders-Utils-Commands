package net.dravigen.wait_a_sec.mixin;

import btw.client.gui.HamperGui;
import btw.client.gui.HopperGui;
import btw.client.gui.InfernalEnchanterGui;
import btw.client.gui.RenameItemGui;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiContainer.class)
public abstract class GuiContainerMixin {
	
	@Inject(method = "doesGuiPauseGame", at = @At("RETURN"), cancellable = true)
	private void pauseTheGame(CallbackInfoReturnable<Boolean> cir) {
		GuiContainer gui = (GuiContainer) (Object) this;
		
		if (gui instanceof GuiBrewingStand ||
				gui instanceof GuiEnchantment ||
				gui instanceof InfernalEnchanterGui ||
				gui instanceof GuiMerchant ||
				gui instanceof GuiRepair) return;
		
		cir.setReturnValue(true);
	}
}

