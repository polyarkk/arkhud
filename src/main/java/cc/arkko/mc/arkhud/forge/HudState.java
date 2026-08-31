package cc.arkko.mc.arkhud.forge;

public enum HudState {
    ALL_VISIBLE {
        @Override
        public HudState next() {
            return NO_HUD;
        }
    },

    NO_HUD {
        @Override
        public HudState next() {
            return ALL_HIDDEN;
        }
    },

    ALL_HIDDEN {
        @Override
        public HudState next() {
            return ALL_VISIBLE;
        }
    }

    ;

    public abstract HudState next();
}
