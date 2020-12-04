package net.silentchaos512.treasurebags.lib;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.treasurebags.TreasureBags;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * A rough group for mobs. Very similar to what Scaling Health uses, but no blights and every type
 * has a loot table.
 */
public enum EntityGroup {
    PEACEFUL,
    HOSTILE,
    BOSS,
    PLAYER;

    private final ResourceLocation lootTable;

    EntityGroup() {
        lootTable = TreasureBags.getId("entity_group/" + this.name().toLowerCase(Locale.ROOT));
    }

    public static EntityGroup from(Entity entity) {
        if (entity instanceof PlayerEntity)
            return PLAYER;
        if (!entity.isNonBoss())
            return BOSS;
        if (entity instanceof IMob)
            return HOSTILE;
        return PEACEFUL;
    }

    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Nullable
    public static EntityGroup byName(String name) {
        for (EntityGroup group : values()) {
            if (group.getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return null;
    }
}
