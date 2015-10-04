package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ev3Navigation.Odometer;

public class Navigator{

	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	private double trackRadius, wheelRadius;
	
	private static final int motorSpeed = 200;
	private static final int motorAcceleration = 2000;
	
	
	//for isNavigating method
	private boolean isNavigating =false;
	
	//I'm lazy and don't want to write Math.PI each time
	//plus the TA said to write all constants at the top
	private final double PI = Math.PI;
	
	//constructor
	public Navigator(Odometer odometer, EV3LargeRegulatedMotor leftMotor, 
			EV3LargeRegulatedMotor rightMotor, double wheelRadius, double trackRadius){
		this.odometer=odometer;
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.trackRadius =trackRadius;
		this.wheelRadius = wheelRadius;
		
		rightMotor.setSpeed(motorSpeed);
		leftMotor.setSpeed(motorSpeed);
		
		rightMotor.setAcceleration(motorAcceleration);
		leftMotor.setAcceleration(motorAcceleration);
		
	}
	
	public void travelTo(double x, double y){
		double deltaX=x-odometer.getX(), deltaY=y-odometer.getY();
		turnTo(Math.atan(deltaX/deltaY));
		isNavigating=true;
		
		//wheel rotation= travel distance/wheel radius
		//travel distance = Ã(x^2+y^2)
		double wheelTheta = Math.sqrt(Math.pow(deltaX, 2)
				+Math.pow(deltaY, 2))/wheelRadius;

		turnMotors(wheelTheta, wheelTheta);
		
		//Will only call after wheels are done rotating due to rightMotor blocking
		//Ensures that the robot will go to coordinates with a range of 3
		//range is to avoid oscillations
		// Useful if the robot has to avoid an obstacle in it's path
		if(Math.abs(x-odometer.getX())>3 || Math.abs(y-odometer.getY())>3){
			travelTo(x, y);
		}
		isNavigating=false;
	}
	
	public void turnTo(double theta){
		
		isNavigating=true;
		double oldTheta = Math.toRadians(odometer.getTheta());
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
		turnMotors(wheelTheta, -wheelTheta);
		/*leftMotor.flt();
		rightMotor.flt();
		leftMotor.rotate((int)Math.toDegrees(wheelTheta), true);
		rightMotor.rotate(-(int)Math.toDegrees(wheelTheta));
		System.out.println("angle is "+wheelTheta);*/
		isNavigating=false;
	}
	
	//method to turn motors x and y degrees
	private void turnMotors(double left, double right){
		//leftMotor.flt();
		//rightMotor.flt();
		//System.out.println("The angle is "+ (int)Math.toDegrees(right));
		leftMotor.rotate((int)Math.toDegrees(left), true);
		rightMotor.rotate((int)Math.toDegrees(right));
	}
	
	public boolean isNavigating(){
		return isNavigating;	
	}
}

