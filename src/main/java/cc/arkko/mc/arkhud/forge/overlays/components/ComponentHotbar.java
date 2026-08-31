package cc.arkko.mc.arkhud.forge.overlays.components;

import cc.arkko.mc.arkhud.forge.compacts.IpnLockedHotbarSlots;
import cc.arkko.mc.arkhud.forge.configs.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int BORDER_THICKNESS = 1;
    private static final int BORDER_COLOR = 0xff333333;
    private static final int BORDER_COLOR_IPN_LOCKED = 0x5eff5e41;

    // 主手槽位切换时物品的水平滑动偏移；0 = 普通位置，负值 = 向左突出
    private final float[] itemXOffset = new float[HOTBAR_SLOT_COUNT];
    private boolean itemXOffsetInitialized = false;

    // 每帧预计算的槽位渲染 X，背景与物品两个渲染循环复用，避免重复 Math.round
    private final int[] slotRenderX = new int[HOTBAR_SLOT_COUNT];

    public void render(Minecraft mc, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Entity cameraEntity = mc.getCameraEntity();

        if (!(cameraEntity instanceof Player player)) {
            return;
        }

        int selected = player.getInventory().selected;
        int startY = (screenHeight - HOTBAR_HEIGHT) / 2 + 36;
        int baseBlockX = blockX(screenWidth, false);

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
        List<ItemStack> items = player.getInventory().items;

        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            int blockX = slotRenderX[i];
            int blockY = startY + i * SLOT_STRIDE;
            StaticComponents.renderItemSlot(guiGraphics, mc, blockX + ITEM_OFFSET, blockY + ITEM_OFFSET, partialTick, player, items.get(i), i + 1);
        }
    }

    private static int blockX(int screenWidth, boolean mainHand) {
        return screenWidth - SCREEN_MARGIN - SLOT_SIZE - (mainHand ? MAIN_HAND_PROTRUSION : 0);
    }

}
