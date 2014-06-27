import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import com.sun.awt.AWTUtilities; 
import java.util.Timer; 
import java.awt.event.*; 
import processing.serial.*; 
import controlP5.*; 
import javax.swing.*; 
import java.io.*; 
import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class visual_servo_controller extends PApplet {

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
 
/******************** start of Servo24.pde ********************/
/*
 *
 *	name:		visual servo controller 
 *	verstion:	0.1
 *	author:		lisper (lisper.li@dfrobot.com)
 *
 */
////////////////////////////////////////////////////////////////
/*
 *	hotkey:
 *	type ' ': start or stop play
 *	type 'b' : stop play
 *	type 'q':  remove all Servo
 *	type 'z':  undo, remove last-insert Servo
 *	type 'y :  redo
 *	type 'a':  timeLine to begin
 *	type 's':  save data
 *	type 'o':  open data
 *	type 'l':  show hide grid line
 *	mouse right press and drap up and down: quick change value
 *	move the Servo out of display : delete it
 *	pressed control key and mouse right clicked : delete a Servo 
 */

///////////////////////////////////////////////////////////////

    //for support Opaque




    //processing GUI library

// import javax.swing.JOptionPane.*;



//import java.awt.Point;
//import java.awt.MouseInfo;
//PImage hand_img;
//PImage area_img;

PImage mid_img;
PImage left_img;
PImage right_img;

//
String fileName = "newfile";

ControlP5 cp5;
//PApplet p;
ServoList servoList = new ServoList ();

SerialOpen serialOpen;
TimeLine timeLine;
PlayButton playButton;
FileButton fileOpenButton;
WindowButton windowButton;
//PFont buttonfont = createFont("Arial", 12, true);
ControlFont cfont;

String title = "visual servo controller V0.1";


int backColor = color (17, 17, 15);

//windows size
int winWidth = 1200;
int winHeight = 700;

//one line height
int lineHeight = 24;
//default play time
int playTime = 18;

//default wave range
int waveMin = 500;
int waveMax = 2500;

//default min angle
float angleMin = 0;
//default max angle
float angleMax = 180.0f;

float lineStep = 0.5f;

//default Servo begin numble
int beginServoId = 0;
//default Servo end numble
int endServoId = 23;

//
int minX = 50;
int maxX = winWidth-minX;
int minY = lineHeight*3;
int maxY = lineHeight*(24+3);
PFont font;
//PFont captionfont;
PFont messagefont;
PFont baudFont;
ControlFont buttonfont;
int rectLineColor = color (0, 53, 0);
int timeTextColor = color (99, 99, 99);
int playModeTextColor = color (150, 150, 150);
int captionTextColor  = color (230, 180, 150);
int timeLineColor;// = 0xffffaf00;
boolean playState;
boolean isDrawLines = true;
boolean isPlayModeOpen;
// boolean inInsert;

boolean isPortSelectOpen;
boolean isBaudSelectOpen;

Timer checkIdtimer;
String setName = "servoset.lsp";
String protocol;
String string1, string2, string3;
int sendInterval = 30;
int move = 0;
int moveStep = 10;

int scale = 60;
int cx;
int cy;
int oldx;
int oldy;
boolean isMoveWindow;
boolean isMoveFace;
PImage backImage;
SerialWrite serialWrite;
Thread serialThread;
boolean isDrawRect = false;
int moveOldX;
int oldAxis;

//for init processing
public void init () {
    //frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.removeNotify(); 
    //frame.setAlwaysOnTop(true);
    frame.setUndecorated(true); 
    // to add notify again.   
    frame.addNotify();
    super.init ();
}

//
public void setup() {
    loadSet (setName);    //load set from servoset.lsp
    //frame.setResizable (true);
    //setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setTitle (title);
    PImage icopi = loadImage("icon.png");    // as icon

    mid_img = loadImage ("mid_bg.png");
    left_img = loadImage ("left_bg.png");
    right_img = loadImage ("right_bg.png");

    PGraphics icopg = createGraphics (16, 16, JAVA2D);
    icopg.beginDraw ();    //draw icon
    // if (icopi != null)
    icopg.image (icopi, 0, 0, 16, 16);
    icopg.endDraw ();
    frame.setIconImage(icopg.image);    //set icon
    size(winWidth, winHeight);
    cp5 = new ControlP5(this);
    //captionfont = createFont ("Arial", 24);
    messagefont = createFont ("Arial", 24);
    baudFont = loadFont ("BenguiatITCbyBT-Book-14.vlw");
    buttonfont = new ControlFont (createFont ("ArialUnicod eMs", 12), 12);
    font = loadFont ("ArialUnicodeMS-10.vlw");
    cfont = new ControlFont(font, 10);
    cp5.setFont (cfont);

    timeLine = new TimeLine (maxY+12, 596-20+2, 40, 20);
    timeLine.setRectColor (10, 100, 100, 60);

    timeLine.setRange (0, playTime);
    timeLine.setSize (minX, playTime*scale+minX);

    timeLine.setDelay (10); //1000 is one second
    timeLine.setStep (0.01f);
    timeLine.setPlayMode (TimeLine.ONETIME);
    timeLine.setValue (0);
    //timeLine.lineColor = color (250, 180, 0);
    timeLine.setLineColor (timeLineColor);
    serialOpen = new SerialOpen (this, cp5, winWidth-274, 20+36);

    playButton = new PlayButton (cp5, 50, maxY+25);
    fileOpenButton = new FileButton (cp5, 20, 36);
    windowButton = new WindowButton (cp5, width-80, 5);

    frame.addMouseWheelListener(new MouseWheelListener() {
        public void mouseWheelMoved(MouseWheelEvent evt) {
            //while mouseWheel move call mouseWheel (int)
            mouseWheel(evt.getWheelRotation());
        }
    }
    );

    //*//////////////////////////////////////

    //* frame.addWindowListener (new WindowAdapter() {
    //* 		public void windowClosing (WindowEvent event) { //while click close button run this
    //* 		timeLine.stop ();
    //* 		//println ("sure close this window ?");
    //* 		if (servoList != null && !servoList.list.isEmpty ()) {
    //* 		int option= JOptionPane.showConfirmDialog(
    //* 			frame, 
    //* 			"do you want save data?", 
    //* 			"will exit...", 
    //* 			JOptionPane.YES_NO_OPTION);
    //* 		if (option==JOptionPane.YES_OPTION) {
    //* 			println ("click yes");
    //* 			saveData ();
    //* 		}
    //* 		}
    //* 		println ("in closing");
    //* 		}
    //* 		}
    //* 		);

    //* AWTUtilities.setWindowOpaque(frame, false);
    //* AWTUtilities.setWindowOpacity(frame, 0.9f);

    //*///////////////////////////////////////

    serialWrite = new SerialWrite ();
    serialThread = new Thread (serialWrite);
    serialThread.start ();

    cp5.getTooltip ().setDelay (500);
    cp5.getTooltip ().setAlpha (120);
    cp5.getTooltip ().setBorder(7);
    cp5.getTooltip ().setColorLabel(0xffffff00);
    cp5.getTooltip ().setColorBackground(0xff006688);
    cp5.getTooltip ().register (serialOpen.connectButton, "open serial port");
    //cp5.getTooltip ().register (serialOpen.serialList, "chose a serial port");
    //cp5.getTooltip ().register (serialOpen.baudSelect, "chose a baud for serial");
    cp5.getTooltip ().register (playButton.playButton, "play");
    cp5.getTooltip ().register (playButton.pauseButton, "pause");
    cp5.getTooltip ().register (playButton.stopButton, "stop");
    cp5.getTooltip ().register (playButton.toBeginButton, "back to begin");
    cp5.getTooltip ().register (fileOpenButton.openButton, "open a file");
    cp5.getTooltip ().register (fileOpenButton.saveButton, "save to file");
    cp5.getTooltip ().register (fileOpenButton.exportButton, "export data to file");
    cp5.getTooltip ().register (fileOpenButton.clearButton, "delete all");
    cp5.getTooltip ().register (fileOpenButton.undoButton, "uninsert a servo");
    cp5.getTooltip ().register (fileOpenButton.redoButton, "reinsert a servo");
    cp5.getTooltip ().register (fileOpenButton.demoButton, "start a sample");
    cp5.getTooltip ().register (fileOpenButton.addButton, "pressed and drag to below area to add a new Servo");
}


//
public void draw() {

    if (backImage != null) {
        image (backImage, 0, 0, width, height);
    } else {
        background(backColor);
    }
    timeLine.update ();
    //drawText ();
    servoList.showTimeLine ();

    if (serialOpen.isOpen () && timeLine.local_x!=oldAxis) {
        cmdSend (timeLine.local_x);
        oldAxis = timeLine.local_x;
    }

    //if (timeLine.isBeginMove) {
    if (timeLine.isStart || timeLine.isBeginMove) {
        if (timeLine.getAxis() < minX) {

            moveLeft ((minX-timeLine.getAxis())/10);
        } else if (timeLine.getAxis() > maxX) {
            moveRight ((timeLine.getAxis()-maxX)/10);
        }
    }


    noStroke ();
    fill (39, 39, 39, 0);
    rect (0, 0, width, 30);
    rect (0, height-30, width, height);
    rect (0, 0, 5, height);
    rect (width-5, 0, width, height);

    // fill (color (150, 150, 150));
    fill (200, 150, 0);
    textSize (20);
    text (title, 10, 24);

    //rect (440, 50, 30, 20);
    if (!isDrawRect)
        ;//image (hand_img, 588, 34, 25, 25);
    if (isDrawRect) {
        textSize (12);
        fill (50, 140, 200, 100);
        rect (mouseX, mouseY-lineHeight/2, 100, lineHeight, servoList.radius);
        fill (200, 100, 0);
        textAlign (CENTER);
        text ("new Servo", mouseX+50, mouseY+4);
        textAlign (LEFT);
    } else if (isMoveFace) {
        //println ("move="+(mouseX-moveOldX));
        if (mouseX > moveOldX) {
            moveRight ((mouseX-moveOldX) / 10);
        } else if (mouseX < moveOldX) {
            moveLeft ((moveOldX-mouseX) / 10);
        }
    } else if (mousePressed && mouseButton == LEFT && servoList.isOneInRange () && mouseX > maxX) {
        moveRight (10);
    }
    //    noStroke ();
    //    fill (0);
    //    strokeWeight (0);
    textFont (messagefont);
    fill (200);
    textSize (18);
    text (fileName, 260, 24);
}

//move Servo face to left
public void moveLeft (int theStep) {
    if (theStep == 0)
        theStep = 1;
    if (move >= -playTime*scale-theStep+1100 && move <= -theStep) {
        move += theStep;
        servoList.setAllPosition (theStep);
        timeLine.move (theStep);
    }
}

//move Servo face to right
public void moveRight (int theStep) {
    if (theStep == 0)
        theStep = 1;
    if (move > -playTime*scale+1100 && move <= 0) { // -290, 0
        if (move-theStep <= -playTime*scale+1100) {
            theStep = move+playTime*scale-1100;
        }
        move -= theStep;
        servoList.setAllPosition (-theStep);
        timeLine.move (-theStep);
    }
}

//draw rect line 
public void drawLines (int theHeight, int theTimeWidth, int time) {
    stroke (rectLineColor);
    strokeWeight (1);
    for (int i=lineHeight*3, t=0; i<height && t < time; i+=theHeight, t++)
        line (minX+move, i, playTime*scale+move, i);
    for (int i=minX+move; i<= playTime*scale+move; i+=60) {
        line (i, minY, i, maxY);
    }
    fill (timeTextColor);
    for (float i=0; i<= playTime; i += lineStep*2) {
        text (String.format ("%.1f", i), timeLine.toAxis (i), maxY+18);
    }
}


//test if mouse in rect range
public boolean inRectRange (int theMinX, int theMinY, int theMaxX, int theMaxY) {
    if (mouseX > theMinX && mouseX < theMaxX && mouseY > theMinY && mouseY < theMaxY)
        return true;
    else
        return false;
}


/******************** end of Servo24.pde ********************/
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

		int myY = PApplet.parseInt (getPosition().y);
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
				p.strokeWeight (1.5f);
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
					p.strokeWeight (1.5f);
					p.stroke (0xff59fdfc);
					p.rect (0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, radius);
				}
				//p.fill (currentColor);
				//p.noStroke ();

				p.textAlign (CENTER);

				p.fill (beginAngleColor);
				translate(0, getHeight()/2+8/2);
				p.text (PApplet.parseInt(beginAngle), 10, 0);

				p.fill (endAngleColor);

				int myY = PApplet.parseInt (getPosition().y);
				int posy;
				for (posy=0; posy<1000 && myY > posy; posy+=height)
					;
				setPosition (getPosition ().x, posy);         
				p.text (PApplet.parseInt(endAngle), width-10, 0);

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
	public void onEnter() {
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
	public void onScroll(int n) {
		// y -= n;
		//y = constrain(y, 0, getHeight()-10);
	}

	//
	public void onPress() {
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
	public void onClick() {
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
						inputValue = JOptionPane.showInputDialog("Input beginAngle ("+PApplet.parseInt(angleMin)+"~"+PApplet.parseInt(angleMax)+")");
						if (inputValue != null)
							beginAngle = constrain (Integer.parseInt (inputValue.trim ()), angleMin, angleMax);
					} 
					else if (inRight ()) {
						inputValue = JOptionPane.showInputDialog("Input endAngle ("+PApplet.parseInt(angleMin)+"~"+PApplet.parseInt(angleMax)+")");
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
	public void onRelease() {
		currentColor = defaultColor;

		isLeft = false;
		isRight = false;
	}

	//
	public void onMove() {
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
	public void setMove (boolean state) {
		isMove = state;
	}

	//
	public void onDrag() {
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
	public boolean inLeft () {
		Pointer p = getPointer();
		if (p.x() < 20)
			return true;
		else
			return false;
	}

	//
	public void setXpos (float theX) {
		setPosition (theX, getPosition().y);
	}

	//
	public int getLayer () {
		return PApplet.parseInt((getPy1()-lineHeight*2)/lineHeight);
	}



	//
	public void setX1 (float theX) {
		float myWidth = getWidth ();
		float x1 = getPx1();
		float x2 = getPx2();
		setPosition (theX+50, getPosition().y);
		float temp = PApplet.parseInt (getWidth()+x1-getPosition().x);
		temp = constrain (temp, minWidth, maxWidth);
		setSize (PApplet.parseInt (temp), height);
	}

	//
	public void setX2 (float theX) {
		float myWidth = getWidth ();
		float x1 = getPx1();
		float x2 = getPx2();
		float temp = theX-getPx1();
		temp = constrain (temp, minWidth, maxWidth);
		setSize (PApplet.parseInt (temp), height);
		//setPosition (theX, getPosition().y);
	}

	//
	public boolean inRight () {
		Pointer p = getPointer ();
		if (p.x() > getWidth()-20)
			return true;
		else 
			return false;
	}

	//
	public void onReleaseOutside() {
		isLeft = false;
		isRight = false;
		onLeave();
	}

	//
	public void onLeave() {
		isLeft = false;
		isRight = false;
		currentColor = defaultColor;
		currentLeftColor = leftColor;
		currentRightColor = rightColor;
		cursor(ARROW);
		isShowTime = false;
	}

	//
	public void setBeginAngle (float theAngle) {
		beginAngle = theAngle;
	}


	//
	public void setEndAngle (float theAngle) {
		endAngle = theAngle;
	}

	//
	public void setServoId (int theId) {
		servoId = constrain (theId, beginServoId, endServoId);
	}

	//
	public void setMinWidth (int value) {
		minWidth = value;
	}

	//
	public void setMaxWidth (int value) {
		maxWidth = value;
		if (width > maxWidth)
			width = maxWidth;
		//println ("width="+width);
	}

	//
	public void setShowSide (boolean theState) {
		isShowSide = theState;
	}

	//
	public float getBeginAngle () {
		return beginAngle;
	}

	//
	public float getEndAngle () {
		return endAngle;
	}

	//
	public int getServoId () {
		return servoId;
	}

	//
	public float getPx1 () {
		return getPosition ().x;
	}

	//
	public void setPx1 (int theAxis) {
		//x = theAxis;
		setPosition (theAxis, getHeight ());
	}

	//
	public void setPx2 (int theAxis) {
		setWidth (PApplet.parseInt (theAxis-getPosition ().x));
	}
	//
	public float getPy1 () {
		return getPosition ().y;
	}

	//
	public float getPx2 () {
		return getPosition ().x+getWidth ();
	}

	//
	public float getPy2 () {
		return getPosition ().y+getHeight ();
	}

	//
	public boolean inRange () {
		Pointer p1 = getPointer();
		if (p1.x() > 0 && p1.x() < width && p1.y() > 0 && p1.y() < height)
			return true;
		else
			return false;
	}

	//
	public void myMouseWheel (int Amount) {
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
 
/******************** start of ServoId.pde ********************/
/*
   some static id for controlP5

 */

static class ServoId {
	static final int OPEN_BUTTON     	= 1000;
	static final int PLAY_BUTTON     	= 1001;
	static final int PAUSE_BUTTON    	= 1002;
	static final int STOP_BUTTON     	= 1003;
	static final int TO_BEGIN_BUTTON 	= 1004;
	static final int PLAYMODE_BUTTON 	= 1005;
	static final int SPEED_BUTTON    	= 1006;
	static final int OPENFILE_BUTTON 	= 1007;
	static final int SAVE_BUTTON     	= 1008;
	static final int EXPORT_BUTTON   	= 1009;
	static final int CLEAR_BUTTON    	= 1010;
	static final int UNDO_BUTTON     	= 1011;
	static final int REDO_BUTTON     	= 1012;
	static final int ADD_BUTTON      	= 1013;
	static final int SETTIME_BUTTON  	= 1014;
	static final int CLOSE_BUTTON    	= 1015;
	static final int MINIMIZE_BUTTON 	= 1016;
	static final int SET_BUTTON      	= 1017;
	static final int DFROBOT_LABEL   	= 1018;
	static final int DEMO_BUTTON     	= 1019;
	static final int BAUDSELECT_BUTTON	= 1020;
	static final int PORTSELECT_BUTTON	= 1021;
	static int SERVOID []  = {
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23
	};
}

/******************** end of ServoId.pde ********************/
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
 
/******************* start of ServoList.pde ********************/
/*
 *	ServoList demo created by lisper <leyapin@gmail.com> 2013-11-30
 *	last time:    2013-12-3 14:33 by lisper
 *	
 *	Create a custom Controller, please not that
 *	MyButton extends Controller,
 *	is an indicator for the super class about the type of
 *	custom controller to be created.
 *
 *	class ServoList
 *	method:
 *	add () : add a servo in left_top
 *	add (int theX, int theY)  : add a servo in mouse place
 *	remove () : remove a servo if mouse in it and mouseRight clicked
 *	removeLast () : remove last-insert Servo
 *	removeAll () : remove all Servo
 *	removeOut () : remove Servo that out of display
 *	boolean isOneInRange () : if mouse in one of the Servo
 *	checkClash () : check clash and fix in mouseReleased()
 *	checkOut () : if out of range set it in range
 */

// use Arraylist organize all Servo
class ServoList {

	int currentColor = 0xaf888877;
	int defaultColor = 0xaf888877;
	int errorColor    =0xffff0000;
	int defaultSideColor = 0x00ffffff;
	int currentSideColor = 0xff666655;
	int pressedColor = 0xaf888877;
	int moveColor    = 0xaf888877;
	int enterColor   = 0xaf888877;
	int sideColor    = 0xff666655;
	int leftColor  = 0x00ffffff;//= 0xff00aaff;
	int rightColor = 0x00ffffff;// = 0xffa050ff;
	int beginAngleColor = 0xff111111;
	int endAngleColor = 0xff111111;
	int servoIdColor = 0xffffffff;
	int enterSideColor = 0xff888877;
	boolean isShowSide = true;
	int radius = 0;
	Servo currentServo;
	boolean isIdClash = false;

	//Timer checkIdtimer = new Timer ();

	Servo clashServo1;
	Servo clashServo2;

	ArrayList<Servo> list;
	ArrayList<Servo> redoList;
	int addId;
	public ServoList () {
		list = new ArrayList<Servo> ();
		this.redoList = new ArrayList<Servo> ();
		addId = 0; // always addId++
		//checkIdtimer.schedule (new CheckIdClash (), 0, 1000);


	}

	public void setValue (Servo servo) {
		servo.currentColor    = this.currentColor;
		servo.defaultColor    = this.defaultColor;
		servo.pressedColor    = this.pressedColor;
		servo.moveColor       = this.moveColor;
		servo.enterColor      = this.enterColor;
		servo.sideColor       = this.sideColor;
		servo.leftColor       = this.leftColor;
		servo.rightColor      = this.rightColor;
		servo.beginAngleColor = this.beginAngleColor;
		servo.endAngleColor   = this.endAngleColor;
		servo.servoIdColor    = this.servoIdColor;
		servo.isShowSide      = this.isShowSide;
		servo.radius 	        = this.radius;
		servo.enterSideColor = this.enterSideColor;
		servo.defaultSideColor = this.defaultSideColor;
		servo.currentLeftColor = this.leftColor;
		servo.currentRightColor = this.rightColor;
		servo.currentSideColor = this.sideColor;
	}

	/*
	//add a Servo in left_top
	void add () {
	Servo servo = new Servo(cp5, "servo"+addId++);
//servo.setShowSide (true);
list.add (servo);
}
	 */

//add a Servo in mouse current place
public void add (int theX, int theY) {
	Servo servo = new Servo(cp5, "servo"+addId++);
	servo.setSize (100, lineHeight);
	servo.setPosition (theX, theY-servo.getHeight());
	servo.setMinWidth (50);
	servo.setMaxWidth (playTime*scale);
	setValue (servo);
	//servo.radius1 = 0;
	//servo.setShowSide (true);
	list.add (servo);
	if (!redoList.isEmpty ())
		redoList.clear ();

	int myY = PApplet.parseInt (servo.getPosition().y);
	int posy;
	for (posy=0; posy<1000 && myY > posy; posy+=lineHeight)
		;
	println ("layer= "+servo.getLayer ());
	servo.setPosition (servo.getPosition ().x, posy);
}

public void setAllMaxWidth (int theMaxWidth) {
	for (int i=0; i<list.size (); i++) {
		list.get(i).setMaxWidth (theMaxWidth);
	}
}

//
public void add (Servo servo) {
	list.add (servo);
}

//remove if mouse in range
public void remove () {
	for (int i=list.size ()-1; i >= 0; i--) {
		if ((list.get(i)).inRange ()) {
			//Servo servo = (Servo)list.get(i).clone ();
			Servo servo = new Servo(cp5, "servo"+addId++);
			servo.setPosition (list.get(i).getPx1(), list.get(i).getPy1 ());
			servo.setVisible (false);
			servo.setBeginAngle (list.get(i).getBeginAngle ());
			servo.setEndAngle (list.get(i).getEndAngle ());
			servo.setSize (list.get(i).getWidth (), lineHeight);
			servo.setServoId (list.get(i).getServoId ());
			servo.setMinWidth (50);
			servo.setMaxWidth (playTime*scale);
			servo.radius = list.get(i).radius;
			servo.setShowSide (true);
			setValue (servo);
			redoList.add (servo);
			//list.get(i).setVisible (false);
			//redoList.add (list.get(i));//
			list.get(i).remove ();
			list.remove (i);//
			currentServo = null;
			break;
		}
	}
}

//
public void remove (Servo theServo) {
	theServo.setVisible (false);
	redoList.add (theServo);
	list.remove (theServo);
	currentServo = null;
}

/*
//remove if mouse in range
void remove () {
for (int i=list.size ()-1; i >= 0; i--) {
if (((Servo)list.get(i)).inRange ()) {
((Servo)list.get(i)).remove ();
servoList.list.remove (i);
break;
}
}
}
 */

//
public void setMove (boolean state) {
	for (int i=0; i<list.size(); i++) {
		list.get(i).setMove(state);
	}
	for (int i=0; i<redoList.size(); i++) {
		redoList.get(i).setMove (state);
	}
}

//
public void setAllPosition (int theMove) {
	for (int i=0; i<list.size (); i++) {
		list.get(i).setPosition (list.get(i).getPx1()+theMove, list.get(i).getPy1());
	}
}

//
public void removeLast () {
	if (!list.isEmpty ()) {
		list.get(list.size()-1).remove ();
		list.remove (list.size ()-1);
		currentServo = null;
	}
}

//
public void undoList () {
	if (!list.isEmpty ()) {
		list.get(list.size ()-1).setVisible (false);
		redoList.add (list.get(list.size ()-1));
		//list.get(list.size()-1).remove ();
		list.remove (list.size ()-1);
		currentServo = null;
	}
}

//
public void redoList () {
	println (redoList.size ());
	if (!redoList.isEmpty ()) {

		list.add (redoList.get (redoList.size ()-1));
		list.get(list.size ()-1).onLeave ();
		redoList.get (redoList.size ()-1).setVisible (true);

		redoList.remove (redoList.size ()-1);
	}
	else {
		println ("empty");
	}
}

//
public void removeAll () {
	for (int i=list.size ()-1; i >= 0; i--) {
		list.get(i).remove ();
		list.remove (i);
	}
	currentServo = null;
}

//
public void clear () {
	if (!list.isEmpty ()) {
		int option= JOptionPane.showConfirmDialog(
				frame, 
				"are you sure clear all servo?", 
				"clear?", 
				JOptionPane.YES_NO_OPTION);
		if (option==JOptionPane.YES_OPTION) {       
			removeAll ();
		}
	}
	currentServo = null;
}

//if one Servo out of display, remove it
public void removeOut () {
	for (int i=list.size ()-1; i >= 0; i--) {
		Servo servo = list.get(i);
		if  (servo.getPx1() >= playTime*scale+minX+move || servo.getPx2() <= minX+move
				|| list.get(i).getPy1() >= maxY || list.get(i).getPy2() <= minY) {
			//if (list.get(i).getPx1() >= maxX || list.get(i).getPy1() >= maxY
			//  || list.get(i).getPx2() <= minX || list.get(i).getPy2() <= minY) {
			//redoList.add (list.get(i));
			//list.get(i).setVisible (false);
			list.get(i).remove ();
			list.remove (i);
			currentServo = null;
		}
		}
	}

	/*
	//if one Servo out of display, remove it
	void removeOut () {
	for (int i=list.size ()-1; i >= 0; i--) {
	if (list.get(i).getPx1() >= maxX || list.get(i).getPy1() >= maxY
	|| list.get(i).getPx2() <= minX || list.get(i).getPy2() <= minY) {
	list.get(i).remove ();
	list.remove (i);
	}
	}
	}
	 */

	//
	public void setAllHeight (int theHeight) {
		for (int i=0; i<list.size(); i++) {
			list.get(i).setSize (list.get(i).getWidth(), theHeight);
		}
	}


	//if one of Servo in range
	public boolean isOneInRange () {
		//currentServo = null;
		for (int i=0; i < list.size (); i++)
			if (((Servo)list.get(i)).inRange ()) {
				if (currentServo != null)
					currentServo.isShowFocus = false;
				currentServo = list.get(i);
				currentServo.isShowFocus = true;
				return true;
			}
		if (currentServo != null)
			currentServo.isShowFocus = false;
		return false;
	}

	//
	public void fixInRange () {
		for (int i=0; i < list.size (); i++) {
			if (!((Servo)list.get(i)).inRange ()) {
				list.get(i).onLeave ();
			}
		}
	}

	//
	public void showTimeLine () {

		textSize (10);
		fill (color (200, 60, 10));
		stroke (color (160, 160, 60));
		strokeWeight(0.1f);
		if (currentServo != null) {
			Servo servo = currentServo;
			line (servo.getPx1 (), servo.getPy2()-lineHeight/2, servo.getPx1 (), maxY);
			line (servo.getPx2 (), servo.getPy2()-lineHeight/2, servo.getPx2 (), maxY);
			textSize (9);
			text (String.format ("%.2f", timeLine.toValue (PApplet.parseInt (servo.getPx1 ()))), servo.getPx1 (), maxY+10);
			text (String.format ("%.2f", timeLine.toValue (PApplet.parseInt (servo.getPx2 ()))), servo.getPx2 (), maxY+10);
		}


		/*
		//  text (String.format ("%.1f", i), timeLine.toAxis (i), maxY+18);
		for (int i=0; i < list.size (); i++) {
		Servo servo = list.get(i);
		if (servo.inRange ()) {
		line (servo.getPx1 (), servo.getPy2()-lineHeight/2, list.get(i).getPx1 (), maxY);
		line (servo.getPx2 (), servo.getPy2()-lineHeight/2, list.get(i).getPx2 (), maxY);
		text (String.format ("%.2f",timeLine.toValue (int (servo.getPx1 ()))), servo.getPx1 (), maxY+10);
		text (String.format ("%.2f",timeLine.toValue (int (servo.getPx2 ()))), servo.getPx2 (), maxY+10);
		}
		}
		 */
	}

	public int getMaxAxis () {
		int maxAxis = 0;
		for (int i=0; i<list.size (); i++) {
			if (list.get(i).getPx2() > maxAxis)
				maxAxis = PApplet.parseInt (list.get(i).getPx2());
		}
		return maxAxis;
	}

	//check clash and fix, will call checkTwoLineClash ()
	public void checkClash () {
		for (int i=0; i<list.size(); i++) {
			for (int j = i+1; j<list.size(); j++) {
				checkTwoLineClash (list.get(i), list.get(j));
			}
		}
	} // checkClash ?

	//will call checkClash ()
	public void checkTwoLineClash (Servo s1, Servo s2) {
		if (s1.getPosition().y == s2.getPosition().y) {
			float p1x1 = s1.getPx1 ();
			float p1x2 = s1.getPx2 ();
			float p2x1 = s2.getPx1 ();
			float p2x2 = s2.getPx2 ();

			if (p1x1 >= p2x1 && p1x1 < p2x2) { // p2 and p1
				s1.setXpos (p2x2);
				checkClash ();
			}
			else if (p1x2 > p2x1 && p1x2 < p2x2) { //p1 and p2
				s2.setXpos (p1x2);
				checkClash ();
			}
			else if (p2x1 > p1x1 && p2x1 < p1x2) {
				s2.setXpos (p1x2);
				checkClash ();
			}
		}
	}

	//if one Servo out of limit set it back
	public void checkOut () {
		float p1x1, p1x2;
		for (int i=0; i<list.size(); i++) {
			p1x1 = list.get(i).getPx1 ();
			p1x2 = list.get(i).getPx2 ();
			if (p1x1 < minX+move) {
				list.get(i).setXpos (minX+move);
			}
			if (p1x2 > playTime*scale+move+minX) {
				list.get(i).setXpos (playTime*scale+move+minX-list.get(i).getWidth ());
			}
		}
	}


	//
	public void CheckIdClash () {
		if (list.size () == 0)
			return;
		else if (list.size () == 1)
			list.get(0).currentSideColor = sideColor;
		if (isIdClash) {
			if ((!list.contains (clashServo1) || !list.contains (clashServo2)) || !isTwoIdClash (clashServo1, clashServo2) ) {
				clashServo1.servoIdColor = servoIdColor;
				clashServo2.servoIdColor = servoIdColor;
				isIdClash = false;
				CheckIdClash ();
			}
		}
		else {
			for (int i=0; i<servoList.list.size ()-1; i++) {
				for (int j= i+1; j<servoList.list.size (); j++) {
					if (isTwoIdClash (servoList.list.get(i), servoList.list.get(j))) {
						isIdClash = true;
						return;
					}
				}
			}
		}
	}

	//
	public boolean isTwoIdClash (Servo s1, Servo s2) {
		if (s1.getServoId () != s2.getServoId ()) {
			s1.currentSideColor = servoList.sideColor;
			s2.currentSideColor = servoList.sideColor;
			return false;
		}
		if ((s1.getPx1() >= s2.getPx1() && s1.getPx1() < s2.getPx2 ())
				|| (s1.getPx2() > s2.getPx1() && s1.getPx2() <= s2.getPx2 ())
				|| (s2.getPx1() >= s1.getPx1() && s2.getPx1() < s1.getPx2 ())) {
			s1.servoIdColor = errorColor;
			s2.servoIdColor = errorColor;
			clashServo1 = s1;
			clashServo2 = s2;
			println ("error! id clash!");
			println ("id="+s1.getServoId ()+" in "+
					(PApplet.parseInt (s1.getPy1()/24-2))+" "+(PApplet.parseInt (s2.getPy1()/24-2)));
			return true;
		}
		else {
			s1.servoIdColor = servoIdColor;
			s2.servoIdColor = servoIdColor;
			return false;
		}
	}


	//////////////////////////////////////////////////////////////
} // ServoList ?

/******************* end of ServoList.pde ********************/
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


/******************** start of commonButton.pde ********************/
/*
 *	public class FileButton 
 *	FileButton (ControlP5 thecp5, int theX, int theY) 
 *	void createButton (ControlP5 thecp5, int theX, int theY) 
 *	controlP5.Button openButton;
 *	controlP5.Button saveButton;
 *	controlP5.Button exportButton;
 *	controlP5.Button clearButton;
 *	controlP5.Button undoButton;
 *	controlP5.Button redoButton;
 *	controlP5.Button addButton;
 *	controlP5.Button demoButton;
 *	controlP5.Button dfrobotlabel;
 *	controlP5.DropdownList playModeButton;
 *	
 */


//for add button place and width
int add_x;
int add_y;
int add_w;
int add_h;


//some common button class
public class FileButton {
	//ControlP5 mycp5;
	controlP5.Button openButton;
	controlP5.Button saveButton;
	controlP5.Button exportButton;
	controlP5.Button clearButton;
	controlP5.Button undoButton;
	controlP5.Button redoButton;
	controlP5.Button addButton;
	controlP5.Button demoButton;
	controlP5.Button dfrobotlabel;
	controlP5.DropdownList playModeButton;


	//load button image
	PImage[] open_img    = { 
		loadImage ("open_a.png"), 
		loadImage ("open_b.png"), 
		loadImage ("open_c.png")
	};
	PImage[] export_img    = { 
		loadImage ("export_a.png"), 
		loadImage ("export_b.png"), 
		loadImage ("export_c.png")
	};
	PImage[] save_img    = { 
		loadImage ("save_a.png"), 
		loadImage ("save_b.png"), 
		loadImage ("save_c.png")
	};
	PImage[] undo_img    = { 
		loadImage ("undo_a.png"), 
		loadImage ("undo_b.png"), 
		loadImage ("undo_c.png")
	};
	PImage[] redo_img    = { 
		loadImage ("redo_a.png"), 
		loadImage ("redo_b.png"), 
		loadImage ("redo_c.png")
	};
	PImage[] clear_img    = { 
		loadImage ("clear_a.png"), 
		loadImage ("clear_b.png"), 
		loadImage ("clear_c.png")
	}; 
	PImage[] add_img    = { 
		loadImage ("add_a.png"), 
		loadImage ("add_b.png"), 
		loadImage ("add_c.png")
	};
	PImage[] demo_img    = { 
		loadImage ("sample_a.png"), 
		loadImage ("sample_b.png"), 
		loadImage ("sample_c.png")
	};

	FileButton (ControlP5 thecp5, int theX, int theY) {
		createButton (thecp5, theX, theY);
	}

	//create common button for controlP5
	public void createButton (ControlP5 thecp5, int theX, int theY) {
		openButton = thecp5.addButton ("openButton")
			.setCaptionLabel ("Open")
			.setImages (open_img)
			.setPosition (theX, theY)
			.setColorBackground (color (50, 50, 50))
			.setId (ServoId.OPENFILE_BUTTON)
			.setSize (66, 23)
			;
		saveButton = thecp5.addButton ("saveButton")
			.setCaptionLabel ("Save")
			.setImages(save_img)
			.setPosition (theX+67, theY)
			.setColorBackground (color (100, 50, 50))
			.setId (ServoId.SAVE_BUTTON)
			.setSize (66, 23)
			;

		exportButton = thecp5.addButton ("exportButton")
			.setCaptionLabel ("Export")
			.setImages (export_img)
			.setVisible (false)
			.setPosition (theX+67*2, theY)
			.setColorBackground (color (100, 100, 50))
			.setId (ServoId.EXPORT_BUTTON)
			.setSize (66, 23)
			;

		openButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER)
			.setFont (buttonfont);

		saveButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER)
			.setFont (buttonfont);

		exportButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER)
			.setFont (buttonfont);


		clearButton = thecp5.addButton ("clearButton")
			.setLabel ("Clear")
			.setImages (clear_img)
			.setPosition (theX+67*2, theY)
			.setColorBackground (color (150, 100, 20))
			.setId (ServoId.CLEAR_BUTTON)
			.setSize (66, 23)
			;

		clearButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont(buttonfont)
			;


		undoButton = thecp5.addButton ("undoButton")
			.setLabel ("Undo")
			.setImages (undo_img)
			//.setVisible (false)
			.setPosition (theX+67*3, theY)
			.setColorBackground (color (150, 100, 20))
			.setId (ServoId.UNDO_BUTTON)
			.setSize (66, 23)
			;

		undoButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont(buttonfont)
			;

		redoButton = thecp5.addButton ("redoButton")
			.setLabel ("Redo")
			// .setVisible (false)
			.setImages (redo_img)
			.setPosition (theX+67*4, theY)
			.setColorBackground (color (150, 100, 20))
			.setId (ServoId.REDO_BUTTON)
			.setSize (66, 23)
			;

		redoButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont(buttonfont)
			;


		demoButton = cp5.addButton ("demoButton")
			.setLabel ("demo")
			.setImages (demo_img)
			.setPosition (theX+67*5, theY)
			.setColorBackground (color (255, 255, 255, 1))
			.setColorForeground (color (30, 30, 30, 120))
			.setColorBackground (color (150, 100, 20))
			.setId (ServoId.DEMO_BUTTON)
			.setSize (82, 23)
			;

		demoButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER)
			.setFont (buttonfont)
			;

		playModeButton = thecp5.addDropdownList ("Play Mode")
			.setPosition (theX+67*12-20, 60)
			.setSize (110, 80)
			//.setVisible (false)
			.setId (ServoId.PLAYMODE_BUTTON)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			//.setColorActive(color(50, 100, 1))
			;

		playModeButton.captionLabel ()
			.setColor(color(230));
		playModeButton.addItem ("Play only once", 0);
		playModeButton.addItem ("Back and forth", 1);
		playModeButton.addItem ("Loop playback", 2);
		//playModeButton.setIndex (1);
		playModeButton.setItemHeight(23);
		playModeButton.setBarHeight(23);
		playModeButton.setItemHeight (12);

		playModeButton.captionLabel()
			.toUpperCase(false)
			.align(LEFT, CENTER)
			.setFont (new ControlFont(baudFont, 14))
			;

		playModeButton.getItem (0).toUpperCase(false);
		// playModeButton.getItem (1).toUpperCase(false);
		// playModeButton.getItem (2).toUpperCase(false);

		add_x = theX+67*5+83;
		add_y = theY;
		add_w = 110;
		add_h = 23;
		addButton = thecp5.addButton ("addButton")
			.setLabel ("add")
			.setImages (add_img)
			//.setVisible (false)
			.setPosition (add_x, add_y)
			.setColorBackground (color (150, 100, 20))
			.setId (ServoId.ADD_BUTTON)
			.setSize (add_w, add_h)
			;

		redoButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont(buttonfont)
			;

		dfrobotlabel = cp5.addButton ("dfrobot")
			.setLabel ("www.dfrobot.com")
			.setPosition (winWidth-130, winHeight-27)
			.setSize (130, 20)
			.setColorBackground (color (255, 255, 255, 1))
			.setColorForeground (color (30, 30, 30, 120))
			.setColorActive (color (20, 20, 20, 120))
			.setId (ServoId.DFROBOT_LABEL)
			;
		dfrobotlabel.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setColor (color (160, 120, 0))
			// .setFont(buttonfont)
			.setFont (new ControlFont(baudFont, 14))
			;
	}
}

//
public class AboutDialog extends JDialog {
	public AboutDialog(JFrame parent) {
		super(parent, "About Dialog", true);

		Box b = Box.createVerticalBox();
		b.add(Box.createGlue());
		b.add(new JLabel("designed by DFRobot"));
		b.add(new JLabel("www.dfrobot.com"));
		b.add(new JLabel("2013"));
		b.add(Box.createGlue());
		getContentPane().add(b, "Center");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("Ok");
		p2.add(ok);
		getContentPane().add(p2, "South");

		ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
				setVisible(false);
				}
				});

		setSize(250, 150);
	}
}

