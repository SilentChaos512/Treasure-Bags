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
import net.silentchaos512.treasurebags.lib.StandardEntityGroups;
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
        return ItemLootEntry.lootTableItem(ModItems.TREASURE_BAG)
                .apply(SetBagTypeFunction.builder(bagType));
    }

    private static StandaloneLootEntry.Builder<?> bagOfRarity(Rarity rarity) {
        return ItemLootEntry.lootTableItem(ModItems.TREASURE_BAG)
                .apply(SelectBagRarity.builder(rarity));
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
        return SetCount.setCount(ConstantRange.exactly(count));
    }

    private static LootFunction.Builder<?> setCount(int min, int max) {
        return SetCount.setCount(RandomValueRange.between(min, max));
    }

    @Override
    public String getName() {
        return "Treasure Bags - Loot Tables";
    }

    private static class Gifts extends GiftLootTables {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(Const.LootTables.STARTING_INVENTORY, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(treasureBag(Const.Bags.SPAWN))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_SPAWN, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(Items.OAK_LOG)
                                    .apply(SetCount.setCount(ConstantRange.exactly(8)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(Items.APPLE)
                                    .apply(SetCount.setCount(ConstantRange.exactly(10)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(Items.TORCH)
                                    .apply(SetCount.setCount(ConstantRange.exactly(20)))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_DUNGEON, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(TableLootEntry.lootTableReference(LootTables.SIMPLE_DUNGEON))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_ENDER, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .bonusRolls(0, 1)
                            .add(ItemLootEntry.lootTableItem(Items.ENDER_PEARL).setWeight(40))
                            .add(ItemLootEntry.lootTableItem(Items.ENDER_EYE).setWeight(10))
                            .add(ItemLootEntry.lootTableItem(Items.ENDER_CHEST).setWeight(1))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_FOOD, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(RandomValueRange.between(2, 3))
                            .add(ItemLootEntry.lootTableItem(Items.COOKED_COD)
                                    .setWeight(10)
                                    .apply(setCount(2, 4))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.COOKED_SALMON)
                                    .setWeight(10)
                                    .apply(setCount(2, 4))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.COOKED_PORKCHOP)
                                    .setWeight(7)
                                    .apply(setCount(1, 3))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.COOKED_BEEF)
                                    .setWeight(7)
                                    .apply(setCount(1, 3))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.COOKED_CHICKEN)
                                    .setWeight(8)
                                    .apply(setCount(2, 4))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BREAD)
                                    .setWeight(12)
                                    .apply(setCount(2, 6))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_INGOTS, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(RandomValueRange.between(1, 2))
                            .bonusRolls(0, 1)
                            .add(ItemLootEntry.lootTableItem(Items.IRON_INGOT)
                                    .setWeight(30)
                                    .apply(SetCount.setCount(RandomValueRange.between(2, 4)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.GOLD_INGOT)
                                    .setWeight(15)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                            )
                            .add(TagLootEntry.expandTag(Tags.Items.INGOTS)
                                    .setWeight(1)
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_LITERACY, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(RandomValueRange.between(1, 2))
                            .add(ItemLootEntry.lootTableItem(Items.BOOK)
                                    .setWeight(12)
                                    .apply(SetCount.setCount(RandomValueRange.between(2, 4)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.PAPER)
                                    .setWeight(15)
                                    .apply(SetCount.setCount(RandomValueRange.between(4, 10)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BOOKSHELF)
                                    .setWeight(3)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.WRITABLE_BOOK)
                                    .setWeight(1)
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .when(RandomChance.randomChance(0.5f))
                            .add(ItemLootEntry.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(new StringTextComponent("That Time I Was Slapped With a Salmon, Vol. 4")))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(new StringTextComponent("The Adventures of Tardigrade Man")))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(new StringTextComponent("Reading For Dummies")))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(new StringTextComponent("The Story of Larry the Sheep")))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_NATURE, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(Items.SUGAR_CANE)
                                    .setWeight(10)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.GRASS)
                                    .setWeight(12)
                                    .apply(SetCount.setCount(RandomValueRange.between(4, 12)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.FERN)
                                    .setWeight(8)
                                    .apply(SetCount.setCount(RandomValueRange.between(2, 5)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.VINE)
                                    .setWeight(7)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.LILY_PAD)
                                    .setWeight(6)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 3)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.CACTUS)
                                    .setWeight(5)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 3)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.PUMPKIN)
                                    .setWeight(3)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.MELON)
                                    .setWeight(3)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(RandomValueRange.between(1, 2))
                            .add(ItemLootEntry.lootTableItem(Items.CARROT)
                                    .setWeight(5)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.POTATO)
                                    .setWeight(5)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.COCOA_BEANS)
                                    .setWeight(2)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                            )
                            .add(TagLootEntry.expandTag(Tags.Items.SEEDS)
                                    .setWeight(10)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                            )
                            .add(TagLootEntry.expandTag(Tags.Items.CROPS)
                                    .setWeight(5)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(TagLootEntry.expandTag(ItemTags.SAPLINGS)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 3)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(TagLootEntry.expandTag(ItemTags.FLOWERS)
                                    .apply(SetCount.setCount(RandomValueRange.between(1, 3)))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_PLAYER, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(Items.GOLDEN_APPLE))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_STICKS_AND_STONES, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(Items.STICK)
                                    .setWeight(15)
                                    .apply(SetCount.setCount(RandomValueRange.between(4, 8)))
                            )
                            .add(TagLootEntry.expandTag(Tags.Items.RODS)
                                    .setWeight(1)
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .bonusRolls(0, 1)
                            .add(ItemLootEntry.lootTableItem(Items.COBBLESTONE)
                                    .setWeight(72)
                                    .apply(SetCount.setCount(RandomValueRange.between(6, 12)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.STONE)
                                    .setWeight(48)
                                    .apply(SetCount.setCount(RandomValueRange.between(3, 7)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.ANDESITE)
                                    .setWeight(30)
                                    .apply(SetCount.setCount(RandomValueRange.between(4, 8)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.DIORITE)
                                    .setWeight(30)
                                    .apply(SetCount.setCount(RandomValueRange.between(4, 8)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.GRANITE)
                                    .setWeight(30)
                                    .apply(SetCount.setCount(RandomValueRange.between(4, 8)))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.DIAMOND)
                                    .setWeight(1)
                            )
                            .add(ItemLootEntry.lootTableItem(Items.EMERALD)
                                    .setWeight(1)
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(Items.BONE)
                                    .setWeight(10)
                                    .apply(setCount(2, 4))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BONE_MEAL)
                                    .setWeight(4)
                                    .apply(setCount(3, 8))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.BONE_BLOCK)
                                    .setWeight(1)
                                    .apply(setCount(1, 2))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_DEFAULT, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(Items.STICK)
                                    .apply(setCount(4))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_TEST, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(1))
                            .add(ItemLootEntry.lootTableItem(Items.IRON_INGOT)
                                    .apply(setCount(5))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.GOLD_INGOT)
                                    .apply(setCount(2))
                            )
                            .add(ItemLootEntry.lootTableItem(Items.DIAMOND))
                    )
            );
        }
    }

    private static class Entities extends EntityLootTables {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(StandardEntityGroups.BOSS.getLootTable(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantRange.exactly(2))
                            .add(bagOfRarity(Rarity.COMMON)
                                    .setWeight(3)
                            )
                            .add(bagOfRarity(Rarity.UNCOMMON)
                                    .setWeight(6)
                            )
                            .add(bagOfRarity(Rarity.RARE)
                                    .setWeight(10)
                            )
                            .add(bagOfRarity(Rarity.EPIC)
                                    .setWeight(7)
                            )
                    )
            );
            consumer.accept(StandardEntityGroups.HOSTILE.getLootTable(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(EmptyLootEntry.emptyItem()
                                    .setWeight(200)
                            )
                            .add(bagOfRarity(Rarity.COMMON)
                                    .setWeight(10)
                            )
                            .add(bagOfRarity(Rarity.UNCOMMON)
                                    .setWeight(5)
                            )
                            .add(bagOfRarity(Rarity.RARE)
                                    .setWeight(2)
                            )
                            .add(bagOfRarity(Rarity.EPIC)
                                    .setWeight(1)
                            )
                    )
            );
            consumer.accept(StandardEntityGroups.PEACEFUL.getLootTable(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(EmptyLootEntry.emptyItem()
                                    .setWeight(500)
                            )
                            .add(bagOfRarity(Rarity.COMMON)
                                    .setWeight(12)
                            )
                            .add(bagOfRarity(Rarity.UNCOMMON)
                                    .setWeight(6)
                            )
                            .add(bagOfRarity(Rarity.RARE)
                                    .setWeight(2)
                            )
                            .add(bagOfRarity(Rarity.EPIC)
                                    .setWeight(1)
                            )
                    )
            );
            consumer.accept(StandardEntityGroups.PLAYER.getLootTable(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .when(KilledByPlayer.killedByPlayer())
                            .add(treasureBag(Const.Bags.PLAYER))
                    )
            );
        }
    }
}
