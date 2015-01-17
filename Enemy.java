package com.leapmotionsi;

import javax.swing.ImageIcon;

public class Enemy extends Sprite {

	int health = 5;
	
	public Enemy() {
	    setImage((new ImageIcon(this.getClass().getResource("enemy.png"))).getImage());
	    setX(350);
	    setY(0);
	}
    
	/**
	 * 	First moves the enemy down into position, then moves it left or right towards the player.
	 */
	public void move(Player player) {
		System.out.println(this.getX() + " |E| " + this.getY());
		if (this.getY() < 100) this.setY(this.getY() + 5);
		else if (this.getX() < player.getX()) this.setX(this.getX() + 5);
		else if (this.getX() > player.getX()) this.setX(this.getX() - 5);
	}
	
	public boolean inPosition() {
		return this.getY() >= 100;
	}
	
	public boolean takeDamage() {
		health -= 1;
		return health == 0;
	}
}