//   JDialog f = new AboutDialog(new JFrame());
//    f.show();


/******************** end of commonButton.pde ********************/
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
 
/******************** start of controlEvent.pde ********************/

// controlP5 event processing 
public void controlEvent(ControlEvent theEvent) {
	//println("<=> controlEvent : "+theEvent);
	int id = theEvent.getId ();

	switch (id) {
		case ServoId.PLAY_BUTTON:	//click play button
			if (!servoList.isIdClash) {
				servoList.setMove(false);
				timeLine.start ();
			}
			break;
		case ServoId.PAUSE_BUTTON:	//click pause button
			servoList.setMove(true);
			timeLine.pause ();
			break;
		case ServoId.STOP_BUTTON:	//click stop button
			servoList.setMove(true);
			timeLine.stop ();
			break;
		case ServoId.TO_BEGIN_BUTTON:	//click toBegin button
			to_begin ();

			break;
		case ServoId.OPEN_BUTTON:	//click open serial button
			timeLine.stop ();
			serialOpen.process ();
			break;
		case ServoId.SAVE_BUTTON:	//click save button
			to_begin ();
			if (servoList.list.isEmpty ()) {
				println ("nothing for save!");
			}
			else
				saveData ();
			break;
		case ServoId.OPENFILE_BUTTON:	//click openfile button
			to_begin ();
			timeLine.stop ();
			loadData ();
			break;
		case ServoId.CLEAR_BUTTON:	//click clear button
			timeLine.stop ();
			servoList.clear ();
			servoList.isIdClash = false;
			break;
		case ServoId.UNDO_BUTTON:	//click undo button
			servoList.undoList ();
			//servoList.CheckIdClash ();
			break;
		case ServoId.REDO_BUTTON:	//click redo button
			println ("redo");
			servoList.redoList ();
			break;
			//    case ServoId.SPEED_BUTTON:
			//        if (playButton != null)
			//            timeLine.setDelay (int (playButton.speedSlider.getValue ()));
			//        if (timeLine.isStart) {
			//            timeLine.pause ();
			//            timeLine.start ();
			//        }
			//        break;
		case ServoId.PORTSELECT_BUTTON:	//click portselect button
			if (serialOpen != null && serialOpen.isOpen ())
				serialOpen.close ();
			break;
		case ServoId.BAUDSELECT_BUTTON:	//click baudselect button
			if (serialOpen != null && serialOpen.isOpen ())
				serialOpen.close ();
			break;
		case ServoId.SET_BUTTON:	//click set time button

			setTimeDialog ();
			break;
		case ServoId.DEMO_BUTTON:	//click demo button
			create_demo ();
			break;
		case ServoId.MINIMIZE_BUTTON:	//click minimize button
			this.frame.setExtendedState(JFrame.ICONIFIED); 
			break;
		case ServoId.CLOSE_BUTTON:	//click close windows button
			timeLine.stop ();
			//println ("sure close this window ?");
			inclose ();

			break;
		case ServoId.ADD_BUTTON:	//click add button
			break;
		case ServoId.DFROBOT_LABEL:	//click dfrobot label
			link ("http://www.dfrobot.com");
			break;
		case ServoId.PLAYMODE_BUTTON:	//click play mode button
			if (playButton!= null) 
				switch (PApplet.parseInt (fileOpenButton.playModeButton.getValue())) {
					case 0:
						//set play mode to onetime
						timeLine.setPlayMode (timeLine.ONETIME);
						break;
					case 1:
						//set play mode to to_and_fro
						timeLine.setPlayMode (timeLine.TO_AND_FRO);
						break;
					case 2:
						//set play mode to circle
						timeLine.setPlayMode (timeLine.CIRCLE);
						break;
				}
			break;
	}
}

