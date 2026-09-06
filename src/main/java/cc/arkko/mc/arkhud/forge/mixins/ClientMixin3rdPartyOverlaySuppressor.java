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
import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.PlayerHealthHUD;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {
    StaminaHUDController.class,
    ManaBarOverlay.class,
    PlayerHealthHUD.class,
    GunHudOverlay.class
}, remap = false)
public abstract class ClientMixin3rdPartyOverlaySuppressor implements IGuiOverlay {
    @WrapMethod(method = "render")
    public void suppressRender(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height, Operation<Void> opt) {
        if (!ModForgeArkHud.shouldRenderCustomHud()) {
            opt.call(gui, graphics, partialTick, width, height);
        }
    }
}
