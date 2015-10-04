package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

public class ObstacleAvoider extends Thread {
	
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	private EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
	
	private boolean seeWall=false;
	private SampleProvider usDistance;
	private int motorSpeed;
	
	//constants
	private static final int CHECK_PERIOD=25,
			WALL_DIST = 30, 
			fastMotorSpeed=450, 
			slowMotorSpeed =30,
			bufferZone = 4;
	
	public ObstacleAvoider(EV3LargeRegulatedMotor leftMotor, 
			EV3LargeRegulatedMotor rightMotor, int motorSpeed){
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.motorSpeed = motorSpeed;
		rightMotor.flt();
		leftMotor.flt();
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
				leftMotor.setSpeed(motorSpeed);         
	            rightMotor.setSpeed((int) (motorSpeed) );
			}
			else if(distance>WALL_DIST+bufferZone){
				seeWall=true;
				rightMotor.setSpeed((int)(fastMotorSpeed));         
	            leftMotor.setSpeed((int) (slowMotorSpeed) );
			}
			else if(distance>WALL_DIST-bufferZone && distance<WALL_DIST+bufferZone){
				seeWall=true;
				leftMotor.setSpeed(motorSpeed);         
	            rightMotor.setSpeed((int) (motorSpeed) );
			}
			else{
				seeWall=true;
				leftMotor.setSpeed((int)(fastMotorSpeed));         
	            rightMotor.setSpeed((int) (slowMotorSpeed) );
			}
			
			if(seeWall){
				leftMotor.forward();
				rightMotor.forward();
			}
			
			checkEnd =System.currentTimeMillis();
			if (checkEnd - checkStart < CHECK_PERIOD) {
				try {
					Thread.sleep(CHECK_PERIOD - (checkEnd - checkStart));
				} catch (InterruptedException e) {
					// Should be no interruptions
				}
			}
		}
	}
	
	
	
	public boolean getSeeWall(){
		return seeWall;
	}

}
