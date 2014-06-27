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

/******************** start of fileProcess.pde ********************/
/* 
 *	about open and save file process
 *	void saveData ()        : getData and call saveStringToFile ()
 *	void exportData ()        : getData and save data to SD card
 *	void saveStringToFile (String mystring)    : open swing saveDialog and save the String to file
 *	void loadData ()        : call getStringFromFile () and call loadToList ()
 *	void loadToList (String lines, int addId) :    load user data to program
 *	String [] getStringsFromFile (String path)    : open swing openDialog and return String
 *	void loadSet (String theName)        : load setfile and call setValue
 *	void setValue (String variable, String value)    : 
 *	String [] loadSetFile (String theName)
 *	void readProtocol ()
 *	String shiftString (String theString)
 *	void setTimeDialog ()
 */

//* //
//* void saveData () {
//* 	to_begin ();
//* 	String buffer = new String ();
//* 	buffer += "%24servo%\n";
//* 	buffer += playTime+"\n";
//* 	for (int i=0; i<servoList.list.size (); i++) {
//* 		Servo servo = servoList.list.get(i);
//* 		buffer += int(servo.getServoId ())+"\t"
//* 			+getLayer (int (servo.getPy1()))+"\t"
//* 			+int(servo.getBeginAngle ())+"\t"
//* 			+int(servo.getEndAngle ())+"\t"
//* 			+timeLine.toValue (int (servo.getPx1 ()))+"\t"
//* 			+timeLine.toValue (int(servo.getPx2 ()))+"\n";
//* 	}
//* 
//* 	String name = saveStringToFile (buffer);    // open a save window to and the string
//* 	fileName = name;
//* } // saveToFile ?


void saveData () {

	String name = getSaveFileName ();
	if (name != null) {
		int length = servoList.list.size ();
		XML xml = new XML ("df24servo");
		xml.setName ("df24servo");
		xml.setInt ("time", playTime);
		for (int i=0; i<length; i++) {
			xml.addChild ("servo");
		}
		XML [] children = xml.getChildren ("servo");
		for (int i=0; i<length; i++) {
			Servo servo = servoList.list.get(i);
			children[i].setInt ("id", 
					servo.getServoId ());
			children[i].setInt ("layer", 
					getLayer (int (servo.getPy1 ())));
			children[i].setInt ("begin_angle", 
					int (servo.getBeginAngle ()));
			children[i].setInt ("end_angle", 
					int (servo.getEndAngle ()));
			children[i].setFloat ("begin_time", 
					timeLine.toValue (int (servo.getPx1 ())));
			children[i].setFloat ("end_time", 
					timeLine.toValue (int (servo.getPx2 ())));
		}

		if (name.endsWith (".xml")) {
			saveXML (xml, name);
			fileName = name;
		}
		else {
			fileName = name+".xml";
			saveXML (xml, name+".xml");
		}
	}
}

//
void exportData () {
	to_begin ();
	String buffer = new String ();
	for (int i=0; i<servoList.list.size (); i++) {
		Servo servo = servoList.list.get(i);
		buffer += string1+int(servo.getServoId ())+string2+
			int(servo.getBeginAngle ())+int(servo.getEndAngle ())
			+"\t"+int(servo.getPx1 ())+"\t"+int(servo.getPy1())
			+"\t"+int(servo.getWidth ())+"\n";
	}
	String name = saveStringToFile (buffer);    // open a save window to save the string
	// if (name != null)

	fileName = name;
}


// reusable, save a string data to file by java swing
String saveStringToFile (String mystring) {
	String name = fileName;

	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
		e.printStackTrace();
	}

	// create a file chooser
	final JFileChooser fc = new JFileChooser();

	// in response to a button click:
	int returnVal = fc.showSaveDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		try {
			File file = fc.getSelectedFile();
			//println (file.getName ());
			//println ("path="+file.getPath ());
			println ("path="+file.getPath ());
			if (file.exists ()) {

				println ("exists");
			}

			BufferedWriter writer = new BufferedWriter( new FileWriter( file));  // file+".txt"
			writer.write(mystring);
			writer.close( );
			name = file.getPath ();
			//JOptionPane.showMessageDialog(this, "The Message was Saved Successfully!",
			// "Success!", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog( this, "could not save file", "IOException", JOptionPane.ERROR_MESSAGE );
		}
	}
	return name;
} // saveString ?

