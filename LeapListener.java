package com.leapmotionsi;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

/**
 *	This class interfaces with the Leap Motion. All controls are handled here.
 */
public class LeapListener extends Listener {
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    // Keep a reference to the previous change so we can measure the change in hand position.
    Frame f;
    
    public void onFrame(Controller controller) {
        // Gets the current frame.
        Frame frame = controller.frame();
		Player p = GameWindow.getGame().getPlayer();
        
        if (f != null) {
        	// Find the difference in the hand's position from the last frame.
        	Vector v = frame.translation(f);
        	if (p != null) {    		
        		p.changeX((int)(v.getX() * 8));
        		p.changeY((int)(v.getY() * -8));
        	}
        	System.out.println(v + " " + frame.currentFramesPerSecond());
        }
        
        f = frame;
        
        if (p != null) {
        	// We want to shoot if the player grabs their hand.
        	// It's pretty sensitive, so we shoot at 90%+
        	p.setGrabbing(frame.hands().rightmost().grabStrength() > 0.9);
        	System.out.println(p.isGrabbing());
        }
        
        if (GameWindow.getGame().prescreen || !GameWindow.getGame().gameOn) {
        	// Check if the player swiped to start the game.
	        GestureList gestures = frame.gestures();
	        for (int i = 0; i < gestures.count(); i++) {
	            Gesture gesture = gestures.get(i);
	            if (gesture.type() == Gesture.Type.TYPE_SWIPE) {
	            	GameWindow.getGame().startGame();
	            	break;
	            }
	        }
        }
        
        // Slow the input down so we can get more accurate results over time.
        try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
