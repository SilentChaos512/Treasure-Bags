package net.silentchaos512.treasurebags.lib;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.api.IEntityGroup;

import java.util.Locale;
import java.util.function.Predicate;

/**
 * A rough group for mobs. Very similar to what Scaling Health uses, but no blights and every type
 * has a loot table.
 */
public enum StandardEntityGroups implements IEntityGroup {
    PLAYER(e -> e instanceof Player),
    BOSS(e -> !e.canChangeDimensions()),
    HOSTILE(e -> e instanceof Enemy),
    PEACEFUL(e -> !(e instanceof Player || !e.canChangeDimensions() || e instanceof Enemy));

    private final ResourceLocation lootTable;
    private final Predicate<Entity> predicate;

    StandardEntityGroups(Predicate<Entity> predicate) {
        this.lootTable = TreasureBags.getId("entity_group/" + this.getName());
        this.predicate = predicate;
    }

    @Override
    public ResourceLocation getId() {
        return TreasureBags.getId(this.getName());
    }

    @Override
    public boolean matches(Entity entity) {
        return this.predicate.test(entity);
    }

    @Override
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
