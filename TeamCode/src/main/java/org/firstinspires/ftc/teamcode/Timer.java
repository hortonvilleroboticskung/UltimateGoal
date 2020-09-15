package org.firstinspires.ftc.teamcode;

public class Timer {
    long startTime;

    public Timer(){
        startTime = System.currentTimeMillis();
    }

    public long getTimeElapsed(){
        return System.currentTimeMillis()-startTime;
    }

    public boolean hasTimeElapsed(long msec){
        return getTimeElapsed() >= msec;
    }

    public void reset(){
        startTime = System.currentTimeMillis();
    }
}

