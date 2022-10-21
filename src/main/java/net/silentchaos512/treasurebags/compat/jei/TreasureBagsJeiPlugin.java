package net.silentchaos512.treasurebags.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.setup.TbItems;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;

@JeiPlugin
public class TreasureBagsJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = TreasureBags.getId("plugin/main");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(TbItems.TREASURE_BAG.get(), (stack, ctx) -> {
            IBagType bagType = BagTypeManager.typeFromBag(stack);
            return bagType != null ? bagType.getId().toString() : "null";
        });
    }
}
