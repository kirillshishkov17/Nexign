package Entity;

import java.io.BufferedWriter;

/**
 * Информация об абоненте
 */
public class Subscriber {
    private double totalCost;
    private double totalTime;
    private final BufferedWriter bufferedWriter;

    public Subscriber(double totalCost, double totalTime, BufferedWriter bufferedWriter) {
        this.totalCost = totalCost;
        this.totalTime = totalTime;
        this.bufferedWriter = bufferedWriter;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
