package jp.hym.falingballgamge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

public class SensorValue{

	//public boolean isDiffDigree;

    private static final int MATRIX_SIZE = 16;
    float[]  inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[]    I = new float[MATRIX_SIZE];
    public float[] orientationValues   = new float[3];
    float[] orientationValuesBB   = new float[3];
    public float[] magneticValues      = new float[3];
    public float[] accelerometerValues = new float[3];

    final int displayOrientation;

    public SensorValue(int displayOrientation){
        this.displayOrientation = displayOrientation;
    }



    public void onSensorChanged(SensorEvent event){
    	//信頼できない値は無視
    	//と言いたい所だが、Nexusだとこればっかなので、全て通す。
		//if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

    	switch (event.sensor.getType()) {
	        case Sensor.TYPE_MAGNETIC_FIELD:
	            magneticValues = event.values.clone();
	            //Log.d("tets15", "mag");
	            break;
	        case Sensor.TYPE_ACCELEROMETER:
	            accelerometerValues = event.values.clone();
	            //Log.d("tets15", "acc");
	            break;
	    }

	    if (magneticValues != null && accelerometerValues != null) {

	        SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);

//	        //Activityの表示が縦固定の場合。横向きになる場合、修正が必要です
//	        if(isDiffDigree){
//	        	SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
//	        }else{
//	        	SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, outR);
//	        }
//	        SensorManager.remapCoordinateSystem(inR,  SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
	        
	        
	        
	        ////
	        

//	        // ディスプレイの回転方向を求める(縦もちとか横持ちとか)
//	        Display disp = this.getWindowManager().getDefaultDisplay();
//	        // ↓コレを使うためにはAPIレベルを8にする必要がある
//	        int dispDir = disp.getRotation();
//	 
//	        // 画面回転してない場合はそのまま
//	        if (dispDir == Surface.ROTATION_0) {
//	            SensorManager.getOrientation(outR, orientationValuesBB);
//	 
//	            // 回転している
//	        } else {
//	 
//	            float[] outR = new float[16];
//	 
//	            // 90度回転
//	            if (dispDir == Surface.ROTATION_90) {
//	                SensorManager.remapCoordinateSystem(
//	                        rotate, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR);
//	                // 180度回転
//	            } else if (dispDir == Surface.ROTATION_180) {
//	                float[] outR2 = new float[16];
//	 
//	                SensorManager.remapCoordinateSystem(
//	                        rotate, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR2);
//	                SensorManager.remapCoordinateSystem(
//	                        outR2, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR);
//	                // 270度回転
//	            } else if (dispDir == Surface.ROTATION_270) {
//	                SensorManager.remapCoordinateSystem(
//	                        outR, SensorManager.AXIS_MINUS_Y,SensorManager.AXIS_MINUS_X, outR);
//	            }
//	            SensorManager.getOrientation(outR, orientationValuesBB);
//	        }
	        

            if(displayOrientation == Surface.ROTATION_0) {
                SensorManager.remapCoordinateSystem(inR,  SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            }
            else if(displayOrientation == Surface.ROTATION_90) {
                SensorManager.remapCoordinateSystem(inR,  SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
            }
            else if(displayOrientation == Surface.ROTATION_180) {
                SensorManager.remapCoordinateSystem(inR,  SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, outR);
            }
            else if(displayOrientation == Surface.ROTATION_270) {
                SensorManager.remapCoordinateSystem(inR,  SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, outR);
            }

            SensorManager.getOrientation(outR, orientationValuesBB);


			//今回と前回の差を計算
			float[] subst = new float[3];
			subst[0] = Math.abs( orientationValuesBB[0] - orientationValues[0] );
			subst[1] = Math.abs( orientationValuesBB[1] - orientationValues[1] );
			subst[2] = Math.abs( orientationValuesBB[2] - orientationValues[2] );
//			float subdist_s = subst[0] * subst[0] + subst[1] * subst[1] + subst[2] * subst[2];

			for(int i=0;i<3;i++){
				final float rad1 = (float)Math.toRadians(1);
				float w = 0.1f;
				if( subst[i] > rad1 * 45 ) w = 1.0f;
//				else if( subdist_s > rad1 * rad1 * 60 ) w = 0.2f;
//				else if( subdist_s > rad1 * rad1 *  5 ) w = 0.3f;
//				else w = 0.1f;
				orientationValues[i] = orientationValuesBB[i] * w + orientationValues[i] * (1.0f-w);
				}
//			orientationValues[0] = orientationValuesBB[0] * w + orientationValues[0] * (1.0f-w);
//			orientationValues[1] = orientationValuesBB[1] * w + orientationValues[1] * (1.0f-w);
//			orientationValues[2] = orientationValuesBB[2] * w + orientationValues[2] * (1.0f-w);

//			final float overv = (float)Math.toRadians(5);
//			float subdist = subst[0] * subst[0] + subst[1] * subst[1] + subst[2] * subst[2];
//			float w = 1;
////			if( subdist > overv*overv ){
////				orientationValues[0] += subst[0] * w;
////				orientationValues[1] += subst[1] * w;
////				orientationValues[2] += subst[2] * w;
////			}
//
			/*
	        Log.d("test8", String.format("Orientation: z=%.1f, x=%.1f, Y=%.1f",
	        				Math.toDegrees(orientationValues[0]),	//Z軸方向,azimuth
	        				Math.toDegrees(orientationValues[1]),	//X軸方向,pitch
	        				Math.toDegrees(orientationValues[2])	//Y軸方向,roll
	        	
	        	));
	        */
	    }

    }


    public float getRoll(){
    	return orientationValues[2];
    }
    public float getPitch(){
    	return orientationValues[1];
    }
    public float getAzimuth(){
    	return orientationValues[0];
    }
    public float getRollDegree(){
    	return (float) Math.toDegrees(getRoll());
    }
    public float getPitchDegree(){
    	return (float) Math.toDegrees(getPitch());
    }
    public float getAzimuthDegree(){
    	return (float) Math.toDegrees(getAzimuth());
    }
}