//
String getSaveFileName () {
	String name = null;
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
		e.printStackTrace();
	}

	// create a file chooser
	final JFileChooser fc = new JFileChooser();

	// in response to a button click:
	int returnVal = fc.showSaveDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		//println (file.getName ());
		//println ("path="+file.getPath ());
		println ("path="+file.getPath ());

		if (file.exists ()) {
			println ("exists");
			int option = JOptionPane.showConfirmDialog(
					frame, 
					"this file is already exists, do you want overwrite it?", 
					"warning", 
					JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION)
				return null;
		} 
		else {
			String thename = file.getPath ();
			if (!thename.endsWith (".xml")) {
				File file1 =new File (thename+".xml");
				if (file1.exists ()) {
					println ("exists");
					int option = JOptionPane.showConfirmDialog(
							frame, 
							"this file is already exists, do you want overwrite it?", 
							"warning", 
							JOptionPane.YES_NO_OPTION);
					if (option != JOptionPane.YES_OPTION)
						return null;
				}
			}
		}
		name = file.getPath ();
		//JOptionPane.showMessageDialog(this, "The Message was Saved Successfully!",
		// "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	return name;
}


String getOpenFileName () {
	String name = null;
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception e) {
		e.printStackTrace();
	}

	// create a file chooser
	final JFileChooser fc = new JFileChooser();

	// in response to a button click:
	int returnVal = fc.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		//println (file.getName ());
		//println ("path="+file.getPath ());
		println ("path="+file.getPath ());
		if (!file.exists ()) {
			//JOptionPane.showMessageDialog(this, "this file does not exists!", 
			// "error!", JOptionPane.INFORMATION_MESSAGE);
			// return null;
		}
		name = file.getPath ();
		//JOptionPane.showMessageDialog(this, "The Message was Saved Successfully!",
		// "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	return name;
}

//
void loadData () {
	String name  = getOpenFileName ();
	XML xml;
	if (!name.endsWith (".xml")) {
		File file = new File (name+".xml");
		if (file.exists ()) {
			name += ".xml";
		}
		else {
			JOptionPane.showMessageDialog(
					this, 
					"can't open this file", 
					"error!", 
					JOptionPane.ERROR_MESSAGE );
			return ;
		}
	}

	xml = loadXML (name);
	if (xml != null) {
		//   println (xml.getInt ("time"));
		servoList.removeAll ();
		setTime (xml.getInt ("time"));  
		String xmlName = xml.getName ();
		if (xmlName != "df24servo") {
			JOptionPane.showMessageDialog(
					this, 
					"data file foramt error!", 
					"error!", 
					JOptionPane.ERROR_MESSAGE );
			return ;
		}
		fileName = name;
		XML [] children = xml.getChildren ("servo");
		println ("length="+children.length);
		for (int i=0; i < children.length; i++) {
			int id = children[i].getInt ("id");

			int layer = children[i].getInt ("layer");
			int y_axis = layerToAxis (layer);
			float beginTime = children[i].getFloat ("begin_time");
			int x1_axis = timeLine.toAxis (beginTime);
			float endTime = children[i].getFloat ("end_time");
			int x2_axis = timeLine.toAxis (endTime);
			int servoWidth = x2_axis-x1_axis;
			int beginAngle = children[i].getInt ("begin_angle");
			int endAngle = children[i].getInt ("end_angle");

			Servo servo = new Servo(cp5, "servo"+(++servoList.addId));
			servoList.add (servo);  
			++servoList.addId;
			servo.setServoId (id);
			servo.setPosition (x1_axis, y_axis);
			servo.setSize (x2_axis-x1_axis, lineHeight);
			servo.setBeginAngle (beginAngle);
			servo.setEndAngle (endAngle);

			servo.setMinWidth (50);
			servo.setMaxWidth (maxX-minX);
			servoList.setValue (servo);
		}
	}
}