//
public void inclose () {
	if (servoList != null && !servoList.list.isEmpty ()) {
		int option= JOptionPane.showConfirmDialog(
				frame, 
				"do you want save data?", "close", 
				JOptionPane.YES_NO_CANCEL_OPTION
				);
		if (option==JOptionPane.YES_OPTION) {
			saveData ();
			serialWrite.flag = false;
			exit ();
		} 
		else if (option==JOptionPane.NO_OPTION) {
			serialWrite.flag = false;
			exit ();
		}
		else {
		} // not exit
	} 
	else {
		serialWrite.flag = false;
		exit ();
	}
}

//created a demo program
public void create_demo () {
	servoList.clear ();
	servoList.setAllPosition (-move);
	timeLine.move (-move);
	move = 0;
	timeLine.stop ();
	timeLine.setValue (0);
	servoList.setAllPosition (move);
	if (servoList.list.isEmpty ()) {
		for (int i=0; i<24; i++) {
			servoList.add (0, i*24+3*24+2);
			servoList.list.get(i).setServoId (i);
			servoList.list.get(i).setX2 (1000);
		}
	}
	//println ("demo");
	//loadDemo ();
}


//if click toBegin button then called and set timeline to begin
public void to_begin () {
	servoList.setMove(true);
	servoList.setAllPosition (-move);
	timeLine.move (-move);
	move = 0;
	timeLine.stop ();
	timeLine.setValue (0);
}


