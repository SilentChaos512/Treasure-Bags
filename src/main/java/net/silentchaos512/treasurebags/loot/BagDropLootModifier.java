package net.silentchaos512.treasurebags.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.setup.EntityGroups;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BagDropLootModifier extends LootModifier {
    public static final Supplier<Codec<BagDropLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst).apply(inst, BagDropLootModifier::new)
            )
    );

    public BagDropLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // FIXME: Remove and use this method instead of EventHandler when Forge fixes their stuff...
        if (true) {
            return generatedLoot;
        }

        TreasureBags.LOGGER.debug(context.getQueriedLootTableId() + ": " + context.getParamOrNull(LootContextParams.THIS_ENTITY));

        Entity thisEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (thisEntity == null) {
            return generatedLoot;
        }

        ObjectArrayList<ItemStack> ret = new ObjectArrayList<>(generatedLoot);

        EntityGroups.forEachGroup(thisEntity, (group, entity) -> {
            LootTable lootTable = context.getLevel().getServer().getLootData().getLootTable(group.getLootTable());
            lootTable.getRandomItems(context, ret::add);
        });

        return ret;
    }
}
