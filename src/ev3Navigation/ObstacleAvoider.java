package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

public class ObstacleAvoider extends Thread {
	
	private EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
	
	private MotorController motorControl;
	
	private boolean seeWall=false;
	private SampleProvider usDistance;
	
	
	//constants
	private static final int CHECK_PERIOD=25,
			WALL_DIST = 20, 
			fastMotorSpeed=300, 
			slowMotorSpeed =50,
			bufferZone = 4;
	
	public ObstacleAvoider(MotorController motorControl){
		this.motorControl=motorControl;
		usDistance = usSensor.getMode("Distance");
		
	}
	
	public void run(){
		int distance;
		float[] usData = new float[usDistance.sampleSize()];
		long checkStart, checkEnd;
		while(true){
			checkStart=System.currentTimeMillis();
			usSensor.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);
			//if it is far from the obstacle assume no obstacle; run normal program
			if(distance>WALL_DIST+2*bufferZone){
				seeWall=false;
				motorControl.resetSpeed();
			}
			else if(distance>WALL_DIST&&distance<WALL_DIST+2*bufferZone){
				seeWall=true;
				motorControl.setLeftMotorSpeed(slowMotorSpeed);
				motorControl.setRightMotorSpeed(fastMotorSpeed);
			}
			else if(distance>WALL_DIST-bufferZone && distance<WALL_DIST+bufferZone){
				seeWall=true;
				motorControl.resetSpeed();
			}
			else{
				seeWall=true;
				motorControl.setLeftMotorSpeed(fastMotorSpeed);
				motorControl.setRightMotorSpeed(slowMotorSpeed);
			}
			
			if(seeWall){
				motorControl.forward();
			}
			
			checkEnd =System.currentTimeMillis();
			if (checkEnd - checkStart < CHECK_PERIOD) {
				try {
					Thread.sleep(CHECK_PERIOD - (checkEnd - checkStart));
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	
	
	public boolean getSeeWall(){
		return seeWall;
	}

}
