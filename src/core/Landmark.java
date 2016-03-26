package core;

import processing.core.PApplet;

public class Landmark {
	
	public final PApplet _parent;
	public final double _xPos;
	public final double _yPos;
	
	public Landmark(PApplet parent, double xPos, double yPos) {
		_parent = parent;
		_xPos = xPos;
		_yPos = yPos;
	}
	
	
	public void draw() {
		
		int Yellow 	= _parent.color(255, 255, 0);
		int Red 	= _parent.color(255, 0, 0);
		int Green 	= _parent.color(0, 255, 0);
		int Blue 	= _parent.color(0, 0, 255);
		int White 	= _parent.color(255);
		int Black 	= _parent.color(0);
		
		_parent.strokeWeight(0);
		_parent.fill(Green);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * 10, (float)_yPos * 10, 20, 20);
	}

}
