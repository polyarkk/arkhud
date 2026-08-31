package cc.arkko.mc.arkhud.forge.mixins;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ManaBarOverlay.class, remap = false)
public abstract class ClientMixinIssManaBarOverlay implements IGuiOverlay {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void hideManaBar(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }
}