//
void loadSet (String theName) {
	String [] lines = loadSetFile (theName);
	if (lines != null) {
		for (int i=0; i < lines.length; i++) {
			String word [] = lines[i]
				.replaceAll ("\\s*", "")
				.split ("=");
			if ((word.length == 1 && word[0].equals ("")) 
					|| (word[0].length() > 0 && (word[0].charAt(0) == '#' || word[0].charAt(0) == ';')))
				continue;
			if (word.length == 2) {
				//println ("ok="+"<"+word[0]+">"+" <"+word[1]+">");
				setValue (word[0], word[1]);
			}
			else {
				println ("set file format error!");
			}
		}
	}
	if (protocol == null) {
		string1 = "#";
		string2 = "P";
		string3 = "\r";
	}
}

//
void setValue (String variable, String value) {
	try {
		if (value.startsWith ("0x"))
			value = value.substring (2, value.length ());

		if (variable.equals("defaultColor")) {
			servoList.defaultColor = (int)Long.parseLong (value, 16);
			servoList.currentColor = servoList.defaultColor;
		}
		else if (variable.equals("pressedColor"))
			servoList.pressedColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("enterColor"))
			servoList.enterColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("moveColor"))
			servoList.moveColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("sideColor"))
			servoList.sideColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("enterColor"))
			servoList.enterColor = (int)Long.parseLong (value, 16);
		else if (variable.equals("leftColor"))
			servoList.leftColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("rightColor"))
			servoList.rightColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("beginAngleColor"))
			servoList.beginAngleColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("endAngleColor"))
			servoList.endAngleColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("servoIdColor"))
			servoList.servoIdColor= (int)Long.parseLong (value, 16);
		else if (variable.equals("enterSideColor")) {
			servoList.enterSideColor= (int)Long.parseLong (value, 16);
			servoList.defaultSideColor= servoList.enterSideColor;
		}
		else if (variable.equals("errorColor"))
			servoList.errorColor= (int)Long.parseLong (value, 16);
		else if (variable.equals ("backColor"))
			backColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("rectLineColor"))
			rectLineColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("timeTextColor"))
			timeTextColor = (int)Long.parseLong (value, 16);///////////////
		else if (variable.equals ("playModeTextColor"))
			playModeTextColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("captionTextColor"))
			captionTextColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("timeLineColor"))
			timeLineColor = (int)Long.parseLong (value, 16);
		else if (variable.equals ("backImage")) {
			backImage = loadImage (value.substring (1, value.length ()-1));
			if (backImage == null)
				println ("can't open file: "+value);
		}
		else if (variable.equals("isShowSide")) {
			if (value.equals ("false"))
				servoList.isShowSide= false;
			else if (value.equals ("true"))
				servoList.isShowSide= true;
		}
		else if (variable.equals("radius"))
			servoList.radius = Integer.parseInt(value, 10);
		else if (variable.equals("playTime"))
			playTime = Integer.parseInt(value, 10);
		else if (variable.equals ("waveMin"))
			waveMin = Integer.parseInt(value, 10);
		else if (variable.equals ("waveMax"))
			waveMax = Integer.parseInt(value, 10);
		else if (variable.equals ("angleMin"))
			angleMin = Float.parseFloat(value);
		else if (variable.equals ("angleMax"))
			angleMax = Float.parseFloat(value);    //
		else if (variable.equals ("beginServoId"))
			beginServoId = Integer.parseInt(value);
		else if (variable.equals ("endServoId"))
			endServoId = Integer.parseInt(value);
		else if (variable.equals ("protocol")) {
			protocol = value;
			readProtocol ();
		}
		else {
			println ("error! no this variable: "+variable+" "+value);
		}
	}
	catch (NumberFormatException NFE) {
		println ("error! NumberFormatException : "+variable+" "+value);
	}
}

//read set file to String by the file name
String [] loadSetFile (String theName) {
	File file = new File (theName);
	try {
		println (file +" "+file.getCanonicalPath ());
		if (file.exists () && file.isFile ()) {
			String lines [] = loadStrings (file);
			return lines;
		}
		else {
			println ("error! setFile does not exist!");
		}
	}
	catch (Exception e) {
	}
	return null;
}


