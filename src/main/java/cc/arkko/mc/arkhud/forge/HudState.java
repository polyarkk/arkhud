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
