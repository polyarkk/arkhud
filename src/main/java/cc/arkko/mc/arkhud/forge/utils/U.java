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

package cc.arkko.mc.arkhud.forge.utils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
