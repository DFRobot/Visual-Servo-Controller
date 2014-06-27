 /**********************************************************************************
 * This file is part of visual servo controller.                                   *
 *                                                                                 *
 * visual servo controller is free software: you can redistribute it and/or modify *
 * it under the terms of the GNU General Public License as published by            *
 * the Free Software Foundation, either version 3 of the License, or               *
 * (at your option) any later version.                                             *
 *                                                                                 *
 * visual servo controller is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU General Public License for more details.                                    *
 *                                                                                 *
 * You should have received a copy of the GNU General Public License               *
 * along with visual servo controller.  If not, see <http://www.gnu.org/licenses/>.*
 *                                                                                 *
 * Copyright (C) 2014 DFRobot                                                      *
 * DFRobot-A great source for opensource hardware and robot.                       *
 * official website:  http://www.dfrobot.com                                       *
 ***********************************************************************************/

/******************** start of Servo.pde ********************/
/*
 *    ServoList demo created by lisper <leyapin@gmail.com> 2013-11-30
 *    last time:    2013-12-5 14:33 by lisper
 *    class with beginAngle, endAngle and servoId, inherit a controlP5 controller
 *    class Servo extends Controller 
 *    method:
 *    Servo(ControlP5 cp5, String theName) 
 *    setBeginAngle (float theAngle)
 *    setEndAngle (float theAngle)
 *    setServoId (int theId)
 *    float getBeginAngle ()
 *    float getEndAngle ()
 *    int getServoId () 
 *    boolean inRange () : if mouse in a servo rannge
 *    boolean inLeft () 
 *    boolean inRigh
 */


//
//class Servo extends Controller implements Cloneable {
class Servo extends Controller {

	int currentColor = 0xaf888877;
	int defaultColor = 0xaf888877;  
	int defaultSideColor;
	int currentSideColor;
	int errorColor;
	int pressedColor = 0xaf888877;
	int moveColor    = 0xaf888877;
	int enterColor   = 0xaf888877;
	int sideColor    = 0xff666655;
	int leftColor ;//= 0xff00aaff;
	int rightColor = 0xffa050ff;
	int beginAngleColor;
	int endAngleColor;
	int servoIdColor;
	int enterSideColor;
	int currentLeftColor;
	int currentRightColor;
	boolean isShowSide;
	boolean isShowFocus;
	int radius = height/2;
	int y;
	int pressedx;
	int pressedy;
	float beginAngle = angleMin;
	float endAngle = angleMax;
	int servoId = 0;
	boolean isLeft = false;
	boolean isRight = false;
	int wheelAmount;
	int minWidth ; //= 50;
	int maxWidth ; //= maxX-minX;
	int allowHeight = 1000;
	boolean isShowTime;
	boolean isMove;

	int mouseNum = 0;
	long startTime = 0;

	int oldMove;

	// use the convenience constructor of super class Controller
	// Servo will automatically registered and move to the 
	// default controlP5 tab.
	MouseWheelEventClass wheel;

