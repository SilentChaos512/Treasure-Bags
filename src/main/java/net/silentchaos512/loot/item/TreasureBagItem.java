package net.silentchaos512.loot.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.silentchaos512.lib.item.LootContainerItem;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.config.Config;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class TreasureBagItem extends LootContainerItem {
    //    private static final String NBT_BAG_COLOR = "BagColor";
//    private static final String NBT_BAG_OVERLAY_COLOR = "BagOverlayColor";
//    private static final String NBT_BAG_STRING_COLOR = "BagStringColor";
    private static final String NBT_BAG_TYPE = "BagType";
//    private static final String NBT_CUSTOM_NAME = "CustomName";

    public TreasureBagItem() {
        super(TreasureBags.getId("default_bag"), true, new Properties().group(ItemGroup.MISC));
    }

    public ItemStack stackOfType(IBagType type) {
        return stackOfType(type, 1);
    }

    public ItemStack stackOfType(IBagType type, int count) {
        ItemStack result = new ItemStack(this, count);
        return setBagProperties(result, type);
    }

    /**
     * Create a bag of a type that may exist. Useful for recipes/ingredients, since bag types may
     * not exist before they load.
     *
     * @param typeName The type ID
     * @return A stack of one treasure bag
     */
    public ItemStack stackOfType(ResourceLocation typeName) {
        ItemStack result = new ItemStack(this);
        return setBagType(result, typeName);
    }

    @Nullable
    public static IBagType getBagType(ItemStack stack) {
        String typeStr = getBagTypeString(stack);
        return BagTypeManager.getValue(new ResourceLocation(typeStr));
    }

    private static String getBagTypeString(ItemStack stack) {
        return getData(stack).getString(NBT_BAG_TYPE);
    }

    /**
     * Set the BagType tag, nothing else. This is needed for loading from loot tables, because bag
     * types may not exist yet.
     *
     * @param stack     The bag
     * @param bagTypeId The bag type ID
     * @return The bag (modified original, not a copy)
     */
    public static ItemStack setBagType(ItemStack stack, ResourceLocation bagTypeId) {
        if (!(stack.getItem() instanceof TreasureBagItem)) return stack;
        CompoundNBT tag = getData(stack);
        tag.putString(NBT_BAG_TYPE, bagTypeId.toString());
        return stack;
    }

    public static ItemStack setBagProperties(ItemStack stack, IBagType type) {
        if (!(stack.getItem() instanceof TreasureBagItem)) return stack;
        CompoundNBT tag = getData(stack);
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

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        IBagType type = getBagType(stack);
        if (type != null) {
            return new StringTextComponent(type.getCustomName());
        }
        return super.getDisplayName(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack, int tintIndex) {
        IBagType bagType = getBagType(stack);
        if (bagType != null) {
            if (tintIndex == 0) return bagType.getBagColor();
            if (tintIndex == 1) return bagType.getBagOverlayColor();
            if (tintIndex == 2) return bagType.getBagStringColor();
        }
        return 0xFFFFFF;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        // Bag type
        if (flagIn.isAdvanced()) {
            IBagType type = getBagType(stack);
            if (type != null) {
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".type", type.getId())
                        .applyTextStyle(TextFormatting.YELLOW));
            } else {
                String typeStr = getBagTypeString(stack);
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".unknownType")
                        .applyTextStyle(TextFormatting.RED));
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".type", typeStr)
                        .applyTextStyle(TextFormatting.YELLOW));
            }
        }

        // Loot table (or whatever ItemLootContainer wants to do)
        super.addInformation(stack, worldIn, tooltip, flagIn);

        // Display data pack ID
        String typeStr = getBagTypeString(stack);
        ResourceLocation typeName = ResourceLocation.tryCreate(typeStr);
        if (typeName != null) {
            tooltip.add(new StringTextComponent(typeName.getNamespace())
                    .applyTextStyle(TextFormatting.BLUE).applyTextStyle(TextFormatting.ITALIC));
        } else {
            tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".invalidType")
                    .applyTextStyle(TextFormatting.RED));
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (!(playerIn instanceof ServerPlayerEntity)) {
            return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
        }
        ServerPlayerEntity playerMP = (ServerPlayerEntity) playerIn;

        // Generate items from loot table, give to player.
        boolean openWholeStack = playerMP.isCrouching();
        Collection<ItemStack> lootDrops = getDropsFromStack(heldItem, playerMP, openWholeStack);
        if (lootDrops.isEmpty()) {
            TreasureBags.LOGGER.warn("No drops from bag, is the loot table valid? {}, table={}", heldItem, getLootTable(heldItem));
            return new ActionResult<>(ActionResultType.FAIL, heldItem);
        }
        lootDrops.forEach(stack -> {
            ItemStack copy = stack.copy();
            if (Config.GENERAL.alwaysSpawnItems.get() || !playerIn.inventory.addItemStackToInventory(copy)) {
                ItemEntity entityItem = new ItemEntity(playerIn.world, playerIn.getPosX(), playerIn.getPosYHeight(0.5), playerIn.getPosZ(), copy);
                entityItem.setNoPickupDelay();
                entityItem.setOwnerId(playerIn.getUniqueID());
                playerIn.world.addEntity(entityItem);
            }
            listItemReceivedInChat(playerMP, stack);
        });

        // Play item pickup sound...
        playerMP.world.playSound(null, playerMP.getPosX(), playerMP.getPosY(), playerMP.getPosZ(),
                SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                ((playerMP.getRNG().nextFloat() - playerMP.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        heldItem.shrink(openWholeStack ? heldItem.getCount() : 1);
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    private Collection<ItemStack> getDropsFromStack(ItemStack stack, ServerPlayerEntity player, boolean wholeStack) {
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

    private static void listItemReceivedInChat(ServerPlayerEntity playerMP, ItemStack stack) {
        ITextComponent itemReceivedText = new TranslationTextComponent(
                "item.silentlib.lootContainer.itemReceived",
                stack.getCount(),
                stack.getDisplayName());
        playerMP.sendMessage(itemReceivedText);
    }
}
