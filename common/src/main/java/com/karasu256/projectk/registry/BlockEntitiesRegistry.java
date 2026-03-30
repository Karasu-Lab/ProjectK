package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KBlockEntityRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 5)
public class BlockEntitiesRegistry implements IKRegistryTarget {
    public static void register() {
        KBlockEntityRegistry.register(ProjectK.MOD_ID);
    }

    public static <T extends net.minecraft.world.level.block.entity.BlockEntity> RegistrySupplier<BlockEntityType<T>> blockEntity(String id, Supplier<BlockEntityType<T>> supplier) {
        return KBlockEntityRegistry.blockEntity(ProjectK.MOD_ID, id, supplier);
    }
}
