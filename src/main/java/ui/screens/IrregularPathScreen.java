
package ui.screens;

import ui.MobileFrame;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrregularPathScreen extends JPanel {

    private DefaultListModel<String> cartModel;
    private JPanel topPanel; // Make it a field to update its border
    private int totalUnits = 0;
    private final int maxUnits = 26; // Example max units

    // Map to hold course units. In a real app, this would come from a database.
    private final Map<String, Integer> courseUnits = new HashMap<>();

    public IrregularPathScreen() {
        // Populate course units
        courseUnits.put("IT-211", 3);
        courseUnits.put("GE-102", 3);
        courseUnits.put("IT-311", 3);
        courseUnits.put("IT-312", 3);
        courseUnits.put("IT-313", 3);
        courseUnits.put("GE-105", 3);
        courseUnits.put("PE-3", 2);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Pane: The Cart ---
        topPanel = new JPanel(new BorderLayout());
        updateCartTitle(); // Set initial title
        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        topPanel.add(new JScrollPane(cartList));
        topPanel.setPreferredSize(new Dimension(0, 180));

        // --- Bottom Pane: The Menu ---
        JTabbedPane bottomTabbedPane = new JTabbedPane();
        bottomTabbedPane.addTab("Back Subjects (Priority)", createSubjectListPanel(true));
        bottomTabbedPane.addTab("On-Sem Subjects (BSIT 3rd Year)", createSubjectListPanel(false));

        // --- Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomTabbedPane);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // --- Validate Button ---
        JButton validateButton = new JButton("Validate & Proceed");
        add(validateButton, BorderLayout.SOUTH);
        
        validateButton.addActionListener(e -> {
            if (cartModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your schedule is empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert the cart model to a List
            List<String> enrolledSubjects = new ArrayList<>();
            for (int i = 0; i < cartModel.getSize(); i++) {
                enrolledSubjects.add(cartModel.getElementAt(i));
            }

            // Save the list of subjects to the session
            SessionManager.getInstance().setEnrolledSubjects(enrolledSubjects);

            JOptionPane.showMessageDialog(this, "Schedule Validated! Proceeding to Assessment.");
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("AssessmentOfFees");
            }
        });
    }

    private void updateCartTitle() {
        topPanel.setBorder(BorderFactory.createTitledBorder("Your Schedule (" + totalUnits + "/" + maxUnits + " Units)"));
    }

    private JPanel createSubjectListPanel(boolean isBackSubjects) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        DefaultListModel<String> listModel = new DefaultListModel<>();

        if (isBackSubjects) {
            listModel.addElement("IT-211: Data Structures (Failed)");
            listModel.addElement("GE-102: Ethics (Failed)");
        } else {
            listModel.addElement("IT-311: Networking 1");
            listModel.addElement("IT-312: Web Development (Prereq: IT-211)");
            listModel.addElement("IT-313: Software Engineering");
        }

        JList<String> list = new JList<>(listModel);
        list.setCellRenderer(new SubjectListRenderer(isBackSubjects));
        
        JButton addButton = new JButton("Add to Schedule");
        addButton.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                 String subjectToAdd = selected.replace(" (Failed)", "");
                if (cartModel.contains(subjectToAdd)) {
                    JOptionPane.showMessageDialog(this, "Subject is already in your schedule.", "Duplicate Subject", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (selected.contains("(Prereq: IT-211)")) {
                    JOptionPane.showMessageDialog(this, "Cannot add subject. Prerequisite 'Data Structures' is not yet passed.", "Prerequisite Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String courseCode = selected.split(":")[0];
                int units = courseUnits.getOrDefault(courseCode, 0);

                if (totalUnits + units > maxUnits) {
                    JOptionPane.showMessageDialog(this, "Cannot add subject. Exceeds maximum allowed units (" + maxUnits + ").", "Unit Limit Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                totalUnits += units;
                cartModel.addElement(subjectToAdd);
                updateCartTitle();
                panel.revalidate();
                panel.repaint();
            }
        });

        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

    // Custom renderer to color failed subjects red
    private static class SubjectListRenderer extends DefaultListCellRenderer {
        private final boolean isBackSubjects;

        public SubjectListRenderer(boolean isBackSubjects) {
            this.isBackSubjects = isBackSubjects;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isBackSubjects && value.toString().contains("(Failed)")) {
                label.setForeground(Color.RED);
                label.setText("<html><b>" + value.toString().replace("(Failed)", "") + "</b> [FAILED]</html>");
            }
            return label;
        }
    }
}
