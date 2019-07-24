package net.silentchaos512.loot.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.item.TreasureBagItem;
import net.silentchaos512.loot.network.SyncBagTypesPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class BagTypeManager implements IResourceManagerReloadListener {
    public static final BagTypeManager INSTANCE = new BagTypeManager();
    private static final Marker MARKER = MarkerManager.getMarker("BagTypeManager");
    private static final String RESOURCES_PATH = "treasurebags/bag_types";

    private static final Map<ResourceLocation, IBagType> MAP = new LinkedHashMap<>();
    private static int fileCount;

    private BagTypeManager() {}

    @Nullable
    public static IBagType getValue(ResourceLocation id) {
        return MAP.get(id);
    }

    public static Collection<IBagType> getValues() {
        return MAP.values();
    }

    @Nullable
    public static IBagType typeFromBag(ItemStack stack) {
        if (!(stack.getItem() instanceof TreasureBagItem)) return null;
        return TreasureBagItem.getBagType(stack);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(
                RESOURCES_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        MAP.clear();
        fileCount = 0;

        for (ResourceLocation id : resources) {
            ++fileCount;
            try (IResource iresource = resourceManager.getResource(id)) {
                String path = id.getPath().substring(RESOURCES_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
                if (TreasureBags.LOGGER.isTraceEnabled()) {
                    TreasureBags.LOGGER.trace(MARKER, "Found bag type file: {}, reading as '{}'", id, name);
                }

                JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                if (json == null) {
                    TreasureBags.LOGGER.error(MARKER, "Could not load bag type {} as it's null or empty", name);
                } else {
                    addBagType(deserialize(name, json));
                }
            } catch (IllegalArgumentException | JsonParseException ex) {
                TreasureBags.LOGGER.error(MARKER, "Parsing error loading bag type {}", id, ex);
            } catch (IOException ex) {
                TreasureBags.LOGGER.error(MARKER, "Could not read bag type {}", id, ex);
            }
        }
    }

    private static void addBagType(IBagType type) {
        MAP.put(type.getId(), type);
    }

    private static IBagType deserialize(ResourceLocation name, JsonObject json) {
        return BagType.Serializer.deserialize(name, json);
    }

    public static void handleSyncPacket(SyncBagTypesPacket packet, Supplier<NetworkEvent.Context> context) {
        MAP.clear();
        packet.getBagTypes().forEach(type -> MAP.put(type.getId(), type));
        TreasureBags.LOGGER.info("Read {} bag types from server", MAP.size());
        context.get().setPacketHandled(true);
    }

    public static Collection<ITextComponent> getErrorMessages(ServerPlayerEntity player) {
        Collection<ITextComponent> messages = new ArrayList<>();

        // Bag type files that failed to load
        if (fileCount != MAP.size()) {
            int errorCount = fileCount - MAP.size();
            String counter = errorCount + " bag type file" + (errorCount > 1 ? "s" : "");
            ITextComponent prefix = new StringTextComponent("[Treasure Bags] ").applyTextStyle(TextFormatting.YELLOW);
            messages.add(prefix.appendSibling(new StringTextComponent(counter + " failed to load! Check your log")));
        }

        // Loot tables that failed to load or are missing
        int missingLootTables = countMissingLootTables(player);
        if (missingLootTables > 0) {
            String counter = missingLootTables == 1
                    ? "1 bag type has a missing or invalid loot table"
                    : missingLootTables + " bag types have missing or invalid loot tables";
            messages.add(errorMessage(counter));
        }

        return messages;
    }

    private static int countMissingLootTables(ServerPlayerEntity player) {
        MinecraftServer server = player.world.getServer();
        if (server == null) return 0;

        Collection<ResourceLocation> lootTables = server.getLootTableManager().getLootTableKeys();
        return (int) MAP.values().stream().filter(bagType -> !lootTables.contains(bagType.getLootTable())).count();
    }

    private static ITextComponent errorMessage(String str) {
        return new StringTextComponent("[Treasure Bags] ").applyTextStyle(TextFormatting.YELLOW)
                .appendSibling(new StringTextComponent(str).applyTextStyle(TextFormatting.WHITE));
    }
}
