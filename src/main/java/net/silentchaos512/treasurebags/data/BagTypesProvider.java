package net.silentchaos512.treasurebags.data;

import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Rarity;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.lib.Const;
import net.silentchaos512.treasurebags.lib.StandardEntityGroups;
import net.silentchaos512.utils.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<?> run(CachedOutput cache) {
        Path outputFolder = this.generator.getPackOutput().getOutputFolder();
        Set<ResourceLocation> entries = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        //noinspection OverlyLongLambda
        getBagTypes().forEach(builder -> {
            if (entries.contains(builder.bagTypeId)) {
                throw new IllegalStateException("Duplicate bag type: " + builder.bagTypeId);
            }

            entries.add(builder.bagTypeId);
            Path path = outputFolder.resolve(String.format("data/%s/treasurebags_types/%s.json", builder.bagTypeId.getNamespace(), builder.bagTypeId.getPath()));
            list.add(saveStable(cache, builder.serialize(), path));
        });

        return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
    }

    public static CompletableFuture<?> saveStable(CachedOutput p_253653_, JsonElement p_254542_, Path p_254467_) {
        // Slightly modified version of DataProvider.saveStable. Only difference is that this one does not sort keys!
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");
                    GsonHelper.writeValue(jsonwriter, p_254542_, null);
                }

                p_253653_.writeIfNeeded(p_254467_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                TreasureBags.LOGGER.error("Failed to save file to {}", p_254467_, ioexception);
            }

        }, Util.backgroundExecutor());
    }
}
