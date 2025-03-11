package com.example;

import java.io.Serializable;
import java.util.Date;

public class PriceSpread implements Serializable {
    private String symbol;
    private double highPrice;
    private double lowPrice;
    private Date time;

    public PriceSpread(String symbol, double highPrice, double lowPrice, Date time) {
        this.symbol = symbol;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.time = time;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public Date getTime() {
        return time;
    }
}
