package map;

import processing.core.PApplet;

public class Landmark {
	
	public final PApplet _parent;
	public final double _xPos;
	public final double _yPos;
	public final int _id;
	
	private static int _idGenerator = 0;
	
	public Landmark(PApplet parent, double xPos, double yPos) {
		_parent = parent;
		_xPos = xPos;
		_yPos = yPos;
		_id = _idGenerator++;
	}
	
	
	public void draw() {
		
		int Yellow 	= _parent.color(255, 255, 0);
		int Red 	= _parent.color(255, 0, 0);
		int Green 	= _parent.color(0, 255, 0);
		int Blue 	= _parent.color(0, 0, 255);
		int White 	= _parent.color(255);
		int Black 	= _parent.color(0);
		final int MUL = 10;
		
		// flip and invert the axis to place (0,0) in bottom left corner
		_parent.translate(0, _parent.height);
		_parent.scale(1, -1);
		
		_parent.strokeWeight(0);
		_parent.fill(Green);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * MUL, (float)_yPos * MUL, 10, 10);
		
		// flip and invert the axis to place (0,0) back in top left corner
		_parent.translate(0, _parent.height);
		_parent.scale(1, -1);
	}

}
