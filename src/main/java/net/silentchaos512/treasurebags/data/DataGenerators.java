package net.silentchaos512.treasurebags.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {}

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(new BagTypesProvider(gen));
        gen.addProvider(new ModLootTables(gen));
    }
}
