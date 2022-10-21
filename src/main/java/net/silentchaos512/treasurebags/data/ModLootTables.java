package net.silentchaos512.treasurebags.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.GiftLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.silentchaos512.treasurebags.lib.Const;
import net.silentchaos512.treasurebags.lib.StandardEntityGroups;
import net.silentchaos512.treasurebags.loot.SelectBagRarity;
import net.silentchaos512.treasurebags.loot.SetBagTypeFunction;
import net.silentchaos512.treasurebags.setup.TbItems;

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
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(
                Pair.of(Entities::new, LootContextParamSets.ENTITY),
                Pair.of(Gifts::new, LootContextParamSets.GIFT)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        //map.forEach((p_218436_2_, p_218436_3_) -> LootTableManager.validateLootTable(validationtracker, p_218436_2_, p_218436_3_));
    }

    private static LootPoolSingletonContainer.Builder<?> treasureBag(ResourceLocation bagType) {
        return LootItem.lootTableItem(TbItems.TREASURE_BAG)
                .apply(SetBagTypeFunction.builder(bagType));
    }

    private static LootPoolSingletonContainer.Builder<?> bagOfRarity(Rarity rarity) {
        return LootItem.lootTableItem(TbItems.TREASURE_BAG)
                .apply(SelectBagRarity.builder(rarity));
    }

    @Nonnull
    private static SetNameFunction setName(Component text) {
        Constructor<SetNameFunction> constructor = ObfuscationReflectionHelper.findConstructor(SetNameFunction.class, LootItemCondition[].class, Component.class, LootContext.EntityTarget.class);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(new LootItemCondition[0], text, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private static LootItemConditionalFunction.Builder<?> setCount(int count) {
        return SetItemCountFunction.setCount(ConstantValue.exactly(count));
    }

    private static LootItemConditionalFunction.Builder<?> setCount(int min, int max) {
        return SetItemCountFunction.setCount(UniformGenerator.between(min, max));
    }

    @Override
    public String getName() {
        return "Treasure Bags - Loot Tables";
    }

    private static class Gifts extends GiftLoot {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(Const.LootTables.STARTING_INVENTORY, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(treasureBag(Const.Bags.SPAWN))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_SPAWN, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(Items.OAK_LOG)
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(8)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(Items.APPLE)
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(10)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(Items.TORCH)
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(20)))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_DUNGEON, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootTableReference.lootTableReference(BuiltInLootTables.SIMPLE_DUNGEON))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_ENDER, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .setBonusRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(40))
                            .add(LootItem.lootTableItem(Items.ENDER_EYE).setWeight(10))
                            .add(LootItem.lootTableItem(Items.ENDER_CHEST).setWeight(1))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_FOOD, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(2, 3))
                            .add(LootItem.lootTableItem(Items.COOKED_COD)
                                    .setWeight(10)
                                    .apply(setCount(2, 4))
                            )
                            .add(LootItem.lootTableItem(Items.COOKED_SALMON)
                                    .setWeight(10)
                                    .apply(setCount(2, 4))
                            )
                            .add(LootItem.lootTableItem(Items.COOKED_PORKCHOP)
                                    .setWeight(7)
                                    .apply(setCount(1, 3))
                            )
                            .add(LootItem.lootTableItem(Items.COOKED_BEEF)
                                    .setWeight(7)
                                    .apply(setCount(1, 3))
                            )
                            .add(LootItem.lootTableItem(Items.COOKED_CHICKEN)
                                    .setWeight(8)
                                    .apply(setCount(2, 4))
                            )
                            .add(LootItem.lootTableItem(Items.BREAD)
                                    .setWeight(12)
                                    .apply(setCount(2, 6))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_INGOTS, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(1, 2))
                            .setBonusRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                    .setWeight(100)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                            )
                            .add(LootItem.lootTableItem(Items.COPPER_INGOT)
                                    .setWeight(70)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6)))
                            )
                            .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                    .setWeight(40)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                            )
                            .add(LootItem.lootTableItem(Items.NETHERITE_INGOT)
                                    .setWeight(1)
                            )
                            .add(TagEntry.expandTag(Tags.Items.INGOTS)
                                    .setWeight(20)
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_LITERACY, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(1, 2))
                            .add(LootItem.lootTableItem(Items.BOOK)
                                    .setWeight(12)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                            )
                            .add(LootItem.lootTableItem(Items.PAPER)
                                    .setWeight(15)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 10)))
                            )
                            .add(LootItem.lootTableItem(Items.BOOKSHELF)
                                    .setWeight(3)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                            )
                            .add(LootItem.lootTableItem(Items.WRITABLE_BOOK)
                                    .setWeight(1)
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .when(LootItemRandomChanceCondition.randomChance(0.5f))
                            .add(LootItem.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(Component.literal("That Time I Was Slapped With a Salmon, Vol. 4")))
                            )
                            .add(LootItem.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(Component.literal("The Adventures of Tardigrade Man")))
                            )
                            .add(LootItem.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(Component.literal("Reading For Dummies")))
                            )
                            .add(LootItem.lootTableItem(Items.BOOK)
                                    .apply(() -> setName(Component.literal("The Story of Larry the Sheep")))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_NATURE, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.SUGAR_CANE)
                                    .setWeight(10)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                            )
                            .add(LootItem.lootTableItem(Items.GRASS)
                                    .setWeight(12)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 12)))
                            )
                            .add(LootItem.lootTableItem(Items.FERN)
                                    .setWeight(8)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                            )
                            .add(LootItem.lootTableItem(Items.VINE)
                                    .setWeight(7)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                            )
                            .add(LootItem.lootTableItem(Items.LILY_PAD)
                                    .setWeight(6)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                            )
                            .add(LootItem.lootTableItem(Items.CACTUS)
                                    .setWeight(5)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                            )
                            .add(LootItem.lootTableItem(Items.PUMPKIN)
                                    .setWeight(3)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                            )
                            .add(LootItem.lootTableItem(Items.MELON)
                                    .setWeight(3)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(1, 2))
                            .add(LootItem.lootTableItem(Items.CARROT)
                                    .setWeight(5)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                            )
                            .add(LootItem.lootTableItem(Items.POTATO)
                                    .setWeight(5)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                            )
                            .add(LootItem.lootTableItem(Items.COCOA_BEANS)
                                    .setWeight(2)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                            )
                            .add(TagEntry.expandTag(Tags.Items.SEEDS)
                                    .setWeight(10)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                            )
                            .add(TagEntry.expandTag(Tags.Items.CROPS)
                                    .setWeight(5)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(TagEntry.expandTag(ItemTags.SAPLINGS)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(TagEntry.expandTag(ItemTags.FLOWERS)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_PLAYER, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
                    )
            );

            consumer.accept(Const.LootTables.BAGS_STICKS_AND_STONES, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.STICK)
                                    .setWeight(15)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                            )
                            .add(TagEntry.expandTag(Tags.Items.RODS)
                                    .setWeight(1)
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .setBonusRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(Items.COBBLESTONE)
                                    .setWeight(72)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 12)))
                            )
                            .add(LootItem.lootTableItem(Items.STONE)
                                    .setWeight(48)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7)))
                            )
                            .add(LootItem.lootTableItem(Items.ANDESITE)
                                    .setWeight(30)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                            )
                            .add(LootItem.lootTableItem(Items.DIORITE)
                                    .setWeight(30)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                            )
                            .add(LootItem.lootTableItem(Items.GRANITE)
                                    .setWeight(30)
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                            )
                            .add(LootItem.lootTableItem(Items.DIAMOND)
                                    .setWeight(1)
                            )
                            .add(LootItem.lootTableItem(Items.EMERALD)
                                    .setWeight(1)
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.BONE)
                                    .setWeight(10)
                                    .apply(setCount(2, 4))
                            )
                            .add(LootItem.lootTableItem(Items.BONE_MEAL)
                                    .setWeight(4)
                                    .apply(setCount(3, 8))
                            )
                            .add(LootItem.lootTableItem(Items.BONE_BLOCK)
                                    .setWeight(1)
                                    .apply(setCount(1, 2))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_DEFAULT, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.STICK)
                                    .apply(setCount(4))
                            )
                    )
            );

            consumer.accept(Const.LootTables.BAGS_TEST, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                    .apply(setCount(5))
                            )
                            .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                    .apply(setCount(2))
                            )
                            .add(LootItem.lootTableItem(Items.DIAMOND))
                    )
            );
        }
    }

    private static class Entities extends EntityLoot {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(StandardEntityGroups.BOSS.getLootTable(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(2))
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
                            .add(EmptyLootItem.emptyItem()
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
                            .add(EmptyLootItem.emptyItem()
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
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .add(treasureBag(Const.Bags.PLAYER))
                    )
            );
        }
    }
}
