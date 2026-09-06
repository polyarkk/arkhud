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

package cc.arkko.mc.arkhud.forge;

import cc.arkko.mc.arkhud.forge.compacts.IpnLockedHotbarSlots;
import cc.arkko.mc.arkhud.forge.overlays.OverlayHudMain;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ModForgeArkHud.MOD_ID)
public class ModForgeArkHud {
    public static final String MOD_ID = "arkhud";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean depTacz = false;
    public static boolean depParcool = false;
    public static boolean depIpn = false;
    public static boolean depIss = false;
    public static boolean depPasterDream = false;

    public static HudState hudState = HudState.ALL_VISIBLE;

    public static long glInvalidOperation1282s = 0;

    public ModForgeArkHud() {
        depTacz = ModList.get().isLoaded("tacz");
        depParcool = ModList.get().isLoaded("parcool");
        depIpn = ModList.get().isLoaded("inventoryprofilesnext");
        depIss = ModList.get().isLoaded("irons_spellbooks");
        depPasterDream = ModList.get().isLoaded("pasterdream");
        MinecraftForge.EVENT_BUS.register(this);
        ConfigManager.init();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        if (depIpn) {
            IpnLockedHotbarSlots.onTick();
        }
    }

    @SubscribeEvent
    public void onRegisterCommand(RegisterClientCommandsEvent ev) {
        LOGGER.info("registering commands");
        CommandManager.init(ev.getDispatcher());
    }

    public static boolean shouldRenderCustomHud() {
        if (ConfigManager.disabled) {
            return false;
        }

        Player ply = Minecraft.getInstance().player;

        if (ply == null || ply.isSpectator()) {
            return false;
        }

        return true;
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent ev) {
            LOGGER.info("registering overlays");
            ev.registerAboveAll("arkhud", new OverlayHudMain());
        }
    }
}
