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

package cc.arkko.mc.arkhud.forge.mixins.chatcore;

import cc.arkko.mc.arkhud.forge.ConfigManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rpgperspective.client.RPGHudRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RPGHudRenderer.class, remap = false)
public class ClientMixinRPGHudRenderer {
    @WrapMethod(method = "renderBelowPopupsNow")
    private void suppressChatCoreHud(GuiGraphics gui, int screenW, int screenH, Operation<Void> original) {
        if (ConfigManager.disabled) {
            original.call(gui, screenW, screenH);
        }
    }

    @WrapMethod(method = "onMouseScroll")
    private void onMouseScroll2(InputEvent.MouseScrollingEvent event, Operation<Void> original) {
        if (ConfigManager.disabled) {
            original.call(event);
        }
    }
}
