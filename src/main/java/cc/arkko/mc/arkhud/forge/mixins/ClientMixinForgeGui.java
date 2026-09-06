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
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ForgeGui.class, remap = false)
public class ClientMixinForgeGui extends Gui {
    public ClientMixinForgeGui(Minecraft p_232355_, ItemRenderer p_232356_) {
        super(p_232355_, p_232356_);
    }

    @WrapMethod(method = { "renderHealth", "renderFood", "renderAir" })
    private void hideDefaultHud(int width, int height, GuiGraphics guiGraphics, Operation<Void> original) {
        if (!ModForgeArkHud.shouldRenderCustomHud()) {
            original.call(width, height, guiGraphics);
        }
    }

    @WrapMethod(method = "renderArmor")
    private void hideDefaultHud(GuiGraphics guiGraphics, int width, int height, Operation<Void> original) {
        if (!ModForgeArkHud.shouldRenderCustomHud()) {
            original.call(guiGraphics, width, height);
        }
    }
}