/******************** start of controlEvent.pde ********************/
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

/******************** start of fileProcess.pde ********************/
/* 
 *	about open and save file process
 *	void saveData ()        : getData and call saveStringToFile ()
 *	void exportData ()        : getData and save data to SD card
 *	void saveStringToFile (String mystring)    : open swing saveDialog and save the String to file
 *	void loadData ()        : call getStringFromFile () and call loadToList ()
 *	void loadToList (String lines, int addId) :    load user data to program
 *	String [] getStringsFromFile (String path)    : open swing openDialog and return String
 *	void loadSet (String theName)        : load setfile and call setValue
 *	void setValue (String variable, String value)    : 
 *	String [] loadSetFile (String theName)
 *	void readProtocol ()
 *	String shiftString (String theString)
 *	void setTimeDialog ()
 */

//* //
//* void saveData () {
//* 	to_begin ();
//* 	String buffer = new String ();
//* 	buffer += "%24servo%\n";
//* 	buffer += playTime+"\n";
//* 	for (int i=0; i<servoList.list.size (); i++) {
//* 		Servo servo = servoList.list.get(i);
//* 		buffer += int(servo.getServoId ())+"\t"
//* 			+getLayer (int (servo.getPy1()))+"\t"
//* 			+int(servo.getBeginAngle ())+"\t"
//* 			+int(servo.getEndAngle ())+"\t"
//* 			+timeLine.toValue (int (servo.getPx1 ()))+"\t"
//* 			+timeLine.toValue (int(servo.getPx2 ()))+"\n";
//* 	}
//* 
//* 	String name = saveStringToFile (buffer);    // open a save window to and the string
//* 	fileName = name;
//* } // saveToFile ?


