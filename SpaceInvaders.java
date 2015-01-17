package com.leapmotionsi;

import java.io.IOException;


import com.leapmotion.leap.Controller;

public class SpaceInvaders {

	public static void main(String[] args) {
        new GameWindow();
        
        LeapListener listener = new LeapListener();
        Controller controller = new Controller();
        controller.addListener(listener);

        // Keep the thread alive so the controller can read input.
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        controller.removeListener(listener);
    }
}
