package pl.pg.kaboom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;

/**
 * Created by pgalinsk 2015-06-15
 */

@EReceiver
public class DataBroadcastReceiver extends BroadcastReceiver {


    @ReceiverAction("DATA_RECEIVED")
    public void onDataReceived(@ReceiverAction.Extra("Data") byte[] data, Context ctx){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO NOTHING
    }
}
