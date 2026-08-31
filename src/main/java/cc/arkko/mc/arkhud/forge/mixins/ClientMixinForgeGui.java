package cc.arkko.mc.arkhud.forge.mixins;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeGui.class, remap = false)
public class ClientMixinForgeGui extends Gui {
    public ClientMixinForgeGui(Minecraft p_232355_, ItemRenderer p_232356_) {
        super(p_232355_, p_232356_);
    }

    @Inject(method = { "renderHealth", "renderFood", "renderArmor", "renderAir" }, at = @At("HEAD"), cancellable = true)
    private void hideDefaultHud(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }
}
