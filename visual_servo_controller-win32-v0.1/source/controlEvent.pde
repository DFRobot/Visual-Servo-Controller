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
				switch (int (fileOpenButton.playModeButton.getValue())) {
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
void inclose () {
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
void create_demo () {
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
void to_begin () {
	servoList.setMove(true);
	servoList.setAllPosition (-move);
	timeLine.move (-move);
	move = 0;
	timeLine.stop ();
	timeLine.setValue (0);
}


/******************** start of controlEvent.pde ********************/
