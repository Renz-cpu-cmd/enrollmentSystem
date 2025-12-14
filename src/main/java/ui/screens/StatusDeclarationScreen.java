package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.SelectionCard;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusDeclarationScreen extends JPanel implements ScreenView {

    private final SelectionCard regularCard;
    private final SelectionCard irregularCard;
    private final JButton proceedButton;
    private Boolean isRegular;

    public StatusDeclarationScreen() {
        setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBorder(new EmptyBorder(24, 24, 24, 24));
        column.setMaximumSize(new Dimension(540, Integer.MAX_VALUE));

        JLabel header = new JLabel("Confirm Your Status");
        header.setFont(Theme.HEADING_FONT);
        header.setForeground(Theme.TEXT_HEADER);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtext = new JLabel("Select your academic standing for the 2nd Semester.");
        subtext.setFont(Theme.BODY_FONT);
        subtext.setForeground(Theme.TEXT_BODY);
        subtext.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtext.setBorder(new EmptyBorder(8, 0, 16, 0));

        JPanel cardsRow = new JPanel();
        cardsRow.setOpaque(false);
        cardsRow.setLayout(new GridLayout(1, 2, 16, 0));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        regularCard = new SelectionCard("Regular Status", "Pre-loaded block schedule.", Theme.ICON_BG_GREEN);
        irregularCard = new SelectionCard("Irregular Status", "Custom schedule selection.", Theme.ICON_BG_RED);

        regularCard.addActionListener(e -> selectStatus(true));
        irregularCard.addActionListener(e -> selectStatus(false));

        cardsRow.add(regularCard);
        cardsRow.add(irregularCard);

        proceedButton = new JButton("Proceed");
        proceedButton.setFont(Theme.SUBHEADING_FONT);
        proceedButton.setBackground(Theme.PRIMARY);
        proceedButton.setForeground(Color.WHITE);
        proceedButton.setFocusPainted(false);
        proceedButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        proceedButton.setEnabled(false);
        proceedButton.addActionListener(e -> navigateNext());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(24, 0, 0, 0));
        footer.add(proceedButton);

        column.add(header);
        column.add(subtext);
        column.add(cardsRow);
        column.add(footer);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(column, gbc);
    }

    private void selectStatus(boolean regular) {
        this.isRegular = regular;
        regularCard.setSelectedState(regular);
        irregularCard.setSelectedState(!regular);
        proceedButton.setEnabled(true);
    }

    private void navigateNext() {
        if (isRegular == null) {
            JOptionPane.showMessageDialog(this, "Please select your status.");
            return;
        }

        Screen target = isRegular ? Screen.REGULAR_PATH : Screen.IRREGULAR_PATH;
        Navigation.to(this, target);
    }

    @Override
    public void onEnter(NavigationContext context) {
        isRegular = null;
        regularCard.setSelectedState(false);
        irregularCard.setSelectedState(false);
        proceedButton.setEnabled(false);
    }

    @Override
    public void onLeave() {
        // No teardown required.
    }
}