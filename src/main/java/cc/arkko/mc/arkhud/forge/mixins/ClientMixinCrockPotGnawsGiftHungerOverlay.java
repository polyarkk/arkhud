package cc.arkko.mc.arkhud.forge.mixins;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import com.sihenzhang.crockpot.client.GnawsGiftHungerOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GnawsGiftHungerOverlay.class, remap = false)
public class ClientMixinCrockPotGnawsGiftHungerOverlay {
    @Inject(method = "onRenderGuiOverlayPost", at = @At("HEAD"), cancellable = true)
    private static void suppressRenderGnawsGiftHud(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }
}
