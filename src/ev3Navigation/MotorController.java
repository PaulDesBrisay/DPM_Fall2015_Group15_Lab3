package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;

public class MotorController {
	
	private EV3LargeRegulatedMotor leftMotor,
	rightMotor;
	
	private int motorSpeed;
	
	public MotorController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int motorSpeed){
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.motorSpeed=motorSpeed;
	}

}
