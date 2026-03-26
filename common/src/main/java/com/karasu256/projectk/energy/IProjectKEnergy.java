package com.karasu256.projectk.energy;

import com.karasu256.karasulab.karasucore.api.block.ICableInputable;
import com.karasu256.karasulab.karasucore.api.block.ICableOutputable;
import com.karasu256.karasulab.karasucore.api.data.ICapacity;
import com.karasu256.karasulab.karasucore.api.data.IEnergy;

public interface IProjectKEnergy extends ICableOutputable, ICableInputable, IEnergy, ICapacity {
}