public void saveData () {

	String name = getSaveFileName ();
	if (name != null) {
		int length = servoList.list.size ();
		XML xml = new XML ("df24servo");
		xml.setName ("df24servo");
		xml.setInt ("time", playTime);
		for (int i=0; i<length; i++) {
			xml.addChild ("servo");
		}
		XML [] children = xml.getChildren ("servo");
		for (int i=0; i<length; i++) {
			Servo servo = servoList.list.get(i);
			children[i].setInt ("id", 
					servo.getServoId ());
			children[i].setInt ("layer", 
					getLayer (PApplet.parseInt (servo.getPy1 ())));
			children[i].setInt ("begin_angle", 
					PApplet.parseInt (servo.getBeginAngle ()));
			children[i].setInt ("end_angle", 
					PApplet.parseInt (servo.getEndAngle ()));
			children[i].setFloat ("begin_time", 
					timeLine.toValue (PApplet.parseInt (servo.getPx1 ())));
			children[i].setFloat ("end_time", 
					timeLine.toValue (PApplet.parseInt (servo.getPx2 ())));
		}

		if (name.endsWith (".xml")) {
			saveXML (xml, name);
			fileName = name;
		}
		else {
			fileName = name+".xml";
			saveXML (xml, name+".xml");
		}
	}
}

//
public void exportData () {
	to_begin ();
	String buffer = new String ();
	for (int i=0; i<servoList.list.size (); i++) {
		Servo servo = servoList.list.get(i);
		buffer += string1+PApplet.parseInt(servo.getServoId ())+string2+
			PApplet.parseInt(servo.getBeginAngle ())+PApplet.parseInt(servo.getEndAngle ())
			+"\t"+PApplet.parseInt(servo.getPx1 ())+"\t"+PApplet.parseInt(servo.getPy1())
			+"\t"+PApplet.parseInt(servo.getWidth ())+"\n";
	}
	String name = saveStringToFile (buffer);    // open a save window to save the string
	// if (name != null)

	fileName = name;
}


// reusable, save a string data to file by java swing
public String saveStringToFile (String mystring) {
	String name = fileName;

	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
		e.printStackTrace();
	}

	// create a file chooser
	final JFileChooser fc = new JFileChooser();

	// in response to a button click:
	int returnVal = fc.showSaveDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		try {
			File file = fc.getSelectedFile();
			//println (file.getName ());
			//println ("path="+file.getPath ());
			println ("path="+file.getPath ());
			if (file.exists ()) {

				println ("exists");
			}

			BufferedWriter writer = new BufferedWriter( new FileWriter( file));  // file+".txt"
			writer.write(mystring);
			writer.close( );
			name = file.getPath ();
			//JOptionPane.showMessageDialog(this, "The Message was Saved Successfully!",
			// "Success!", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog( this, "could not save file", "IOException", JOptionPane.ERROR_MESSAGE );
		}
	}
	return name;
} // saveString ?

//
public String getSaveFileName () {
	String name = null;
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
		e.printStackTrace();
	}

	// create a file chooser
	final JFileChooser fc = new JFileChooser();

	// in response to a button click:
	int returnVal = fc.showSaveDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		//println (file.getName ());
		//println ("path="+file.getPath ());
		println ("path="+file.getPath ());

		if (file.exists ()) {
			println ("exists");
			int option = JOptionPane.showConfirmDialog(
					frame, 
					"this file is already exists, do you want overwrite it?", 
					"warning", 
					JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION)
				return null;
		} 
		else {
			String thename = file.getPath ();
			if (!thename.endsWith (".xml")) {
				File file1 =new File (thename+".xml");
				if (file1.exists ()) {
					println ("exists");
					int option = JOptionPane.showConfirmDialog(
							frame, 
							"this file is already exists, do you want overwrite it?", 
							"warning", 
							JOptionPane.YES_NO_OPTION);
					if (option != JOptionPane.YES_OPTION)
						return null;
				}
			}
		}
		name = file.getPath ();
		//JOptionPane.showMessageDialog(this, "The Message was Saved Successfully!",
		// "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	return name;
}


public String getOpenFileName () {
	String name = null;
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
		e.printStackTrace();
	}

	// create a file chooser
	final JFileChooser fc = new JFileChooser();

	// in response to a button click:
	int returnVal = fc.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		//println (file.getName ());
		//println ("path="+file.getPath ());
		println ("path="+file.getPath ());
		if (!file.exists ()) {
			//JOptionPane.showMessageDialog(this, "this file does not exists!", 
			// "error!", JOptionPane.INFORMATION_MESSAGE);
			// return null;
		}
		name = file.getPath ();
		//JOptionPane.showMessageDialog(this, "The Message was Saved Successfully!",
		// "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	return name;
}

//
public void loadData () {
	String name  = getOpenFileName ();
	XML xml;
	if (!name.endsWith (".xml")) {
		File file = new File (name+".xml");
		if (file.exists ()) {
			name += ".xml";
		}
		else {
			JOptionPane.showMessageDialog(
					this, 
					"can't open this file", 
					"error!", 
					JOptionPane.ERROR_MESSAGE );
			return ;
		}
	}

	xml = loadXML (name);
	if (xml != null) {
		//   println (xml.getInt ("time"));
		servoList.removeAll ();
		setTime (xml.getInt ("time"));  
		String xmlName = xml.getName ();
		if (xmlName != "df24servo") {
			JOptionPane.showMessageDialog(
					this, 
					"data file foramt error!", 
					"error!", 
					JOptionPane.ERROR_MESSAGE );
			return ;
		}
		fileName = name;
		XML [] children = xml.getChildren ("servo");
		println ("length="+children.length);
		for (int i=0; i < children.length; i++) {
			int id = children[i].getInt ("id");

			int layer = children[i].getInt ("layer");
			int y_axis = layerToAxis (layer);
			float beginTime = children[i].getFloat ("begin_time");
			int x1_axis = timeLine.toAxis (beginTime);
			float endTime = children[i].getFloat ("end_time");
			int x2_axis = timeLine.toAxis (endTime);
			int servoWidth = x2_axis-x1_axis;
			int beginAngle = children[i].getInt ("begin_angle");
			int endAngle = children[i].getInt ("end_angle");

			Servo servo = new Servo(cp5, "servo"+(++servoList.addId));
			servoList.add (servo);  
			++servoList.addId;
			servo.setServoId (id);
			servo.setPosition (x1_axis, y_axis);
			servo.setSize (x2_axis-x1_axis, lineHeight);
			servo.setBeginAngle (beginAngle);
			servo.setEndAngle (endAngle);

			servo.setMinWidth (50);
			servo.setMaxWidth (maxX-minX);
			servoList.setValue (servo);
		}
	}
}


//
public void loadSet (String theName) {
	String [] lines = loadSetFile (theName);
	if (lines != null) {
		for (int i=0; i < lines.length; i++) {
			String word [] = lines[i]
				.replaceAll ("\\s*", "")
				.split ("=");
			if ((word.length == 1 && word[0].equals ("")) 
					|| (word[0].length() > 0 && (word[0].charAt(0) == '#' || word[0].charAt(0) == ';')))
				continue;
			if (word.length == 2) {
				//println ("ok="+"<"+word[0]+">"+" <"+word[1]+">");
				setValue (word[0], word[1]);
			}
			else {
				println ("set file format error!");
			}
		}
	}
	if (protocol == null) {
		string1 = "#";
		string2 = "P";
		string3 = "\r";
	}
}

//
public void setValue (String variable, String value) {
	try {
		if (value.startsWith ("0x"))
			value = value.substring (2, value.length ());

		if (variable.equals("defaultColor")) {
			servoList.defaultColor = (int)Long.parseLong (value, 16);
			servoList.currentColor = servoList.defaultColor;
		}
		else if (variable.equals("pressedColor"))
			servoList.pressedColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("enterColor"))
			servoList.enterColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("moveColor"))
			servoList.moveColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("sideColor"))
			servoList.sideColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("enterColor"))
			servoList.enterColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("leftColor"))
			servoList.leftColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("rightColor"))
			servoList.rightColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("beginAngleColor"))
			servoList.beginAngleColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("endAngleColor"))
			servoList.endAngleColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("servoIdColor"))
			servoList.servoIdColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("enterSideColor")) {
			servoList.enterSideColor= (int)Long.parseLong (value, 16);
			servoList.defaultSideColor= servoList.enterSideColor;
		}
		else if (variable.equals("errorColor"))
			servoList.errorColor= (int)Long.parseLong (value, 16);
		else if (variable.equals ("backColor"))
			backColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("rectLineColor"))
			rectLineColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("timeTextColor"))
			timeTextColor = (int)Long.parseLong (value, 16);///////////////
		else if (variable.equals ("playModeTextColor"))
			playModeTextColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("captionTextColor"))
			captionTextColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("timeLineColor"))
			timeLineColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("backImage")) {
			backImage = loadImage (value.substring (1, value.length ()-1));
			if (backImage == null)
				println ("can't open file: "+value);
		}
		else if (variable.equals("isShowSide")) {
			if (value.equals ("false"))
				servoList.isShowSide= false;
			else if (value.equals ("true"))
				servoList.isShowSide= true;
		}
		else if (variable.equals("radius"))
			servoList.radius = Integer.parseInt(value, 10);
		else if (variable.equals("playTime"))
			playTime = Integer.parseInt(value, 10);
		else if (variable.equals ("waveMin"))
			waveMin = Integer.parseInt(value, 10);
		else if (variable.equals ("waveMax"))
			waveMax = Integer.parseInt(value, 10);
		else if (variable.equals ("angleMin"))
			angleMin = Float.parseFloat(value);
		else if (variable.equals ("angleMax"))
			angleMax = Float.parseFloat(value);    //
		else if (variable.equals ("beginServoId"))
			beginServoId = Integer.parseInt(value);
		else if (variable.equals ("endServoId"))
			endServoId = Integer.parseInt(value);
		else if (variable.equals ("protocol")) {
			protocol = value;
			readProtocol ();
		}
		else {
			println ("error! no this variable: "+variable+" "+value);
		}
	}
	catch (NumberFormatException NFE) {
		println ("error! NumberFormatException : "+variable+" "+value);
	}
}

//read set file to String by the file name
public String [] loadSetFile (String theName) {
	File file = new File (theName);
	try {
		println (file +" "+file.getCanonicalPath ());
		if (file.exists () && file.isFile ()) {
			String lines [] = loadStrings (file);
			return lines;
		}
		else {
			println ("error! setFile does not exist!");
		}
	}
	catch (Exception e) {
	}
	return null;
}


