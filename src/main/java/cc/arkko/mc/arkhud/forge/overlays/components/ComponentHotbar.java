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

import cc.arkko.mc.arkhud.forge.ConfigManager;
import cc.arkko.mc.arkhud.forge.compacts.IpnLockedHotbarSlots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ComponentHotbar {
    private static final int SLOT_SIZE = 16;
    private static final int SLOT_GAP = 4;
    private static final int SCREEN_MARGIN = 4;
    private static final int MAIN_HAND_PROTRUSION = 8;
    private static final int SLOT_STRIDE = SLOT_SIZE + SLOT_GAP;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int HOTBAR_HEIGHT = HOTBAR_SLOT_COUNT * SLOT_SIZE + (HOTBAR_SLOT_COUNT - 1) * SLOT_GAP;
    private static final int ITEM_SIZE = 16;
    private static final int ITEM_OFFSET = (SLOT_SIZE - ITEM_SIZE) / 2;
    private static final float SLOT_ANIM_INTERPOLATION = 0.25F;
    private static final long HIDE_DELAY_MS = 2000L; // 无活动 2 秒后开始隐藏
    private static final float HIDE_INTERPOLATION = 0.35F; // Mth.lerp 插值系数
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int BORDER_THICKNESS = 1;
    private static final int BORDER_COLOR = 0xff333333;
    private static final int BORDER_COLOR_IPN_LOCKED = 0x5eff5e41;
    private static final int HIDE_OFFSET = SCREEN_MARGIN + SLOT_SIZE + MAIN_HAND_PROTRUSION + BORDER_THICKNESS; // 完全移出屏幕右缘所需位移

    // 主手槽位切换时物品的水平滑动偏移；0 = 普通位置，负值 = 向左突出
    private final float[] itemXOffset = new float[HOTBAR_SLOT_COUNT];
    private boolean itemXOffsetInitialized = false;

    // 每帧预计算的槽位渲染 X，背景与物品两个渲染循环复用，避免重复 Math.round
    private final int[] slotRenderX = new int[HOTBAR_SLOT_COUNT];
    private final boolean[] slotWasFilled = new boolean[HOTBAR_SLOT_COUNT];
    // hotbar 自动隐藏：整体水平位移（向右为正），0 = 原位显示，HIDE_OFFSET = 完全移出屏幕右缘
    private float renderOffset = 0.0F;
    private long lastActivityTime = Long.MIN_VALUE;
    private int lastSelected = -1;

    public void render(Minecraft mc, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Entity cameraEntity = mc.getCameraEntity();

        if (!(cameraEntity instanceof Player player)) {
            return;
        }

        int selected = player.getInventory().selected;
        List<ItemStack> items = player.getInventory().items;

        // 更新自动隐藏状态并取得当前水平位移；完全移出屏幕后不再渲染
        int offset = offsetHideHotbar(selected, items, partialTick);

        if (offset >= HIDE_OFFSET) {
            return; // 窗口内已完全看不到 hotbar，不再渲染
        }

        int startY = (screenHeight - HOTBAR_HEIGHT) / 2 + 36;
        int baseBlockX = blockX(screenWidth, false) + offset;

        // 指数插值：每 tick 向目标位置移动剩余距离的 SLOT_ANIM_INTERPOLATION（25%）
        float anim = 1.0F - (float)Math.pow(1.0F - SLOT_ANIM_INTERPOLATION, partialTick);

        // 一次循环完成插值更新与渲染 X 预计算，背景与物品两个渲染循环直接复用
        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            float target = i == selected ? -MAIN_HAND_PROTRUSION : 0.0F;

            if (!itemXOffsetInitialized) {
                itemXOffset[i] = target;
            } else {
                itemXOffset[i] += (target - itemXOffset[i]) * anim;
            }

            slotRenderX[i] = baseBlockX + Math.round(itemXOffset[i]);
        }

        itemXOffsetInitialized = true;

        // 背景体块 + outline：16px 透明黑色，间隔 4px，距离屏幕右侧 4px；主手槽位向左突出 8px
        // 背景、outline 与物品使用同一插值偏移，切换主手槽位时一起平滑滑动
        // 参照原版 renderHotbar，将背景整体推到 z=-90，保证渲染在物品之后
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);

        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            int blockX = slotRenderX[i];
            int blockY = startY + i * SLOT_STRIDE;
            guiGraphics.fill(blockX, blockY, blockX + SLOT_SIZE, blockY + SLOT_SIZE, BACKGROUND_COLOR);
            int borderColor = IpnLockedHotbarSlots.isSlotLocked(i) ? BORDER_COLOR_IPN_LOCKED : BORDER_COLOR;
            guiGraphics.renderOutline(blockX - 1, blockY - 1, SLOT_SIZE + 2, SLOT_SIZE + 2, borderColor);
        }

        guiGraphics.pose().popPose();

        // 物品：与体块等大（16px），体块内偏移为 0；主手槽位切换时沿水平方向平滑滑动
        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            int blockX = slotRenderX[i];
            int blockY = startY + i * SLOT_STRIDE;
            StaticComponents.renderItemSlot(guiGraphics, mc, blockX + ITEM_OFFSET, blockY + ITEM_OFFSET, partialTick, player, items.get(i), i + 1);
        }
    }

    /**
     * 计算 hotbar 的整体水平位移（向右为正，渲染时加到基础 X 上）。
     * 0 = 原位显示；达到 HIDE_OFFSET 时 hotbar 已完全移出屏幕右缘，render 不再绘制。
     * <p>
     * 活动事件（任一发生即刷新 2 秒计时并回显）：
     * 1. 主手槽位 selected 发生变化
     * 2. hotbar 中任意空槽位被放入新物品（槽位由空变为非空）
     * <p>
     * 隐藏与回显动画均使用 Mth.lerp 指数插值，系数 HIDE_INTERPOLATION = 0.35，
     * 按 partialTick 折算，每 tick 向目标位置移动剩余距离的约 35%。
     *
     * @return 四舍五入后的水平位移像素数
     */
    private int offsetHideHotbar(int selected, List<ItemStack> items, float partialTick) {
        float delta = 1.0F - (float)Math.pow(1.0F - HIDE_INTERPOLATION, partialTick);

        if (!ConfigManager.autoHideHotbar) {
            renderOffset = Mth.lerp(delta, renderOffset, 0);

            return Math.round(renderOffset);
        }

        long now = System.currentTimeMillis();

        // 活动检测：主手槽位切换，或某槽位由空变为非空
        boolean active = selected != lastSelected;

        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            boolean filled = !items.get(i).isEmpty();

            if (filled && !slotWasFilled[i]) {
                active = true;
            }

            slotWasFilled[i] = filled;
        }

        if (active) {
            lastActivityTime = now;
        }

        lastSelected = selected;

        // 超过 HIDE_DELAY_MS 无活动则隐藏（右移出屏幕），否则回到原位显示
        int target = now - lastActivityTime >= HIDE_DELAY_MS ? HIDE_OFFSET : 0;

        // Mth.lerp 指数插值：每 tick 移动剩余距离的约 35%，与帧率无关
        renderOffset = Mth.lerp(delta, renderOffset, target);

        return Math.round(renderOffset);
    }

    private static int blockX(int screenWidth, boolean mainHand) {
        return screenWidth - SCREEN_MARGIN - SLOT_SIZE - (mainHand ? MAIN_HAND_PROTRUSION : 0);
    }

}
