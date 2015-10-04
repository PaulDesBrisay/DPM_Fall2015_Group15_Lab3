package ev3Navigation;

import ev3Navigation.Odometer;
import ev3Navigation.OdometryDisplay;
import ev3Navigation.Navigator;
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

	// Constants
	public static final double WHEEL_RADIUS = 2.2;
	public static final double TRACK = 5.73;
	public static final int motorSpeed =200, motorAcceleration=1500;;

	public static void main(String[] args) {
		int buttonChoice;
		// some objects that need to be instantiated
		final TextLCD t = LocalEV3.get().getTextLCD();
		
		MotorController mControl = new MotorController(leftMotor, rightMotor, 
				motorSpeed, motorAcceleration);
		
	    Odometer odometer = new Odometer(mControl,
				WHEEL_RADIUS, TRACK);
	   final ObstacleAvoider avoider = new ObstacleAvoider(mControl);
	    final Navigator navigator = new Navigator(odometer,
	    		WHEEL_RADIUS, TRACK, avoider, mControl);
	
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
			(new Thread() {
				public void run() {
					navigator.run(new double[][]{new double[]{60.0,30.0},
							new double[]{30.0,30.0},
							new double[]{30.0,60.0},
							new double[]{60.0,0.0}});
				}
			}).start();

			
		} else {
			
			(new Thread() {
				public void run() {
					navigator.run(new double[][]{new double[]{0.0,60.0},
							new double[]{60.0,0.0}});
					avoider.run();
				}
			}).start();
			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
	
