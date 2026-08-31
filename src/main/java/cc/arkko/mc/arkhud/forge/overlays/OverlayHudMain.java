package cc.arkko.mc.arkhud.forge.overlays;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import cc.arkko.mc.arkhud.forge.configs.ConfigManager;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentBottomLeft;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentBottomRight;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentHotbar;
import cc.arkko.mc.arkhud.forge.overlays.components.ComponentBottomMiddle;
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

        componentBottomLeft.render(mc, guiGraphics, screenHeight);
        componentBottomRight.render(mc, guiGraphics, screenWidth, screenHeight);
        componentHotbar.render(mc, guiGraphics, partialTick, screenWidth, screenHeight);

        if (ModForgeArkHud.DEP_INSTALLED_PARCOOL) {
            componentBottomMiddle.render(mc, guiGraphics, partialTick, screenWidth, screenHeight);
        }

        mc.getProfiler().pop();
    }
}
