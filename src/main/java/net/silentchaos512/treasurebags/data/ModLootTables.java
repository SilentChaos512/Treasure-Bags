package net.silentchaos512.treasurebags.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.data.loot.GiftLootTables;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetName;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.silentchaos512.treasurebags.lib.Const;
import net.silentchaos512.treasurebags.lib.EntityGroup;
import net.silentchaos512.treasurebags.loot.SelectBagRarity;
import net.silentchaos512.treasurebags.loot.SetBagTypeFunction;
import net.silentchaos512.treasurebags.setup.ModItems;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTables extends LootTableProvider {
    public ModLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(Entities::new, LootParameterSets.ENTITY),
                Pair.of(Gifts::new, LootParameterSets.GIFT)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        //map.forEach((p_218436_2_, p_218436_3_) -> LootTableManager.validateLootTable(validationtracker, p_218436_2_, p_218436_3_));
    }

    private static StandaloneLootEntry.Builder<?> treasureBag(ResourceLocation bagType) {
        return ItemLootEntry.builder(ModItems.TREASURE_BAG)
                .acceptFunction(SetBagTypeFunction.builder(bagType));
    }

    private static StandaloneLootEntry.Builder<?> bagOfRarity(Rarity rarity) {
        return ItemLootEntry.builder(ModItems.TREASURE_BAG)
                .acceptFunction(SelectBagRarity.builder(rarity));
    }

    @Nonnull
    private static SetName setName(ITextComponent text) {
        Constructor<SetName> constructor = ObfuscationReflectionHelper.findConstructor(SetName.class, ILootCondition[].class, ITextComponent.class, LootContext.EntityTarget.class);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(new ILootCondition[0], text, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private static LootFunction.Builder<?> setCount(int count) {
        return SetCount.builder(ConstantRange.of(count));
    }

    private static LootFunction.Builder<?> setCount(int min, int max) {
        return SetCount.builder(RandomValueRange.of(min, max));
    }

    @Override
    public String getName() {
        return "Treasure Bags - Loot Tables";
    }

    private static class Gifts extends GiftLootTables {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(Const.LootTables.STARTING_INVENTORY, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(treasureBag(Const.Bags.SPAWN))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_SPAWN, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(ItemLootEntry.builder(Items.OAK_LOG)
                                    .acceptFunction(SetCount.builder(ConstantRange.of(8)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .addEntry(ItemLootEntry.builder(Items.APPLE)
                                    .acceptFunction(SetCount.builder(ConstantRange.of(10)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .addEntry(ItemLootEntry.builder(Items.TORCH)
                                    .acceptFunction(SetCount.builder(ConstantRange.of(20)))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_DUNGEON, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(TableLootEntry.builder(LootTables.CHESTS_SIMPLE_DUNGEON))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_ENDER, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .bonusRolls(0, 1)
                            .addEntry(ItemLootEntry.builder(Items.ENDER_PEARL).weight(40))
                            .addEntry(ItemLootEntry.builder(Items.ENDER_EYE).weight(10))
                            .addEntry(ItemLootEntry.builder(Items.ENDER_CHEST).weight(1))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_FOOD, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(2, 3))
                            .addEntry(ItemLootEntry.builder(Items.COOKED_COD)
                                    .weight(10)
                                    .acceptFunction(setCount(2, 4))
                            )
                            .addEntry(ItemLootEntry.builder(Items.COOKED_SALMON)
                                    .weight(10)
                                    .acceptFunction(setCount(2, 4))
                            )
                            .addEntry(ItemLootEntry.builder(Items.COOKED_PORKCHOP)
                                    .weight(7)
                                    .acceptFunction(setCount(1, 3))
                            )
                            .addEntry(ItemLootEntry.builder(Items.COOKED_BEEF)
                                    .weight(7)
                                    .acceptFunction(setCount(1, 3))
                            )
                            .addEntry(ItemLootEntry.builder(Items.COOKED_CHICKEN)
                                    .weight(8)
                                    .acceptFunction(setCount(2, 4))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BREAD)
                                    .weight(12)
                                    .acceptFunction(setCount(2, 6))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_INGOTS, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(1, 2))
                            .bonusRolls(0, 1)
                            .addEntry(ItemLootEntry.builder(Items.IRON_INGOT)
                                    .weight(30)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(2, 4)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.GOLD_INGOT)
                                    .weight(15)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                            )
                            .addEntry(TagLootEntry.getBuilder(Tags.Items.INGOTS)
                                    .weight(1)
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_LITERACY, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(1, 2))
                            .addEntry(ItemLootEntry.builder(Items.BOOK)
                                    .weight(12)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(2, 4)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.PAPER)
                                    .weight(15)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(4, 10)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BOOKSHELF)
                                    .weight(3)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.WRITABLE_BOOK)
                                    .weight(1)
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .acceptCondition(RandomChance.builder(0.5f))
                            .addEntry(ItemLootEntry.builder(Items.BOOK)
                                    .acceptFunction(() -> setName(new StringTextComponent("That Time I Was Slapped With a Salmon, Vol. 4")))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BOOK)
                                    .acceptFunction(() -> setName(new StringTextComponent("The Adventures of Tardigrade Man")))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BOOK)
                                    .acceptFunction(() -> setName(new StringTextComponent("Reading For Dummies")))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BOOK)
                                    .acceptFunction(() -> setName(new StringTextComponent("The Story of Larry the Sheep")))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_NATURE, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(Items.SUGAR_CANE)
                                    .weight(10)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.GRASS)
                                    .weight(12)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(4, 12)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.FERN)
                                    .weight(8)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(2, 5)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.VINE)
                                    .weight(7)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.LILY_PAD)
                                    .weight(6)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 3)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.CACTUS)
                                    .weight(5)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 3)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.PUMPKIN)
                                    .weight(3)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.MELON)
                                    .weight(3)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(1, 2))
                            .addEntry(ItemLootEntry.builder(Items.CARROT)
                                    .weight(5)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.POTATO)
                                    .weight(5)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.COCOA_BEANS)
                                    .weight(2)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                            )
                            .addEntry(TagLootEntry.getBuilder(Tags.Items.SEEDS)
                                    .weight(10)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                            )
                            .addEntry(TagLootEntry.getBuilder(Tags.Items.CROPS)
                                    .weight(5)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(TagLootEntry.getBuilder(ItemTags.SAPLINGS)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 3)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(TagLootEntry.getBuilder(ItemTags.FLOWERS)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(1, 3)))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_PLAYER, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(ItemLootEntry.builder(Items.GOLDEN_APPLE))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_STICKS_AND_STONES, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(Items.STICK)
                                    .weight(15)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(4, 8)))
                            )
                            .addEntry(TagLootEntry.getBuilder(Tags.Items.RODS)
                                    .weight(1)
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .bonusRolls(0, 1)
                            .addEntry(ItemLootEntry.builder(Items.COBBLESTONE)
                                    .weight(72)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(6, 12)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.STONE)
                                    .weight(48)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(3, 7)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.ANDESITE)
                                    .weight(30)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(4, 8)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.DIORITE)
                                    .weight(30)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(4, 8)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.GRANITE)
                                    .weight(30)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(4, 8)))
                            )
                            .addEntry(ItemLootEntry.builder(Items.DIAMOND)
                                    .weight(1)
                            )
                            .addEntry(ItemLootEntry.builder(Items.EMERALD)
                                    .weight(1)
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(Items.BONE)
                                    .weight(10)
                                    .acceptFunction(setCount(2, 4))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BONE_MEAL)
                                    .weight(4)
                                    .acceptFunction(setCount(3, 8))
                            )
                            .addEntry(ItemLootEntry.builder(Items.BONE_BLOCK)
                                    .weight(1)
                                    .acceptFunction(setCount(1, 2))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_DEFAULT, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(Items.STICK)
                                    .acceptFunction(setCount(4))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_TEST, LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(Items.IRON_INGOT)
                                    .acceptFunction(setCount(5))
                            )
                            .addEntry(ItemLootEntry.builder(Items.GOLD_INGOT)
                                    .acceptFunction(setCount(2))
                            )
                            .addEntry(ItemLootEntry.builder(Items.DIAMOND))
                    )
            );
        }
    }

    private static class Entities extends EntityLootTables {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(EntityGroup.BOSS.getLootTable(), LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(2))
                            .addEntry(bagOfRarity(Rarity.COMMON)
                                    .weight(3)
                            )
                            .addEntry(bagOfRarity(Rarity.UNCOMMON)
                                    .weight(6)
                            )
                            .addEntry(bagOfRarity(Rarity.RARE)
                                    .weight(10)
                            )
                            .addEntry(bagOfRarity(Rarity.EPIC)
                                    .weight(7)
                            )
                    )
            );
            consumer.accept(EntityGroup.HOSTILE.getLootTable(), LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(EmptyLootEntry.func_216167_a()
                                    .weight(200)
                            )
                            .addEntry(bagOfRarity(Rarity.COMMON)
                                    .weight(10)
                            )
                            .addEntry(bagOfRarity(Rarity.UNCOMMON)
                                    .weight(5)
                            )
                            .addEntry(bagOfRarity(Rarity.RARE)
                                    .weight(2)
                            )
                            .addEntry(bagOfRarity(Rarity.EPIC)
                                    .weight(1)
                            )
                    )
            );
            consumer.accept(EntityGroup.PEACEFUL.getLootTable(), LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(EmptyLootEntry.func_216167_a()
                                    .weight(500)
                            )
                            .addEntry(bagOfRarity(Rarity.COMMON)
                                    .weight(12)
                            )
                            .addEntry(bagOfRarity(Rarity.UNCOMMON)
                                    .weight(6)
                            )
                            .addEntry(bagOfRarity(Rarity.RARE)
                                    .weight(2)
                            )
                            .addEntry(bagOfRarity(Rarity.EPIC)
                                    .weight(1)
                            )
                    )
            );
            consumer.accept(EntityGroup.PLAYER.getLootTable(), LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .acceptCondition(KilledByPlayer.builder())
                            .addEntry(treasureBag(Const.Bags.PLAYER))
                    )
            );
        }
    }
}
