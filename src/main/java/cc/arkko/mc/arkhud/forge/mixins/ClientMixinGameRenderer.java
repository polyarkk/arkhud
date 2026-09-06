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
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class ClientMixinGameRenderer {
    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    protected abstract void renderItemActivationAnimation(int p_109101_, int p_109102_, float p_109103_);

    @WrapMethod(method = "renderLevel")
    public void onRenderLevel(float p_109090_, long p_109091_, PoseStack p_109092_, Operation<Void> original) {
        if (ModForgeArkHud.hudState.equals(HudState.NO_HUD)) {
            minecraft.options.hideGui = false;
        }

        original.call(p_109090_, p_109091_, p_109092_);

        if (ModForgeArkHud.hudState.equals(HudState.NO_HUD)) {
            minecraft.options.hideGui = true;
        }
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
            shift = At.Shift.BEFORE, // fragile
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void remainVignetteVisibleOnHideHud(
        float p_109094_, long p_109095_, boolean p_109096_, CallbackInfo ci,
        int i, int j, Window window, Matrix4f matrix4f, PoseStack posestack, GuiGraphics guigraphics
    ) {
        Minecraft mc = Minecraft.getInstance();

        if (ModForgeArkHud.hudState.equals(HudState.NO_HUD)) {
            Entity ent = mc.getCameraEntity();

            if (ent != null) {
                renderItemActivationAnimation(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), p_109094_);
                RenderSystem.enableBlend();

                if (Minecraft.useFancyGraphics()) {
                    mc.gui.renderVignette(guigraphics, ent);
                } else {
                    RenderSystem.enableDepthTest();
                }

                RenderSystem.clear(256, Minecraft.ON_OSX);
            }
        }
    }
}
