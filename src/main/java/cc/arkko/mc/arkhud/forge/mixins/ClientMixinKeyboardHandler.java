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
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class ClientMixinKeyboardHandler {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
        method = "keyPress",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;hideGui:Z"),
        cancellable = true
    )
    public void onF1Key(CallbackInfo ci) {
        ModForgeArkHud.HUD_STATE = ModForgeArkHud.HUD_STATE.next();

        minecraft.options.hideGui = !ModForgeArkHud.HUD_STATE.equals(HudState.ALL_VISIBLE);

        ci.cancel();
    }
}
