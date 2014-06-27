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
	void createButton (ControlP5 thecp5, int theX, int theY) {
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
