package com.thnki.queuebreaker.model;


import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class Progress
{
    private int mMsg;
    private boolean toBeShown;

    private Progress(int msg)
    {
        mMsg = msg;
    }

    private Progress()
    {

    }

    public int getMsg()
    {
        return mMsg;
    }

    public static void show(int msg)
    {
        Log.d("UploadIssue", "Progress show");
        Progress progress = new Progress(msg);
        progress.toBeShown = true;
        EventBus.getDefault().post(progress);
    }

    public static void hide()
    {
        Progress progress = new Progress();
        progress.toBeShown = false;
        EventBus.getDefault().post(progress);
    }

    public boolean toBeShown()
    {
        return toBeShown;
    }
}
