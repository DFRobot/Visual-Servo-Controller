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
	void createPlayButton (ControlP5 thecp5, int theX, int theY) {
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
