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

import cc.arkko.mc.arkhud.forge.C;
import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.utils.U;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.client.resource.pojo.display.gun.AmmoCountStyle;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

import java.text.DecimalFormat;

/**
 * hud for holding items & rewritten tacz gun hud
 */
public class ComponentBottomRight {
    public static final TagKey<Item> TAG_FORGE_TOOLS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "tools"));

    private static final ResourceLocation SEMI = new ResourceLocation(GunMod.MOD_ID, "textures/hud/fire_mode_semi.png");
    private static final ResourceLocation AUTO = new ResourceLocation(GunMod.MOD_ID, "textures/hud/fire_mode_auto.png");
    private static final ResourceLocation BURST = new ResourceLocation(GunMod.MOD_ID, "textures/hud/fire_mode_burst.png");
    private static final ResourceLocation BACKPACK = new ResourceLocation(ModForgeArkHud.MOD_ID, "textures/hud/hud_backpack.png");
    private static final ResourceLocation BOW_AND_ARROW = new ResourceLocation(ModForgeArkHud.MOD_ID, "textures/hud/hud_bow_and_arrow.png");
    private static final ResourceLocation WRENCH = new ResourceLocation(ModForgeArkHud.MOD_ID, "textures/hud/hud_wrench.png");
    private static final ResourceLocation LIGHTNING = new ResourceLocation(ModForgeArkHud.MOD_ID, "textures/hud/hud_lightning.png");

    private static final int MAX_AMMO_COUNT = 9999;

    private static final DecimalFormat DECIMAL_FORMAT_00 = new DecimalFormat("00");
    private static final DecimalFormat DECIMAL_FORMAT_000 = new DecimalFormat("000");
    private static final DecimalFormat DECIMAL_FORMAT_000_PERCENT = new DecimalFormat("000%");
    private static final DecimalFormat DECIMAL_FORMAT_0000 = new DecimalFormat("0000");

    private long checkTimestamp = -1L;
    private int cacheAmmoMaxChargedCount = 0;
    private int cacheAmmoInventoryCount = 0;
    private int cacheItemInventoryCount = 0;

    public void render(Minecraft mc, GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        LocalPlayer ply = mc.player;

        if (ply == null) {
            return;
        }

        if (ModForgeArkHud.depTacz && renderTaczGunHud(guiGraphics, mc, ply, screenWidth, screenHeight)) {
            return;
        }

        // 副手物品考虑在 ComponentBottomLeft 中实现
        ItemStack itemStack = ply.getMainHandItem();

        if (ItemStack.EMPTY.equals(itemStack)) {
            return;
        }

        if (itemStack.isStackable() && !(itemStack.getItem() instanceof AirItem)) {
            // 普通物品：数量
            renderStackableItemHud(guiGraphics, mc, ply, screenWidth, screenHeight, itemStack);

            return;
        }

        int marginRight = 0;

        // 理论上工具储电和耐久是二选一的
        // 唯一一次见到既有储电还有耐久的工具只有gtnh里的电动工具了
        if (isCompatibleEnergyToolItem(itemStack)) {
            // 储电工具：闪电icon+电量百分比
            marginRight += renderEnergyToolItemHud(guiGraphics, mc, ply, screenWidth, screenHeight, itemStack, marginRight);
            // } else if (itemStack.is(TAG_FORGE_TOOLS)) {
        } else if (itemStack.isDamageableItem()) {
            // 工具/武器：耐久度（扳手icon+数字）
            marginRight += renderToolItemHud(guiGraphics, mc, ply, screenWidth, screenHeight, itemStack, marginRight);
        }

        // 弓/弩： 耐久度+背包箭数（耐久度左侧附加弓箭icon+数字），只有背包有箭时才显示弓箭数量
        marginRight += renderInvProjectileHud(guiGraphics, mc, ply, screenWidth, screenHeight, itemStack, marginRight);
    }

    private static boolean isCompatibleEnergyToolItem(ItemStack itemStack) {
        return U.isEnergyStorageItem(itemStack) && !U.isPlaceableItem(itemStack);
    }

    /**
     * 渲染 TacZ 武器 HUD
     *
     * @return 未渲染返回 false，否则返回 true
     */
    private boolean renderTaczGunHud(GuiGraphics guiGraphics, Minecraft mc, LocalPlayer ply, int screenWidth, int screenHeight) {
        if (!(ply instanceof IClientPlayerGunOperator)) {
            return false;
        }

        ItemStack itemStack = ply.getMainHandItem();

        if (!(itemStack.getItem() instanceof IGun gun)) {
            return false;
        }

        ResourceLocation gunId = gun.getGunId(itemStack);
        GunData gunData = TimelessAPI.getClientGunIndex(gunId).map(ClientGunIndex::getGunData).orElse(null);
        GunDisplayInstance display = TimelessAPI.getGunDisplay(itemStack).orElse(null);

        if (gunData == null || display == null) {
            return false;
        }

        boolean useInvAmmo = gun.useInventoryAmmo(itemStack);
        boolean useDummyAmmo = gun.useDummyAmmo(itemStack);
        boolean overheatLocked = gun.isOverheatLocked(itemStack);

        int ammoCount;

        if (useInvAmmo) {
            ammoCount = cacheAmmoInventoryCount + (gun.hasBulletInBarrel(itemStack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
        } else {
            ammoCount = gun.getCurrentAmmoCount(itemStack) + (gun.hasBulletInBarrel(itemStack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
        }

        ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);

        int ammoCountColor;

        if (ammoCount < (cacheAmmoMaxChargedCount * 0.25) && ammoCount < 10 || overheatLocked) {
            ammoCountColor = C.RED;
        } else {
            ammoCountColor = useInvAmmo && useDummyAmmo ? C.CYAN : useInvAmmo ? C.YELLOW : C.WHITE;
        }

        int inventoryAmmoCountColor;

        if (!useInvAmmo && useDummyAmmo) {
            inventoryAmmoCountColor = C.CYAN;
        } else {
            inventoryAmmoCountColor = 0xAAAAAA;
        }

        String currentAmmoCountText;

        if (display.getAmmoCountStyle() == AmmoCountStyle.PERCENT) {
            currentAmmoCountText = DECIMAL_FORMAT_000_PERCENT.format((float)ammoCount / (cacheAmmoMaxChargedCount == 0 ? 1f : cacheAmmoMaxChargedCount));
        } else {
            currentAmmoCountText = DECIMAL_FORMAT_000.format(ammoCount);
        }

        String inventoryAmmoCountText = useInvAmmo ? "" : DECIMAL_FORMAT_0000.format(cacheAmmoInventoryCount);

        if ((!useInvAmmo && gunData.getReloadData().isInfinite()) || cacheAmmoInventoryCount == Integer.MAX_VALUE) {
            inventoryAmmoCountText = "∞";
        }

        cacheInventoryAmmoCount(ply, itemStack, gunData, gun, useInvAmmo);

        PoseStack poseStack = guiGraphics.pose();

        Font font = mc.font;

        poseStack.pushPose();
        poseStack.scale(1.5f, 1.5f, 1);
        guiGraphics.drawString(font, currentAmmoCountText, (screenWidth - 62) / 1.5f, (screenHeight - 30) / 1.5f, ammoCountColor, false);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(0.8f, 0.8f, 1);
        guiGraphics.drawString(font, inventoryAmmoCountText, (screenWidth - 60 + mc.font.width(currentAmmoCountText) * 1.5f) / 0.8f, (screenHeight - 30) / 0.8f, inventoryAmmoCountColor, false);
        poseStack.popPose();

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        FireMode fireMode = IGun.getMainHandFireMode(ply);
        ResourceLocation fireModeTexture = switch (fireMode) {
            case AUTO -> AUTO;
            case BURST -> BURST;
            default -> SEMI;
        };
        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.blit(fireModeTexture, (int)(screenWidth - 60.5 + mc.font.width(currentAmmoCountText) * 1.5), screenHeight - 25, 0, 0, 10, 10, 10, 10);

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        return true;
    }

    private void cacheInventoryAmmoCount(LocalPlayer player, ItemStack stack, GunData gunData, IGun gun, boolean useInvAmmo) {
        everyTickDo(() -> {
            // 当前枪械的最大装弹量
            cacheAmmoMaxChargedCount = AttachmentDataUtils.getAmmoCountWithAttachment(stack, gunData);
            // 玩家背包弹药数
            if (IGunOperator.fromLivingEntity(player).needCheckAmmo()) {
                if (gun.useDummyAmmo(stack)) {
                    // 缓存虚拟弹药数
                    cacheAmmoInventoryCount = gun.getDummyAmmoAmount(stack);
                } else {
                    Inventory inventory = player.getInventory();
                    cacheAmmoInventoryCount = 0;
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack inventoryItem = inventory.getItem(i);
                        if (inventoryItem.getItem() instanceof IAmmo ammo && ammo.isAmmoOfGun(stack, inventoryItem)) {
                            cacheAmmoInventoryCount += inventoryItem.getCount();
                        }
                        if (inventoryItem.getItem() instanceof IAmmoBox ammoBox && ammoBox.isAmmoBoxOfGun(stack, inventoryItem)) {
                            if (ammoBox.isAllTypeCreative(inventoryItem) || ammoBox.isCreative(inventoryItem)) {
                                cacheAmmoInventoryCount = Integer.MAX_VALUE;
                                return;
                            }
                            cacheAmmoInventoryCount += ammoBox.getAmmoCount(inventoryItem);
                        }
                    }
                }
            } else {
                cacheAmmoInventoryCount = MAX_AMMO_COUNT;
            }
            if (useInvAmmo) {
                gun.setCurrentAmmoCount(stack, cacheAmmoInventoryCount);
            }
        });
    }

    private void cacheInventoryItemCount(ItemStack item, Inventory inventory, int mainHandSlotIndex) {
        everyTickDo(() -> {
            cacheItemInventoryCount = 0;

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (i == mainHandSlotIndex) {
                    continue;
                }

                ItemStack stuff = inventory.getItem(i);

                if (stuff.getItem().equals(item.getItem())) {
                    cacheItemInventoryCount += stuff.getCount();
                }
            }
        });
    }

    private int renderEnergyToolItemHud(
        GuiGraphics guiGraphics, Minecraft mc, LocalPlayer ply, int screenWidth, int screenHeight,
        ItemStack itemStack,
        int marginRight
    ) {
        IEnergyStorage energyStorage = U.getItemEnergyStorage(itemStack);

        double percentage = 0;

        if (energyStorage.getMaxEnergyStored() != 0) {
            percentage = (double)energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
        }

        String text = DECIMAL_FORMAT_000_PERCENT.format(percentage);

        int energyColor = C.WHITE;

        if (percentage < .15f) {
            energyColor = C.RED;
        } else if (percentage < .3f) {
            energyColor = C.YELLOW;
        }

        return 8 + renderStandardIconTextHud2(guiGraphics, mc, screenWidth, screenHeight, LIGHTNING, text, energyColor, marginRight);
    }

    private void renderStackableItemHud(
        GuiGraphics guiGraphics, Minecraft mc, LocalPlayer ply, int screenWidth, int screenHeight,
        ItemStack itemStack
    ) {
        String text = DECIMAL_FORMAT_00.format(itemStack.getCount());

        renderStandardIconTextHud2(guiGraphics, mc, screenWidth, screenHeight, BACKPACK, text, C.WHITE, 0);
    }

    private int renderToolItemHud(
        GuiGraphics guiGraphics, Minecraft mc, LocalPlayer ply, int screenWidth, int screenHeight,
        ItemStack itemStack, int marginRight
    ) {
        boolean damageable = itemStack.isDamageableItem();
        boolean unbreakable = itemStack.getTag() != null && itemStack.getTag().getBoolean("Unbreakable");

        String text;
        int durabilityColor = C.WHITE;

        if (!damageable || unbreakable) {
            text = "∞";
            durabilityColor = C.CYAN;
        } else {
            int maxDmg = itemStack.getMaxDamage();
            int durability = maxDmg - itemStack.getDamageValue();

            double percentage = 0;

            if (maxDmg != 0) {
                percentage = (double)durability / maxDmg;
            }

            text = DECIMAL_FORMAT_000_PERCENT.format(percentage);

            if (durability < maxDmg * .15f) {
                durabilityColor = C.RED;
            } else if (durability < maxDmg * .3f) {
                durabilityColor = C.YELLOW;
            }
        }

        return 8 + renderStandardIconTextHud2(guiGraphics, mc, screenWidth, screenHeight, WRENCH, text, durabilityColor, marginRight);
    }

    private int renderInvProjectileHud(
        GuiGraphics guiGraphics, Minecraft mc, LocalPlayer ply, int screenWidth, int screenHeight,
        ItemStack itemStack, int marginRight
    ) {
        ItemStack projectileItemStack = ply.getProjectile(itemStack);

        if (projectileItemStack.isEmpty()) {
            return 0;
        }

        Inventory inv = ply.getInventory();

        cacheInventoryItemCount(projectileItemStack, inv, inv.selected);

        int count = cacheItemInventoryCount;

        return 8 + renderStandardIconTextHud2(guiGraphics, mc, screenWidth, screenHeight, BOW_AND_ARROW, DECIMAL_FORMAT_000.format(count), count > 5 ? C.WHITE : C.YELLOW, marginRight);
    }

    private int renderStandardIconTextHud2(
        GuiGraphics guiGraphics, Minecraft mc, int screenWidth, int screenHeight,
        ResourceLocation icon, String text, int color, int marginRight
    ) {
        PoseStack poseStack = guiGraphics.pose();
        Font font = mc.font;

        // 修正后的固定图标X：使得text="0000"渲染布局等价旧方法text="000"
        final int iconX = (int)(screenWidth - 37.0) - marginRight;
        final int iconY = screenHeight - 25;
        final float scaledTextWidth = mc.font.width(text) * 1.5f;

        // 文本右对齐，文本右边缘贴图标左边缘
        float textDrawX = (iconX - scaledTextWidth) / 1.5f;
        float textDrawY = (screenHeight - 30) / 1.5f;

        poseStack.pushPose();
        poseStack.scale(1.5f, 1.5f, 1);
        guiGraphics.drawString(font, text, textDrawX, textDrawY, color, false);
        poseStack.popPose();

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(
            ((color >> 16) & 0xFF) / 255f,
            ((color >> 8) & 0xFF) / 255f,
            (color & 0xFF) / 255f,
            1f
        );
        guiGraphics.blit(icon, iconX, iconY, 0, 0, 10, 10, 10, 10);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        // ==========计算总渲染宽度（屏幕像素）==========
        // 文本在屏幕上的左边界
        float textScreenLeft = textDrawX * 1.5f;
        // 图标右边界
        int iconScreenRight = iconX + 10;
        // 总宽度：图标最右侧 - 文本最左侧
        return (int)Math.ceil(iconScreenRight - textScreenLeft);
    }

    private void everyTickDo(Runnable fn) {
        // 0.05 秒 (1 tick) 检查一次
        if ((System.currentTimeMillis() - checkTimestamp) > 50) {
            checkTimestamp = System.currentTimeMillis();
            fn.run();
        }
    }
}
