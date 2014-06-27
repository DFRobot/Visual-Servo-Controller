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

	void setValue (Servo servo) {
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
void add (int theX, int theY) {
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

	int myY = int (servo.getPosition().y);
	int posy;
	for (posy=0; posy<1000 && myY > posy; posy+=lineHeight)
		;
	println ("layer= "+servo.getLayer ());
	servo.setPosition (servo.getPosition ().x, posy);
}

void setAllMaxWidth (int theMaxWidth) {
	for (int i=0; i<list.size (); i++) {
		list.get(i).setMaxWidth (theMaxWidth);
	}
}

//
void add (Servo servo) {
	list.add (servo);
}

//remove if mouse in range
void remove () {
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
void remove (Servo theServo) {
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
void setMove (boolean state) {
	for (int i=0; i<list.size(); i++) {
		list.get(i).setMove(state);
	}
	for (int i=0; i<redoList.size(); i++) {
		redoList.get(i).setMove (state);
	}
}

//
void setAllPosition (int theMove) {
	for (int i=0; i<list.size (); i++) {
		list.get(i).setPosition (list.get(i).getPx1()+theMove, list.get(i).getPy1());
	}
}

//
void removeLast () {
	if (!list.isEmpty ()) {
		list.get(list.size()-1).remove ();
		list.remove (list.size ()-1);
		currentServo = null;
	}
}

//
void undoList () {
	if (!list.isEmpty ()) {
		list.get(list.size ()-1).setVisible (false);
		redoList.add (list.get(list.size ()-1));
		//list.get(list.size()-1).remove ();
		list.remove (list.size ()-1);
		currentServo = null;
	}
}

//
void redoList () {
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
void removeAll () {
	for (int i=list.size ()-1; i >= 0; i--) {
		list.get(i).remove ();
		list.remove (i);
	}
	currentServo = null;
}

//
void clear () {
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
void removeOut () {
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
	void setAllHeight (int theHeight) {
		for (int i=0; i<list.size(); i++) {
			list.get(i).setSize (list.get(i).getWidth(), theHeight);
		}
	}


	//if one of Servo in range
	boolean isOneInRange () {
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
	void fixInRange () {
		for (int i=0; i < list.size (); i++) {
			if (!((Servo)list.get(i)).inRange ()) {
				list.get(i).onLeave ();
			}
		}
	}

	//
	void showTimeLine () {

		textSize (10);
		fill (color (200, 60, 10));
		stroke (color (160, 160, 60));
		strokeWeight(0.1);
		if (currentServo != null) {
			Servo servo = currentServo;
			line (servo.getPx1 (), servo.getPy2()-lineHeight/2, servo.getPx1 (), maxY);
			line (servo.getPx2 (), servo.getPy2()-lineHeight/2, servo.getPx2 (), maxY);
			textSize (9);
			text (String.format ("%.2f", timeLine.toValue (int (servo.getPx1 ()))), servo.getPx1 (), maxY+10);
			text (String.format ("%.2f", timeLine.toValue (int (servo.getPx2 ()))), servo.getPx2 (), maxY+10);
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
				maxAxis = int (list.get(i).getPx2());
		}
		return maxAxis;
	}

	//check clash and fix, will call checkTwoLineClash ()
	void checkClash () {
		for (int i=0; i<list.size(); i++) {
			for (int j = i+1; j<list.size(); j++) {
				checkTwoLineClash (list.get(i), list.get(j));
			}
		}
	} // checkClash ?

	//will call checkClash ()
	void checkTwoLineClash (Servo s1, Servo s2) {
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
	void checkOut () {
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
	void CheckIdClash () {
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
	boolean isTwoIdClash (Servo s1, Servo s2) {
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
					(int (s1.getPy1()/24-2))+" "+(int (s2.getPy1()/24-2)));
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