//
//void readProtocol () {
//    if (protocol.charAt (0) == '\"' && protocol.charAt (protocol.length ()-1)== '\"') {
//        protocol = protocol.substring(1, protocol.length()-1);
//        protocol = protocol.replace ("\\r", "\r")   
//            .replace ("\\n", "\n")
//                .replace ("\\t", "\t")
//                    .replace ("\\s", " "); //the space: ' '
//        int sub_id = protocol.indexOf ("%i");
//        int sub_value = protocol.indexOf ("%v");
//        if (sub_id >= 0 && sub_value > 0 && (sub_value > sub_id)) {
//            string1 = protocol.substring (0, sub_id);
//            string2 = protocol.substring (sub_id+2, sub_value);
//            string3 = protocol.substring (sub_value+2, protocol.length ());
//            println (string1+" "+sub_id);
//            println (string2+" "+sub_value);
//            println (string3);
//        }
//
//        println (protocol);
//    }
//}



//
public void readProtocol () {
	// myString = new String ();
	if (protocol.length () > 4 && protocol.startsWith("\"") && protocol.endsWith ("\"")) {
		String myString = shiftString (protocol.substring (1, protocol.length()-1));

		int sub_id = myString.indexOf ("%i");
		int sub_value = myString.indexOf ("%v");
		if (sub_id >= 0 && sub_value > 0 && (sub_value > sub_id)) {
			string1 = myString.substring (0, sub_id);
			string2 = myString.substring (sub_id+2, sub_value);
			string3 = myString.substring (sub_value+2, myString.length ());
			println (protocol);
			println ("protocol start with \""+string1+"\" in "+sub_id);
			println ("protocol middle with \""+string2+"\" in "+sub_value);
			println ("protocol end with \""+string3+"\" in "+PApplet.parseInt(sub_value+3));
			println ("protocol result: "+myString);
		}
	}
}

//"support \s \r \n \t \\ " //
public String shiftString (String theString) {
	String myString = new String();
	for (int i=0; i<theString.length(); i++) {
		if (theString.charAt(i) != '\\') {
			myString += theString.charAt(i);
		}
		else {        // theString.charAt(i) == '\\'
			if (i < theString.length()-1) {
				switch(theString.charAt(i+1)) {
					case 's':
						myString += ' ';
						i++;
						continue;
					case 'r':
						myString += '\r';
						i++;
						continue;
					case 'n':
						myString += '\n';
						i++;
						continue;
					case 't':
						myString += '\t';
						i++;
						continue;
					case '\\':
						myString += '\\';
						i++;
						continue;
					default:
						if ( i < theString.length()-2) {
							try { 
								int myHex = Integer.parseInt (theString.substring (i+1, i+3), 16);
								//println (myHex);
								myString += (char)myHex;
								i+=2;
								continue;
							}
							catch (NumberFormatException NFE) {
								println ("error! NumberFormatException : "+theString.substring (i+1, i+3));
							}
						}
						else {
							println ("error! unknow char: "+theString.charAt (i+1));
						}
				}
			}
		}
	}
	return myString;
}

//
public void setTimeDialog () {
	timeLine.stop ();
	to_begin ();
	String inputValue = JOptionPane.showInputDialog("Set the total time (second), must be integer");
	try {
		if (inputValue != null) {
			move = 0;
			timeLine.setAxis (minX);
			setTime (inputValue);
		}
	}
	catch (NumberFormatException NFE) {
		println ("error! NumberFormatException : "+inputValue);
	}
}


//
public void setTime (int theSecond) {
	move = 0;
	timeLine.setAxis (minX);
	//if (
	playTime = constrain (
			theSecond, 
			PApplet.parseInt (timeLine.toValue (servoList.getMaxAxis ())+1), 
			3600);
	//lineStep = playTime/20.0;
	timeLine.setRange (0, playTime);
	timeLine.setSize (minX, playTime*scale+minX);
	servoList.setAllMaxWidth (playTime*scale);
}

public void setTime (String theSecond) {
	move = 0;
	timeLine.setAxis (minX);
	playTime = constrain (
			Integer.parseInt (theSecond.trim ()), 
			PApplet.parseInt (timeLine.toValue (servoList.getMaxAxis ())+1), 
			3600);
	//lineStep = playTime/20.0;
	timeLine.setRange (0, playTime);
	timeLine.setSize (minX, playTime*scale+minX);
	servoList.setAllMaxWidth (playTime*scale);
}

//
public int layerToAxis (int theValue) {
	return theValue*lineHeight+lineHeight*2;
}

//
public int getLayer (int theAxis) {
	return PApplet.parseInt((theAxis-lineHeight*2)/lineHeight);
}


/******************** end of fileProcess.pde ********************/
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

/******************** start of keyProcess.pde ********************/
//hot key process
public void keyPressed () {
	if (key == ESC) {
		inclose ();
		key=0;
	}
	Servo servo;
	switch (key) {
		//space key for play and pause
		case ' ':
			playState = !playState;
			if (playState && !servoList.isIdClash) {
				servoList.setMove(false);
				timeLine.start ();
			}
			else {
				servoList.setMove(true);
				timeLine.pause ();
			}
			//    servoList.add ();
			break;
		//b key for stop
		case 'b':
		case 'B':
			servoList.setMove(true);
			timeLine.stop ();
			break;
		//q key for clear
		case 'q':
		case 'Q':
			timeLine.stop ();
			servoList.clear ();
			servoList.isIdClash = false;
			break;
		//a key for toBegin 
		case 'a':
		case 'A':
			servoList.setAllPosition (-move);
			timeLine.move (-move);
			move = 0;
			servoList.setMove(true);
			timeLine.stop ();
			timeLine.setValue (0);
			break;
		//z key for undo
		case 'z':
		case 'Z':
			servoList.undoList ();
			break;
		//y key for redo
		case 'y':
		case 'Y':
			timeLine.stop ();
			servoList.redoList ();
			break;
		//s key for save data
		case 's':
		case 'S':
			if (servoList.list.isEmpty ()) {

				println ("nothing for save!");
			}
			else
				saveData ();
			break;
		//o key open file
		case 'o':
		case 'O':
			timeLine.stop ();
			loadData ();
			break;
		//l key for show rect line or not
		case 'l':
		case 'L':
			isDrawLines = !isDrawLines;
			break;
		//delete key for delete Servo
		case DELETE:
			println ("type delete");
			servo = servoList.currentServo;
			if (servo != null) {
				println ("type delete");
				servoList.remove (servo);
			}
			break;
		case CODED:
			switch (keyCode) {
				//left key for move current Servo to left
				case LEFT:
					//timeLine.pause ();
					servo = servoList.currentServo;
					if (servo == null) {
						moveLeft (10);
					} 
					else {
						if (!playState) {
							servo.setPosition (servo.getPx1 ()-1, servo.getPy1 ());
						}
					}
					break;
				//right key for move current Servo to right
				case RIGHT:
					//timeLine.pause ();
					servo = servoList.currentServo;
					if (servo == null) {
						moveRight (10);
					}
					else {
						if (!playState) {
							servo.setPosition (servo.getPx1 ()+1, servo.getPy1 ());
						}
					}
					break;
			}
			break;
	}
	servoList.CheckIdClash ();
}

//
public void keyReleased () {
	servoList.checkClash ();    // not necessary
	servoList.removeOut ();
	//key = 0;    //fix a bug
}


/******************** end of keyProcess.pde ********************/
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

/******************** start of mouseProcess.pde ********************/
//
public void mousePressed () {
	//while mouse clicked the add button, then add a new Servo
	if (!timeLine.isStart && inRectRange (add_x, add_y, add_x+add_w, add_y+add_h)) {
		isDrawRect = true;
	}
	else 	//if mouse pressed in top of windows, then set move windows
		if (inRectRange (0, 0, width-75, 30)) {
			AWTUtilities.setWindowOpacity(frame, 0.9f);
			isMoveWindow = true;
			cx = mouseX;
			cy = mouseY;
		}

	//show the Time line
	servoList.showTimeLine ();

	boolean isInRange = servoList.isOneInRange ();
	if (!isInRange) { //if none of Servos is in mouse Range
		servoList.currentServo = null;	
		//if mouse left dragged and mouse is the space area in the rect,then set move face
		if (mouseButton == LEFT  && inRectRange (minX, minY, maxX, maxY)) {
			isMoveFace = true;
			moveOldX = mouseX;
		}
	} 
	//if mouse center button is pressed, then set move face
	if (mouseButton == CENTER) {
		isMoveFace = true;
		moveOldX = mouseX;
	}

	if (mouseButton == LEFT) {
		if (!isInRange && inRectRange (minX, minY, maxX-100, maxY) && !timeLine.isStart) {
			;//servoList.add (mouseX, mouseY);
			//servoList.list.get (servoList.list.size ()-1).isMove = true;
		}
		else if (timeLine.inRange ()) { 
			//if mouse left button is pressed and in timeline button range, then set move timeline
			timeLine.isBeginMove = true;
		}
	}
	else if (keyPressed && key == CODED && keyCode == CONTROL && mouseButton == RIGHT) {
		// if control key pressed and mouse right button clicked, then delete a Servo which in mouse range
		servoList.remove ();
	}

	//fix bug
	if (isPlayModeOpen)
		fileOpenButton.playModeButton.setOpen (false);
	if (isPortSelectOpen)
		serialOpen.serialList.setOpen (false);
	if (isBaudSelectOpen)
		serialOpen.baudSelect.setOpen (false);

	if ( fileOpenButton.playModeButton.isOpen ())
		isPlayModeOpen = true;
	else 
		isPlayModeOpen = false;

	if ( serialOpen.serialList.isOpen ())
		isPortSelectOpen = true;
	else 
		isPortSelectOpen = false;

	if ( serialOpen.baudSelect.isOpen ())
		isBaudSelectOpen = true;
	else 
		isBaudSelectOpen = false;


	//println (fileOpenButton.playModeButton.isOpen ());
}

//
public void mouseClicked () {
}



// check clash and fix
public void mouseReleased () {
	if (isDrawRect) {
		servoList.add (mouseX, mouseY);
		isDrawRect = false;
	}
	servoList.removeOut ();
	servoList.checkOut ();
	servoList.checkClash ();
	servoList.removeOut ();//////////////
	servoList.fixInRange ();
	servoList.CheckIdClash ();
	timeLine.isBeginMove = false;
	isMoveWindow = false;
	AWTUtilities.setWindowOpacity(frame, 1.0f);

	//    for (int i=0; i < servoList.list.size (); i++) {
	//        Servo servo = servoList.list.get(i);
	//        println ("++"+servo.getPx1()+" "+servo.getPx2()+" "+servo.getPy1()+" "+ servo.getPy2());
	//    }
	if (isMoveFace)
		isMoveFace = false;
}

//
public void mouseDragged () {
	Point mouse, winloc;
	mouse = MouseInfo.getPointerInfo ().getLocation ();
	winloc =frame.getLocation ();

	if (!frame.isUndecorated()) {
		winloc.x += 3;
		winloc.y += 29;
	}
	mouseX = mouse.x-winloc.x;
	mouseY = mouse.y-winloc.y;

	if (isMoveWindow) {
		frame.setLocation(
				frame.getLocationOnScreen().x+mouseX-cx, 
				frame.getLocationOnScreen().y+mouseY-cy);
		oldx = mouseX;
		oldy = mouseY;
	}
}

// mouseWheel (MouseEvent event)
public void mouseWheel (int amount) {
	// scale += amount;
	servoList.CheckIdClash ();
}

/******************** end of mouseProcess.pde ********************/
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

/***************** start of playButton.pde  *****************/
/*
 *	class PlayButton :
 *	Button toBeginButton;
 *	Button playButton;
 *	Button pauseButton;
 *	Button stopButton;
 *	DropdownList playModeButton;
 *	Slider playSpeed;
 *	createPlayButton (ControlP5 thecp5, int theX, int theY)
 */

