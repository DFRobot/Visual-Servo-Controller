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

import com.sun.awt.AWTUtilities;    //for support Opaque

import java.util.Timer;
import java.awt.event.*;
import processing.serial.*;
import controlP5.*;    //processing GUI library
import javax.swing.*;
// import javax.swing.JOptionPane.*;
import java.io.*;

import java.awt.*;
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


color backColor = color (17, 17, 15);

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
float angleMax = 180.0;

float lineStep = 0.5;

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
void setup() {
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
    timeLine.setStep (0.01);
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
void draw() {

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
void moveLeft (int theStep) {
    if (theStep == 0)
        theStep = 1;
    if (move >= -playTime*scale-theStep+1100 && move <= -theStep) {
        move += theStep;
        servoList.setAllPosition (theStep);
        timeLine.move (theStep);
    }
}

//move Servo face to right
void moveRight (int theStep) {
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
void drawLines (int theHeight, int theTimeWidth, int time) {
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
boolean inRectRange (int theMinX, int theMinY, int theMaxX, int theMaxY) {
    if (mouseX > theMinX && mouseX < theMaxX && mouseY > theMinY && mouseY < theMaxY)
        return true;
    else
        return false;
}


/******************** end of Servo24.pde ********************/
