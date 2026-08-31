package cc.arkko.mc.arkhud.forge.overlays.components;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.configs.ConfigManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sihenzhang.crockpot.effect.CrockPotEffect;
import com.sihenzhang.crockpot.effect.CrockPotEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.ForgeMod;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ComponentBottomLeft {
    double displayHp = 20;

    public void render(Minecraft mc, GuiGraphics guiGraphics, int screenHeight) {
        AbstractClientPlayer ply = mc.player;

        if (ply == null) {
            return;
        }

        int x = 16;
        int avatarSize = 8;
        int y = screenHeight - 24 - avatarSize;

        ResourceLocation skin = ply.getSkinTextureLocation();
        boolean drawHat = ply.isModelPartShown(PlayerModelPart.HAT);
        boolean flip = LivingEntityRenderer.isEntityUpsideDown(ply);

        // head
        PlayerFaceRenderer.draw(guiGraphics, skin, x, y, avatarSize, drawHat, flip);

        if (mc.options.keyShift.isDown()) {
            // experience level
            String text = "等级 Lv. " + ply.experienceLevel;

            guiGraphics.drawString(mc.font, text, x + avatarSize + 2, y, -1);
        } else {
            // name
            guiGraphics.drawString(mc.font, getNameForDisplay(ply), x + avatarSize + 2, y, -1);
        }

        // health
        float maxHp = ply.getMaxHealth();
        double hp = ply.getHealth() / (maxHp == 0 ? 20f : maxHp);
        double deltaHp = displayHp - hp;

        displayHp = Mth.lerp(0.25, displayHp, hp);

        int hpCol = 0xffffffff;

        if (hp < .2f) {
            hpCol = 0xffff6a6a;
        } else if (ply.getArmorValue() >= 16) {
            hpCol = 0xffd9d919;
        } else if (ply.getArmorValue() > 0) {
            hpCol = 0xff6a6aff;
        }

        guiGraphics.fill(x, y + 9, x + (int)(72 * hp), y + 13, hpCol);

        if (deltaHp > 0) {
            guiGraphics.fill(x + (int)(72 * hp), y + 9, x + (int)(72 * (hp + deltaHp)), y + 13, 0xffff5555);
        }

        guiGraphics.fill(x, y + 9, x + 72, y + 13, -1, 0x80808080);

        // hunger
        double hunger = ply.getFoodData().getFoodLevel() / 20f;
        boolean hasHungerEffect = ply.hasEffect(MobEffects.HUNGER);
        int hungerColor = 0xffffffff;

        if (hasHungerEffect) {
            hungerColor = 0xff56ad56;
        } else if (ModForgeArkHud.DEP_INSTALLED_CROCK_POT && ply.hasEffect(CrockPotEffects.GNAWS_GIFT.get())) {
            hungerColor = 0xff6aa6ff;
        }

        guiGraphics.fill(x, y + 14, x + (int)(72 * hunger), y + 15, hungerColor);
        guiGraphics.fill(x, y + 14, x + 72, y + 15, -1, 0x80808080);

        if (ConfigManager.potionEffects) {
            // potion effects
            Collection<MobEffectInstance> effects = ply.getActiveEffects();

            if (!effects.isEmpty()) {
                RenderSystem.enableBlend();
                MobEffectTextureManager mobEffectTextures = mc.getMobEffectTextures();
                List<MobEffectInstance> sortedEffects = effects.stream().sorted(Comparator.reverseOrder()).toList();
                // 图标位于饥饿条（上方 y+14..y+15 的长方形）下方 1px，左侧与 x 对齐，大小 4px，图标间距 2px
                int iconX = x;
                int iconY = y + 16;
                final int iconSize = 8;
                final int iconGap = 2;

                for (MobEffectInstance instance : sortedEffects) {
                    if (!instance.showIcon()) {
                        continue;
                    }

                    // 结束闪烁逻辑直接参考原版 Gui#renderEffects：仅非环境效果在剩余 200 tick 内按余弦衰减闪烁
                    float alpha = 1.0F;

                    if (!instance.isAmbient() && instance.endsWithin(200)) {
                        int duration = instance.getDuration();
                        int l = 10 - duration / 20;
                        alpha = Mth.clamp((float)duration / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                            + Mth.cos((float)duration * (float)Math.PI / 5.0F) * Mth.clamp((float)l / 10.0F * 0.25F, 0.0F, 0.25F);
                    }

                    TextureAtlasSprite sprite = mobEffectTextures.get(instance.getEffect());
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
                    guiGraphics.blit(iconX, iconY, 0, iconSize, iconSize, sprite);
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                    iconX += iconSize + iconGap;
                }
                RenderSystem.disableBlend();
            }
        }
    }

    public Component getNameForDisplay(AbstractClientPlayer ply) {
        return PlayerTeam.formatNameForTeam(ply.getTeam(), Component.literal(ply.getGameProfile().getName()));
    }
}