//
public class PlayButton {
	//ControlP5 mycp5;
	controlP5.Button toBeginButton;
	controlP5.Button playButton;
	controlP5.Button pauseButton;
	controlP5.Button stopButton;

	controlP5.Slider speedSlider;
	controlP5.Textlabel textlabel;

	controlP5.Button setTimeButton;


	//load image for button
	PImage[] toBegin_img	= { 
		loadImage ("toBegin_a.png"), 
		loadImage ("toBegin_b.png"), 
		loadImage ("toBegin_c.png")
	};
	PImage[] play_img	= { 
		loadImage ("play_a.png"), 
		loadImage ("play_b.png"), 
		loadImage ("play_c.png")
	};
	PImage[] pause_img	= { 
		loadImage ("pause_a.png"), 
		loadImage ("pause_b.png"), 
		loadImage ("pause_c.png")
	};
	PImage[] stop_img	= { 
		loadImage ("stop_a.png"), 
		loadImage ("stop_b.png"), 
		loadImage ("stop_c.png")
	};


	PlayButton (ControlP5 thecp5, int theX, int theY) {
		createPlayButton (thecp5, theX, theY);
	}

	//add toBegin button
	public void createPlayButton (ControlP5 thecp5, int theX, int theY) {
		toBeginButton = thecp5.addButton ("toBeginButton")
			.setImages (toBegin_img)
			.setPosition (theX, theY)
			.setColorBackground (color (100, 50, 20))
			.setId (ServoId.TO_BEGIN_BUTTON)
			.setSize (25, 23)
			;

		toBeginButton.getCaptionLabel()
			.setFont (cfont)
			;

		//add play button
		playButton = thecp5.addButton ("Play")
			.setLabel ("Play")
			.setImages (play_img)
			.setPosition (theX+30, theY)
			.setColorBackground (color (100, 50, 20))
			.setId (ServoId.PLAY_BUTTON)
			.setSize (25, 23)
			;
		playButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont(buttonfont)
			;
		//add pause button
		pauseButton = thecp5.addButton ("Pause")
			.setImages (pause_img)
			.setPosition (theX+60, theY)
			.setColorBackground (color (100, 50, 20))
			.setId (ServoId.PAUSE_BUTTON)
			.setSize (25, 23)
			;
		pauseButton .align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont (buttonfont)
			;
		//add stop button
		stopButton = thecp5.addButton ("Stop")
			.setImages (stop_img)
			.setPosition (theX+90, theY)
			.setColorBackground (color (100, 50, 20))
			.setId (ServoId.STOP_BUTTON)
			// .align(CENTER, CENTER, CENTER, CENTER)
			.setSize (25, 23)
			;
		stopButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont (buttonfont)
			;

		speedSlider = thecp5.addSlider ("Speed")
			.setSize (100, 20)
			.setRange (1, 99)
			.setValue (10)
			.setVisible (false)
			.setId (ServoId.SPEED_BUTTON)
			.setSliderMode(Slider.FLEXIBLE)
			.setColorBackground (color (80, 80, 70))
			.setColorForeground(color(150, 150, 50))
			.setPosition (260, 36)
			;

		//add play mode button
		textlabel = cp5.addTextlabel ("text")
			.setText ("Play Mode")
			.setVisible (false)
			.setColor (playModeTextColor)
			.setPosition (450, 40)
			;
		textlabel.setFont (buttonfont)
			.align(CENTER, CENTER, CENTER, CENTER);

		//add set time Button
		setTimeButton = thecp5.addButton ("setTimeButton")
			.setLabel ("Set time")
			.setPosition (930, 29)
			.setVisible (false)
			.setColorBackground (color (150, 100, 20))
			.setId (ServoId.SETTIME_BUTTON)
			.setSize (80, 20)
			;

		setTimeButton.align(CENTER, CENTER, CENTER, CENTER)
			.captionLabel()
			.toUpperCase(false)
			.setFont(buttonfont)
			;
	}
}


/***************** end of playButton.pde  *****************/
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

/******************** start of sendData.pde ********************/


public class SerialWrite implements Runnable {
	boolean flag = true;    //if false ,the thread will die
	public void run () {
		long oldTime = millis ();
		long currentTime;
		int x1 = timeLine.local_x;
		while (flag) {
			currentTime = millis ();                 
			if (serialOpen.isOpen () && !timeLine.isBeginMove && timeLine.local_x != x1) {
				cmdSend (timeLine.local_x);
				//println (currentTime);
				//oldTime = currentTime;
				x1 = timeLine.local_x;
			}
			delay (10);
			//delay (10);
		}
	}
}

//read the TimeLine value and get <Id Angle> then send to serial port
public void cmdSend (float axisValue) {
	for (int i=0; i < servoList.list.size (); i++) {
		if (axisValue > (servoList.list.get(i)).getPx1() 
				&& axisValue < (servoList.list.get(i)).getPx2()) {
			float data = map (axisValue-(servoList.list.get(i)).getPx1(), 
					0, servoList.list.get(i).getWidth (), 
					servoList.list.get(i).getBeginAngle(), 
					servoList.list.get(i).getEndAngle());
			println (">"+servoList.list.get(i).getServoId ()+"  "+PApplet.parseInt (data));
			serialOpen.cmdSend (servoList.list.get(i).getServoId (), PApplet.parseInt (data));
			delay(3);
		}
	}
	serialOpen.myPort.write ('\r');
	println ("\r");
}

/******************** end of sendData.pde ********************/
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

/******************** start of serialOpen.pde ********************/
/*
 *    serial by lisper <leyapin@gmail.com> 2013-11-19
 *    public class SliderClass 
 *    
 *    
 *    public SerialOpen (PApplet theClass, ControlP5 cp5, int x, int y)
 *    public boolean isOpen () //if  serial is open
 *    public void CreateButton (int x, int y)
 *    public void cmdSend (int num, float angle) {
 *    public void process ()     //call it while clicked the button
 *    
 *    
 */

public class SerialOpen {
	PApplet outClass;
	private ControlP5 mycp5;
	private int x;
	private int y;
	public Serial myPort;
	public DropdownList serialList;
	public DropdownList baudSelect;
	//public Button updateButton;
	public controlP5.Button connectButton;
	public boolean isSerialOpen;
	String portName;
	public boolean isInMotify;
	//load image for button
	PImage[] connect_img    = { 
		loadImage ("connect_a.png"), 
		loadImage ("connect_b.png"), 
		loadImage ("connect_c.png")
	};
	PImage[] disconnect_img    = { 
		loadImage ("disconnect_a.png"), 
		loadImage ("disconnect_b.png"), 
		loadImage ("disconnect_c.png")
	};
	//private int baud_rate = 57600;

	private String baudList[] = {
		"2400", "4800", "9600", "14400", "19200", "28800", "57600", "115200", "1000000"
	};
	private int baudIntList[] = {
		2400, 4800, 9600, 14400, 19200, 28800, 57600, 115200, 1000000
	};

	//
	public SerialOpen (PApplet theClass, ControlP5 cp5, int x, int y) {
		this.mycp5 = cp5;
		this.x = x;
		this.y = y;
		outClass = theClass;
		//serialNum = myPort.list().length;    //get available serial number
		CreateButton (x, y);
		isSerialOpen = false;
		CheckThread check = new CheckThread ("hello");
		Thread t1 = new Thread (check);
		t1.start ();
	}

	//
	public void CreateButton (int x, int y) {
		//add connect button
		connectButton = this.mycp5.addButton ("ConnectButton")
			.setLabel ("Connect")
			.setImages (connect_img)
			.setPosition (x, y-20)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			.setColorBackground (color (0, 0, 0, 1))
			.setId (ServoId.OPEN_BUTTON)
			.setSize (60, 23)
			;
		connectButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER);

		//serial dropdownlist
		serialList = this.mycp5.addDropdownList("No Port")
			.setPosition(x+80, y+4)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			.setItemHeight(20)
			//  .setfont (new ControlFont(baudFont, 16))
			.setBarHeight(23)
			.setHeight (30)
			.setId (ServoId.PORTSELECT_BUTTON)
			.setSize (90, 120)           
			;

		serialList.captionLabel()
			.toUpperCase(false)
			.align(LEFT, CENTER)
			//.setSize (14)
			.setColor (color (100, 150, 200))
			.setFont (new ControlFont(baudFont, 14))
			;
		//add set baud button
		baudSelect = this.mycp5.addDropdownList ("baud")
			.setPosition (x+180, y+4)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			.setItemHeight(12)
			.setId (ServoId.BAUDSELECT_BUTTON)
			.setBarHeight(23)
			//.setHeight (10)
			.setSize (80, 120)
			;
		baudSelect.captionLabel()
			.toUpperCase(false)
			.align(LEFT, CENTER)
			.setColor (color (100, 150, 200))
			.setFont (new ControlFont(baudFont, 14));

		for (int i=0; i<baudList.length; i++) {
			baudSelect.addItem (baudList[i], i);
		}
		buildSerialList ();
		baudSelect.setValue (6);
	} // createButton ?

	//////////////////////////////////////
	//"open" button process, when it clicked, call in ControlEvent 
	public void process () {
		if (myPort.list ().length  == 0) {  
			println ("no available serial port!");
		}
		else {
			if (isOpen ()) {    //if serial is open, close it
				close ();
			}  
			else {    //if serial is close, open it
				open ();
			}
		}
	} // process ?

	//open serial
	public void open () {
		myPort = new Serial (outClass, 
				Serial.list()[PApplet.parseInt(serialList.getValue())], 
				baudIntList[PApplet.parseInt(baudSelect.getValue())]); // open serial
		if (myPort == null) { 
			//   connectButton.setState (false);  
			println ("error! can't open serial port!");
		}
		else {    //successed open serial
			isSerialOpen = true;
			connectButton.setLabel ("Disconnect");
			connectButton.setImages (disconnect_img);
			// connectButton.setState (true);
			connectButton.setColorBackground (color (0, 150, 0)); 
			// openButton.setforeColor (0);
			println (myPort.list()[PApplet.parseInt(serialList.getValue())]+" is opened");
		}

	}

	//close serial
	public void close () {
		timeLine.stop ();//////////////////////////////
		myPort.stop ();
		isSerialOpen = false;
		connectButton.setLabel ("Connect"); 
		connectButton.setImages (connect_img);
		//connectButton.setState (false);  
		connectButton.setColorBackground (color (80, 80, 80, 0)); 
		println (myPort.list()[PApplet.parseInt(serialList.getValue())]+" is closed");

	}

	//if serial is open
	public boolean isOpen () {
		return isSerialOpen;
	}

	// command send to serial port
	public void cmdSend (int num, float angle) {
		int value = angleToValue (angle);
		//myPort.write (string1+num+string2+value+string3);
		myPort.write (string1+num+string2+value);
		println (string1+num+string2+value);
	}

	//angle(0~180) to value(500~2500)
	private int angleToValue (float angle) {
		return PApplet.parseInt((waveMax-waveMin) * angle / (angleMax-angleMin) + waveMin);
	} // SerialOpen ?

	//
	public class CheckThread implements Runnable {
		private String name;
		private int length;
		CheckThread (String name) {
			this.name = name;
			this.length = myPort.list ().length;
		}
		//
		public void run () {
			for (;;) {
				delay (1000);
				if (length != myPort.list ().length) {
					isInMotify = true;
					buildSerialList ();
					length = myPort.list ().length;
					isInMotify = false;
					// println (serialList.getItem (0).getName ());
				}
			}
		} // run ?
	} // CheckThread ?

	//synchronized ?
	private void buildSerialList () {
		serialList.clear ();

		for (int i=0 ;i<myPort.list ().length; i++) {
			serialList.addItem(myPort.list()[i], i);    //add serial name to serialList
		}
		if (myPort.list().length > 0) {
			serialList.setLabel (myPort.list()[0]);
			println (serialList.getItem (0).getName ());
			//println (">>"+serialList.getItem(int (serialList.getValue ())).getName ());
		}
		else {
			serialList.setLabel ("No Port");
		}
	}
} // SerialOpen ?

