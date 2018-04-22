package com.thnki.queuebreaker.model;

import org.greenrobot.eventbus.EventBus;

public class ToastMsg
{
    private int mMsg = -9999;
    private String mTxtMsg;

    private ToastMsg(int msg)
    {
        mMsg = msg;
    }

    private ToastMsg(String msg)
    {
        mTxtMsg = msg;
    }

    public int getMsg()
    {
        return mMsg;
    }

    public String getTxtMsg()
    {
        return mTxtMsg;
    }

    public static void show(int msg)
    {
        ToastMsg toast = new ToastMsg(msg);
        EventBus.getDefault().post(toast);
    }

    public static void show(String msg)
    {
        ToastMsg toast = new ToastMsg(msg);
        EventBus.getDefault().post(toast);
    }
}
