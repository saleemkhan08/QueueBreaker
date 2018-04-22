package com.thnki.queuebreaker.model;


import org.greenrobot.eventbus.EventBus;

public class Snack
{
    private int mMsg;

    private Snack(int msg)
    {
        mMsg = msg;
    }

    public int getMsg()
    {
        return mMsg;
    }

    public static void show(int msg)
    {
        Snack snack = new Snack(msg);
        EventBus.getDefault().post(snack);
    }
}
