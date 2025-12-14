package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgressStepper extends JPanel {
    public static final String[] STEPS = {"Bio-Data", "Docs", "Program", "Block"};

    private final String[] steps;
    private int targetStep;
    private float animatedStep; // Used for smooth animation

    public ProgressStepper() {
        this(STEPS);
    }

    public ProgressStepper(String[] steps) {
        this.steps = steps.clone();
        this.targetStep = 0;
        this.animatedStep = 0f;
        setBackground(Theme.BACKGROUND_COLOR);
        setPreferredSize(new Dimension(400, 70)); // Adjusted height for labels
    }

    public void setCurrentStep(int newStep) {
        if (newStep >= 0 && newStep < steps.length) {
            this.targetStep = newStep;
            animateStep(this.targetStep);
        }
    }

    private void animateStep(int newTargetStep) {
        int animationDuration = 300; // milliseconds
        Timer timer = new Timer(10, new ActionListener() {
            long startTime = -1;
            float startAnimatedStep = animatedStep;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;
                double progress = (double) elapsed / animationDuration;

                if (progress >= 1.0) {
                    progress = 1.0;
                    animatedStep = newTargetStep;
                    ((Timer) e.getSource()).stop();
                } else {
                    animatedStep = (float) (startAnimatedStep + (newTargetStep - startAnimatedStep) * progress);
                }
                repaint();
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int stepCount = steps.length;
        int stepWidth = width / stepCount;

        for (int i = 0; i < stepCount; i++) {
            int x = i * stepWidth;
            int circleDiameter = 20;
            int circleY = (height / 2) - (circleDiameter / 2) - 10; // Adjusted for label space

            // Draw the line connecting circles
            if (i > 0) {
                g2d.setColor(i <= animatedStep ? Theme.PRIMARY_COLOR : Theme.BORDER_COLOR);
                g2d.setStroke(new BasicStroke(2));
                int prevCircleCenterX = (i - 1) * stepWidth + (stepWidth / 2);
                int currentCircleCenterX = x + (stepWidth / 2);
                g2d.drawLine(prevCircleCenterX, circleY + circleDiameter / 2, currentCircleCenterX, circleY + circleDiameter / 2);
            }

            // Draw the circle
            int currentCircleX = x + (stepWidth / 2) - (circleDiameter / 2);
            if (i <= animatedStep) {
                g2d.setColor(Theme.PRIMARY_COLOR);
                g2d.fillOval(currentCircleX, circleY, circleDiameter, circleDiameter);
                g2d.setColor(Color.WHITE);
                g2d.setFont(Theme.LABEL_FONT.deriveFont(Font.BOLD));
                String stepNumber = String.valueOf(i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = currentCircleX + (circleDiameter - fm.stringWidth(stepNumber)) / 2;
                int textY = circleY + ((circleDiameter - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(stepNumber, textX, textY);
            } else {
                g2d.setColor(Theme.BORDER_COLOR);
                g2d.fillOval(currentCircleX, circleY, circleDiameter, circleDiameter);
            }

            // Draw the step label
            g2d.setColor(i <= animatedStep ? Theme.TEXT_PRIMARY_COLOR : Theme.TEXT_SECONDARY_COLOR);
            g2d.setFont(Theme.LABEL_FONT);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(steps[i]);
            g2d.drawString(steps[i], x + (stepWidth / 2) - (labelWidth / 2), circleY + circleDiameter + 15);
        }
    }
}