/******************** end of serialOpen.pde ********************/
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
 
/******************* start of timeLine.pde1 *********************/
/**
 * TimeLine by lisper 2013-11-19
 *
 *    class    TimeLine (int theY , int length, int theWidth, int theHeight) 
 *             TimeLine (int theY, int lenght)
 *    method : 
 *        update ()
 *        setSize (int theLeftAxis, int theRightAxis)
 *        setRange (float theMinValue, float theMaxValue)
 *        setAxis (int theAxisX)
 *        setValue (flaot theValue)
 *        setLineColor (int r, int g, int b)
 *        setLineColor (color theColor)
 *        setRectColor (int r, int g, int b)
 *        setRectColor (color theColor)        
 *        float getValue ()
 *        int getAxis ()
 *        setPlayMode ()    => CIRCLE or TO_AND_FRO or ONETIME
 *        setDelay (int theTime)
 *        setStep (float theStep)
 *        start ()
 *        pause ()
 *        stop ()

 *  class MyTask extends java.util.TimerTask
 *    
 */


//
public class TimeLine {
	public static final int CIRCLE = 1;
	public static final int ONETIME = 2;
	public static final int TO_AND_FRO = 3;
	Timer timer;
	int local_x;    //the line location 
	int local_xp;
	int local_y;    //the line axisY
	float length;    // the line length
	int width;    //the rectangle size
	int height;
	int leftAxis;
	int rightAxis;
	int delayTime = 33;  //for play speed
	float step = 1;
	int lineColor = color (30, 40, 30, 120);
	int rectBackColor = color (255, 247, 153, 60);
	int rectColor = color (150, 150, 100, 60); //while pressed
	int playMode = CIRCLE;    //normal play mode

	float minValue;
	float maxValue;
	float value;

	boolean isStart;    
	boolean isBeginMove;
	int playdir = 1; //the playdir in to-and-fro playMode


	//location and size and line length
	public TimeLine (int theY, int length, int theWidth, int theHeight) {
		this.local_y = theY;
		this.width = theWidth;
		this.height = theHeight;
		this.length = length;
	}

	//
	public TimeLine (int theY, int length) {
		this.local_y = theY;
		this.width = 10;
		this.height = 20;
		this.length = length;
	}

	// start play 
	public void start () {
		if (!isStart) {
			timer = new Timer ();
			timer.schedule (new MyTask(), 0, delayTime);
			isStart = true;
		}
	}

	//pause play
	public void pause () {
		if (isStart) {
			timer.cancel ();
			isStart = false;
		}
	}

	//stop play
	public void stop () {
		if (isStart) {
			timer.cancel ();
			isStart = false;
			playdir = 1;
		}
	}

	//
	public void setSize (int theLeft, int theRight) {
		leftAxis = theLeft;
		rightAxis = theRight;
	}

	//
	public void setDelay (int theTime) {
		delayTime = theTime;
	}

	//
	public void setRange (float theMin, float theMax) {
		minValue = theMin;
		maxValue = theMax;
	}

	//
	public void setAxis (int theAxis) {
		//        if (theAxis >= leftAxis && theAxis <= rightAxis)
		//            local_x = theAxis;
		//        else
		//            local_x = leftAxis;
		local_x = constrain (theAxis, leftAxis, rightAxis);
		value = map (local_x, leftAxis, rightAxis, minValue, maxValue);
	}

	//
	public float toValue (int theAxis) {
		return map (theAxis, leftAxis, rightAxis, minValue, maxValue);
	}

	//
	public int toAxis (float theValue) {
		return PApplet.parseInt (map (theValue, minValue, maxValue, leftAxis, rightAxis));
	}

	//
	public void setValue (float theValue) {
		if (theValue >= minValue && theValue <= maxValue)
			value = theValue;
		else 
			value = minValue;
		local_x = PApplet.parseInt (map (value, minValue, maxValue, leftAxis, rightAxis));
	}

	//
	public void setLineColor (int theColor) {
		lineColor  = theColor;
	}

	//
	public void setLineColor (int r, int g, int b) {
		lineColor = color (r, g, b);
	}

	//
	public void setRectColor (int theColor) {
		rectColor = theColor;
	}

	//
	public void setRectColor (int r, int g, int b) {
		rectColor = color (r, g, b);
	}

	//
	public void setRectColor (int r, int g, int b, int a) {
		rectColor = color (r, g, b, a);
	}

	//
	public void setStep (float theStep) {
		step = theStep;
	}

	//
	public void setPlayMode (int theMode) {
		playdir = 1;
		playMode = theMode;
	}

	//
	public void move (int theDistance) {
		local_x += theDistance;
		leftAxis  += theDistance;
		rightAxis += theDistance;
	}

	//
	public float getValue () {  
		return map (local_x, leftAxis, rightAxis, minValue, maxValue);
	}

	//
	public int getAxis () {
		return local_x;
	}

	//
	public void update () {
		noStroke ();
		fill (60, 60, 60);
		//        rect (0, minY-12, winWidth, 12);
		//        rect (0, maxY, winWidth, 24);

		rect (minX+move-20, minY-12, playTime*scale+40, 13);
		stroke (250, 250, 250);
		line (minX+move-20-1 , minY-12, playTime*scale+move+minX+20-1, minY-12);
		noStroke ();
		fill (60, 60, 60);
		rect (minX+move-20, maxY, playTime*scale+40, 23);
		stroke (250, 250, 250);
		line (minX+move-20-1, maxY+23-1, playTime*scale+move+minX+20-1, maxY+23-1);
		textSize (10);
		strokeWeight (1);
		fill (timeTextColor);
		if (isDrawLines) {
			drawLines ();
		}
		drawScale ();
		if (isBeginMove) {
			local_x = mouseX;
			if (local_x < leftAxis)
				local_x = leftAxis;
			else if (local_x > rightAxis)
				local_x = rightAxis;

			value = map (local_x, leftAxis, rightAxis, minValue, maxValue);
			stroke (rectColor);
			fill (rectColor);
			//   if (serialOpen.isOpen () && local_x!=local_xp)
			//     cmdSend (local_x);
			local_xp = local_x;
		} 
		else {
			stroke (rectBackColor);
			fill (rectBackColor);
		}
		// triangle (local_x, local_y-15-1, local_x-width/2.0+1, local_y-height/2.0-1, local_x+width/2.0-1, local_y-height/2.0-1);
		rect (local_x-width/2.0f, local_y-height/2.0f, width, height);
		stroke (250, 200, 0);
		line (local_x-width/2.0f, local_y-height/2.0f, local_x+width/2, local_y-height/2.0f);
		stroke (lineColor);
		fill (lineColor);
		line (local_x, local_y-height/2-1-2, local_x, local_y-height/2-length);
		triangle (local_x-2, local_y-height/2-length, local_x+2, local_y-height/2-length, local_x, local_y-height/2-length+4);
		triangle (local_x, local_y-height/2-4-2, local_x-2, local_y-height/2-2, local_x+2, local_y-height/2-2);
		//textMode (CENTER);
		text (String.format ("%.2f", value), local_x, local_y-height/2-length-2);
	}

	//
	public synchronized void drawLines () {
		stroke (rectLineColor);
		for (int i=minX+move; i<=playTime*scale+minX+move; i+=scale) {
			line (i, minY, i, maxY);
		}
		for (int i=minY; i<=maxY; i+=lineHeight) {
			line (minX+move, i, playTime*2*30+minX+move, i);
		}


	}

	//
	public void drawScale () {
		float j=0;
		textSize (9);
		for (int i=minX+move; i<= playTime*scale+minX+move; i+=scale/2, j += lineStep) {
			if ((i-minX-move) % scale == 0) {
				stroke (200, 100, 0);
				line (i, maxY+2, i, maxY+8);
				text (String.format ("%.1f", j), timeLine.toAxis (j), maxY+18);
			}
			else {
				stroke (200, 200, 200);
				line (i, maxY+2, i, maxY+5);
			}
		}

		stroke (timeTextColor);

		for (int i=minX+move; i<=playTime*scale+minX+move; i+= 60, j += lineStep*2) {

		}
	}

	//
	public boolean inRange () {
		if (mouseX > (local_x-width/2) && mouseX < (local_x+width/2) 
				&& mouseY > (local_y-height/2) && mouseY < (local_y+height/2)) 
			return true;
		else
			return false;
	}

	//
	class MyTask extends java.util.TimerTask {
		public void run () {
			playControl ();
			//println (value);
			//            if (serialOpen.isOpen () && !isBeginMove )
			//                cmdSend (local_x);
		}

		//
		public void playControl () {
			switch (playMode) {
				// if play mode is circle
				case CIRCLE:
					if (value <= maxValue) {
						value += step;
					} 
					else {
						value = minValue;
					}
					break;
					// if play mode is to_and_fro
				case TO_AND_FRO:
					if (value >= minValue && value <= maxValue) {
						value += step*playdir;
					} 
					else {
						if (playdir == 1)
							value = maxValue;
						else if (playdir == -1)
							value = minValue;
						playdir = -playdir;
					}
					break;
					// if play mode is onetime 
				case ONETIME:
					if (value <= maxValue) {
						value += step;
					} 
					else 
						pause ();
					break;
			}
			local_x = PApplet.parseInt (map (value, minValue, maxValue, leftAxis, rightAxis));
		}
	}
} // TimeLine ?


/******************* end of timeLine.pde1 *********************/
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
 
/******************** start of window.pde ********************/

//
public class WindowButton {
	//ControlP5 mycp5;
	controlP5.Button miniButton;
	controlP5.Button closeButton;
	controlP5.Button setButton;

	//load image for button
	PImage[] close_img = { 
		loadImage ("close_a.png"), 
		loadImage ("close_b.png"), 
		loadImage ("close_c.png")
	};

	PImage[] mini_img = { 
		loadImage ("mini_a.png"), 
		loadImage ("mini_b.png"), 
		loadImage ("mini_c.png")
	};

	PImage[] set_img = { 
		loadImage ("set_a.png"), 
		loadImage ("set_b.png"), 
		loadImage ("set_c.png")
	};

	WindowButton (ControlP5 thecp5, int theX, int theY) {
		createButton (thecp5, theX, theY);
	}

	//
	public void createButton (ControlP5 thecp5, int theX, int theY) {
		setButton  = thecp5.addButton ("setButton")
			.setCaptionLabel ("Set")
			.setPosition (theX, theY)
			.setImages (set_img)
			//.setColorBackground (color (60, 60, 60))
			.setId (ServoId.SET_BUTTON)
			.setSize (21, 20)
			;
		miniButton = thecp5.addButton ("miniButton")
			.setCaptionLabel ("-")
			.setPosition (theX+26, theY)
			.setImages (mini_img)
			//.setColorBackground (color (60, 60, 60))
			.setId (ServoId.MINIMIZE_BUTTON)
			.setSize (21, 20)
			;
		closeButton = thecp5.addButton ("closeButton")
			.setCaptionLabel ("X")
			.setPosition (theX+52, theY)
			.setImages (close_img)
			//.setColorBackground (color (60, 60, 60))
			.setId (ServoId.CLOSE_BUTTON)
			.setSize (21, 20)
			;

		miniButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER)
			.setFont (buttonfont);
		closeButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER)
			.setFont (buttonfont);
	}
}

/******************** end of window.pde ********************/
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "visual_servo_controller" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
