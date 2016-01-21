package com.sunnywong.flashlight;


import android.app.Activity;
import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ToggleButton;

//FlashlightActivity is a class that inherits from Activity (Activity is superclass)
public class FlashlightActivity extends Activity implements OnClickListener{
	
    private static Camera camera;
    private static Parameters parameters;
    private ToggleButton onOff;
    //SURFACE STUFF
    public SurfaceView mSurfaceView;
    public SurfaceHolder mSurfaceHolder;
    
    //Override Activity method 
	@Override
    public void onCreate(Bundle savedInstanceState) {

		//Super lets you access the method of parent Flashlight Activity, NOT Activity
        super.onCreate(savedInstanceState);
        
        //Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		   
        //define your UI from layout resource 
        setContentView(R.layout.main);                
        
        onOff = (ToggleButton) this.findViewById(R.id.toggleButton_on_off);
        onOff.setOnClickListener(this);
        onOff.setChecked(false);   
        
    }
	// SURFACE STUFF 
	mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
	mSurfaceHolder = mSurfaceView.getHolder();
	mSurfaceHolder.addCallback(this);
	
	Parameters parameters = mCamera.getParameters();
	parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
	mCamera.setParameters(parameters);

	
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	
	@Override
    protected void onPause() {
        super.onPause();
        /**
         * Release Camera 
         */		
        if (camera != null) {            
            camera.release();
            camera = null;
        }
        onOff.setChecked(false);  
    }
	
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        /**
	         * Check if light was on when App was paused. If so, turn back on. 
	         */
            if(parameters != null && parameters.getFlashMode()==Parameters.FLASH_MODE_TORCH){
				if (camera == null)
					camera = Camera.open();
				camera.setParameters(parameters);
				onOff.setChecked(true);
			}	      
	    }
	 
	private void setFlashOn(){
		if (camera == null)
			camera = Camera.open();			
			parameters = camera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);		
			camera.setParameters(parameters);
	}
	
	private void setFlashOff(){		
		parameters = camera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);		
		camera.setParameters(parameters);
	}

	//SOUND
	private void playSound() {
		MediaPlayer player = MediaPlayer.create(this, R.raw.flashlight_switch);
		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();	
			}
		});	
	}
		
	@Override
	public void onClick(View view) {
	//VIBRATION
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);
		
		playSound();
		
		switch(view.getId()){
			case R.id.toggleButton_on_off:
				if(onOff.isChecked()){
					setFlashOn();
				}else{					
					setFlashOff();
				}
			break;
		}
	}
	
	//SURFACE 
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	///SURFACE 

}
