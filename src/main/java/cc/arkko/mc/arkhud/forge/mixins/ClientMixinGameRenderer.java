/*
 * Copyright (c) 2026.
 * This file is part of ArkHUD (https://github.com/polyarkk/arkhud)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 3.0, as published by the
 * Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cc.arkko.mc.arkhud.forge.mixins;

import cc.arkko.mc.arkhud.forge.HudState;
import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class ClientMixinGameRenderer {
    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    protected abstract void renderItemActivationAnimation(int p_109101_, int p_109102_, float p_109103_);

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void onRenderHand1(CallbackInfo ci) {
        if (ModForgeArkHud.hudState.equals(HudState.NO_HUD)) {
            minecraft.options.hideGui = false;
        }
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void onRenderHand2(CallbackInfo ci) {
        if (ModForgeArkHud.hudState.equals(HudState.NO_HUD)) {
            minecraft.options.hideGui = true;
        }
    }

    // @Inject(
    //     method = "render",
    //     at = @At(
    //         value = "INVOKE",
    //         target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
    //         shift = At.Shift.BEFORE, // fragile
    //         ordinal = 0
    //     ),
    //     locals = LocalCapture.CAPTURE_FAILSOFT
    // )
    // private void remainVignetteVisibleOnHideHud(
    //     float p_109094_, long p_109095_, boolean p_109096_, CallbackInfo ci,
    //     int i, int j, Window window, Matrix4f matrix4f, PoseStack posestack, GuiGraphics guigraphics
    // ) {
    //     Minecraft mc = Minecraft.getInstance();
    //
    //     if (ModForgeArkHud.HUD_STATE.equals(HudState.NO_HUD)) {
    //         Entity ent = mc.getCameraEntity();
    //
    //         if (ent != null) {
    //             renderItemActivationAnimation(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), p_109094_);
    //             RenderSystem.enableBlend();
    //
    //             if (Minecraft.useFancyGraphics()) {
    //                 mc.gui.renderVignette(guigraphics, ent);
    //             } else {
    //                 RenderSystem.enableDepthTest();
    //             }
    //
    //             RenderSystem.clear(256, Minecraft.ON_OSX);
    //         }
    //     }
    // }
}
