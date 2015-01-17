package com.leapmotionsi;

import javax.swing.ImageIcon;

public class Bullet extends Sprite {
	
	public Bullet() {
        setImage((new ImageIcon(this.getClass().getResource("bullet.png"))).getImage());
	}
}
