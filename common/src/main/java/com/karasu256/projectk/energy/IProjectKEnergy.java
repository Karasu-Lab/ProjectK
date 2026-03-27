package com.karasu256.projectk.energy;


import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.IEnergy;

public interface IProjectKEnergy extends ICableOutputable, ICableInputable, IEnergy, ICapacity {
}
