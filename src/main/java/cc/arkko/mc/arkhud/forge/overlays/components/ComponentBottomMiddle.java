package cc.arkko.mc.arkhud.forge.overlays.components;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.configs.ConfigManager;
import cc.arkko.mc.arkhud.forge.utils.SealedValue;
import cc.arkko.mc.arkhud.forge.utils.U;
import com.alrex.parcool.common.capability.IStamina;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.ForgeMod;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.MAX_MANA;

public class ComponentBottomMiddle {
    private static final int BAR_WIDTH = 128;
    private static final int BAR_HEIGHT = 4;
    private static final int BAR_MARGIN = 1;
    private static final int SCREEN_BOTTOM_MARGIN = 16;
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int RED_OUTLINE_COLOR = 0xffff5555;
    // 与原版手持物品名提示（Gui#toolHighlightTimer）一致：1 tick = 50ms，最后 10 tick 淡出
    private static final long MS_PER_TICK = 50;
    private static final long FADE_MS = 10 * MS_PER_TICK;

    // 数值满后的隐藏截止时间（毫秒），等价于原版 Gui#toolHighlightTimer 的倒计时
    private final SealedValue<Long> fullHideDeadlineAirBar = U.sealValue(0L);
    private final SealedValue<Long> fullHideDeadlineStaminaBar = U.sealValue(0L);
    private final SealedValue<Long> fullHideDeadlineManaBar = U.sealValue(0L);

    public void render(Minecraft mc, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        LocalPlayer ply = mc.player;

        if (ply == null) {
            return;
        }

        int slot = 0;

        int air = ply.getAirSupply();
        int maxAir = ply.getMaxAirSupply();

        if (air < 0) {
            air = 0;
        }

        renderBar(
            mc, guiGraphics, screenWidth, screenHeight, 0xff20b2fb, slot++,
            air, maxAir, (double) air / maxAir < .3f, fullHideDeadlineAirBar
        );

        if (ModForgeArkHud.DEP_INSTALLED_PARCOOL) {
            IStamina stamina = IStamina.get(ply);

            if (stamina == null) {
                return;
            }

            renderBar(
                mc, guiGraphics, screenWidth, screenHeight, 0xfffefe00, slot++,
                stamina.get(), stamina.getActualMaxStamina(), stamina.isExhausted(), fullHideDeadlineStaminaBar
            );
        }

        if (ModForgeArkHud.DEP_INSTALLED_ISS) {
            int mana = ClientMagicData.getPlayerMana();
            int maxMana = (int)ply.getAttributeValue(MAX_MANA.get());

            renderBar(
                mc, guiGraphics, screenWidth, screenHeight, 0xff54fcfc, slot++,
                mana, maxMana, (double)mana / maxMana < .3f, fullHideDeadlineManaBar
            );
        }
    }

    public void renderBar(
        Minecraft mc, GuiGraphics guiGraphics, int screenWidth, int screenHeight,
        int color, int slot, double value, double maxValue, boolean redOutlined,
        SealedValue<Long> fullHideDeadline
    ) {
        // 数值满后的隐藏计时与渐变，与原版手持物品名提示（Gui#toolHighlightTimer / renderSelectedItemName）完全一致
        boolean full = maxValue > 0 && value >= maxValue;
        long now = System.currentTimeMillis();
        int l;

        if (!full) {
            // 数值未满：跳过隐藏计时，直接以不透明状态渲染（等价于切换手持物品时重置 toolHighlightTimer）
            fullHideDeadline.set(0L);
            l = 255;
        } else {
            if (fullHideDeadline.get() <= 0) {
                // 进入满值，用与原版相同的公式初始化隐藏计时：(int)(40 * notificationDisplayTime) tick
                long hideMs = (long)(40.0D * mc.options.notificationDisplayTime().get()) * MS_PER_TICK;
                fullHideDeadline.set(now + hideMs);
            }

            long remaining = fullHideDeadline.get() - now;
            if (remaining <= 0) {
                return;
            }

            // 与原版相同的渐变：alpha = toolHighlightTimer * 256 / 10，超过 255 截断
            // 满值计时前半段保持不透明，最后 10 tick（500ms）线性淡出
            l = (int)((float)remaining * 256.0F / FADE_MS);
            if (l > 255) {
                l = 255;
            }
        }

        if (l <= 0) {
            return;
        }

        int innerWidth = BAR_WIDTH - BAR_MARGIN * 2;
        int innerHeight = BAR_HEIGHT - BAR_MARGIN * 2;
        int barX = (screenWidth - BAR_WIDTH) / 2;
        int barY = screenHeight - SCREEN_BOTTOM_MARGIN - BAR_HEIGHT - slot * (BAR_HEIGHT + 2);

        int fillWidth = maxValue > 0 ? (int)((long)innerWidth * value / maxValue) : 0;
        fillWidth = Math.max(0, Math.min(innerWidth, fillWidth));

        // 前景色应用与原版物品名相同的 alpha 渐变
        int barColor = (color & 0x00ffffff) | (l << 24);

        // 数值（前景）
        guiGraphics.fill(barX + BAR_MARGIN, barY + BAR_MARGIN, barX + BAR_MARGIN + fillWidth, barY + BAR_MARGIN + innerHeight, barColor);
        // 背景框（压在数值条之后）
        guiGraphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, -1, BACKGROUND_COLOR);

        // redOutlined 时在黑色背景外圈加 1px 间距的红色线框（同样应用 alpha 渐变）
        if (redOutlined) {
            guiGraphics.renderOutline(barX - 1, barY - 1, BAR_WIDTH + 2, BAR_HEIGHT + 2, (RED_OUTLINE_COLOR & 0x00ffffff) | (l << 24));
        }
    }
}
