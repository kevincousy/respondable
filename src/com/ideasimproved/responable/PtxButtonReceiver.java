package com.ideasimproved.responable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PtxButtonReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent inIntent) {

		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("event", "true");
		context.startActivity(i);
	}
}
