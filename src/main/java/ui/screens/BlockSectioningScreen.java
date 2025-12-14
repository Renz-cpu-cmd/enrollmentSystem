package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.ModernTable;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BlockSectioningScreen extends JPanel implements ScreenView {

    private static final String[] COLUMN_NAMES = {
        "Block", "Year", "Code", "Description", "Room", "Time", "Status"
    };

    private static final Object[][] BLOCK_DATA = {
        {"BSIT-2-1", "2", "IT 211", "Data Structures & Algo", "NB-101", "Mon 7:00-10:00", "OPEN"},
        {"BSIT-2-1", "2", "IT 212", "Object Oriented Prog", "CL-LAB2", "Tue 8:00-11:00", "OPEN"},
        {"BSIT-2-1", "2", "GEC 5", "Purposive Comm", "AVR-A", "Wed 1:00-4:00", "OPEN"},
        {"BSIT-2-1", "2", "PE 3", "Individual Sports", "GYM", "Fri 8:00-10:00", "FULL"},
        {"BSIT-2-2", "2", "IT 211", "Data Structures & Algo", "NB-102", "Thu 1:00-4:00", "OPEN"}
    };

    private final JTextField ticketField;
    private final JTextField lastNameField;
    private final JTextField blockFilterField;
    private final ModernTable blockTable;

    public BlockSectioningScreen() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        ticketField = createInputField("Enter Ticket Number");
        lastNameField = createInputField("Enter Last Name");
        blockFilterField = createInputField("Block Filter");

        add(buildFilterPanel(), BorderLayout.NORTH);
        blockTable = createTable();
        add(buildTableSection(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFilterPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(Theme.CARD_BG);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(20, 24, 20, 24)));

        JLabel title = new JLabel("Subjects Enrollment");
        title.setFont(Theme.HEADING_FONT);
        title.setForeground(Theme.TEXT_HEADER);
        container.add(title, BorderLayout.NORTH);

        JPanel inputs = new JPanel(new GridBagLayout());
        inputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 16);

        addFieldGroup(inputs, ticketField, "Enter Ticket Number", 0, gbc);
        addFieldGroup(inputs, lastNameField, "Enter Last Name", 1, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        addFieldGroup(inputs, blockFilterField, "Block Filter", 2, gbc);

        container.add(inputs, BorderLayout.CENTER);
        return container;
    }

    private void addFieldGroup(JPanel container, JTextField field, String labelText, int gridx, GridBagConstraints baseGbc) {
        GridBagConstraints gbc = (GridBagConstraints) baseGbc.clone();
        gbc.gridx = gridx;
        gbc.weightx = 1;

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.LABEL_FONT);
        label.setForeground(Theme.TEXT_BODY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(field);

        container.add(wrapper, gbc);
    }

    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField();
        Theme.styleTextField(field);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private ModernTable createTable() {
        DefaultTableModel model = new DefaultTableModel(BLOCK_DATA, COLUMN_NAMES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return new ModernTable(model);
    }

    private JScrollPane buildTableSection() {
        JScrollPane pane = new JScrollPane(blockTable);
        blockTable.setFillsViewportHeight(true);
        ModernTable.applySmartScrolling(pane);
        return pane;
    }

    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton proceedButton = new JButton("Select Block & Proceed");
        proceedButton.setFont(Theme.SUBHEADER_FONT);
        proceedButton.setBackground(Theme.PRIMARY);
        proceedButton.setForeground(Color.WHITE);
        proceedButton.setFocusPainted(false);
        proceedButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        proceedButton.addActionListener(e -> handleProceed());

        footer.add(proceedButton);
        return footer;
    }

    private void handleProceed() {
        int viewRow = blockTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a block row to continue.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = blockTable.convertRowIndexToModel(viewRow);
        String block = blockTable.getModel().getValueAt(modelRow, 0).toString();

        AssessmentScreen.returnScreen = Screen.BLOCK_SECTIONING;
        JOptionPane.showMessageDialog(this,
            "Block " + block + " locked in. Redirecting to assessment...",
            "Block Selected",
            JOptionPane.INFORMATION_MESSAGE);
        Navigation.to(this, Screen.ASSESSMENT);
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future enhancement: auto-fill filters via context.
    }

    @Override
    public void onLeave() {
        // No teardown necessary.
    }
}