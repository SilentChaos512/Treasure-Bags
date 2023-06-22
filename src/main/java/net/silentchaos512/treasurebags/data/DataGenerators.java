package net.silentchaos512.treasurebags.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.loot.BagDropLootModifier;

public final class DataGenerators {
    private DataGenerators() {}

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(true, new BagTypesProvider(gen));
        gen.addProvider(true, new ModLootTables(gen));

        gen.addProvider(true, new GlobalLootModifierProvider(event.getGenerator().getPackOutput(), TreasureBags.MOD_ID) {
            @Override
            protected void start() {
                add("bag_drops", new BagDropLootModifier(new LootItemCondition[0]));
            }
        });
    }
}
