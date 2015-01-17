package com.leapmotionsi;

import java.util.Random;

import javax.swing.ImageIcon;


public class Asteroid extends Sprite {

	int speed = 1;
	
	public Asteroid() {
	    setImage((new ImageIcon(this.getClass().getResource("asteroid" + ((new Random()).nextInt(3) + 1) + ".png"))).getImage());
	}
	
	public int getSpeed() {
		return speed;
	}
	
	// Assign a random speed and starting location for variety.
	public void spawn() {
		Random ran = new Random();
		setY(0);
		setX(ran.nextInt(700));
	    int r = ran.nextInt(10);
	    if (r == 1)
	    	speed = 3;
	    else if (r <= 4)
	    	speed = 2;
	    else
	    	speed = 1;
	}
}