//
//void readProtocol () {
//    if (protocol.charAt (0) == '\"' && protocol.charAt (protocol.length ()-1)== '\"') {
//        protocol = protocol.substring(1, protocol.length()-1);
//        protocol = protocol.replace ("\\r", "\r")   
//            .replace ("\\n", "\n")
//                .replace ("\\t", "\t")
//                    .replace ("\\s", " "); //the space: ' '
//        int sub_id = protocol.indexOf ("%i");
//        int sub_value = protocol.indexOf ("%v");
//        if (sub_id >= 0 && sub_value > 0 && (sub_value > sub_id)) {
//            string1 = protocol.substring (0, sub_id);
//            string2 = protocol.substring (sub_id+2, sub_value);
//            string3 = protocol.substring (sub_value+2, protocol.length ());
//            println (string1+" "+sub_id);
//            println (string2+" "+sub_value);
//            println (string3);
//        }
//
//        println (protocol);
//    }
//}



//
void readProtocol () {
	// myString = new String ();
	if (protocol.length () > 4 && protocol.startsWith("\"") && protocol.endsWith ("\"")) {
		String myString = shiftString (protocol.substring (1, protocol.length()-1));

		int sub_id = myString.indexOf ("%i");
		int sub_value = myString.indexOf ("%v");
		if (sub_id >= 0 && sub_value > 0 && (sub_value > sub_id)) {
			string1 = myString.substring (0, sub_id);
			string2 = myString.substring (sub_id+2, sub_value);
			string3 = myString.substring (sub_value+2, myString.length ());
			println (protocol);
			println ("protocol start with \""+string1+"\" in "+sub_id);
			println ("protocol middle with \""+string2+"\" in "+sub_value);
			println ("protocol end with \""+string3+"\" in "+int(sub_value+3));
			println ("protocol result: "+myString);
		}
	}
}

//"support \s \r \n \t \\ " //
String shiftString (String theString) {
	String myString = new String();
	for (int i=0; i<theString.length(); i++) {
		if (theString.charAt(i) != '\\') {
			myString += theString.charAt(i);
		}
		else {        // theString.charAt(i) == '\\'
			if (i < theString.length()-1) {
				switch(theString.charAt(i+1)) {
					case 's':
						myString += ' ';
						i++;
						continue;
					case 'r':
						myString += '\r';
						i++;
						continue;
					case 'n':
						myString += '\n';
						i++;
						continue;
					case 't':
						myString += '\t';
						i++;
						continue;
					case '\\':
						myString += '\\';
						i++;
						continue;
					default:
						if ( i < theString.length()-2) {
							try { 
								int myHex = Integer.parseInt (theString.substring (i+1, i+3), 16);
								//println (myHex);
								myString += (char)myHex;
								i+=2;
								continue;
							}
							catch (NumberFormatException NFE) {
								println ("error! NumberFormatException : "+theString.substring (i+1, i+3));
							}
						}
						else {
							println ("error! unknow char: "+theString.charAt (i+1));
						}
				}
			}
		}
	}
	return myString;
}

//
void setTimeDialog () {
	timeLine.stop ();
	to_begin ();
	String inputValue = JOptionPane.showInputDialog("Set the total time (second), must be integer");
	try {
		if (inputValue != null) {
			move = 0;
			timeLine.setAxis (minX);
			setTime (inputValue);
		}
	}
	catch (NumberFormatException NFE) {
		println ("error! NumberFormatException : "+inputValue);
	}
}


//
void setTime (int theSecond) {
	move = 0;
	timeLine.setAxis (minX);
	//if (
	playTime = constrain (
			theSecond, 
			int (timeLine.toValue (servoList.getMaxAxis ())+1), 
			3600);
	//lineStep = playTime/20.0;
	timeLine.setRange (0, playTime);
	timeLine.setSize (minX, playTime*scale+minX);
	servoList.setAllMaxWidth (playTime*scale);
}

void setTime (String theSecond) {
	move = 0;
	timeLine.setAxis (minX);
	playTime = constrain (
			Integer.parseInt (theSecond.trim ()), 
			int (timeLine.toValue (servoList.getMaxAxis ())+1), 
			3600);
	//lineStep = playTime/20.0;
	timeLine.setRange (0, playTime);
	timeLine.setSize (minX, playTime*scale+minX);
	servoList.setAllMaxWidth (playTime*scale);
}

//
int layerToAxis (int theValue) {
	return theValue*lineHeight+lineHeight*2;
}

//
int getLayer (int theAxis) {
	return int((theAxis-lineHeight*2)/lineHeight);
}


/******************** end of fileProcess.pde ********************/
