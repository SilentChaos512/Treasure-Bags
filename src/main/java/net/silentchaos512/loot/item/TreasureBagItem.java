package net.silentchaos512.loot.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.silentchaos512.lib.item.ItemLootContainer;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
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

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (!(playerIn instanceof EntityPlayerMP)) {
            return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
        }
        EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;

        // Generate items from loot table, give to player.
        boolean openWholeStack = playerMP.isSneaking();
        Collection<ItemStack> lootDrops = getDropsFromStack(heldItem, playerMP, openWholeStack);
        if (lootDrops.isEmpty()) {
            TreasureBags.LOGGER.warn("No drops from bag, is the loot table valid? {}", heldItem);
            return new ActionResult<>(EnumActionResult.FAIL, heldItem);
        }
        lootDrops.forEach(stack -> {
            PlayerUtils.giveItem(playerMP, stack);
            listItemReceivedInChat(playerMP, stack);
        });

        // Play item pickup sound...
        playerMP.world.playSound(null, playerMP.posX, playerMP.posY, playerMP.posZ,
                SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                ((playerMP.getRNG().nextFloat() - playerMP.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        heldItem.shrink(openWholeStack ? heldItem.getCount() : 1);
        return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
    }

    private Collection<ItemStack> getDropsFromStack(ItemStack stack, EntityPlayerMP player, boolean wholeStack) {
        Collection<ItemStack> list = new ArrayList<>();
        int openCount = wholeStack ? stack.getCount() : 1;
        for (int i = 0; i < openCount; ++i) {
            this.getLootDrops(stack, player).forEach(s -> mergeItem(list, s));
        }
        return list;
    }

    private static void mergeItem(Collection<ItemStack> list, ItemStack stack) {
        for (ItemStack itemStack : list) {
            if (ItemHandlerHelper.canItemStacksStack(itemStack, stack)) {
                int space = itemStack.getMaxStackSize() - itemStack.getCount();
                int toMerge = Math.min(space, stack.getCount());
                itemStack.grow(toMerge);
                stack.shrink(toMerge);

                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        if (!stack.isEmpty()) {
            list.add(stack);
        }
    }

    private static void listItemReceivedInChat(EntityPlayerMP playerMP, ItemStack stack) {
        ITextComponent itemReceivedText = new TextComponentTranslation(
                "item.silentlib.lootContainer.itemReceived",
                stack.getCount(),
                stack.getDisplayName());
        playerMP.sendMessage(itemReceivedText);
    }
}
