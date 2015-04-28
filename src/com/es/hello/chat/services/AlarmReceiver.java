package com.es.hello.chat.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {

	Intent pushIntent = new Intent(context, HelloMainService.class);
	pushIntent.addCategory(HelloMainService.TAG);
	context.startService(pushIntent);
    }
}
