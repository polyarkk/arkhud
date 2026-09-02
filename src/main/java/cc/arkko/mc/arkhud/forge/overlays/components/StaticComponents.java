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

package cc.arkko.mc.arkhud.forge.overlays.components;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.index.ClientAmmoIndex;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Objects;

public class StaticComponents {
    private static final float ITEM_SCALE = 0.8F;

    static void renderItemSlot(GuiGraphics guiGraphics, Minecraft mc, int x, int y, float partialTick, Player player, ItemStack stack, int seed) {
        if (stack.isEmpty()) {
            return;
        }

        float f = (float)stack.getPopTime() - partialTick;

        if (f > 0.0F) {
            float f1 = 1.0F + f / 5.0F;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float)(x + 8), (float)(y + 12), 0.0F);
            guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            guiGraphics.pose().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
        }

        // 物品与数字/耐久条一起缩小为原来的 0.8x，围绕物品中心 (x+8, y+8) 缩放
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float)(x + 8), (float)(y + 8), 0.0F);
        guiGraphics.pose().scale(ITEM_SCALE, ITEM_SCALE, 1.0F);
        guiGraphics.pose().translate((float)(-(x + 8)), (float)(-(y + 8)), 0.0F);
        guiGraphics.renderItem(player, stack, x, y, seed);
        guiGraphics.renderItemDecorations(mc.font, stack, x, y);
        guiGraphics.pose().popPose();

        if (f > 0.0F) {
            guiGraphics.pose().popPose();
        }
    }

    public static void renderSelectedItemName(GuiGraphics guiGraphics, Minecraft minecraft, int toolHighlightTimer, ItemStack lastToolHighlight) {
        if (toolHighlightTimer > 0 && !lastToolHighlight.isEmpty()) {
            MutableComponent mutablecomponent = Component.empty().append(lastToolHighlight.getHoverName()).withStyle(lastToolHighlight.getRarity().getStyleModifier());

            if (lastToolHighlight.hasCustomHoverName()) {
                mutablecomponent.withStyle(ChatFormatting.ITALIC);
            }

            Component highlightTip = lastToolHighlight.getHighlightTip(mutablecomponent);

            int screenWidth = guiGraphics.guiWidth();
            int screenHeight = guiGraphics.guiHeight();

            // 右下角、文本右对齐：右边缘距离屏幕右侧 37px
            int i = minecraft.font.width(highlightTip);
            int j = screenWidth - 37 - i;
            int k = screenHeight - 39;

            // 渐变 alpha 与原版一致：toolHighlightTimer 的前 10 tick 淡入，其余时间保持不透明
            int l = (int)((float)toolHighlightTimer * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            String ammo = Strings.EMPTY;

            if (ModForgeArkHud.depTacz && lastToolHighlight.getItem() instanceof IGun gun) {
                GunData gunData = TimelessAPI.getClientGunIndex(gun.getGunId(lastToolHighlight)).map(ClientGunIndex::getGunData).orElse(null);

                if (gunData != null) {
                    ammo = TimelessAPI.getClientAmmoIndex(gunData.getAmmoId()).map(ClientAmmoIndex::getName).orElse(ammo);
                }
            }

            if (l > 0) {
                Font font = IClientItemExtensions.of(lastToolHighlight).getFont(lastToolHighlight, IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);

                int k2 = Objects.equals(ammo, Strings.EMPTY) ? k : k - 9;

                if (font == null) {
                    guiGraphics.drawString(minecraft.font, highlightTip, j, k2, 16777215 + (l << 24));
                } else {
                    j = screenWidth - 37 - font.width(highlightTip);
                    guiGraphics.drawString(font, highlightTip, j, k2, 16777215 + (l << 24));
                }

                if (k2 != k) {
                    Component c = Component.translatable(ammo);
                    guiGraphics.drawString(minecraft.font, c, screenWidth - 37 - minecraft.font.width(c), k, 16777215 + (l << 24));
                }
            }
        }
    }
}
