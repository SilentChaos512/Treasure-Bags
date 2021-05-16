package net.silentchaos512.treasurebags.lib;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.api.IEntityGroup;

public class TagEntityGroup implements IEntityGroup {
    private final ResourceLocation groupId;
    private final ITag<EntityType<?>> entityTypeTag;

    public TagEntityGroup(ResourceLocation groupId, ITag<EntityType<?>> entityTypeTag) {
        this.groupId = groupId;
        this.entityTypeTag = entityTypeTag;
    }

    @Override
    public ResourceLocation getId() {
        return this.groupId;
    }

    @Override
    public boolean matches(Entity entity) {
        return this.entityTypeTag.contains(entity.getType());
    }

    @Override
    public ResourceLocation getLootTable() {
        String path = groupId.toString().replace(':', '.');
        return TreasureBags.getId("entity_group/" + path);
    }
}
