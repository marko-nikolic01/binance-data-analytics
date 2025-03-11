package com.example;

public class SumAndCount {
    double sum = 0.0;
    long count = 0;

    void add(double value) {
        this.sum += value;
        this.count++;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    double getAverage() {
        return count == 0 ? 0.0 : sum / count;
    }
}
