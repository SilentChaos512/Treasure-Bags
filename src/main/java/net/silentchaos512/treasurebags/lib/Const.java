package net.silentchaos512.treasurebags.lib;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.treasurebags.TreasureBags;

public class Const {
    public static final class Bags {
        public static final ResourceLocation DUNGEON = get("dungeon");
        public static final ResourceLocation ENDER = get("ender");
        public static final ResourceLocation FOOD = get("food");
        public static final ResourceLocation INGOTS = get("ingots");
        public static final ResourceLocation LITERACY = get("literacy");
        public static final ResourceLocation NATURE = get("nature");
        public static final ResourceLocation PLAYER = get("player");
        public static final ResourceLocation SPAWN = get("spawn");
        public static final ResourceLocation STICKS_AND_STONES = get("sticks_and_stones");

        private Bags() {}
    }

    public static final class LootTables {
        public static final ResourceLocation STARTING_INVENTORY = get("starting_inventory");

        public static final ResourceLocation BAGS_DUNGEON = get("bags/dungeon");
        public static final ResourceLocation BAGS_ENDER = get("bags/ender");
        public static final ResourceLocation BAGS_FOOD = get("bags/food");
        public static final ResourceLocation BAGS_INGOTS = get("bags/ingots");
        public static final ResourceLocation BAGS_LITERACY = get("bags/literacy");
        public static final ResourceLocation BAGS_NATURE = get("bags/nature");
        public static final ResourceLocation BAGS_PLAYER = get("bags/player");
        public static final ResourceLocation BAGS_SPAWN = get("bags/spawn");
        public static final ResourceLocation BAGS_STICKS_AND_STONES = get("bags/sticks_and_stones");

        private LootTables() {}
    }

    private static ResourceLocation get(String path) {
        return TreasureBags.getId(path);
    }
}
