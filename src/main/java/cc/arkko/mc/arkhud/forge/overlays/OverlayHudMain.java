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

package cc.arkko.mc.arkhud.forge.overlays;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentBottomLeft;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentBottomMiddle;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentBottomRight;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentHotbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class OverlayHudMain implements IGuiOverlay {
    private final ComponentBottomLeft componentBottomLeft = new ComponentBottomLeft();
    private final ComponentBottomRight componentBottomRight = new ComponentBottomRight();
    private final ComponentHotbar componentHotbar = new ComponentHotbar();
    private final ComponentBottomMiddle componentBottomMiddle = new ComponentBottomMiddle();

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!ModForgeArkHud.shouldRenderCustomHud()) {
            return;
        }

        Minecraft mc = gui.getMinecraft();

        mc.getProfiler().push("arkhud_rendering");

        componentBottomLeft.render(mc, partialTick, guiGraphics, screenHeight);
        componentBottomRight.render(mc, guiGraphics, screenWidth, screenHeight);
        componentHotbar.render(mc, guiGraphics, partialTick, screenWidth, screenHeight);
        componentBottomMiddle.render(mc, guiGraphics, partialTick, screenWidth, screenHeight);

        mc.getProfiler().pop();
    }
}
