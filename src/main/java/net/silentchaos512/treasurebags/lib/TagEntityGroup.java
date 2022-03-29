package net.silentchaos512.treasurebags.lib;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.api.IEntityGroup;

public class TagEntityGroup implements IEntityGroup {
    private final ResourceLocation groupId;
    private final TagKey<EntityType<?>> entityTypeTag;

    public TagEntityGroup(ResourceLocation groupId, TagKey<EntityType<?>> entityTypeTag) {
        this.groupId = groupId;
        this.entityTypeTag = entityTypeTag;
    }

    @Override
    public ResourceLocation getId() {
        return this.groupId;
    }

    @Override
    public boolean matches(Entity entity) {
        return entity.getType().is(this.entityTypeTag);
    }

    @Override
    public ResourceLocation getLootTable() {
        String path = groupId.toString().replace(':', '.');
        return TreasureBags.getId("entity_group/" + path);
    }
}
