package com.onevour.core.applications.commons;

import java.text.NumberFormat;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class StopWatch extends org.springframework.util.StopWatch {

    boolean isAutoStopNewTask = true;

    public StopWatch() {
        super();
    }

    public StopWatch(String id) {
        super(id);
    }

    public StopWatch(boolean isAutoStopNewTask) {
        start();
        this.isAutoStopNewTask = isAutoStopNewTask;
    }

    public StopWatch(String id, boolean isAutoStopNewTask) {
        super(id);
        this.isAutoStopNewTask = isAutoStopNewTask;
    }

    @Override
    public void start(String taskName) throws IllegalStateException {
        if (isAutoStopNewTask && isRunning()) {
            stop();
        }
        super.start(taskName);
    }

    public void start(String taskName, Runnable task) {
        if (isAutoStopNewTask && isRunning()) {
            stop();
        }
        super.start(taskName);
        task.run();
        stop();
    }

    public <T> T start(String taskName, Callable<T> task) throws Exception {
        if (isAutoStopNewTask && isRunning()) {
            stop();
        }
        try {
            super.start(taskName);
            return task.call();
        } finally {
            stop();
        }
    }

    @Override
    public String prettyPrint() {
        if (isAutoStopNewTask && isRunning()) {
            stop();
        }
        return super.prettyPrint();
    }

    public String shortSummaryMillis() {
        return "StopWatch '" + this.getId() + "': running time = " + this.getTotalTimeMillis() + " ms";
    }

    public String shortSummarySecond() {
        return "StopWatch '" + this.getId() + "': running time = " + this.getTotalTimeSeconds() + " s";
    }

    public String prettyPrintMillis() {
        if (isAutoStopNewTask && isRunning()) {
            stop();
        }
        StringBuilder sb = new StringBuilder(this.shortSummaryMillis());
        sb.append('\n');
        if (getTaskInfo().length == 0) {
            sb.append("No task info kept");
        } else {
            sb.append("---------------------------------------------\n");
            sb.append("ms         %     Task name\n");
            sb.append("---------------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(6);
            nf.setMaximumIntegerDigits(3);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            TaskInfo[] var4 = this.getTaskInfo();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                TaskInfo task = var4[var6];
                sb.append(nf.format(task.getTimeNanos())).append("  ");
                sb.append(pf.format((double) task.getTimeMillis() / (double) this.getTotalTimeMillis())).append("  ");
                sb.append(task.getTaskName()).append('\n');
            }
        }
        return sb.toString();
    }

    public String prettyPrintSecond() {
        if (isAutoStopNewTask && isRunning()) {
            stop();
        }
        StringBuilder sb = new StringBuilder(this.shortSummarySecond());
        sb.append('\n');
        if (getTaskInfo().length == 0) {
            sb.append("No task info kept");
        } else {
            sb.append("---------------------------------------------\n");
            sb.append("s         %     Task name\n");
            sb.append("---------------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(3);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            TaskInfo[] var4 = this.getTaskInfo();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                TaskInfo task = var4[var6];
                sb.append(nf.format(task.getTimeSeconds())).append("  ");
                sb.append(pf.format((double) task.getTimeSeconds() / (double) this.getTotalTimeSeconds())).append("  ");
                sb.append(task.getTaskName()).append('\n');
            }
        }
        return sb.toString();
    }
}