	Servo(ControlP5 cp5, String theName) {    //init a servo
		super(cp5, theName);
		isMove = true;
		setWidth( constrain (width, minWidth, maxWidth));
		wheel = new MouseWheelEventClass();

		int myY = int (getPosition().y);
		int posy;
		for (posy=0; posy<1000 && myY > posy; posy += height)
			;
		setPosition (getPosition ().x, posy);
		// replace the default view with a custom view.
		setView(new ControllerView() {

				public void display(PApplet p, Object b) {
				p.textFont (font);
				//currentSideColor = 0xff555555;
				if (isShowSide) {
				p.stroke (currentSideColor);
				} 
				else
				p.noStroke ();
				p.fill (currentColor);
				//p.rect(0, 0, getWidth(), getHeight(), radius); //the back rect
				p.image (mid_img, 20, 0, getWidth()-40, getHeight());
				p.noStroke ();
				p.fill (currentLeftColor);
				//p.rect (0, 0, 20, getHeight (), radius, 0, 0, radius); //left rect
				p.image (left_img, 0, 0);
				int wid = getWidth () - getWidth()/5*4;
				p.fill (currentRightColor);
				//p.rect (getWidth()-20, 0, 20, getHeight (), 0, radius, radius, 0); //right rect
				p.image (right_img, getWidth()-20, 0);
				p.stroke (0, 200, 255, 120);
				p.strokeWeight (1.5);
				p.line (20-1, 5, 20-1, height-5);
				p.line (width-20, 5, width-20, height-5);
				//* if (isShowSide) {
				//*     p.stroke (currentSideColor);
				//*     //p.noFill ();
				//*     p.strokeWeight (1);
				//*     p.rect(0, 0, getWidth(), getHeight(), radius); //the back rect
				//*                }
				//p.strokeWeight (0);
				if (isShowFocus) {
					p.noFill ();
					p.strokeWeight (1.5);
					p.stroke (0xff59fdfc);
					p.rect (0.75, 0.75, getWidth()-1.5, getHeight()-1.5, radius);
				}
				//p.fill (currentColor);
				//p.noStroke ();

				p.textAlign (CENTER);

				p.fill (beginAngleColor);
				translate(0, getHeight()/2+8/2);
				p.text (int(beginAngle), 10, 0);

				p.fill (endAngleColor);

				int myY = int (getPosition().y);
				int posy;
				for (posy=0; posy<1000 && myY > posy; posy+=height)
					;
				setPosition (getPosition ().x, posy);         
				p.text (int(endAngle), width-10, 0);

				p.fill (servoIdColor);
				//p.textSize (20);
				p.textAlign (CENTER);
				p.text (servoId, width/2, 0);



				/////////////////////////
				//* if (isShowTime) {
				//*  *	translate (0, -(getHeight()/2+8/2));
				//*  *	p.fill (0xaf004f00);
				//*  *	p.text (int (getPx1()), 0, -5);
				//*  *	p.text (int (getPx2()), width, -5);
				//* 	translate (0, (getHeight()/2+8/2));
				//* 	line (0, getPy1(), 0, maxY);
				//* 	line (0, getPy1 (), 0, maxY);

				//* 	text (getPx1 ()+"a", getPx1 (), maxY+10);
				//* 	text (getPx2 (), getPx2 (), maxY+10);
				//* }
				/////////////////////////
				}
		}
		);
	} // Servo ?

	public Object clone() {  
		Servo servo = null;  
		try {  
			servo = (Servo)super.clone();
		}
		catch(CloneNotSupportedException e) {  
			e.printStackTrace();
		}  
		return servo;
	}  


	// override various input methods for mouse input control
	void onEnter() {
		this.bringToFront();  //2013-12-12
		currentColor  = enterColor;
		currentLeftColor = enterSideColor;
		currentRightColor = enterSideColor;
		if (inLeft ()) {
			beginAngle += wheelAmount;
		} 
		else if (inRight ()) {
			endAngle += wheelAmount;
		}
		isShowTime = true;
	}

	//
	void onScroll(int n) {
		// y -= n;
		//y = constrain(y, 0, getHeight()-10);
	}

	//
	void onPress() {
		currentColor = pressedColor;
		Pointer p1 = getPointer();
		pressedx = p1.x();
		pressedy = p1.y();

		if (inLeft ()) {
			isLeft = true;
		} 
		else if (inRight ()) {
			isRight = true;
		}
	}

