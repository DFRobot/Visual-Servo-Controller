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
void mousePressed () {
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
void mouseClicked () {
}



// check clash and fix
void mouseReleased () {
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
void mouseDragged () {
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
void mouseWheel (int amount) {
	// scale += amount;
	servoList.CheckIdClash ();
}

/******************** end of mouseProcess.pde ********************/
