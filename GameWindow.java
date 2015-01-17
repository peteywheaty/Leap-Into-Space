package com.leapmotionsi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GameWindow extends JFrame {
	private static final long serialVersionUID = 5547564537912247240L;

	static GameWindow game;

	public boolean gameOn = true;
	public boolean prescreen = true;

	private Player player;

	public GameWindow() {
		game = this;

		// Initialize game.
		add(new Space()); // Adds Space (a JPanel) to this window (JFrame)
		setTitle("Space Invaders");
		setSize(700, 700);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Exit the program when the user closes the window.
	}

	/**
	 * 	Starts or restarts the game. Called after the player swipes.
	 */
	public void startGame() {
		prescreen = false;
		gameOn = true;
	}

	public static GameWindow getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	class Space extends JPanel implements Runnable {
		private static final long serialVersionUID = 5195014420440931365L;

		private Thread drawer;
		
		private int score = 0;

		long asteroidTime;
		List<Asteroid> astPool;
		List<Asteroid> asts;

		long gunTime;
		List<Bullet> bults;

		long enemyTime;
		long enemyGunTime;
		List<BulletE> enemyBults;
		Enemy enemy;

		public Space() {
			setFocusable(true);
			setBackground(Color.black);

			drawer = new Thread(this);
			drawer.start();

			// For smoother transitions.
			setDoubleBuffered(true);
		}

		private void init() {
			score = 0;
			
			player = new Player();
			enemy = null;
			
			asteroidTime = System.currentTimeMillis();
			astPool = new LinkedList<Asteroid>();
			asts = new ArrayList<Asteroid>();

			gunTime = System.currentTimeMillis();
			bults = new ArrayList<Bullet>();

			enemyTime = System.currentTimeMillis() - 7500; // Have the first enemy spawn 7.5s in then every 15s after.
			enemyGunTime = System.currentTimeMillis();
			enemyBults = new ArrayList<BulletE>();
			
			// Initialize 20 asteroids that we use repeatedly.
			for (int i = 0; i < 20; i++)
				astPool.add(new Asteroid());
		}

		public void paint(Graphics g)
		{
			super.paint(g);

			// Erase everything.
			g.setColor(Color.black);
			g.fillRect(0, 0, 700, 700);

			if (prescreen) {
				g.drawImage((new ImageIcon(this.getClass().getResource("logo.png"))).getImage(), 0, 0, this);
			} else {	      
				// Draw the player.
				g.drawImage(player.getImage(), player.getX(), player.getY(), this);
				// Draw the sprites : asteroids and bullets.
				for (Sprite s : asts.toArray(new Asteroid[asts.size()])) {
					g.drawImage(s.getImage(), s.getX(), s.getY(), this);
				}
				for (Sprite s : bults.toArray(new Bullet[bults.size()])) {
					g.drawImage(s.getImage(), s.getX(), s.getY(), this);
				}
				for (Sprite s : enemyBults.toArray(new BulletE[enemyBults.size()])) {
					g.drawImage(s.getImage(), s.getX(), s.getY(), this);
				}

				if (!gameOn) {
					// If the game is over, draw an explain on the spaceship and the game over screen.
					g.drawImage((new ImageIcon(this.getClass().getResource("explosion.png"))).getImage(), player.getX() - 7, player.getY() - 7, this);
					g.drawImage((new ImageIcon(this.getClass().getResource("gameover.png"))).getImage(), 0, 300, this);
				}
				
				// Draw the enemy if it exists.
				if (enemy != null)
					g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);

				// Draw the score in white.
				g.setColor(Color.white);   
				g.drawString(Integer.toString(score), 15, 15);
			}
			
			// Updates the buffer.
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}

		/**
		 * 	Responsible for spawning and moving asteroids.
		 */
		private void asteroidCycle() {
			long time = System.currentTimeMillis();
			for (Asteroid a : asts.toArray(new Asteroid[asts.size()])) {
				// Move each asteroid down.
				a.setY(a.getY() + a.getSpeed());
				if (a.collidesWith(player)) {
					// Games over if an asteroid collides with a player.
					gameOn = false;
				}
				else if (a.getY() > 700) {
					// If the asteroid goes off screen, add it back to the queue.
					asts.remove(a);
					astPool.add(a);
				}
			}
			// Spawn an asteroid at a minimum of 300ms since the last one and if there is one in queue.
			if (time - asteroidTime >= 300) {
				if (astPool.size() > 0) {
					asteroidTime = time;
					Asteroid a = astPool.get(0);
					astPool.remove(0);
					a.spawn();
					asts.add(a);
				}
			}
		}

		/**
		 * 	Responsible for moving and spawning bullets shot by the player.
		 */
		private void gunCycle() {
			for (Bullet b : bults.toArray(new Bullet[bults.size()])) {
				// Move the bullets up, remove if off screen.
				b.setY(b.getY() - 5);
				if (b.getY() < 0) {
					bults.remove(b);
					continue;
				}
				// Destroy asteroids if they collide.
				for (Asteroid a : asts.toArray(new Asteroid[asts.size()])) {
					if (b.collidesWith(a)) {
						asts.remove(a);
						astPool.add(a);
						bults.remove(b);
						score += 100;
						break;
					}
				}
				// Damage the enemy if it collides, after 5 hits, destroy it.
				if (enemy != null) {
					if (bults.contains(b) && b.collidesWith(enemy)) {
						bults.remove(b);
						score += 1000;
						if (enemy.takeDamage())
							enemy = null;
					}
				}
			}
			// Shoot a bullet after 200ms of the previous one if the player is grabbing his hand.
			long time = System.currentTimeMillis();
			if (player.isGrabbing() && time - gunTime >= 200) {
				gunTime = time;
				Bullet b = new Bullet();
				b.setY(player.getY());
				b.setX(player.getX() + player.getWidth() / 2 - b.getWidth() / 2 );
				bults.add(b);
			}
		}
		
		/**
		 *  Responsible for moving the enemy and its bullets.
		 */
		private void enemyCycle() {
			// Move the bullets down, remove if off screen.
			for (BulletE b : enemyBults.toArray(new BulletE[enemyBults.size()])) {
				b.setY(b.getY() + 5);
				if (b.getY() > 700) {
					enemyBults.remove(b);
					continue;
				}
				// If they collide with the player, games over.
				if (b.collidesWith(player)) {
					gameOn = false;
				}
			}
			// Spawn an enemy every 15s.
			long time = System.currentTimeMillis();
			if (enemy == null && time - enemyTime >= 15000) {
				enemy = new Enemy();
				enemyTime = time;
			} else if (enemy != null) {
				// Move the enemy, if its in position, shoot if it hasen't in the last 500ms.
				enemy.move(player);
				if (enemy.inPosition()) {
					if (time - enemyGunTime >= 500) {
						enemyGunTime = time;
						BulletE b = new BulletE();
						b.setY(enemy.getY());
						b.setX(enemy.getX() + enemy.getWidth() / 2 - b.getWidth() / 2 );
						enemyBults.add(b);
					}
				}
			}
		}

		@Override
		public void run() {
			// Wait for the user to start.
			while (prescreen) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			while (true) {
				// Set the variables for each game.
				init();

				// Update the screen at roughly 60 fps (16ms per frame).
				// If too much computation is being done and the game cannot keep 
				// up keep a lag counter, letting it catch, maintaining the 60 fps.
				long previous = System.currentTimeMillis();
				long lag = 0;
				while (gameOn) {
					long current = System.currentTimeMillis();
					long elapsed = current - previous;
					previous = current;
					lag += elapsed;

					while (lag >= 16)
					{
						// Does the computation work.
						player.update();
						asteroidCycle();
						gunCycle();
						enemyCycle();
						lag -= 16;
					}
					
					// Update the screen.
					this.repaint();
				}

				// When the games over, wait till the user swips again to start.
				while (!gameOn) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
