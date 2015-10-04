package ev3Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;
	
	private double lastTrachoLeft=0, lastTrachoRight=0;
	private double wheelRadius, botRadius;
	
	//motors to get the tracho readings
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;

	public Odometer(){
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		lock = new Object(); 
	}

	public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double wheelRadius, double botRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		lastTrachoLeft = this.leftMotor.getTachoCount();
		lastTrachoRight = this.rightMotor.getTachoCount();
		this.wheelRadius = wheelRadius;
		this.botRadius=botRadius;
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
	}

	//change this to run on a timer
	
	// run method (required for Thread)
	
	//Methods that have been changed/created
	public void run() {
		long updateStart, updateEnd;
		int currentTrachoLeft = 0, currentTrachoRight=0;
		
		//new x y and theta are all based off the difference between 
		//the trachometer readings of the left and right motor
		//since the last period
		//Ædistance = avg of left and right distance
		//Ætheta = difference between left and right trachos
		//Æx and Æy = Ædistance * cos and sin of Ætheta
		
		while (true) {
			updateStart = System.currentTimeMillis();
			
			//get tracho readings
			currentTrachoLeft = leftMotor.getTachoCount();
			currentTrachoRight=rightMotor.getTachoCount();
			
			//compute deltaTrachos
			double deltaTrachoLeft = currentTrachoLeft - this.lastTrachoLeft,
					deltaTrachoRight = currentTrachoRight - this.lastTrachoRight;
			
			// update the lastTrachoValues 
			this.lastTrachoLeft = currentTrachoLeft; 
			this.lastTrachoRight = currentTrachoRight; 
			
			//get length of change vector
			double deltaPos = getDeltaPos(deltaTrachoLeft, deltaTrachoRight);
			
			//get angle of change vector
			double deltaTheta=getDeltaTheta(deltaTrachoLeft, deltaTrachoRight);
			
			// updating theta
			//and x and y using sin and cos
			synchronized (lock) {
				double deltaThetaRadians  = Math.toRadians(deltaTheta);
				this.theta -=deltaThetaRadians;
				if(this.theta>Math.PI*2) this.theta-=2*Math.PI;
				if(this.theta<0) this.theta+=2*Math.PI;
				x+= deltaPos*Math.sin(theta);
				y+= deltaPos*Math.cos(theta);
			}
			
			//last tracho readings are now the current tracho readings
			lastTrachoLeft = currentTrachoLeft;
			lastTrachoRight = currentTrachoRight;
			
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}
	private double getDeltaPos(double deltaTrachoLeft, double deltaTrachoRight){
		double deltaPosLeft = rotationToDistance(deltaTrachoLeft);
		double deltaPosRight = rotationToDistance(deltaTrachoRight);
		//using distance center of bot traveled = mean of wheels travel distance
		return (deltaPosLeft+deltaPosRight)/2;	
	}
	private double rotationToDistance(double deltaTheta){
		//using distance a point on the wheel rotated = distance traveled 
		//= (Pi*diameter)*(change in wheel angle/360)
		return Math.PI*wheelRadius*deltaTheta/180.00;
	}

	private double getDeltaTheta(double deltaLeft, double deltaRight){
		//positive theta is to the left of the bot
		//using angle rotated = angle of one wheel's arclength
		//System.out.println("The Theta Value is" + (deltaRight-deltaLeft)*wheelRadius/(2*botRadius));
		return ((deltaRight-deltaLeft)*wheelRadius/(2*botRadius));
	}

	
	
	
	
	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = Math.toDegrees(theta);
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = Math.toDegrees(theta);
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}
