
package ui.theme;

import javax.swing.*;
import java.awt.*;

public class ProgressStepper extends JPanel {
    private final String[] steps;
    private int currentStep;

    public ProgressStepper(String[] steps) {
        this.steps = steps;
        this.currentStep = 0;
        setBackground(Theme.BACKGROUND_COLOR);
        setPreferredSize(new Dimension(400, 50)); // Set a default preferred size
    }

    public void setCurrentStep(int currentStep) {
        if (currentStep >= 0 && currentStep < steps.length) {
            this.currentStep = currentStep;
            repaint();
        }
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
            int circleX = x + (stepWidth / 2) - (circleDiameter / 2);
            int circleY = (height / 2) - (circleDiameter / 2);

            // Draw the line connecting circles
            if (i > 0) {
                g2d.setColor(i <= currentStep ? Theme.PRIMARY_COLOR : Theme.BORDER_COLOR);
                g2d.setStroke(new BasicStroke(2));
                int prevCircleX = (i - 1) * stepWidth + (stepWidth / 2);
                g2d.drawLine(prevCircleX, height / 2, circleX, height / 2);
            }

            // Draw the circle
            if (i <= currentStep) {
                g2d.setColor(Theme.PRIMARY_COLOR);
                g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);
                g2d.setColor(Color.WHITE);
                g2d.setFont(Theme.LABEL_FONT.deriveFont(Font.BOLD));
                String stepNumber = String.valueOf(i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = circleX + (circleDiameter - fm.stringWidth(stepNumber)) / 2;
                int textY = circleY + ((circleDiameter - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(stepNumber, textX, textY);
            } else {
                g2d.setColor(Theme.BORDER_COLOR);
                g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);
            }

            // Draw the step label
            g2d.setColor(i == currentStep ? Theme.TEXT_PRIMARY_COLOR : Theme.TEXT_SECONDARY_COLOR);
            g2d.setFont(Theme.LABEL_FONT);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(steps[i]);
            g2d.drawString(steps[i], x + (stepWidth / 2) - (labelWidth / 2), circleY + circleDiameter + 15);
        }
    }
}
