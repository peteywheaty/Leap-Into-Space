package com.leapmotionsi;

import java.awt.Image;

public abstract class Sprite {

	protected int x;
	protected int y;
	private int height;
	private int width;
	private Image image;
	private boolean visible;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}	

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		this.height = image.getHeight(null);
		this.width = image.getWidth(null);
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	public boolean collidesWith(Sprite s) {
		if (((s.x >= this.x && s.x <= this.x + this.width) || (this.x >= s.x && this.x <= s.x + s.width)) 
				&& ((s.y >= this.y && s.y <= this.y + this.height) || (this.y >= s.y && this.y <= s.y + s.height)))
			return true;
		else
			return false;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
