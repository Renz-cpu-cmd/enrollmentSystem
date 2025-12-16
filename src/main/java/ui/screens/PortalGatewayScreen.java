package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.IconCreator;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PortalGatewayScreen extends JPanel implements ScreenView {

    private static final Color NAVY = new Color(0x0C5CB1);
    private static final Color SLATE = new Color(0x64748B);
    private static final Color CARD_BORDER = new Color(0xE2E8F0);
    private static final Color CARD_HOVER_BG = new Color(0xF8FAFC);
    private static final Color GOLD_BAR = new Color(0xDAA520);

    public PortalGatewayScreen() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(Theme.BACKGROUND_COLOR);

        JPanel container = new JPanel(new BorderLayout(0, 24));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(48, 48, 48, 48));

        container.add(createHeader(), BorderLayout.NORTH);
        container.add(createCardRow(), BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        add(container, gbc);
    }

    private JComponent createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Enrollment Gateway");
        title.setForeground(NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel subtitle = new JLabel("Choose how you want to continue");
        subtitle.setForeground(SLATE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);
        return header;
    }

    private JComponent createCardRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 12, 0, 12);

        gbc.gridx = 0;
        row.add(createPortalCard(
            "Freshman Applicant",
            "Start a new application or finish your submission.",
            IconCreator.PERSON_ADD_ICON,
            e -> Navigation.to(this, Screen.DATA_PRIVACY)
        ), gbc);

        gbc.gridx = 1;
        row.add(createPortalCard(
            "Returning Student",
            "Log in to your existing student account.",
            IconCreator.LOGIN_ICON,
            e -> Navigation.to(this, Screen.STUDENT_LOGIN)
        ), gbc);

        return row;
    }

    private JPanel createPortalCard(String title, String description, Icon icon, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new CompoundBorder(new MatteBorder(1, 1, 1, 1, CARD_BORDER), new EmptyBorder(24, 24, 24, 24))
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(300, 320));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        JLabel descLabel = new JLabel("<html><div style='text-align:center;width:240px;'>" + description + "</div></html>");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setForeground(SLATE);
        descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN, 13f));
        descLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel goldBar = new JPanel();
        goldBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        goldBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        goldBar.setBackground(GOLD_BAR);
        goldBar.setVisible(false);

        card.add(iconLabel);
        card.add(titleLabel);
        card.add(descLabel);
        card.add(Box.createVerticalGlue());
        card.add(goldBar);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, title));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE, "background:#F8FAFC");
                card.setBackground(CARD_HOVER_BG);
                goldBar.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE, null);
                card.setBackground(Color.WHITE);
                goldBar.setVisible(false);
            }
        });

        return card;
    }

    @Override
    public void onEnter(NavigationContext context) {
        // No prep needed yet.
    }

    @Override
    public void onLeave() {
        // No teardown necessary.
    }
}
