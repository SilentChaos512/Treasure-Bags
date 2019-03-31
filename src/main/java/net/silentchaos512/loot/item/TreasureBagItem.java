package net.silentchaos512.loot.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.lib.item.ItemLootContainer;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TreasureBagItem extends ItemLootContainer {
//    private static final String NBT_BAG_COLOR = "BagColor";
//    private static final String NBT_BAG_OVERLAY_COLOR = "BagOverlayColor";
//    private static final String NBT_BAG_STRING_COLOR = "BagStringColor";
    private static final String NBT_BAG_TYPE = "BagType";
//    private static final String NBT_CUSTOM_NAME = "CustomName";

    public TreasureBagItem() {
        super(new ResourceLocation(TreasureBags.MOD_ID, "default_bag"), true, new Properties().group(ItemGroup.MISC));
    }

    public ItemStack stackOfType(IBagType type) {
        return stackOfType(type, 1);
    }

    public ItemStack stackOfType(IBagType type, int count) {
        ItemStack result = new ItemStack(this, count);
        return setBagProperties(result, type);
    }

    @Nullable
    public static IBagType getBagType(ItemStack stack) {
        String typeStr = getData(stack).getString(NBT_BAG_TYPE);
        return BagTypeManager.getValue(new ResourceLocation(typeStr));
    }

    /**
     * Set the BagType tag, nothing else. This is needed for loading from loot tables, because bag
     * types may not exist yet.
     *
     * @param stack The bag
     * @param bagTypeId The bag type ID
     * @return The bag (modified original, not a copy)
     */
    public static ItemStack setBagType(ItemStack stack, ResourceLocation bagTypeId) {
        if (!(stack.getItem() instanceof TreasureBagItem)) return stack;
        NBTTagCompound tag = getData(stack);
        tag.putString(NBT_BAG_TYPE, bagTypeId.toString());
        return stack;
    }

    public static ItemStack setBagProperties(ItemStack stack, IBagType type) {
        if (!(stack.getItem() instanceof TreasureBagItem)) return stack;
        NBTTagCompound tag = getData(stack);
        tag.putString(NBT_BAG_TYPE, type.getId().toString());
        setLootTable(stack, type.getLootTable());
//        tag.setInt(NBT_BAG_COLOR, type.getBagColor());
//        tag.setInt(NBT_BAG_OVERLAY_COLOR, type.getBagOverlayColor());
//        tag.setString(NBT_CUSTOM_NAME, type.getCustomName());
        return stack;
    }

    @Nonnull
    @Override
    protected ResourceLocation getLootTable(ItemStack stack) {
        IBagType type = getBagType(stack);
        if (type == null) {
            return super.getLootTable(stack);
        }
        return type.getLootTable();
    }

    public static int getBagColor(ItemStack stack) {
        IBagType type = getBagType(stack);
        if (type != null) {
            return type.getBagColor();
        }
        return 0xFFFFFF;
    }

    public static int getBagOverlayColor(ItemStack stack) {
        IBagType type = getBagType(stack);
        if (type != null) {
            return type.getBagOverlayColor();
        }
        return 0xFFFFFF;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        IBagType type = getBagType(stack);
        if (type != null) {
            return new TextComponentString(type.getCustomName());
        }
        return super.getDisplayName(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) return getBagColor(stack);
        if (tintIndex == 1) return getBagOverlayColor(stack);
        return 0xFFFFFF;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (flagIn.isAdvanced()) {
            IBagType type = getBagType(stack);
            if (type != null) {
                tooltip.add(new TextComponentString("Type: " + type.getId()));
            }
        }
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (!isInGroup(group)) return;
        items.add(new ItemStack(this));

        // Add for each type (sorted by ID)
        List<IBagType> list = new ArrayList<>(BagTypeManager.getValues());
        list.sort(Comparator.comparing(o -> o.getId().toString()));
        for (IBagType type : list) {
            if (type.isVisible()) {
                items.add(stackOfType(type));
            }
        }
    }
}
