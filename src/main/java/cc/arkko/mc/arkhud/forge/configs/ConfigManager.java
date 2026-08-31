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

package cc.arkko.mc.arkhud.forge.configs;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ModForgeArkHud.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DISABLED = CLIENT_BUILDER.comment("whether to use default hud instead of ArkHud")
        .define("disabled", false);

    private static final ForgeConfigSpec.BooleanValue HIDE_ARMOR = CLIENT_BUILDER.comment("whether to hide player's armors")
        .define("hideArmor", false);

    private static final ForgeConfigSpec.BooleanValue POTION_EFFECTS = CLIENT_BUILDER.comment("whether to show potion effects")
        .define("potionEffects", true);

    static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static boolean disabled;
    public static boolean hideArmor;
    public static boolean potionEffects;

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_SPEC);
    }

    public static void setDisabled(boolean disabled) {
        DISABLED.set(disabled);
        DISABLED.save();
        ConfigManager.disabled = disabled;
    }

    public static void setHideArmor(boolean hideArmor) {
        HIDE_ARMOR.set(hideArmor);
        HIDE_ARMOR.save();
        ConfigManager.hideArmor = hideArmor;
    }

    public static void setPotionEffects(boolean potionEffects) {
        POTION_EFFECTS.set(potionEffects);
        POTION_EFFECTS.save();
        ConfigManager.potionEffects = potionEffects;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        disabled = DISABLED.get();
        hideArmor = HIDE_ARMOR.get();
        potionEffects = POTION_EFFECTS.get();
    }
}
