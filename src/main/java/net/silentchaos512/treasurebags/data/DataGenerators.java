package net.silentchaos512.treasurebags.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {}

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(true, new BagTypesProvider(gen));
        gen.addProvider(true, new ModLootTables(gen));
    }
}
