package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ev3Navigation.Odometer;

public class Navigator extends Thread{

	private Odometer odometer;
	
	private double trackRadius, wheelRadius;
	
	private MotorController motorControl;

	private ObstacleAvoider avoider;
	
	//for isNavigating method
	private boolean isNavigating =false;
	
	//to avoid constant oscillations
	private double oldTheta=0;
	
	//constants
	private final double PI = Math.PI;
	private static final int TRAVEL_PERIOD = 100, 
			TARGET_BUFFER=3;
	
	
	//constructor
	public Navigator(Odometer odometer, 
			double wheelRadius, double trackRadius,
			ObstacleAvoider avoider, MotorController motorControl){
		this.odometer=odometer;
		this.trackRadius =trackRadius;
		this.wheelRadius = wheelRadius;
		this.avoider = avoider;
		this.motorControl=motorControl;
	}
	
	
	public void run(double[][] coordinates){
		for(int i=0; i<coordinates.length; i++){
			travelTo(coordinates[i][0], coordinates[i][1]);
		}
	}
	
	public void travelTo(double x, double y){
		long travelStart, travelEnd;
		sleep(TRAVEL_PERIOD);
		
		double deltaX=x-odometer.getX(), deltaY=y-odometer.getY(); 
		double newAngle= Math.atan(deltaX/deltaY);
		turnTo(newAngle);
		isNavigating=true;
		//travel until the robot arrives at it's destination
		while(isNavigating){
			if (Math.abs(x-odometer.getX())<TARGET_BUFFER 
					&& Math.abs(y-odometer.getY())<TARGET_BUFFER) isNavigating=false; 
			travelStart = System.currentTimeMillis();
			
			//Check if robot sees a block-> will only see a block if avoider is running
			if(!avoider.getSeeWall()){
				deltaX=x-odometer.getX();
				deltaY=y-odometer.getY(); 
				newAngle= Math.atan(deltaX/deltaY);
				if(Math.abs(this.oldTheta-newAngle)>(PI/18)&&(deltaX>TARGET_BUFFER&&deltaY>TARGET_BUFFER)){
					turnTo(newAngle);
					//because turnTo sets isNavigating to FALSE
					isNavigating=true;
					
				}
				motorControl.resetSpeed();
				motorControl.forward();
			}
			//sleep Thread
			travelEnd = System.currentTimeMillis();
			if (travelEnd - travelStart < TRAVEL_PERIOD) {
				sleep(TRAVEL_PERIOD - (int)(travelEnd - travelStart));
	
			}
		}
		motorControl.fltBoth();
		sleep(TRAVEL_PERIOD);
		
	}
	
	public void turnTo(double theta){
		
		isNavigating=true;
		double oldTheta = Math.toRadians(odometer.getTheta());
		this.oldTheta=oldTheta;
		double deltaTheta = theta-oldTheta;
		
		//While loop to ensure that the rotation is minimal
		//2¹ periodic=> min distance is from -¹ to ¹
		//ensures that the angle is inserted in the correct range
		//positive minimum rotation: 0<= theta<=¹
		//negative minimum rotation: 0>theta>=-¹
		//for the case that theta=¹ or -¹ it doesn't matter which way it turns
		
		while(deltaTheta>PI||deltaTheta<-PI){
			if(deltaTheta>PI) deltaTheta-=(2*PI);
			else if(deltaTheta<-PI) deltaTheta+=(2*PI);
		}
		
		
		double wheelTheta = (deltaTheta*trackRadius)/wheelRadius;
		
		motorControl.resetSpeed();
		motorControl.turnRad(wheelTheta, -wheelTheta);
		
		isNavigating=false;
	}
	
	private void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {}
	}
	
	public boolean isNavigating(){
		return isNavigating;	
	}
}

