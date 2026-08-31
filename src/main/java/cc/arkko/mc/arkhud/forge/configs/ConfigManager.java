package cc.arkko.mc.arkhud.forge.configs;

import cc.arkko.mc.arkhud.forge.ModForgeArkHud;
import net.minecraft.client.Minecraft;
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
