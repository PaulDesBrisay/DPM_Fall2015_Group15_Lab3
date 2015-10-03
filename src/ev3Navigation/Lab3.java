package ev3Navigation;

import ev3Navigation.Odometer;
import ev3Navigation.OdometryDisplay;
import ev3Navigation.Navigator;
import ev3Odometer.SquareDriver;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;


public class Lab3 {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3ColorSensor sensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));

	// Constants
	public static final double WHEEL_RADIUS = 2.2;
	public static final double TRACK = 5.73;

	public static void main(String[] args) {
		int buttonChoice;
		// some objects that need to be instantiated
		final TextLCD t = LocalEV3.get().getTextLCD();
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		
	    Odometer odometer = new Odometer(leftMotor, rightMotor,
				WHEEL_RADIUS, TRACK);
	    Navigator navigator = new Navigator(odometer, leftMotor, rightMotor,
	    		WHEEL_RADIUS, TRACK);
	
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left   | Right >", 0, 0);
			t.drawString("         |        ", 0, 1);
			t.drawString("Waypoints|avoid   ", 0, 2);
			t.drawString("         |obstacle", 0, 3);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		odometer.start();
		odometryDisplay.start();
		
		//Initial waypoint program
		if (buttonChoice == Button.ID_LEFT) {
					navigator.travelTo(60, 30);
					navigator.travelTo(30, 30);
					navigator.travelTo(30, 60);
					navigator.travelTo(60, 0);
			
			
		} else {
			//Need to implement an obstacle correction program
			//similar to wall follower
			navigator.travelTo(0, 60);
			navigator.travelTo(60, 0);

		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
	
