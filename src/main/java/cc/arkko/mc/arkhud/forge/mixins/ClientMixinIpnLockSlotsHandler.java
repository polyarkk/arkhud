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
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.specific.event.PLockSlotHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LockSlotsHandler.class, remap = false)
public abstract class ClientMixinIpnLockSlotsHandler implements PLockSlotHandler {
    @Inject(method = "postRenderHud", at = @At("HEAD"), cancellable = true)
    public void suppressPostRenderHud(CallbackInfo ci) {
        if (ModForgeArkHud.shouldRenderCustomHud()) {
            ci.cancel();
        }
    }
}
