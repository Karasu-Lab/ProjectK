package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KEntityRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;

@KRegistry(modId = ProjectK.MOD_ID, order = 2)
public class EntitiesRegistry implements IKRegistryTarget {
    public static void register() {
        KEntityRegistry.register(ProjectK.MOD_ID);
    }
}
