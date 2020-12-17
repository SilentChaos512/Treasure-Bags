package net.silentchaos512.treasurebags.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.lib.BagType;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;
import net.silentchaos512.treasurebags.setup.ModLoot;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SelectBagRarity extends LootFunction {
    private final Rarity rarity;

    public SelectBagRarity(Rarity rarity, ILootCondition[] conditions) {
        super(conditions);
        this.rarity = rarity;
    }

    public static IBuilder builder(Rarity rarity, ILootCondition... conditions) {
        return () -> new SelectBagRarity(rarity, conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        Entity entity = context.get(LootParameters.THIS_ENTITY);
        if (entity == null) return ItemStack.EMPTY;

        List<IBagType> list = BagTypeManager.getValues().stream()
                .filter(type -> type.canDropFromMob(entity) && type.getRarity() == this.rarity)
                .collect(Collectors.toList());
        if (list.isEmpty()) return ItemStack.EMPTY;

        IBagType bagType = list.get(TreasureBags.RANDOM.nextInt(list.size()));
        return TreasureBagItem.setBagType(stack, bagType.getId());
    }

    @Override
    public LootFunctionType getFunctionType() {
        return ModLoot.SELECT_BAG_RARITY;
    }

    public static class Serializer extends LootFunction.Serializer<SelectBagRarity> {
        @Override
        public void serialize(JsonObject json, SelectBagRarity function, JsonSerializationContext serializationContext) {
            super.serialize(json, function, serializationContext);
            json.addProperty("rarity", function.rarity.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public SelectBagRarity deserialize(JsonObject json, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            Rarity rarity = BagType.Serializer.deserializeRarity(JSONUtils.getString(json, "rarity"));
            return new SelectBagRarity(rarity, conditionsIn);
        }

    }
}
