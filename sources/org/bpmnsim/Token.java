package org.bpmnsim;
import org.bpmnsim.utils.FileSize;

import java.util.ArrayList;
import java.util.List;

public class Token {

    private int id;


    private List<HistoryEntry> history = new ArrayList<>();
    private FileSize fileSize;

    public Token(int id, FileSize fileSize) {
        this.id = id;
        this.fileSize = fileSize;
    }

    public int getId() {
        return this.id;
    }


    public double getTotalRuntime() {
        return (finishTime - startTime);
    }

    public void addHistoryEntry(String s, Double d) {
        history.add(new HistoryEntry(s, d));
    }


    private double startTime;
    private double finishTime;


    @Override
    public String toString() {
        return "Token ID: " + this.getId() + " - totalRuntime: " + this.getTotalRuntime() + "  " + history.toString();

    }

    public double getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public List<HistoryEntry> getHistory() {
        return history;
    }

    public FileSize getFileSize() {
        return this.fileSize;
    }

    private class HistoryEntry {
        private double time;
        private String text;

        public HistoryEntry(String t, double d) {
            time = d;
            text = t;
        }

        @Override
        public String toString() {
            return text + " " + time;
        }
    }
}