	//
	void onClick() {
		if (mouseButton == LEFT) {
			mouseNum ++;
			String inputValue = "";
			try {
				/*
				   if (mouseNum == 1) 
				   startTime = millis ();
				   if (millis () - startTime > 250) {
				   mouseNum = 1;
				   startTime = millis ();
				   }
				 */
				if (mouseNum == 1 || ( millis () - startTime > 250)) {  //start time     
					mouseNum = 1;
					startTime = millis ();
				}
				if (mouseNum == 2 && (millis() - startTime < 250)) { //if double click and in time
					mouseNum = 0;
					println (millis () - startTime);
					///////////////////////////////////////////////////
					if (inLeft ()) {
						inputValue = JOptionPane.showInputDialog("Input beginAngle ("+int(angleMin)+"~"+int(angleMax)+")");
						if (inputValue != null)
							beginAngle = constrain (Integer.parseInt (inputValue.trim ()), angleMin, angleMax);
					} 
					else if (inRight ()) {
						inputValue = JOptionPane.showInputDialog("Input endAngle ("+int(angleMin)+"~"+int(angleMax)+")");
						if (inputValue != null)
							endAngle = constrain (Integer.parseInt (inputValue.trim ()), angleMin, angleMax);
					}
					else {

						inputValue = JOptionPane.showInputDialog("Input servoId ("+beginServoId+"~"+endServoId+")");
						if (inputValue != null)
							servoId = constrain (Integer.parseInt (inputValue.trim ()), beginServoId, endServoId);
					}
				}
			}
			catch (NumberFormatException NFE) {
				println ("error! NumberFormatException : "+inputValue);
			}
		}

		isLeft = false;
		isRight = false;
		String inputValue = "";
		try {
			if (mouseButton == RIGHT) {
				if (inLeft ()) {
					inputValue = JOptionPane.showInputDialog("Input beginTime");
					if (inputValue != null)
						setX1 (Float.parseFloat (inputValue.trim ())*60+move);
				} 
				else if (inRight ()) {
					inputValue = JOptionPane.showInputDialog("Input endTime");
					if (inputValue != null)
						setX2(Float.parseFloat (inputValue.trim ())*60+50+move);
				}
				else {
					inputValue = JOptionPane.showInputDialog("Input servoId ("+beginServoId+"~"+endServoId+")");
					if (inputValue != null)
						servoId = constrain (Integer.parseInt (inputValue.trim ()), beginServoId, endServoId);
				}
			}
		}
		catch (NumberFormatException NFE) {
			println ("error! NumberFormatException : "+inputValue);
		}
	}

	//
	void onRelease() {
		currentColor = defaultColor;

		isLeft = false;
		isRight = false;
	}

	//
	void onMove() {
	}

	//
	private void dragValue (Pointer p1) {
		if (isLeft) {
			float myAngle = beginAngle+p1.py()-p1.y();
			beginAngle = constrain (myAngle, angleMin, angleMax);
		}
		else if (isRight) {
			float myAngle  = endAngle+p1.py()-p1.y();
			endAngle = constrain (myAngle, angleMin, angleMax);
		} 
		else {
			int myServoId  = servoId + p1.py()-p1.y();
			servoId = constrain (myServoId, beginServoId, endServoId);
		}
	}

	//
	private void dragMove (Pointer p1) {
		//println ("mouseX="+mouseX);
		if (isLeft) {
			int mywidth = width+p1.px()-p1.x();
			//int mywidth = int (mouseX-getPosition().x);
			if (mywidth > minWidth) {
				mywidth = constrain (mywidth, minWidth, maxWidth);
				setSize (mywidth, height);
				setPosition (getPosition().x+p1.x()-p1.px(), getPosition().y);
			}
		}
		else if (isRight) {
			int mywidth = width+p1.x()-p1.px();
			//int mywidth = int (mouseX-getPosition().x);
			mywidth = constrain (mywidth, minWidth, maxWidth);
			setSize (mywidth, height);
		} 
		else {
			int myY = mouseY-pressedy-height/2;
			int posy;
			for (posy=0; posy<allowHeight && myY > posy; posy+=height) {
			}
			// println ("i="+i+" "+myY);
			setPosition (mouseX-pressedx, posy);
		}
	}

	//
	void setMove (boolean state) {
		isMove = state;
	}

	//
	void onDrag() {
		if (isMove) {
			oldMove = move;
			currentColor = moveColor;// 0xbffaff50;
			Pointer p1 = getPointer();
			if (mouseButton == RIGHT) {
				dragValue (p1);
			} 
			else if (mouseButton == LEFT) {//////////////
				dragMove (p1);
			}
		}
	}

	//
	boolean inLeft () {
		Pointer p = getPointer();
		if (p.x() < 20)
			return true;
		else
			return false;
	}

	//
	void setXpos (float theX) {
		setPosition (theX, getPosition().y);
	}

	//
	int getLayer () {
		return int((getPy1()-lineHeight*2)/lineHeight);
	}



