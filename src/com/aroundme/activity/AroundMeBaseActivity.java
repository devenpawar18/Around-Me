package com.aroundme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.aroundme.ApplicationEx;
import com.aroundme.R;

public class AroundMeBaseActivity extends Activity {
	protected static final float SHAKE_THRESHOLD = 1000;
	private SensorManager sensorManager;
	private AlertDialog shakeDialog;
	private final int SHAKE_DIALOG = 3;
	private boolean isOpenDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		boolean isSensorAvailable = sensorManager.registerListener(
				sensorListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		if (!isSensorAvailable) {
			sensorManager.unregisterListener(sensorListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		}
	}

	private final SensorEventListener sensorListener = new SensorEventListener() {
		private float last_x;
		private float last_y;
		private float last_z;
		private long lastUpdate;

		public void onSensorChanged(SensorEvent se) {
			if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				long curTime = System.currentTimeMillis();
				// only allow one update every 100ms.
				if ((curTime - lastUpdate) > 100) {
					long diffTime = (curTime - lastUpdate);
					lastUpdate = curTime;

					float x = se.values[SensorManager.DATA_X];
					float y = se.values[SensorManager.DATA_Y];
					float z = se.values[SensorManager.DATA_Z];

					float speed = Math
							.abs(x + y + z - last_x - last_y - last_z)
							/ diffTime * 10000;

					if (speed > SHAKE_THRESHOLD) {
						if (!isOpenDialog) {
							// if(listDialog!=null)
							// isOpenDialog=false;
							// listDialog.dismiss();
							// }else{
							isOpenDialog = true;
							showDialog(SHAKE_DIALOG);
						}
					}
					last_x = x;
					last_y = y;
					last_z = z;
				}
			}

		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHAKE_DIALOG:
			isOpenDialog = false;
			AlertDialog.Builder shakeBuilder = new AlertDialog.Builder(this);
			String message = "You are at: " + ApplicationEx.myLocation;
			shakeBuilder.setTitle(R.string.select_where);
			shakeBuilder.setMessage(message);
			shakeDialog = shakeBuilder.create();
			shakeDialog.setButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					isOpenDialog = false;
					shakeDialog.dismiss();
				}
			});
			return shakeDialog;
		default:
		}
		return super.onCreateDialog(id);
	}
}
