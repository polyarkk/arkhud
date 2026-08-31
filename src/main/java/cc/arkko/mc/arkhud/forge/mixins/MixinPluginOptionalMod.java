package cc.arkko.mc.arkhud.forge.mixins;

import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class MixinPluginOptionalMod implements IMixinConfigPlugin {
    private final Map<String, String> modClassNamePrefixes = Map.of(
        "tacz", "com.tacz",
        "parcool", "com.alrex.parcool",
        "inventoryprofilesnext", "org.anti_ad.mc.ipn",
        "crockpot", "com.sihenzhang.crockpot",
        "irons_spellbooks", "io.redspace.ironsspellbooks",
        "pasterdream", "net.pasterdream"
    );

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        for (Map.Entry<String, String> entry : modClassNamePrefixes.entrySet()) {
            if (mixinClassName.startsWith(entry.getValue())) {
                return FMLLoader.getLoadingModList().getModFileById(entry.getKey()) != null;
            }
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
