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
void cmdSend (float axisValue) {
	for (int i=0; i < servoList.list.size (); i++) {
		if (axisValue > (servoList.list.get(i)).getPx1() 
				&& axisValue < (servoList.list.get(i)).getPx2()) {
			float data = map (axisValue-(servoList.list.get(i)).getPx1(), 
					0, servoList.list.get(i).getWidth (), 
					servoList.list.get(i).getBeginAngle(), 
					servoList.list.get(i).getEndAngle());
			println (">"+servoList.list.get(i).getServoId ()+"  "+int (data));
			serialOpen.cmdSend (servoList.list.get(i).getServoId (), int (data));
			delay(3);
		}
	}
	serialOpen.myPort.write ('\r');
	println ("\r");
}

/******************** end of sendData.pde ********************/
