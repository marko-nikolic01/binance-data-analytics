package com.example;

public class TradingVolume {
    private double totalVolume = 0.0;

    public void add(double volume) {
        this.totalVolume += volume;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }
}