	//
	void setX1 (float theX) {
		float myWidth = getWidth ();
		float x1 = getPx1();
		float x2 = getPx2();
		setPosition (theX+50, getPosition().y);
		float temp = int (getWidth()+x1-getPosition().x);
		temp = constrain (temp, minWidth, maxWidth);
		setSize (int (temp), height);
	}

	//
	void setX2 (float theX) {
		float myWidth = getWidth ();
		float x1 = getPx1();
		float x2 = getPx2();
		float temp = theX-getPx1();
		temp = constrain (temp, minWidth, maxWidth);
		setSize (int (temp), height);
		//setPosition (theX, getPosition().y);
	}

	//
	boolean inRight () {
		Pointer p = getPointer ();
		if (p.x() > getWidth()-20)
			return true;
		else 
			return false;
	}

	//
	void onReleaseOutside() {
		isLeft = false;
		isRight = false;
		onLeave();
	}

	//
	void onLeave() {
		isLeft = false;
		isRight = false;
		currentColor = defaultColor;
		currentLeftColor = leftColor;
		currentRightColor = rightColor;
		cursor(ARROW);
		isShowTime = false;
	}

	//
	void setBeginAngle (float theAngle) {
		beginAngle = theAngle;
	}


	//
	void setEndAngle (float theAngle) {
		endAngle = theAngle;
	}

	//
	void setServoId (int theId) {
		servoId = constrain (theId, beginServoId, endServoId);
	}

	//
	void setMinWidth (int value) {
		minWidth = value;
	}

	//
	void setMaxWidth (int value) {
		maxWidth = value;
		if (width > maxWidth)
			width = maxWidth;
		//println ("width="+width);
	}

	//
	void setShowSide (boolean theState) {
		isShowSide = theState;
	}

	//
	float getBeginAngle () {
		return beginAngle;
	}

	//
	float getEndAngle () {
		return endAngle;
	}

	//
	int getServoId () {
		return servoId;
	}

	//
	float getPx1 () {
		return getPosition ().x;
	}

	//
	void setPx1 (int theAxis) {
		//x = theAxis;
		setPosition (theAxis, getHeight ());
	}

	//
	void setPx2 (int theAxis) {
		setWidth (int (theAxis-getPosition ().x));
	}
	//
	float getPy1 () {
		return getPosition ().y;
	}

	//
	float getPx2 () {
		return getPosition ().x+getWidth ();
	}

	//
	float getPy2 () {
		return getPosition ().y+getHeight ();
	}

	//
	boolean inRange () {
		Pointer p1 = getPointer();
		if (p1.x() > 0 && p1.x() < width && p1.y() > 0 && p1.y() < height)
			return true;
		else
			return false;
	}

	//
	void myMouseWheel (int Amount) {
		if (inRange () ) {
			Pointer p1 = getPointer();
			if (inLeft()) {
				if (keyPressed && key == CODED && keyCode == CONTROL)
					beginAngle -= Amount*10;
				else if (keyPressed && key == CODED && keyCode == SHIFT)
					setX1 (getPosition().x-50+Amount);
				else
					beginAngle -= Amount;
				beginAngle = constrain (beginAngle, angleMin, angleMax);
			} 
			else if (inRight ()) {
				if (keyPressed && key == CODED && keyCode == CONTROL)
					endAngle -= Amount*10;
				else if (keyPressed && key == CODED && keyCode == SHIFT)
					setX2 (getPosition().x-50+Amount);
				else
					endAngle -= Amount;
				endAngle = constrain (endAngle, angleMin, angleMax);
			}
			else {
				servoId -= Amount;
				servoId = constrain (servoId, beginServoId, endServoId);
			}
		}
	} // myMouseWheel ?

	///////////////////////////////////////////////


	//
	public class MouseWheelEventClass implements MouseWheelListener {
		public MouseWheelEventClass() {
			addMouseWheelListener(this);
		}
		public void mouseWheelMoved(MouseWheelEvent e) {
			String message;
			myMouseWheel (e.getWheelRotation());
		}
	}
} // Servo ?


/******************** end of Servo.pde ********************/
