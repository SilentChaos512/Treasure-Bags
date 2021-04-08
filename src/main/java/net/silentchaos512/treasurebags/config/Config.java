package net.silentchaos512.treasurebags.config;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.lib.IBagType;

import java.util.List;

@Mod.EventBusSubscriber(modid = TreasureBags.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    public static final class Common {
        static final ForgeConfigSpec spec;

        public static final ForgeConfigSpec.BooleanValue alwaysSpawnItems;
        public static final ForgeConfigSpec.ConfigValue<List<? extends String>> disabledBagGroups;

        static {
            ForgeConfigSpec.Builder builder =new ForgeConfigSpec.Builder();

            {
                builder.push("item");

                {
                    builder.push("treasurebag");

                    alwaysSpawnItems = builder
                            .comment("If true, treasure bags will always spawn items on top of the player.",
                                    "Otherwise this only happens if the player's inventory is full",
                                    "Useful for bag-type items; Dank Storage, SGems Gem Bag, backpacks")
                            .define("alwaysSpawnItems", false);

                    disabledBagGroups = builder
                            .comment("Disables all treasure bags in these groups. The built-in bags are in the \"default\" and \"example\" groups")
                            .define("disabledGroups", ImmutableList.of("example", "another_example"), Config::isStringList);

                    builder.pop();
                }

                builder.pop();
            }

            spec = builder.build();
        }

        private Common() {}
    }

    private static boolean isStringList(Object obj) {
        if (obj instanceof List) {
            for (Object entry : (List) obj) {
                if (!(entry instanceof String)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Config() {}

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Common.spec);
    }

    public static void sync() {
    }

    @SubscribeEvent
    public static void sync(ModConfig.Loading event) {
        sync();
    }

    @SubscribeEvent
    public static void sync(ModConfig.Reloading event) {
        sync();
    }
}
