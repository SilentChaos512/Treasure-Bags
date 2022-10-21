package net.silentchaos512.treasurebags.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.silentchaos512.treasurebags.lib.Const;
import net.silentchaos512.treasurebags.lib.StandardEntityGroups;
import net.silentchaos512.utils.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BagTypesProvider implements DataProvider {
    static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public BagTypesProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String getName() {
        return "Treasure Bags - Bag Types";
    }

    protected List<BagTypeBuilder> getBagTypes() {
        List<BagTypeBuilder> ret = new ArrayList<>();

        ret.add(new BagTypeBuilder(Const.Bags.SPAWN, "default", Rarity.COMMON, Const.LootTables.BAGS_SPAWN)
                .noMobDrops()
                .colors(Color.LIMEGREEN, Color.SKYBLUE, Color.GOLD)
        );

        ret.add(new BagTypeBuilder(Const.Bags.DUNGEON, "default", Rarity.RARE, Const.LootTables.BAGS_DUNGEON)
                .dropsFromAllMobs()
                .colors(Color.DARKOLIVEGREEN, Color.LIGHTGRAY, Color.GRAY)
        );

        ret.add(new BagTypeBuilder(Const.Bags.ENDER, "default", Rarity.EPIC, Const.LootTables.BAGS_ENDER)
                .dropsFromAllMobs()
                .colors(Color.BLACK, Color.PURPLE, Color.DARKORCHID)
        );

        ret.add(new BagTypeBuilder(Const.Bags.FOOD, "default", Rarity.COMMON, Const.LootTables.BAGS_FOOD)
                .dropsFromAllMobs()
                .colors(Color.WHITE, Color.RED, Color.PERU)
        );

        ret.add(new BagTypeBuilder(Const.Bags.INGOTS, "default", Rarity.UNCOMMON, Const.LootTables.BAGS_INGOTS)
                .dropsFrom(StandardEntityGroups.BOSS, StandardEntityGroups.HOSTILE)
                .colors(Color.ANTIQUEWHITE.getColor(), Color.OLIVE.getColor(), 0x404040)
        );

        ret.add(new BagTypeBuilder(Const.Bags.LITERACY, "default", Rarity.COMMON, Const.LootTables.BAGS_LITERACY)
                .dropsFromAllMobs()
                .colors(Color.PERU, Color.MINTCREAM, Color.MEDIUMTURQUOISE)
        );

        ret.add(new BagTypeBuilder(Const.Bags.NATURE, "default", Rarity.COMMON, Const.LootTables.BAGS_NATURE)
                .dropsFromAllMobs()
                .colors(Color.FORESTGREEN, Color.SIENNA, Color.OLIVE)
        );

        ret.add(new BagTypeBuilder(Const.Bags.PLAYER, "default", Rarity.EPIC, Const.LootTables.BAGS_PLAYER)
                .dropsFrom(StandardEntityGroups.PLAYER)
                .colors(Color.FUCHSIA, Color.CORNFLOWERBLUE, Color.GOLD)
        );

        ret.add(new BagTypeBuilder(Const.Bags.STICKS_AND_STONES, "default", Rarity.COMMON, Const.LootTables.BAGS_STICKS_AND_STONES)
                .dropsFromAllMobs()
                .colors(Color.SLATEGRAY, Color.CHOCOLATE, Color.LIGHTGRAY)
        );

        ret.add(new BagTypeBuilder(Const.Bags.DEFAULT, "example", Rarity.COMMON, Const.LootTables.BAGS_DEFAULT)
                .noMobDrops()
                .colors(Color.WHITE, Color.WHITE, Color.WHITE)
                .displayName(Component.literal("Treasure Bag"))
        );

        ret.add(new BagTypeBuilder(Const.Bags.TEST, "example", Rarity.COMMON, Const.LootTables.BAGS_TEST)
                .noMobDrops()
                .colors(0xFF00FF, 0X00FF00, 0XB8860B)
                .displayName(Component.literal("Test Bag"))
        );

        return ret;
    }

    @Override
    public void run(CachedOutput cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Set<ResourceLocation> entries = Sets.newHashSet();

        //noinspection OverlyLongLambda
        getBagTypes().forEach(builder -> {
            if (entries.contains(builder.bagTypeId)) {
                throw new IllegalStateException("Duplicate bag type: " + builder.bagTypeId);
            }

            entries.add(builder.bagTypeId);
            Path path = outputFolder.resolve(String.format("data/%s/treasurebags_types/%s.json", builder.bagTypeId.getNamespace(), builder.bagTypeId.getPath()));
            trySaveStable(cache, builder, path);
        });
    }

    private static void trySaveStable(CachedOutput cache, BagTypeBuilder builder, Path path) {
        try {
            DataProvider.saveStable(cache, builder.serialize(), path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
