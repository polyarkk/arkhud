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
import com.mojang.blaze3d.platform.GlDebug;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GlDebug.class)
public class ClientMixinGlDebug {
    @WrapMethod(method = "printDebugLog")
    private static void GlInvalidOperationQuestionMarkWhatGlInvalidOperation(
        int p_84039_, int p_84040_, int p_84041_, int p_84042_, int p_84043_, long p_84044_, long p_84045_,
        Operation<Void> original
    ) {
        if (p_84041_ == 1282) {
            ModForgeArkHud.glInvalidOperation1282s++;
        } else {
            original.call(p_84039_, p_84040_, p_84041_, p_84042_, p_84043_, p_84044_, p_84045_);
        }
    }
}
