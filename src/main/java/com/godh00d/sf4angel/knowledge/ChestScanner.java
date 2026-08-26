package com.godh00d.sf4angel.knowledge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ChestScanner {

    private static final int SCAN_RANGE = 8;

    public static ScanResult scanNearbyInventories(EntityPlayer player) {
        ScanResult result = new ScanResult();
        World world = player.world;
        BlockPos playerPos = player.getPosition();

        for (int x = -SCAN_RANGE; x <= SCAN_RANGE; x++) {
            for (int y = -SCAN_RANGE; y <= SCAN_RANGE; y++) {
                for (int z = -SCAN_RANGE; z <= SCAN_RANGE; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof IInventory) {
                        IInventory inv = (IInventory) te;
                        for (int i = 0; i < inv.getSizeInventory(); i++) {
                            ItemStack stack = inv.getStackInSlot(i);
                            if (!stack.isEmpty()) {
                                String itemId = stack.getItem().getRegistryName() != null
                                    ? stack.getItem().getRegistryName().toString()
                                    : stack.getDisplayName();
                                result.addItem(itemId, stack.getCount());
                                result.totalItems += stack.getCount();
                                result.uniqueItems++;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public static String analyzeAndAdvise(EntityPlayer player, ScanResult scan) {
        if (scan.totalItems == 0) {
            return "Your inventories are empty. Start by sifting cobblestone.";
        }

        StringBuilder advice = new StringBuilder();

        Map<String, Integer> sorted = new LinkedHashMap<>();
        scan.itemCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(e -> sorted.put(e.getKey(), e.getValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            if (count >= 3) break;
            advice.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
            count++;
        }

        if (scan.totalItems > 500) {
            advice.append("| You have many items. Consider upgrading storage.");
        } else if (scan.totalItems < 50) {
            advice.append("| You need more resources. Sift more cobblestone.");
        }

        return advice.toString().trim();
    }

    public static class ScanResult {
        public Map<String, Integer> itemCounts = new LinkedHashMap<>();
        public int totalItems = 0;
        public int uniqueItems = 0;

        public void addItem(String itemId, int count) {
            itemCounts.merge(itemId, count, Integer::sum);
        }

        public int getCount(String itemId) {
            return itemCounts.getOrDefault(itemId, 0);
        }

        public boolean hasItem(String itemId) {
            return itemCounts.containsKey(itemId);
        }
    }
}
