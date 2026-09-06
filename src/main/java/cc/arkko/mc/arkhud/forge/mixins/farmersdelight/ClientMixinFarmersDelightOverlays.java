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

package cc.arkko.mc.arkhud.forge.mixins.farmersdelight;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Mixin(targets = {
    "vectorwing.farmersdelight.client.gui.ComfortHealthOverlay",
    "vectorwing.farmersdelight.client.gui.NourishmentHungerOverlay"
}, remap = false)
@Pseudo
public class ClientMixinFarmersDelightOverlays {
    @WrapMethod(method = "onRenderGuiOverlayPost")
    public void suppressRender(RenderGuiOverlayEvent.Post event, Operation<Void> opt) {
        if (!ModForgeArkHud.shouldRenderCustomHud()) {
            opt.call(event);
        }
    }
}
