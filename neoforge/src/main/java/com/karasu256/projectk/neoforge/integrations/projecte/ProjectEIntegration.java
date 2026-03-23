package com.karasu256.projectk.neoforge.integrations.projecte;

import com.karasu256.projectk.neoforge.integrations.AbstractNeoForgeModIntegration;

@SuppressWarnings("unused")
public class ProjectEIntegration extends AbstractNeoForgeModIntegration implements IProjectEIntegration {
    public static final String MOD_ID = "projecte";

    @Override
    public void bootstrap() {
        super.bootstrap();
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
}
