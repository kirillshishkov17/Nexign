package Entity;

import java.io.PrintWriter;

/**
 * Информация об абоненте
 */
public class Subscriber {
    private double totalCost;
    private double totalTime;
    private final PrintWriter printWriter;

    public Subscriber(double totalCost, double totalTime, PrintWriter printWriter) {
        this.totalCost = totalCost;
        this.totalTime = totalTime;
        this.printWriter = printWriter;
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

    public PrintWriter getPrintWriter() {
        return printWriter;
    }
}
