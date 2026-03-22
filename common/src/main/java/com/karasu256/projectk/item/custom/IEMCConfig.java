package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.EMCData;

import java.util.Optional;

public interface IEMCConfig {
    default Optional<EMCData> getEMC() {
        return EMCData.of(0);
    }
}
