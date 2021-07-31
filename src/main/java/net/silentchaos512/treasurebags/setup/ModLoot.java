package net.silentchaos512.treasurebags.setup;

import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.core.Registry;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.loot.SelectBagRarity;
import net.silentchaos512.treasurebags.loot.SetBagTypeFunction;

public final class ModLoot {
    public static final LootItemFunctionType SELECT_BAG_RARITY = new LootItemFunctionType(new SelectBagRarity.Serializer());
    public static final LootItemFunctionType SET_BAG_TYPE = new LootItemFunctionType(new SetBagTypeFunction.Serializer());

    private ModLoot() {}

    public static void register() {
        Registry.register(Registry.LOOT_FUNCTION_TYPE, TreasureBags.getId("select_bag_rarity"), SELECT_BAG_RARITY);
        Registry.register(Registry.LOOT_FUNCTION_TYPE, TreasureBags.getId("set_bag_type"), SET_BAG_TYPE);
    }
}
