package com.courseapp.ui.student;

import com.courseapp.model.Schedule;
import com.courseapp.model.Student;
import com.courseapp.service.ScheduleService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class SchedulePanel extends JPanel {

    private final Student         student;
    private final ScheduleService scheduleService = new ScheduleService();

    public SchedulePanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("My Weekly Schedule"));

        JButton refreshBtn = UITheme.grayButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        refreshBtn.addActionListener(e -> refreshSchedule());
        topBar.add(Box.createHorizontalStrut(16));
        topBar.add(refreshBtn);

        // ── Content area ──────────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BG_LIGHT);

        Map<String, List<Schedule>> byDay =
            scheduleService.getStudentScheduleByDay(student.getId());

        if (byDay.isEmpty()) {
            JLabel emptyLbl = UITheme.secondaryLabel(
                "You have no scheduled classes yet. Enroll in courses to see your schedule.");
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLbl.setBorder(UITheme.paddingBorder(40, 0));
            content.add(emptyLbl);
        } else {
            for (Map.Entry<String, List<Schedule>> entry : byDay.entrySet()) {
                content.add(buildDayCard(entry.getKey(), entry.getValue()));
                content.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(topBar, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
    }

    private JPanel buildDayCard(String day, List<Schedule> slots) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Day header
        JPanel dayHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        dayHeader.setBackground(UITheme.HEADER_BG);
        JLabel dayLabel = new JLabel(day);
        dayLabel.setFont(UITheme.FONT_HEADER);
        dayLabel.setForeground(Color.WHITE);
        dayHeader.add(dayLabel);
        card.add(dayHeader, BorderLayout.NORTH);

        // Slots table
        String[] cols = {"Course Code", "Course Title", "Start", "End", "Room"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Schedule s : slots) {
            model.addRow(new Object[]{
                s.getCourseCode(),
                s.getCourseTitle(),
                s.getStartTime(),
                s.getEndTime(),
                s.getRoom()
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(0, slots.size() * 30 + 38));

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void refreshSchedule() {
        removeAll();
        setLayout(new BorderLayout(0, 12));
        buildUI();
        revalidate();
        repaint();
    }
}
