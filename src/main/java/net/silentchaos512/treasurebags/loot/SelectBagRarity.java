package net.silentchaos512.treasurebags.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.lib.BagType;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;
import net.silentchaos512.treasurebags.setup.TbLoot;

import java.util.List;
import java.util.Locale;

public class SelectBagRarity extends LootItemConditionalFunction {
    private final Rarity rarity;

    public SelectBagRarity(Rarity rarity, LootItemCondition[] conditions) {
        super(conditions);
        this.rarity = rarity;
    }

    public static Builder builder(Rarity rarity, LootItemCondition... conditions) {
        return new Builder() {
            @Override
            protected Builder getThis() {
                return this;
            }

            @Override
            public LootItemFunction build() {
                return new SelectBagRarity(rarity, conditions);
            }
        };
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) return ItemStack.EMPTY;

        List<IBagType> list = BagTypeManager.getValues().stream()
                .filter(type -> type.canDropFromMob(entity) && type.getRarity() == this.rarity)
                .toList();
        if (list.isEmpty()) return ItemStack.EMPTY;

        IBagType bagType = list.get(TreasureBags.RANDOM.nextInt(list.size()));
        return TreasureBagItem.setBagType(stack, bagType.getId());
    }

    @Override
    public LootItemFunctionType getType() {
        return TbLoot.SELECT_BAG_RARITY.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SelectBagRarity> {
        @Override
        public void serialize(JsonObject json, SelectBagRarity function, JsonSerializationContext serializationContext) {
            super.serialize(json, function, serializationContext);
            json.addProperty("rarity", function.rarity.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public SelectBagRarity deserialize(JsonObject json, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            Rarity rarity = BagType.Serializer.deserializeRarity(GsonHelper.getAsString(json, "rarity"));
            return new SelectBagRarity(rarity, conditionsIn);
        }

    }
}
