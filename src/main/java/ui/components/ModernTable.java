package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Styled table component for dashboard-era screens.
 */
public class ModernTable extends JTable {

    private static final Color SELECTION_COLOR = new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 40);

    public ModernTable() {
        super();
        configureTable();
    }

    public ModernTable(TableModel model) {
        super(model);
        configureTable();
    }

    public ModernTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        configureTable();
    }

    private void configureTable() {
        setFont(Theme.BODY_FONT);
        setRowHeight(35);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(Theme.BORDER_COLOR);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(SELECTION_COLOR);
        setSelectionForeground(Theme.TEXT_HEADER);
        setBackground(Theme.CARD_BG);
        setForeground(Theme.TEXT_HEADER);
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);

        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(Theme.SUBHEADER_FONT);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(createHeaderRenderer());
    }

    private TableCellRenderer createHeaderRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        renderer.setOpaque(true);
        renderer.setBackground(Theme.BG_COLOR);
        renderer.setForeground(Theme.TEXT_HEADER);
        renderer.setBorder(BorderFactory.createEmptyBorder());
        renderer.setFont(Theme.SUBHEADER_FONT);
        return renderer;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        if (!isRowSelected(row)) {
            component.setBackground(row % 2 == 0 ? Theme.CARD_BG : Theme.CARD_BG.brighter());
        } else {
            component.setBackground(SELECTION_COLOR);
        }

        if (column == convertColumnIndexToView(getColumnCount() - 1)) {
            String status = String.valueOf(getValueAt(row, column)).toUpperCase();
            if ("FULL".equals(status)) {
                component.setForeground(Theme.DANGER_COLOR);
            } else if ("OPEN".equals(status)) {
                component.setForeground(Theme.SUCCESS_COLOR);
            } else {
                component.setForeground(Theme.TEXT_HEADER);
            }
        } else {
            component.setForeground(Theme.TEXT_HEADER);
        }
        return component;
    }

    /**
     * Applies consistent scroll pane styling for tables.
     */
    public static void applySmartScrolling(JScrollPane pane) {
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.getViewport().setBackground(Theme.CARD_BG);
        pane.setBackground(Theme.CARD_BG);
        JScrollBar vertical = pane.getVerticalScrollBar();
        if (vertical != null) {
            vertical.setUnitIncrement(18);
        }
    }
}
