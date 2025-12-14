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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PortalGatewayScreen extends JPanel implements ScreenView {

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
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitle = new JLabel("Choose how you want to continue");
        subtitle.setForeground(new Color(110, 117, 130));
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
            new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(24, 24, 24, 24))
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(280, 280));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        JLabel descLabel = new JLabel("<html><div style='text-align:center;width:180px;'>" + description + "</div></html>");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setForeground(new Color(110, 117, 130));
        descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN, 13f));
        descLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        card.add(iconLabel);
        card.add(titleLabel);
        card.add(descLabel);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, title));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE, "background:#F6F9FF");
                card.setBackground(new Color(246, 249, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE, null);
                card.setBackground(Color.WHITE);
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
