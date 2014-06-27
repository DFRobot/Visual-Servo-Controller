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
	void createButton (ControlP5 thecp5, int theX, int theY) {
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
