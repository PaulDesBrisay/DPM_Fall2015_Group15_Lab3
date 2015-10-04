package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

//Basic object that can be used for all labs that controls the motors
//useful for a multi-threaded code where multiple threads access the motors
public class MotorController {
	
	private EV3LargeRegulatedMotor leftMotor,
	rightMotor;
	private int motorSpeed=200;
	
	public MotorController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int motorSpeed){
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.leftMotor.resetTachoCount();
		this.rightMotor.resetTachoCount();
		this.motorSpeed=motorSpeed;
		setSpeed(motorSpeed);
	}
	
	public MotorController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.leftMotor.resetTachoCount();
		this.rightMotor.resetTachoCount();
	}
	
	public MotorController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int motorSpeed, int acceleration){
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.leftMotor.resetTachoCount();
		this.rightMotor.resetTachoCount();
		this.motorSpeed=motorSpeed;
		setSpeed(motorSpeed);
		setAcceleration(acceleration);
	}
	
	public void setLeftMotorSpeed(int motorSpeed){
		this.leftMotor.setSpeed(motorSpeed);
	}
	
	public void setRightMotorSpeed(int motorSpeed){
		this.rightMotor.setSpeed(motorSpeed);
	}
	
	public void setSpeed(int motorSpeed){
		setLeftMotorSpeed(motorSpeed);
		setRightMotorSpeed(motorSpeed);
	}
	
	public void resetSpeed(){
		setSpeed(this.motorSpeed);
	}
	
	public void turnRad(double left, double right){
		turnDegrees((int)Math.toDegrees(left), (int)Math.toDegrees(right));
	}
	
	public void turnDegrees(int left, int right){
		fltBoth();
		this.leftMotor.rotate(left, true);
		this.rightMotor.rotate(right);
		fltBoth();
	}
	
	public void fltBoth(){
		this.leftMotor.flt();
		this.rightMotor.flt();
	}
	
	public void setAcceleration(int acceleration){
		this.leftMotor.setAcceleration(acceleration);
		this.rightMotor.setAcceleration(acceleration);
	}
	
	public int[] getTacho(){
		return new int[]{leftMotor.getTachoCount(), rightMotor.getTachoCount()};
	}
	
	public void forward(){
		leftMotor.forward();
		rightMotor.forward();
	}
}
