package net.silentchaos512.treasurebags.setup;

import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.registry.Registry;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.loot.SelectBagRarity;
import net.silentchaos512.treasurebags.loot.SetBagTypeFunction;

public final class ModLoot {
    public static final LootFunctionType SELECT_BAG_RARITY = new LootFunctionType(new SelectBagRarity.Serializer());
    public static final LootFunctionType SET_BAG_TYPE = new LootFunctionType(new SetBagTypeFunction.Serializer());

    private ModLoot() {}

    public static void register() {
        Registry.register(Registry.LOOT_FUNCTION_TYPE, TreasureBags.getId("select_bag_rarity"), SELECT_BAG_RARITY);
        Registry.register(Registry.LOOT_FUNCTION_TYPE, TreasureBags.getId("set_bag_type"), SET_BAG_TYPE);
    }
}
