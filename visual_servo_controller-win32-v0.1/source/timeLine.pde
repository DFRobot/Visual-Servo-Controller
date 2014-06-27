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
	color lineColor = color (30, 40, 30, 120);
	color rectBackColor = color (255, 247, 153, 60);
	color rectColor = color (150, 150, 100, 60); //while pressed
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
	void start () {
		if (!isStart) {
			timer = new Timer ();
			timer.schedule (new MyTask(), 0, delayTime);
			isStart = true;
		}
	}

	//pause play
	void pause () {
		if (isStart) {
			timer.cancel ();
			isStart = false;
		}
	}

	//stop play
	void stop () {
		if (isStart) {
			timer.cancel ();
			isStart = false;
			playdir = 1;
		}
	}

	//
	void setSize (int theLeft, int theRight) {
		leftAxis = theLeft;
		rightAxis = theRight;
	}

	//
	void setDelay (int theTime) {
		delayTime = theTime;
	}

	//
	void setRange (float theMin, float theMax) {
		minValue = theMin;
		maxValue = theMax;
	}

	//
	void setAxis (int theAxis) {
		//        if (theAxis >= leftAxis && theAxis <= rightAxis)
		//            local_x = theAxis;
		//        else
		//            local_x = leftAxis;
		local_x = constrain (theAxis, leftAxis, rightAxis);
		value = map (local_x, leftAxis, rightAxis, minValue, maxValue);
	}

	//
	float toValue (int theAxis) {
		return map (theAxis, leftAxis, rightAxis, minValue, maxValue);
	}

	//
	int toAxis (float theValue) {
		return int (map (theValue, minValue, maxValue, leftAxis, rightAxis));
	}

	//
	void setValue (float theValue) {
		if (theValue >= minValue && theValue <= maxValue)
			value = theValue;
		else 
			value = minValue;
		local_x = int (map (value, minValue, maxValue, leftAxis, rightAxis));
	}

	//
	void setLineColor (color theColor) {
		lineColor  = theColor;
	}

	//
	void setLineColor (int r, int g, int b) {
		lineColor = color (r, g, b);
	}

	//
	void setRectColor (color theColor) {
		rectColor = theColor;
	}

	//
	void setRectColor (int r, int g, int b) {
		rectColor = color (r, g, b);
	}

	//
	void setRectColor (int r, int g, int b, int a) {
		rectColor = color (r, g, b, a);
	}

	//
	void setStep (float theStep) {
		step = theStep;
	}

	//
	void setPlayMode (int theMode) {
		playdir = 1;
		playMode = theMode;
	}

	//
	void move (int theDistance) {
		local_x += theDistance;
		leftAxis  += theDistance;
		rightAxis += theDistance;
	}

	//
	float getValue () {  
		return map (local_x, leftAxis, rightAxis, minValue, maxValue);
	}

	//
	int getAxis () {
		return local_x;
	}

	//
	void update () {
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
		rect (local_x-width/2.0, local_y-height/2.0, width, height);
		stroke (250, 200, 0);
		line (local_x-width/2.0, local_y-height/2.0, local_x+width/2, local_y-height/2.0);
		stroke (lineColor);
		fill (lineColor);
		line (local_x, local_y-height/2-1-2, local_x, local_y-height/2-length);
		triangle (local_x-2, local_y-height/2-length, local_x+2, local_y-height/2-length, local_x, local_y-height/2-length+4);
		triangle (local_x, local_y-height/2-4-2, local_x-2, local_y-height/2-2, local_x+2, local_y-height/2-2);
		//textMode (CENTER);
		text (String.format ("%.2f", value), local_x, local_y-height/2-length-2);
	}

	//
	synchronized void drawLines () {
		stroke (rectLineColor);
		for (int i=minX+move; i<=playTime*scale+minX+move; i+=scale) {
			line (i, minY, i, maxY);
		}
		for (int i=minY; i<=maxY; i+=lineHeight) {
			line (minX+move, i, playTime*2*30+minX+move, i);
		}


	}

	//
	void drawScale () {
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
	boolean inRange () {
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
		void playControl () {
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
			local_x = int (map (value, minValue, maxValue, leftAxis, rightAxis));
		}
	}
} // TimeLine ?


/******************* end of timeLine.pde1 *********************/
