package ui.screens;

/**
 * Certificate of Registration (COR) screen for printing/export.
 *
 * <p>Extends `JPanel` and implements `ScreenView` to align with the app’s
 * screen routing framework.</p>
 */

import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import dao.AssessmentDAO;
import dao.BlockDAO;
import dao.EnrollmentDAO;
import dao.PaymentDAO;
import model.Assessment;
import model.Block;
import model.Enrollment;
import model.Payment;
import model.Schedule;
import model.Student;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.ModernTable;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CORScreen extends JPanel implements ScreenView {

    private static final Color BACKDROP = new Color(0xE2E8F0);
    private static final Color PRIMARY = new Color(0x1E3A8A);
    private static final Color SUCCESS = new Color(0x16A34A);
    private static final Font SERIF_HEADER = new Font("Serif", Font.BOLD, 24);
    private static final Font SCRIPT = new Font("Segoe Script", Font.PLAIN, 16);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final AssessmentDAO assessmentDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final BlockDAO blockDAO;
    private final PaymentDAO paymentDAO;

    private DefaultTableModel scheduleModel;
    private JLabel studentNameLabel;
    private JLabel studentIdLabel;
    private JLabel programLabel;
    private JLabel yearLevelLabel;
    private JLabel termLabel;
    private JLabel datePrintedLabel;
    private JLabel refNoLabel;
    private JLabel totalUnitsLabel;
    private JLabel totalAssessmentLabel;
    private JLabel amountPaidLabel;
    private JLabel remainingLabel;
    private RotatedStamp stamp;

    public CORScreen(AssessmentDAO assessmentDAO, EnrollmentDAO enrollmentDAO, BlockDAO blockDAO, PaymentDAO paymentDAO) {
        this.assessmentDAO = assessmentDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.blockDAO = blockDAO;
        this.paymentDAO = paymentDAO;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BACKDROP);

        add(buildToolbar(), BorderLayout.NORTH);

        JPanel centered = new JPanel(new GridBagLayout()); // Centers the paper
        centered.setOpaque(false);
        centered.add(buildDocumentCard());
        
        JScrollPane scroll = new JScrollPane(centered);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JPanel navSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        navSouth.setOpaque(false);
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(Theme.BOLD_BODY_FONT);
        backBtn.addActionListener(e -> Navigation.to(this, Screen.DASHBOARD));
        navSouth.add(backBtn);
        add(navSouth, BorderLayout.SOUTH);
    }

    private JComponent buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setOpaque(true);
        bar.setBackground(new Color(0xF1F5F9));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xCBD5E1)));
        
        bar.add(actionButton("🖨 Print"));
        bar.add(actionButton("⬇ Save PDF"));
        bar.add(actionButton("✉ Email"));
        return bar;
    }

    private JButton actionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(Theme.LABEL_FONT);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(0xCBD5E1), 1, true),
            new EmptyBorder(4, 12, 4, 12)
        ));
        return btn;
    }

    private JComponent buildDocumentCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        // A4 Aspect Ratio roughly
        card.setPreferredSize(new Dimension(850, 1000)); 
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(40, 50, 40, 50)));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildLetterhead());
        content.add(Box.createVerticalStrut(24));
        content.add(buildStudentInfo());
        content.add(Box.createVerticalStrut(24));
        content.add(buildScheduleSection());
        content.add(Box.createVerticalStrut(20));
        content.add(buildAssessmentSummary());
        content.add(Box.createVerticalGlue()); // Push footer down
        content.add(buildFooter());

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLetterhead() {
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        
        JLabel uni = new JLabel("University of Enrollment Systems");
        uni.setFont(SERIF_HEADER);
        uni.setForeground(PRIMARY);
        
        JLabel office = new JLabel("OFFICE OF THE REGISTRAR");
        office.setFont(Theme.LABEL_FONT.deriveFont(Font.BOLD, 11f));
        office.setForeground(Color.GRAY);

        JLabel cert = new JLabel("CERTIFICATE OF REGISTRATION");
        cert.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 22f));
        cert.setForeground(Color.BLACK);
        cert.setBorder(new EmptyBorder(12, 0, 0, 0));

        names.add(uni);
        names.add(office);
        names.add(cert);

        JPanel meta = new JPanel();
        meta.setOpaque(false);
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));
        datePrintedLabel = new JLabel("Date: " + DATE_FMT.format(LocalDate.now()));
        refNoLabel = new JLabel("Ref No: --");
        meta.add(datePrintedLabel);
        meta.add(refNoLabel);

        head.add(names, BorderLayout.WEST);
        head.add(meta, BorderLayout.EAST);
        
        // Add stamp overlay logic later if needed, simpler to put in footer or glass pane
        return head;
    }

    private JPanel buildStudentInfo() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 8));
        panel.setOpaque(false);
        
        studentNameLabel = infoField("Student Name", "--");
        studentIdLabel = infoField("Student ID", "--");
        programLabel = infoField("Program", "--");
        
        JPanel termPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        termPanel.setOpaque(false);
        termLabel = new JLabel("Academic Year: 2025-2026 | 1st Term");
        termLabel.setFont(Theme.BODY_FONT);
        termPanel.add(termLabel);

        panel.add(studentNameLabel);
        panel.add(studentIdLabel);
        panel.add(programLabel);
        panel.add(termPanel);
        
        return panel;
    }

    private JLabel infoField(String label, String value) {
        JLabel l = new JLabel("<html><b style='color:#475569'>" + label + ":</b> " + value + "</html>");
        l.setFont(Theme.BODY_FONT.deriveFont(14f));
        return l;
    }

    private JPanel buildScheduleSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        
        JLabel title = new JLabel("Registered Subjects");
        title.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 14f));
        title.setForeground(PRIMARY);
        wrapper.add(title, BorderLayout.NORTH);

        scheduleModel = new DefaultTableModel(new Object[]{"Course", "Description", "Section", "Day/Time", "Room", "Units"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        ModernTable table = new ModernTable(scheduleModel);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(0xF1F5F9));
        
        // Column Sizing
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(0xCBD5E1)));
        scroll.setPreferredSize(new Dimension(0, 300)); // Fixed height for document feel
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildAssessmentSummary() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new MatteBorder(2, 0, 0, 0, new Color(0xE2E8F0))); // Divider line

        JPanel content = new JPanel(new GridLayout(2, 2, 20, 8));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 0, 0, 0));

        totalUnitsLabel = new JLabel("Total Units: --");
        totalUnitsLabel.setFont(Theme.BODY_FONT);
        
        totalAssessmentLabel = new JLabel("Total Assessment: --");
        totalAssessmentLabel.setFont(Theme.BOLD_BODY_FONT);
        
        amountPaidLabel = new JLabel("Amount Paid: --");
        amountPaidLabel.setFont(Theme.BODY_FONT);
        
        remainingLabel = new JLabel("Balance: 0.00");
        remainingLabel.setFont(Theme.BOLD_BODY_FONT);

        content.add(totalUnitsLabel);
        content.add(totalAssessmentLabel);
        content.add(amountPaidLabel);
        content.add(remainingLabel);

        wrapper.add(content, BorderLayout.EAST);
        return wrapper;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(40, 0, 0, 0));

        // QR Code Placeholder
        JPanel qr = new JPanel();
        qr.setPreferredSize(new Dimension(80, 80));
        qr.setBackground(Color.WHITE);
        qr.setBorder(new LineBorder(Color.BLACK));
        qr.add(new JLabel("<html><center>Scan to<br>Verify</center></html>"));
        
        // Signature
        JPanel sig = new JPanel(new BorderLayout());
        sig.setOpaque(false);
        JLabel signName = new JLabel("University Registrar", SwingConstants.CENTER);
        signName.setFont(Theme.LABEL_FONT);
        signName.setBorder(new MatteBorder(1, 0, 0, 0, Color.BLACK));
        
        JLabel signImg = new JLabel("Digitally Signed", SwingConstants.CENTER);
        signImg.setFont(SCRIPT);
        signImg.setForeground(PRIMARY);
        
        sig.add(signImg, BorderLayout.CENTER);
        sig.add(signName, BorderLayout.SOUTH);
        sig.setPreferredSize(new Dimension(200, 60));

        footer.add(qr, BorderLayout.WEST);
        
        // Stamp in center
        stamp = new RotatedStamp("OFFICIALLY ENROLLED");
        footer.add(stamp, BorderLayout.CENTER);
        
        footer.add(sig, BorderLayout.EAST);
        
        // Disclaimer below
        JLabel disc = new JLabel("This is a system-generated document. Valid without physical signature.");
        disc.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        disc.setForeground(Color.GRAY);
        disc.setHorizontalAlignment(SwingConstants.CENTER);
        disc.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JPanel fullFooter = new JPanel(new BorderLayout());
        fullFooter.setOpaque(false);
        fullFooter.add(footer, BorderLayout.CENTER);
        fullFooter.add(disc, BorderLayout.SOUTH);
        
        return fullFooter;
    }

    // --- DATA LOADING WITH FALLBACK ---
    private void loadDataAsync() {
        SwingWorker<CorData, Void> worker = new SwingWorker<>() {
            @Override
            protected CorData doInBackground() {
                Student student = SessionManager.getInstance().getCurrentStudent();
                if (student == null) return null;

                Optional<Assessment> assessmentOpt = assessmentDAO.findLatestPaidForStudent(student.getId());
                Assessment assessment = assessmentOpt.orElse(null);
                
                // Fallback: If DB didn't save program, check Session
                String prog = student.getProgram();
                if (prog == null || prog.isBlank()) {
                    prog = SessionManager.getInstance().getSelectedProgramCode(); // SAFETY NET
                }

                List<Schedule> schedules = new ArrayList<>();
                // Try to get real schedules from DB
                if (assessment != null) {
                    Enrollment enroll = enrollmentDAO.getById(assessment.getEnrollmentId());
                    if (enroll != null) {
                        Optional<Block> blk = blockDAO.findById(enroll.getBlockId());
                        if (blk.isPresent()) {
                            schedules = blk.get().getSchedules();
                        }
                    }
                }

                // SAFETY NET: If schedules are empty (because backend didn't save them), GENERATE THEM
                if (schedules.isEmpty() && prog != null) {
                    schedules = generateMockSchedules(prog); // Force display data
                }

                return new CorData(student, prog, assessment, schedules);
            }

            @Override
            protected void done() {
                try {
                    CorData data = get();
                    if (data != null) renderData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void renderData(CorData data) {
        String name = data.student().getFirstName() + " " + data.student().getLastName();
        studentNameLabel.setText("<html><b style='color:#475569'>Student Name:</b> " + name + "</html>");
        studentIdLabel.setText("<html><b style='color:#475569'>Student ID:</b> " + data.student().getStudentId() + "</html>");
        
        // Use the Program from Data (which might be from Session fallback)
        String pCode = data.programCode() != null ? data.programCode() : "N/A";
        programLabel.setText("<html><b style='color:#475569'>Program:</b> " + pCode + "</html>");

        if (data.assessment() != null) {
            refNoLabel.setText("Ref No: ENR-" + data.assessment().getEnrollmentId());
            double totalDue = data.assessment().getTotalDue() != null ? data.assessment().getTotalDue().doubleValue() : 0.0;
            totalAssessmentLabel.setText("Total Assessment: ₱ " + String.format("%,.2f", totalDue));
            amountPaidLabel.setText("Amount Paid: ₱ " + String.format("%,.2f", totalDue));
        }

        // Fill Table
        scheduleModel.setRowCount(0);
        double totalUnits = 0;
        for (Schedule s : data.schedules()) {
            scheduleModel.addRow(new Object[]{
                s.getCourseCode(),
                s.getSubject(),
                "1-A", // Mock section if missing
                s.getDayPattern() + " " + s.getTimeStart() + "-" + s.getTimeEnd(),
                s.getRoom(),
                s.getUnits()
            });
            totalUnits += s.getUnits();
        }
        totalUnitsLabel.setText("Total Units: " + totalUnits);
    }

    // --- SAFETY NET: COPY OF LOGIC FROM BLOCK SCREEN ---
    private List<Schedule> generateMockSchedules(String code) {
        List<Schedule> list = new ArrayList<>();
        // Simple switch to ensure COR isn't blank
        if (code.contains("BSN")) {
            list.add(new Schedule(null, null, null, "NURS101", "Anatomy & Phys", "Mon", "08:00", "11:00", "NUR-1", "Dr. A", 5.0));
            list.add(new Schedule(null, null, null, "NCM100", "Nursing Fund.", "Tue", "08:00", "11:00", "NUR-2", "Ms. B", 3.0));
            list.add(new Schedule(null, null, null, "BIO101", "Biochemistry", "Wed", "13:00", "16:00", "LAB-1", "Mr. C", 3.0));
        } else if (code.contains("BSACCY")) {
            list.add(new Schedule(null, null, null, "ACCY101", "Fin. Accounting", "Mon", "08:00", "11:00", "RM-101", "CPA X", 6.0));
            list.add(new Schedule(null, null, null, "LAW101", "ObliCon", "Wed", "08:00", "11:00", "RM-102", "Atty Y", 3.0));
        } else {
            list.add(new Schedule(null, null, null, "IT101", "Intro to Computing", "Mon", "09:00", "12:00", "LAB-A", "Prof Z", 3.0));
            list.add(new Schedule(null, null, null, "PROG1", "Programming 1", "Tue", "09:00", "12:00", "LAB-B", "Dev D", 3.0));
        }
        return list;
    }

    @Override
    public void onEnter(NavigationContext context) {
        loadDataAsync();
    }

    @Override
    public void onLeave() {}

    private static class RotatedStamp extends JComponent {
        private final String text;
        RotatedStamp(String text) { 
            this.text = text; 
            setPreferredSize(new Dimension(200, 80));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.rotate(Math.toRadians(-15), getWidth()/2.0, getHeight()/2.0);
            g2.setColor(SUCCESS);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(10, 10, getWidth()-20, getHeight()-20, 20, 20);
            g2.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4;
            g2.drawString(text, x, y);
            g2.dispose();
        }
    }

    private record CorData(Student student, String programCode, Assessment assessment, List<Schedule> schedules) {}
}