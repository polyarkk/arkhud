package cc.arkko.mc.arkhud.forge.mixins;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.PlayerHealthHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerHealthHUD.class, remap = false)
public abstract class ClientMixinPasterDreamPlayerHealthHUD implements IGuiOverlay {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void suppressRender(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }
}
