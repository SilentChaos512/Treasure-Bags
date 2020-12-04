package net.silentchaos512.treasurebags.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Rarity;
import net.silentchaos512.treasurebags.lib.Const;
import net.silentchaos512.treasurebags.lib.EntityGroup;
import net.silentchaos512.utils.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BagTypesProvider implements IDataProvider {
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

        ret.add(new BagTypeBuilder(Const.Bags.SPAWN, Rarity.COMMON, Const.LootTables.BAGS_SPAWN)
                .noMobDrops()
                .colors(Color.LIMEGREEN, Color.SKYBLUE, Color.GOLD)
        );

        ret.add(new BagTypeBuilder(Const.Bags.DUNGEON, Rarity.RARE, Const.LootTables.BAGS_DUNGEON)
                .dropsFromAllMobs()
                .colors(Color.DARKOLIVEGREEN, Color.LIGHTGRAY, Color.GRAY)
        );

        ret.add(new BagTypeBuilder(Const.Bags.ENDER, Rarity.EPIC, Const.LootTables.BAGS_ENDER)
                .dropsFromAllMobs()
                .colors(Color.BLACK, Color.PURPLE, Color.DARKORCHID)
        );

        ret.add(new BagTypeBuilder(Const.Bags.FOOD, Rarity.COMMON, Const.LootTables.BAGS_FOOD)
                .dropsFromAllMobs()
                .colors(Color.WHITE, Color.RED, Color.PERU)
        );

        ret.add(new BagTypeBuilder(Const.Bags.INGOTS, Rarity.UNCOMMON, Const.LootTables.BAGS_INGOTS)
                .dropsFrom(EntityGroup.BOSS, EntityGroup.HOSTILE)
                .colors(Color.ANTIQUEWHITE.getColor(), Color.OLIVE.getColor(), 0x404040)
        );

        ret.add(new BagTypeBuilder(Const.Bags.LITERACY, Rarity.COMMON, Const.LootTables.BAGS_LITERACY)
                .dropsFromAllMobs()
                .colors(Color.PERU, Color.MINTCREAM, Color.MEDIUMTURQUOISE)
        );

        ret.add(new BagTypeBuilder(Const.Bags.NATURE, Rarity.COMMON, Const.LootTables.BAGS_NATURE)
                .dropsFromAllMobs()
                .colors(Color.FORESTGREEN, Color.SIENNA, Color.OLIVE)
        );

        ret.add(new BagTypeBuilder(Const.Bags.PLAYER, Rarity.EPIC, Const.LootTables.BAGS_PLAYER)
                .dropsFrom(EntityGroup.PLAYER)
                .colors(Color.FUCHSIA, Color.CORNFLOWERBLUE, Color.GOLD)
        );

        ret.add(new BagTypeBuilder(Const.Bags.STICKS_AND_STONES, Rarity.COMMON, Const.LootTables.BAGS_STICKS_AND_STONES)
                .dropsFromAllMobs()
                .colors(Color.SLATEGRAY, Color.CHOCOLATE, Color.LIGHTGRAY)
        );

        return ret;
    }

    @Override
    public void act(DirectoryCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        for (BagTypeBuilder builder : getBagTypes()) {
            try {
                String jsonStr = GSON.toJson(builder.serialize());
                String hashStr = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("data/%s/treasurebags_types/%s.json", builder.bagTypeId.getNamespace(), builder.bagTypeId.getPath()));
                if (!Objects.equals(cache.getPreviousHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.recordHash(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save treasure bag types {}", outputFolder, ex);
            }
        }
    }
}
