package cc.arkko.mc.arkhud.forge.utils;

import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("unused")
public class U {
    /**
     * 判断一个ItemStack是否能够存储FE
     */
    public static boolean isEnergyStorageItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    /**
     * 获取物品能量存储信息，若无法储存能量则返回null
     */
    public static IEnergyStorage getItemEnergyStorage(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).resolve().orElse(null);
    }

    /**
     * 物品是否属于游戏定义的“可以右键放置”的物品
     * true：方块物品 / 画 / 展示框 / 盔甲架 / 末影水晶
     */
    public static boolean isPlaceableItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    @SafeVarargs
    public static <T> boolean isOneOf(T o, T... stuff) {
        for (T s : stuff) {
            if (o.equals(s)) {
                return true;
            }
        }

        return false;
    }

    public static <T> SealedValue<T> sealValue(T v) {
        return new SealedValue<>(v);
    }
}
