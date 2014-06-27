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
void keyPressed () {
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
void keyReleased () {
	servoList.checkClash ();    // not necessary
	servoList.removeOut ();
	//key = 0;    //fix a bug
}


/******************** end of keyProcess.pde ********************/
