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

package cc.arkko.mc.arkhud.forge.compacts;

import org.anti_ad.mc.ipn.api.access.IPN;

import java.util.List;

public class IpnLockedHotbarSlots {
    private static int mask = 0;

    public static void onTick() {
        @SuppressWarnings("unchecked")
        List<Integer> lockedSlots = (List<Integer>)IPN.getInstance().getLockedSlots();

        int newMask = 0;

        for (Integer s : lockedSlots) {
            if (s != null && s >= 0 && s < 9) {
                newMask |= 1 << s;
            }
        }

        mask = newMask;
    }

    public static boolean isSlotLocked(int i) {
        return ((mask >> i) & 1) != 0;
    }
}
