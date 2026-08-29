package com.godh00d.sf4angel.init;

import com.godh00d.sf4angel.Reference;
import com.godh00d.sf4angel.SF4Angel;
import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.entity.EntityConstellationObservatory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    private static int entityId = 0;

    public static void register() {
        EntityRegistry.registerModEntity(
            new ResourceLocation(Reference.MOD_ID, "angel"),
            EntityAngel.class,
            Reference.MOD_ID + ".angel",
            entityId++,
            SF4Angel.instance,
            64, 20, false
        );
        EntityRegistry.registerModEntity(
            new ResourceLocation(Reference.MOD_ID, "constellation_observatory"),
            EntityConstellationObservatory.class,
            Reference.MOD_ID + ".constellation_observatory",
            entityId++,
            SF4Angel.instance,
            320, 1, false
        );
    }
}
