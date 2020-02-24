package net.silentchaos512.loot.config;

import net.minecraftforge.fml.loading.FMLPaths;
import net.silentchaos512.utils.config.BooleanValue;
import net.silentchaos512.utils.config.ConfigSpecWrapper;

public class Config {
    private static final ConfigSpecWrapper WRAPPER = ConfigSpecWrapper.create(
            FMLPaths.CONFIGDIR.get().resolve("treasurebags-common.toml"));

    public static final General GENERAL = new General(WRAPPER);

    public static class General {

        public BooleanValue alwaysSpawnItems;


        General(ConfigSpecWrapper wrapper) {
            alwaysSpawnItems = wrapper
                    .builder("item.treasurebag.alwaysSpawnItems")
                    .comment("If true, treasure bags will always spawn items on top of the player.",
                            "Otherwise this only happens if the player's inventory is full",
                            "Useful for bag-type items; Dank Storage, SGems Gem Bag, backpacks")
                    .define(false);
        }

    }

    private Config() {
    }

    public static void init() {
        WRAPPER.validate();
        WRAPPER.validate();
    }
}
