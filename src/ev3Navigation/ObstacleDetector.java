package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

public class ObstacleDetector extends Thread {
	
	private EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
	
	private MotorController motorControl;
	
	private boolean seeWall=false;
	private SampleProvider usDistance;
	
	
	//constants
	private static final int CHECK_PERIOD=25,
			WALL_DIST = 25;
	
	public ObstacleDetector(MotorController motorControl){
		this.motorControl=motorControl;
		usDistance = usSensor.getMode("Distance");
		
	}
	
	public void run(){
		int distance;
		float[] usData = new float[usDistance.sampleSize()];
		long checkStart, checkEnd;
		while(true){
			checkStart=System.currentTimeMillis();
			usSensor.fetchSample(usData,0);
			distance=(int)(usData[0]*100.0);
			
			//can add a filter here
			//also add methods to get distance 
			//so this class has the only interaction with the US
			
			//check if distance<buffer
			//if so, set boolean to true
			//if it's the first time, set flt motors
			
			if(distance<WALL_DIST){
				setSeeWall(true);
				//longer sleep to ensure navigator thread avoider is triggered
				try {
					Thread.sleep(3*CHECK_PERIOD);
				} catch (InterruptedException e) {
				}
			}
			else setSeeWall(false);
			
			checkEnd =System.currentTimeMillis();
			if (checkEnd - checkStart < CHECK_PERIOD) {
				try {
					Thread.sleep(CHECK_PERIOD - (checkEnd - checkStart));
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	private void setSeeWall(boolean bool){
		if(seeWall==bool) return;
		else{
			if(bool) motorControl.fltBoth();
			seeWall=bool;
		}
	}
	
	public boolean getSeeWall(){
		return seeWall;
	}

}
