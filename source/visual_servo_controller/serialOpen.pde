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

/******************** start of serialOpen.pde ********************/
/*
 *    serial by lisper <leyapin@gmail.com> 2013-11-19
 *    public class SliderClass 
 *    
 *    
 *    public SerialOpen (PApplet theClass, ControlP5 cp5, int x, int y)
 *    public boolean isOpen () //if  serial is open
 *    public void CreateButton (int x, int y)
 *    public void cmdSend (int num, float angle) {
 *    public void process ()     //call it while clicked the button
 *    
 *    
 */

public class SerialOpen {
	PApplet outClass;
	private ControlP5 mycp5;
	private int x;
	private int y;
	public Serial myPort;
	public DropdownList serialList;
	public DropdownList baudSelect;
	//public Button updateButton;
	public controlP5.Button connectButton;
	public boolean isSerialOpen;
	String portName;
	public boolean isInMotify;
	//load image for button
	PImage[] connect_img    = { 
		loadImage ("connect_a.png"), 
		loadImage ("connect_b.png"), 
		loadImage ("connect_c.png")
	};
	PImage[] disconnect_img    = { 
		loadImage ("disconnect_a.png"), 
		loadImage ("disconnect_b.png"), 
		loadImage ("disconnect_c.png")
	};
	//private int baud_rate = 57600;

	private String baudList[] = {
		"2400", "4800", "9600", "14400", "19200", "28800", "57600", "115200", "1000000"
	};
	private int baudIntList[] = {
		2400, 4800, 9600, 14400, 19200, 28800, 57600, 115200, 1000000
	};

	//
	public SerialOpen (PApplet theClass, ControlP5 cp5, int x, int y) {
		this.mycp5 = cp5;
		this.x = x;
		this.y = y;
		outClass = theClass;
		//serialNum = myPort.list().length;    //get available serial number
		CreateButton (x, y);
		isSerialOpen = false;
		CheckThread check = new CheckThread ("hello");
		Thread t1 = new Thread (check);
		t1.start ();
	}

	//
	public void CreateButton (int x, int y) {
		//add connect button
		connectButton = this.mycp5.addButton ("ConnectButton")
			.setLabel ("Connect")
			.setImages (connect_img)
			.setPosition (x, y-20)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			.setColorBackground (color (0, 0, 0, 1))
			.setId (ServoId.OPEN_BUTTON)
			.setSize (60, 23)
			;
		connectButton.captionLabel()
			.toUpperCase(false)
			.align(CENTER, CENTER);

		//serial dropdownlist
		serialList = this.mycp5.addDropdownList("No Port")
			.setPosition(x+80, y+4)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			.setItemHeight(20)
			//  .setfont (new ControlFont(baudFont, 16))
			.setBarHeight(23)
			.setHeight (30)
			.setId (ServoId.PORTSELECT_BUTTON)
			.setSize (90, 120)           
			;

		serialList.captionLabel()
			.toUpperCase(false)
			.align(LEFT, CENTER)
			//.setSize (14)
			.setColor (color (100, 150, 200))
			.setFont (new ControlFont(baudFont, 14))
			;
		//add set baud button
		baudSelect = this.mycp5.addDropdownList ("baud")
			.setPosition (x+180, y+4)
			.setColorBackground (color (20, 20, 20, 140))
			.setColorForeground (color (100, 100, 100, 140))
			.setItemHeight(12)
			.setId (ServoId.BAUDSELECT_BUTTON)
			.setBarHeight(23)
			//.setHeight (10)
			.setSize (80, 120)
			;
		baudSelect.captionLabel()
			.toUpperCase(false)
			.align(LEFT, CENTER)
			.setColor (color (100, 150, 200))
			.setFont (new ControlFont(baudFont, 14));

		for (int i=0; i<baudList.length; i++) {
			baudSelect.addItem (baudList[i], i);
		}
		buildSerialList ();
		baudSelect.setValue (6);
	} // createButton ?

	//////////////////////////////////////
	//"open" button process, when it clicked, call in ControlEvent 
	public void process () {
		if (myPort.list ().length  == 0) {  
			println ("no available serial port!");
		}
		else {
			if (isOpen ()) {    //if serial is open, close it
				close ();
			}  
			else {    //if serial is close, open it
				open ();
			}
		}
	} // process ?

	//open serial
	void open () {
		myPort = new Serial (outClass, 
				Serial.list()[int(serialList.getValue())], 
				baudIntList[int(baudSelect.getValue())]); // open serial
		if (myPort == null) { 
			//   connectButton.setState (false);  
			println ("error! can't open serial port!");
		}
		else {    //successed open serial
			isSerialOpen = true;
			connectButton.setLabel ("Disconnect");
			connectButton.setImages (disconnect_img);
			// connectButton.setState (true);
			connectButton.setColorBackground (color (0, 150, 0)); 
			// openButton.setforeColor (0);
			println (myPort.list()[int(serialList.getValue())]+" is opened");
		}

	}

	//close serial
	void close () {
		timeLine.stop ();//////////////////////////////
		myPort.stop ();
		isSerialOpen = false;
		connectButton.setLabel ("Connect"); 
		connectButton.setImages (connect_img);
		//connectButton.setState (false);  
		connectButton.setColorBackground (color (80, 80, 80, 0)); 
		println (myPort.list()[int(serialList.getValue())]+" is closed");

	}

	//if serial is open
	boolean isOpen () {
		return isSerialOpen;
	}

	// command send to serial port
	public void cmdSend (int num, float angle) {
		int value = angleToValue (angle);
		//myPort.write (string1+num+string2+value+string3);
		myPort.write (string1+num+string2+value);
		println (string1+num+string2+value);
	}

	//angle(0~180) to value(500~2500)
	private int angleToValue (float angle) {
		return int((waveMax-waveMin) * angle / (angleMax-angleMin) + waveMin);
	} // SerialOpen ?

	//
	public class CheckThread implements Runnable {
		private String name;
		private int length;
		CheckThread (String name) {
			this.name = name;
			this.length = myPort.list ().length;
		}
		//
		public void run () {
			for (;;) {
				delay (1000);
				if (length != myPort.list ().length) {
					isInMotify = true;
					buildSerialList ();
					length = myPort.list ().length;
					isInMotify = false;
					// println (serialList.getItem (0).getName ());
				}
			}
		} // run ?
	} // CheckThread ?

	//synchronized ?
	private void buildSerialList () {
		serialList.clear ();

		for (int i=0 ;i<myPort.list ().length; i++) {
			serialList.addItem(myPort.list()[i], i);    //add serial name to serialList
		}
		if (myPort.list().length > 0) {
			serialList.setLabel (myPort.list()[0]);
			println (serialList.getItem (0).getName ());
			//println (">>"+serialList.getItem(int (serialList.getValue ())).getName ());
		}
		else {
			serialList.setLabel ("No Port");
		}
	}
} // SerialOpen ?

/******************** end of serialOpen.pde ********************/
