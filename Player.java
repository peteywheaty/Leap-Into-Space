package com.leapmotionsi;

import javax.swing.ImageIcon;

public class Player extends Sprite {
	
	private int xl, yl = 0;
	private boolean grabbing;
	
	public Player() {
        setImage((new ImageIcon(this.getClass().getResource("player.png"))).getImage());
        this.setX(350);
        this.setY(600);
	}
	
	public void changeX(int a) {
		xl += a;
	}
	
	public void changeY(int a) {
		yl += a;
	}
	
	/**
	 * 	Only update the players location on the screen once per frame according to 
	 * 	all the movements made by the player since the last update.
	 */
	public void update() {
		x += xl;
		y += yl;
		if (x > 650) x = 650;
		if (x < 10) x = 10;
		if (y > 650) y = 650;
		if (y < 10) y = 10;
		xl = 0;
		yl = 0;
	}
	
	public boolean isGrabbing() {
		return grabbing;
	}
	
	public void setGrabbing(boolean g) {
		this.grabbing = g;
	}
}
