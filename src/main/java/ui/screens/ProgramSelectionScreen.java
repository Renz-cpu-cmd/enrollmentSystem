package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class ProgramSelectionScreen extends JPanel implements ScreenView {

    private static final String[] COLLEGES = {"College of CCS", "College of Business"};
    private static final String[] PROGRAMS = {"BSIT", "BSCS", "BSACCY", "BSBA"};

    private final boolean isIrregularFreshman = true;

    private final JComboBox<String> collegeCombo = new JComboBox<>(COLLEGES);
    private final JComboBox<String> programCombo = new JComboBox<>(PROGRAMS);
    private final JLabel programNameValue = new JLabel();
    private final JLabel durationValue = new JLabel();
    private final JLabel unitsValue = new JLabel();
    private final JLabel bridgingWarning = new JLabel();

    private final Map<String, ProgramInfo> programDetails = Map.of(
        "BSIT", new ProgramInfo("BS Information Technology", "4 Years", "182"),
        "BSCS", new ProgramInfo("BS Computer Science", "4 Years", "180"),
        "BSACCY", new ProgramInfo("BS Accountancy", "4 Years", "165"),
        "BSBA", new ProgramInfo("BS Business Administration", "4 Years", "160")
    );

    public ProgramSelectionScreen() {
        setLayout(new BorderLayout(0, 24));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        programCombo.addActionListener(e -> updateProgramDetails());
        updateProgramDetails();
    }

    private JComponent createHeader() {
        JLabel heading = new JLabel("Select Your Program", SwingConstants.CENTER);
        heading.setFont(Theme.HEADING_FONT);
        heading.setForeground(Theme.TEXT_HEADER);
        heading.setBorder(new EmptyBorder(0, 0, 8, 0));
        return heading;
    }

    private JComponent createContent() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(24, 28, 24, 28)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(560, Integer.MAX_VALUE));

        card.add(createComboField("Select College", collegeCombo));
        card.add(Box.createVerticalStrut(16));
        card.add(createComboField("Select Program", programCombo));
        card.add(Box.createVerticalStrut(20));
        card.add(createInfoCard());
        card.add(Box.createVerticalStrut(12));

        bridgingWarning.setFont(Theme.BODY_FONT);
        bridgingWarning.setForeground(Theme.DANGER_COLOR);
        bridgingWarning.setVisible(false);
        bridgingWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bridgingWarning);

        container.add(card);
        return container;
    }

    private JComponent createComboField(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.SUBHEADER_FONT);
        label.setForeground(Theme.TEXT_HEADER);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));

        comboBox.setFont(Theme.BODY_FONT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(comboBox);
        return panel;
    }

    private JComponent createInfoCard() {
        JPanel infoCard = new JPanel(new GridLayout(3, 1, 0, 6));
        infoCard.setOpaque(false);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(16, 18, 16, 18)));

        programNameValue.setFont(Theme.BODY_FONT);
        durationValue.setFont(Theme.BODY_FONT);
        unitsValue.setFont(Theme.BODY_FONT);
        programNameValue.setForeground(Theme.TEXT_BODY);
        durationValue.setForeground(Theme.TEXT_BODY);
        unitsValue.setForeground(Theme.TEXT_BODY);

        infoCard.add(programNameValue);
        infoCard.add(durationValue);
        infoCard.add(unitsValue);
        return infoCard;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton proceed = new JButton("Proceed to Block Sectioning");
        proceed.setFont(Theme.SUBHEADER_FONT);
        proceed.setBackground(Theme.PRIMARY);
        proceed.setForeground(Color.WHITE);
        proceed.setBorder(new EmptyBorder(12, 32, 12, 32));
        proceed.addActionListener(e -> {
            if (isIrregularFreshman) {
                JOptionPane.showMessageDialog(
                    ProgramSelectionScreen.this,
                    "Transfer Status Detected. Proceeding to Custom Subject Scheduler.",
                    "Custom Scheduler",
                    JOptionPane.INFORMATION_MESSAGE
                );
                Navigation.to(this, Screen.IRREGULAR_PATH);
            } else {
                Navigation.to(this, Screen.BLOCK_SECTIONING);
            }
        });

        footer.add(proceed);
        return footer;
    }

    private void updateProgramDetails() {
        String selectedProgram = (String) programCombo.getSelectedItem();
        ProgramInfo info = selectedProgram == null ? null : programDetails.get(selectedProgram);

        if (info != null) {
            programNameValue.setText("Program Name: " + info.name());
            durationValue.setText("Duration: " + info.duration());
            unitsValue.setText("Units: " + info.units());
        } else {
            programNameValue.setText("Program Name: —");
            durationValue.setText("Duration: —");
            unitsValue.setText("Units: —");
        }

        String strand = SessionManager.getInstance().getShsStrand();
        boolean technicalProgram = "BSIT".equalsIgnoreCase(selectedProgram) || "BSCS".equalsIgnoreCase(selectedProgram);
        boolean needsBridge = strand != null && strand.equalsIgnoreCase("HUMSS");
        if (technicalProgram && needsBridge) {
            bridgingWarning.setText("Heads up: Bridging subjects will be added because your SHS strand is " + strand + ".");
            bridgingWarning.setVisible(true);
        } else {
            bridgingWarning.setVisible(false);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        updateProgramDetails();
    }

    @Override
    public void onLeave() {
        // No cleanup yet.
    }

    private record ProgramInfo(String name, String duration, String units) {
    }
}