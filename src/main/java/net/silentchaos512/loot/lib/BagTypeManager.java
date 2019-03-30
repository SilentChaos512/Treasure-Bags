package net.silentchaos512.loot.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.loot.TreasureBags;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class BagTypeManager implements IResourceManagerReloadListener {
    public static final BagTypeManager INSTANCE = new BagTypeManager();
    private static final Marker MARKER = MarkerManager.getMarker("BagTypeManager");
    private static final String RESOURCES_PATH = "treasurebags/bag_types";

    private static final Map<ResourceLocation, IBagType> MAP = new LinkedHashMap<>();

    private BagTypeManager() {}

    @Nullable
    public static IBagType getValue(ResourceLocation id) {
        return MAP.get(id);
    }

    public static Collection<IBagType> getValues() {
        return MAP.values();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(
                RESOURCES_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        MAP.clear();

        for (ResourceLocation id : resources) {
            try (IResource iresource = resourceManager.getResource(id)) {
                String path = id.getPath().substring(RESOURCES_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
                TreasureBags.LOGGER.info(MARKER, "Found bag type file: {}, reading as '{}'", id, name);

                JsonObject json = JsonUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
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
}
