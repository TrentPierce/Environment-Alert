package com.PierceHoldings.humiditycutoff;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class EnvironmentCheckerActivity extends Activity implements OnClickListener, SensorEventListener {
	
	
	private Button humidityBtn;
	private Button stopbtn;
	private TextView ambientValue, humidityValue;
	private TextView[] valueFields = new TextView[4];
	private final int AMBIENT=0;
	private final int HUMIDITY=3;
	private SensorManager senseManage;
	private Sensor envSense;
	final Context context = this;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
       
        humidityBtn = (Button)findViewById(R.id.humidity_btn);
        stopbtn = (Button)findViewById(R.id.stop_btn);
        
        
       
        humidityBtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        
        ambientValue = (TextView)findViewById(R.id.ambient_text);
        valueFields[AMBIENT]=ambientValue;
        humidityValue = (TextView)findViewById(R.id.humidity_text);
        valueFields[HUMIDITY]=humidityValue;
        
        senseManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }
    
    public void onClick(View v) {
    		
    	 if(v.getId()==R.id.humidity_btn) {
    		 envSense = senseManage.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
     		if(envSense==null)
         		Toast.makeText(this.getApplicationContext(), "Sorry - your device doesn't have an " +
         				"ambient temperature sensor!", Toast.LENGTH_SHORT).show();
     		else
         		senseManage.registerListener(this, envSense, SensorManager.SENSOR_DELAY_FASTEST);
     		
    	 
    		//humidity
    			envSense = senseManage.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            	
            	if(envSense==null)
            		Toast.makeText(this.getApplicationContext(), "Sorry - your device doesn't have a " +
            				"humidity sensor!", Toast.LENGTH_SHORT).show();
            	else
            		senseManage.registerListener(this, envSense, SensorManager.SENSOR_DELAY_FASTEST);
    		}
    	 if(v.getId()==R.id.stop_btn) {  
       		 Toast StopToast = Toast.makeText(this.getApplicationContext(), "Stopping Service", Toast.LENGTH_SHORT);
            	StopToast.show();
       		 envSense=null;
       	     senseManage.unregisterListener(this);
       	   	    }

    }
    
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    	//find out the accuracy
    	String accuracyMsg = "";
    	switch(accuracy){
    	case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
    		accuracyMsg="Sensor has high accuracy";
    		break;
    	case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
    		accuracyMsg="Sensor has medium accuracy";
    		break;
    	case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
    		accuracyMsg="Sensor has low accuracy";
    		break;
    	case SensorManager.SENSOR_STATUS_UNRELIABLE:
    		accuracyMsg="Sensor has unreliable accuracy";
    		break;
    	default:
    		break;
    	}
    	//output it
    	Toast accuracyToast = Toast.makeText(this.getApplicationContext(), accuracyMsg, Toast.LENGTH_SHORT);
    	accuracyToast.show();
    }
    
    @Override
    public final void onSensorChanged(SensorEvent event) {
    	//retrieve sensor information
    	float sensorValue = event.values[0];
    	TextView currValue = humidityValue;
    	String envInfo="";
    	if(event.values[0] >= 32){
            System.out.println("Changed");
            
            final int NOTIF_ID = 1234;
            NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification note = new Notification(R.drawable.ic_launcher, "Environment Alert", System.currentTimeMillis());
            PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, EnvironmentCheckerActivity.class), 0);
            note.setLatestEventInfo(this, "I have detected unfavorable conditions for your phone. Shutdown now to be safe.", envInfo, intent);
            notifManager.notify(NOTIF_ID, note);
            // notifManager.cancel(NOTIF_ID);
            
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
    				context);
     
    			// set title
    			alertDialogBuilder.setTitle("Conditions are unfavorable for your phone");
     
    			// set dialog message
    			alertDialogBuilder
    				.setMessage("Click yes to shutdown now!")
    				.setCancelable(false)
    				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						// if this button is clicked, close
    						// current activity
    						EnvironmentCheckerActivity.this.finish();
    						
    						PowerManager.goToSleep()
    					}
    				  })
    				.setNegativeButton("Dismiss",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						// if this button is clicked, just close
    						// the dialog box and do nothing
    						dialog.cancel();
    					}
    				});
     
    				// create alert dialog
    				AlertDialog alertDialog = alertDialogBuilder.create();
     
    				// show it
    				alertDialog.show();
    			}
    	 
    	 
    	//check type
    	int currType=event.sensor.getType();
    	switch(currType){
        case Sensor.TYPE_AMBIENT_TEMPERATURE:
      	  envInfo=sensorValue+" degrees Celsius";
      	  currValue=valueFields[AMBIENT];
      	  break;
        case Sensor.TYPE_RELATIVE_HUMIDITY:
      	  envInfo=sensorValue+" percent humidity";
      	  currValue=valueFields[HUMIDITY];
      	  break;
      	  default: break;
        }
    	//output and reset
    	currValue.setText(envInfo);
    
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        envSense = senseManage.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
 		if(envSense==null)
     		Toast.makeText(this.getApplicationContext(), "Sorry - your device doesn't have an " +
     				"ambient temperature sensor!", Toast.LENGTH_SHORT).show();
 		else
     		senseManage.registerListener(this, envSense, SensorManager.SENSOR_DELAY_FASTEST);
 		
	 
		//humidity
			envSense = senseManage.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        	
        	if(envSense==null)
        		Toast.makeText(this.getApplicationContext(), "Sorry - your device doesn't have a " +
        				"humidity sensor!", Toast.LENGTH_SHORT).show();
        	else
        		senseManage.registerListener(this, envSense, SensorManager.SENSOR_DELAY_FASTEST);
    }

	public static int myPid() {
		// TODO Auto-generated method stub
		return 0;
	}
}
