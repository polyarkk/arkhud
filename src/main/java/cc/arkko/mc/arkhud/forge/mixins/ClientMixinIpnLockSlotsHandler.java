package cc.arkko.mc.arkhud.forge.mixins;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.configs.ConfigManager;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.specific.event.PLockSlotHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LockSlotsHandler.class, remap = false)
public abstract class ClientMixinIpnLockSlotsHandler implements PLockSlotHandler {
    @Inject(method = "postRenderHud", at = @At("HEAD"), cancellable = true)
    public void suppressPostRenderHud(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }
}
