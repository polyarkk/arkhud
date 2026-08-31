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
