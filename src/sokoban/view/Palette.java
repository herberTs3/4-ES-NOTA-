package sokoban.view;

import java.awt.Color;

final class Palette {

    static final String FONT_FAMILY = "Segoe UI";

    static final Color BACKGROUND = new Color(0xf4f2ed);
    static final Color SURFACE = new Color(0xfbfaf7);
    static final Color SURFACE_HOVER = new Color(0xeceae3);
    static final Color LINE = new Color(0xdedbd2);
    static final Color INK = new Color(0x2b2d33);
    static final Color MUTED = new Color(0x7c7f87);
    static final Color ERROR = new Color(0xb5432e);

    static final Color FLOOR = new Color(0xfbfaf7);
    static final Color FLOOR_LINE = new Color(0xefece5);
    static final Color ICE = new Color(0xdcedf4);
    static final Color ICE_LINE = new Color(0xc8e1ec);
    static final Color WALL = new Color(0x34373f);
    static final Color WALL_EDGE = new Color(0x4a4e58);
    static final Color GOAL = new Color(0xd9654b);
    static final Color BOX = new Color(0xc9a26b);
    static final Color BOX_LINE = new Color(0xa98350);
    static final Color BOX_IN_PLACE = new Color(0x3e9b7e);
    static final Color BOX_IN_PLACE_LINE = new Color(0x2f7a63);
    static final Color FRAGILE = new Color(0xeadfc8);
    static final Color FRAGILE_LINE = new Color(0xa38d63);
    static final Color PLAYER = new Color(0x3b5ba5);
    static final Color SHADOW = new Color(0, 0, 0, 38);
    static final Color GOAL_HOVER = new Color(0xc2553d);
    static final Color POWER_HAMMER = new Color(0xd9a441);
    static final Color POWER_STRENGTH = new Color(0x8a63b0);
    static final Color MENU_BACKGROUND = new Color(0x1f2a3a);
    static final Color MENU_GRID = new Color(0x2a3749);
    static final Color MENU_TEXT = new Color(0xf4f2ed);
    static final Color MENU_MUTED = new Color(0xa9b4c4);

    private static final Color[] LOCK_COLORS = {
            new Color(0xd9654b), new Color(0x3b7fb8), new Color(0x3e9b7e), new Color(0xb3842c), new Color(0x8a63b0)
    };

    private static final Color[] TITLE_COLORS = {
            new Color(0xd9654b), new Color(0xd9a441), new Color(0x3e9b7e), new Color(0x3b7fb8), new Color(0x8a63b0)
    };

    private Palette() {
    }

    static Color titleColor(int index) {
        return TITLE_COLORS[Math.floorMod(index, TITLE_COLORS.length)];
    }

    static Color lockColor(char letter) {
        return LOCK_COLORS[Math.floorMod(Character.toLowerCase(letter) - 'a', LOCK_COLORS.length)];
    }
}
