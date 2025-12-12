package ui.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Animator {

    private static final int FRAME_RATE = 60; // 60 frames per second

    public static Timer slide(JComponent component, java.awt.Point from, java.awt.Point to, int duration, Runnable onFinish) {
        long startTime = System.currentTimeMillis();
        int fromX = from.x;
        int toX = to.x;
        int fromY = from.y;
        int toY = to.y;

        Timer timer = new Timer(1000 / FRAME_RATE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;
                double progress = (double) elapsed / duration;

                if (progress >= 1.0) {
                    progress = 1.0;
                    ((Timer) e.getSource()).stop();
                    if (onFinish != null) {
                        onFinish.run();
                    }
                }

                // Ease-out interpolation
                progress = 1 - Math.pow(1 - progress, 3);

                int newX = (int) (fromX + (toX - fromX) * progress);
                int newY = (int) (fromY + (toY - fromY) * progress);
                component.setLocation(newX, newY);
            }
        });
        timer.setInitialDelay(0);
        timer.start();
        return timer;
    }
}
