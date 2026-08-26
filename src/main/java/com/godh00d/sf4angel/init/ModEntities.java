package com.godh00d.sf4angel.init;

import com.godh00d.sf4angel.Reference;
import com.godh00d.sf4angel.entity.EntityAngel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModEntities {

    private static int entityId = 0;

    public static void register() {
        GameRegistry.registerEntityEntry(EntityAngel.class,
            EntityEntryBuilder.create()
                .entity(EntityAngel.class)
                .id(new ResourceLocation(Reference.MOD_ID, "angel"), entityId++)
                .name(Reference.MOD_ID + ".angel")
                .tracker(64, 20, false)
                .build()
        );
    }
}
