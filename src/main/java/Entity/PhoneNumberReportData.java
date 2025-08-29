package Entity;

import java.io.BufferedWriter;

public class PhoneNumberReportData {
    private double totalCosts;
    private double totalTime;
    private BufferedWriter bufferedWriter;

    public PhoneNumberReportData(double totalCosts, double totalTime, BufferedWriter bufferedWriter) {
        this.totalCosts = totalCosts;
        this.totalTime = totalTime;
        this.bufferedWriter = bufferedWriter;
    }

    public double getTotalCosts() {
        return totalCosts;
    }

    public void setTotalCosts(double totalCosts) {
        this.totalCosts = totalCosts;
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

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }
}
