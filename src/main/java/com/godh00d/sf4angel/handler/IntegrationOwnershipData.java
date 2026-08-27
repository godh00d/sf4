package com.godh00d.sf4angel.handler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Durable ownership for optional integrations that do not provide their own persistent owner. */
public final class IntegrationOwnershipData extends WorldSavedData {

    private static final String DATA_NAME = "sf4angel_integration_owners";
    private final Map<Key, UUID> owners = new HashMap<>();

    public IntegrationOwnershipData() {
        super(DATA_NAME);
    }

    public IntegrationOwnershipData(String name) {
        super(name);
    }

    public static IntegrationOwnershipData get(World world) {
        if (!(world instanceof WorldServer)) throw new IllegalArgumentException("Server world required");
        IntegrationOwnershipData data = (IntegrationOwnershipData) world.getPerWorldStorage()
            .getOrLoadData(IntegrationOwnershipData.class, DATA_NAME);
        if (data == null) {
            data = new IntegrationOwnershipData(DATA_NAME);
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }
        return data;
    }

    public void put(World world, String kind, BlockPos position, UUID owner) {
        owners.put(new Key(kind, world.provider.getDimension(), position), owner);
        markDirty();
    }

    public UUID owner(World world, String kind, BlockPos position) {
        return owners.get(new Key(kind, world.provider.getDimension(), position));
    }

    public boolean remove(World world, String kind, BlockPos position) {
        if (owners.remove(new Key(kind, world.provider.getDimension(), position)) == null) return false;
        markDirty();
        return true;
    }

    public boolean removeAt(World world, BlockPos position) {
        int dimension = world.provider.getDimension();
        boolean changed = false;
        Iterator<Key> iterator = owners.keySet().iterator();
        while (iterator.hasNext()) {
            Key key = iterator.next();
            if (key.dimension == dimension && key.position.equals(position)) {
                iterator.remove();
                changed = true;
            }
        }
        if (changed) markDirty();
        return changed;
    }

    public boolean removeOwner(String kindPrefix, UUID owner) {
        boolean changed = owners.entrySet().removeIf(entry -> entry.getKey().kind.startsWith(kindPrefix)
            && entry.getValue().equals(owner));
        if (changed) markDirty();
        return changed;
    }

    public List<Record> records(World world, String kindPrefix) {
        int dimension = world.provider.getDimension();
        List<Record> result = new ArrayList<>();
        for (Map.Entry<Key, UUID> entry : owners.entrySet()) {
            Key key = entry.getKey();
            if (key.dimension == dimension && key.kind.startsWith(kindPrefix)) {
                result.add(new Record(key.kind, key.position, entry.getValue()));
            }
        }
        return result;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        owners.clear();
        NBTTagList list = nbt.getTagList("Owners", 10);
        for (int index = 0; index < list.tagCount(); index++) {
            NBTTagCompound tag = list.getCompoundTagAt(index);
            String kind = tag.getString("Kind");
            if (kind.isEmpty() || !tag.hasUniqueId("Owner")) continue;
            Key key = new Key(kind, tag.getInteger("Dimension"), BlockPos.fromLong(tag.getLong("Position")));
            owners.put(key, tag.getUniqueId("Owner"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Key, UUID> entry : owners.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("Kind", entry.getKey().kind);
            tag.setInteger("Dimension", entry.getKey().dimension);
            tag.setLong("Position", entry.getKey().position.toLong());
            tag.setUniqueId("Owner", entry.getValue());
            list.appendTag(tag);
        }
        nbt.setTag("Owners", list);
        return nbt;
    }

    public static final class Record {
        public final String kind;
        public final BlockPos position;
        public final UUID owner;

        private Record(String kind, BlockPos position, UUID owner) {
            this.kind = kind;
            this.position = position;
            this.owner = owner;
        }
    }

    private static final class Key {
        private final String kind;
        private final int dimension;
        private final BlockPos position;

        private Key(String kind, int dimension, BlockPos position) {
            this.kind = kind;
            this.dimension = dimension;
            this.position = position.toImmutable();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Key)) return false;
            Key that = (Key) other;
            return dimension == that.dimension && kind.equals(that.kind) && position.equals(that.position);
        }

        @Override
        public int hashCode() {
            int result = kind.hashCode();
            result = result * 31 + dimension;
            return result * 31 + position.hashCode();
        }
    }
}
