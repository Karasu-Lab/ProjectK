package com.karasu256.projectk.client.resource.item;

import com.karasu256.projectk.client.resource.item.impl.AbstractDustResourceGenerator;
import com.karasu256.projectk.utils.Id;

public class KarasiumDustGenerator extends AbstractDustResourceGenerator {
    public KarasiumDustGenerator() {
        super(Id.id("item/karasium_dust"), Id.id("karasium_dust"), 0xADFF2F);
    }
}
