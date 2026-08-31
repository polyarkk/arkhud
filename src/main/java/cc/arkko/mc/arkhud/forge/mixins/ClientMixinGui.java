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

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.overlays.components.StaticComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class ClientMixinGui {
    @Final
    @Shadow
    protected Minecraft minecraft;

    @Shadow
    protected int toolHighlightTimer;

    @Shadow
    protected ItemStack lastToolHighlight = ItemStack.EMPTY;

    // todo we may inject Gui#render instead for better performance(?)
    @Inject(method = {
        "renderExperienceBar",
        "renderJumpMeter",
        "renderVehicleHealth",
        "renderEffects"
    }, at = @At("HEAD"), cancellable = true)
    private void hideDefaultHud(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void hideHotbar(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
        at = @At("HEAD"), cancellable = true, remap = false
    )
    private void renderSelectedItemName2(GuiGraphics p_283501_, int yShift, CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            StaticComponents.renderSelectedItemName(p_283501_, minecraft, toolHighlightTimer, lastToolHighlight);
            ci.cancel();
        }
    }
}
