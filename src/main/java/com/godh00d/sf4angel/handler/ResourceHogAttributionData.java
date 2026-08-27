package com.godh00d.sf4angel.handler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Durable breeding attribution for Resource Hogs. */
public final class ResourceHogAttributionData extends WorldSavedData {

    private static final String DATA_NAME = "sf4angel_resource_hog_owners";
    private final Map<UUID, Record> records = new HashMap<>();

    public ResourceHogAttributionData() {
        super(DATA_NAME);
    }

    public ResourceHogAttributionData(String name) {
        super(name);
    }

    public static ResourceHogAttributionData get(World world) {
        if (!(world instanceof WorldServer)) throw new IllegalArgumentException("Server world required");
        ResourceHogAttributionData data = (ResourceHogAttributionData) world.getPerWorldStorage()
            .getOrLoadData(ResourceHogAttributionData.class, DATA_NAME);
        if (data == null) {
            data = new ResourceHogAttributionData(DATA_NAME);
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }
        return data;
    }

    public void put(EntityRecord entity, UUID owner) {
        records.put(entity.entity, new Record(entity.entity, owner, entity.dimension, entity.position, entity.seenAt));
        markDirty();
    }

    public void update(UUID entity, BlockPos position, long seenAt) {
        Record current = records.get(entity);
        if (current == null) return;
        current.position = position.toImmutable();
        current.seenAt = seenAt;
        markDirty();
    }

    public boolean remove(UUID entity) {
        if (records.remove(entity) == null) return false;
        markDirty();
        return true;
    }

    public List<Record> records(World world, UUID owner) {
        int dimension = world.provider.getDimension();
        List<Record> result = new ArrayList<>();
        for (Record record : records.values()) {
            if (record.dimension == dimension && record.owner.equals(owner)) result.add(record.copy());
        }
        return result;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        records.clear();
        NBTTagList list = nbt.getTagList("Hogs", 10);
        for (int index = 0; index < list.tagCount(); index++) {
            NBTTagCompound tag = list.getCompoundTagAt(index);
            if (!tag.hasUniqueId("Entity") || !tag.hasUniqueId("Owner")) continue;
            UUID entity = tag.getUniqueId("Entity");
            records.put(entity, new Record(entity, tag.getUniqueId("Owner"), tag.getInteger("Dimension"),
                BlockPos.fromLong(tag.getLong("Position")), tag.getLong("SeenAt")));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Record record : records.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("Entity", record.entity);
            tag.setUniqueId("Owner", record.owner);
            tag.setInteger("Dimension", record.dimension);
            tag.setLong("Position", record.position.toLong());
            tag.setLong("SeenAt", record.seenAt);
            list.appendTag(tag);
        }
        nbt.setTag("Hogs", list);
        return nbt;
    }

    public static final class EntityRecord {
        private final UUID entity;
        private final int dimension;
        private final BlockPos position;
        private final long seenAt;

        public EntityRecord(UUID entity, World world, BlockPos position, long seenAt) {
            this.entity = entity;
            this.dimension = world.provider.getDimension();
            this.position = position.toImmutable();
            this.seenAt = seenAt;
        }
    }

    public static final class Record {
        public final UUID entity;
        public final UUID owner;
        public final int dimension;
        public BlockPos position;
        public long seenAt;

        private Record(UUID entity, UUID owner, int dimension, BlockPos position, long seenAt) {
            this.entity = entity;
            this.owner = owner;
            this.dimension = dimension;
            this.position = position.toImmutable();
            this.seenAt = seenAt;
        }

        private Record copy() {
            return new Record(entity, owner, dimension, position, seenAt);
        }
    }
}
