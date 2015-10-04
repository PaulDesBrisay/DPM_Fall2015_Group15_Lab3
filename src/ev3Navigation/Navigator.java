package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ev3Navigation.Odometer;

public class Navigator{

	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	private double trackRadius, wheelRadius;
	


	private int motorSpeed;
	private ObstacleAvoider avoider;
	
	//for isNavigating method
	private boolean isNavigating =false;
	
	//constants
	private final double PI = Math.PI;
	private static final int TRAVEL_PERIOD = 100, 
			TARGET_BUFFER=2;
	
	//constructor
	public Navigator(Odometer odometer, EV3LargeRegulatedMotor leftMotor, 
			EV3LargeRegulatedMotor rightMotor, 
			double wheelRadius, double trackRadius,
			int motorSpeed, ObstacleAvoider avoider){
		this.odometer=odometer;
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.trackRadius =trackRadius;
		this.wheelRadius = wheelRadius;
		this.motorSpeed=motorSpeed;
		this.avoider = avoider;
		
		rightMotor.setSpeed(motorSpeed);
		leftMotor.setSpeed(motorSpeed);
	}
	
	public void travelTo(double x, double y){
		long travelStart, travelEnd;
		
		//travel until the robot arrives at it's destination
		while(Math.abs(x-odometer.getX())>TARGET_BUFFER || Math.abs(y-odometer.getY())>TARGET_BUFFER){
			
			travelStart = System.currentTimeMillis();
			
			//Check if robot sees a block-> if it does run avoider program
			if(!avoider.getSeeWall()){
				double deltaX=x-odometer.getX(), deltaY=y-odometer.getY(); 
				turnTo(Math.atan(deltaX/deltaY));
				//because turnTo sets isNavigating to FALSE
				isNavigating=true;

				
				leftMotor.setSpeed(motorSpeed);
				rightMotor.setSpeed(motorSpeed);
				leftMotor.forward();
				rightMotor.forward();
				//System.out.println("MOVE BITCH, GET OUT THE WAY");
				
				//sleep the thread
				travelEnd = System.currentTimeMillis();
				if (travelEnd - travelStart < TRAVEL_PERIOD) {
					try {
						Thread.sleep(TRAVEL_PERIOD - (travelEnd - travelStart));
					} catch (InterruptedException e) {
						// SHould be no interruptions
					}
				}
	
			}
		}
		isNavigating=false;
	}
	
	public void turnTo(double theta){
		
		isNavigating=true;
		double oldTheta = Math.toRadians(odometer.getTheta());
		double deltaTheta = theta-oldTheta;
		if(deltaTheta == 0){
			isNavigating=false;
			return;
		}
		
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
		
		leftMotor.setSpeed(motorSpeed);
		rightMotor.setSpeed(motorSpeed);
		
		leftMotor.rotate((int)Math.toDegrees(wheelTheta), true);
		rightMotor.rotate(-(int)Math.toDegrees(wheelTheta));
		//System.out.println("Turning to "+(int)Math.toDegrees(wheelTheta));
		//rightMotor.flt();
		//leftMotor.flt();
		
		isNavigating=false;
	}
	
	
	public boolean isNavigating(){
		return isNavigating;	
	}
}

