package ui.screens;

import fixtures.CourseFixtures;
import model.Course;
import ui.MobileFrame;
import ui.Screen;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessmentOfFeesScreen extends JPanel {

    private final Object[][] data;

    public AssessmentOfFeesScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Assessment of Fees", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        // --- Dynamic Fee Calculation ---
        List<String> enrolledSubjects = SessionManager.getInstance().getEnrolledSubjects();
        int totalUnits = enrolledSubjects.stream()
                .map(subject -> subject.split(" - ")[0])
                .map(CourseFixtures::getCourseByCode)
                .mapToInt(Course::getUnits)
                .sum();

        double tuitionFee = totalUnits * 850.0; // Assuming 850 per unit
        double miscellaneousFees = 5000.00;
        double laboratoryFees = 1500.00;
        double previousBalance = 2000.00; // Example previous balance
        double totalDue = tuitionFee + miscellaneousFees + laboratoryFees + previousBalance;
        DecimalFormat df = new DecimalFormat("#,##0.00");

        data = new Object[][]{
                {"Tuition Fee (" + totalUnits + " units)", df.format(tuitionFee)},
                {"Miscellaneous Fees", df.format(miscellaneousFees)},
                {"Laboratory Fees", df.format(laboratoryFees)},
                {"Previous Balance", df.format(previousBalance)},
                {"", ""}, // Spacer
                {"TOTAL DUE", df.format(totalDue)}
        };

        // --- Fees Table ---
        String[] columnNames = {"Description", "Amount (PHP)"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable feesTable = new JTable(model);
        feesTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(feesTable), BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JPanel paymentPlanPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPlanPanel.add(new JLabel("Payment Plan:"));
        JComboBox<String> paymentPlanComboBox = new JComboBox<>(new String[]{"Full Payment", "Installment (2 Payments)"});
        paymentPlanPanel.add(paymentPlanComboBox);
        bottomPanel.add(paymentPlanPanel);

        JButton finalizeButton = new JButton("Finalize Enrollment");
        bottomPanel.add(finalizeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listener ---
        finalizeButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to finalize your enrollment?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                saveFeesToSession();
                MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
                if (frame != null) {
                    frame.showScreen(Screen.DIGITAL_COR, true);
                }
            }
        });
    }

    private void saveFeesToSession() {
        Map<String, String> assessedFees = new HashMap<>();
        for (Object[] row : data) {
            if (row[0] != null && !row[0].toString().isEmpty()) {
                assessedFees.put(row[0].toString(), row[1].toString());
            }
        }
        SessionManager.getInstance().setAssessedFees(assessedFees);
    }
}