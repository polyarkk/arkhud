package cc.arkko.mc.arkhud.forge.compacts;

import org.anti_ad.mc.ipn.api.access.IPN;

import java.util.List;

public class IpnLockedHotbarSlots {
    private static int mask = 0;

    public static void onTick() {
        @SuppressWarnings("unchecked")
        List<Integer> lockedSlots = (List<Integer>)IPN.getInstance().getLockedSlots();

        for (Integer s : lockedSlots) {
            if (s != null && s >= 0 && s < 9) {
                mask |= 1 << s;
            }
        }
    }

    public static boolean isSlotLocked(int i) {
        return ((mask >> i) & 1) != 0;
    }
}
