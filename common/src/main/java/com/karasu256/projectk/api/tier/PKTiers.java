package com.karasu256.projectk.api.tier;

public enum PKTiers {
    TIER_0(0), TIER_1(1), TIER_2(2), TIER_3(3);

    private final PKTierInfo info;

    PKTiers(int tier) {
        this.info = new PKTierInfo(tier);
    }

    public int getTier() {
        return info.level();
    }

    public PKTierInfo getInfo() {
        return info;
    }
}
