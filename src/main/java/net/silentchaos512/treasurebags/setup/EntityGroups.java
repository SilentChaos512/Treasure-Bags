package net.silentchaos512.treasurebags.setup;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.treasurebags.api.IEntityGroup;
import net.silentchaos512.treasurebags.lib.StandardEntityGroups;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public final class EntityGroups {
    private static final Map<ResourceLocation, IEntityGroup> GROUPS = new HashMap<>();

    private EntityGroups() {}

    static void init() {
        for (StandardEntityGroups group : StandardEntityGroups.values()) {
            create(group);
        }
    }

    public static IEntityGroup getOrCreate(ResourceLocation id, Function<ResourceLocation, IEntityGroup> factory) {
        return GROUPS.computeIfAbsent(id, factory);
    }

    public static void create(IEntityGroup group) {
        GROUPS.put(group.getId(), group);
    }

    public static Stream<IEntityGroup> getGroups(Entity entity) {
        return GROUPS.values().stream().filter(group -> group.matches(entity));
    }
}
