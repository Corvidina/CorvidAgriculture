package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.AgricultureItem;

import java.util.HashMap;

public class CateringReward {
    private final HashMap<AgricultureItem, Integer> rewards;
    private int rewardedVermiculite;
    private int money;
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    public CateringReward(){
        rewards=new HashMap<>();
    }

    public static class Builder {
        private final CateringReward cateringReward;
        private Builder(){
            cateringReward=new CateringReward();
        }
        public static Builder init(){
            return new Builder();
        }
        public CateringReward build(){
            return cateringReward;
        }
        public Builder setRewardedVermiculite(int rewardedVermiculite) {
            cateringReward.setRewardedVermiculite(rewardedVermiculite);
            return this;
        }
        public Builder addItemReward(AgricultureItem item, int count) {
            cateringReward.addItemReward(item, count);
            return this;
        }
        public Builder setRewardedMoney(int money) {
            cateringReward.setRewardedMoney(money);
            return this;
        }
    }

    public void setRewardedVermiculite(int rewardedVermiculite) {
        this.rewardedVermiculite = rewardedVermiculite;
    }

    public void addItemReward(AgricultureItem item, int count) {
        rewards.put(item, count);
    }

    public void setRewardedMoney(int money) {
        this.money=money;
    }

    public int getRewardedVermiculite(){
        return rewardedVermiculite;
    }

    public double getRewardedMoney() {
        return money * plugin.getSellMultiplier();
    }

    public HashMap<AgricultureItem, Integer> getRewards(){
        return rewards;
    }
}
