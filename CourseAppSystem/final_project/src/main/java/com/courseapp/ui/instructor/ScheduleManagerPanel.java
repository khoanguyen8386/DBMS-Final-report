package com.courseapp.ui.instructor;

import com.courseapp.model.Course;
import com.courseapp.model.Instructor;
import com.courseapp.model.Schedule;
import com.courseapp.service.InstructorService;
import com.courseapp.service.ScheduleService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScheduleManagerPanel extends JPanel {

    private final Instructor        instructor;
    private final InstructorService instructorService = new InstructorService();
    private final ScheduleService   scheduleService   = new ScheduleService();

    private JTable            table;
    private DefaultTableModel model;
    private JLabel            statusLabel;

    private static final String[] COLS = {"ID","Course","Day","Start","End","Room"};
    private static final String[] DAYS = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

    public ScheduleManagerPanel(Instructor instructor) {
        this.instructor = instructor;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
        loadSchedule();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("My Teaching Schedule"));

        model = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        statusLabel = UITheme.secondaryLabel(" ");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);
        JButton addBtn    = UITheme.successButton("Add Slot");
        JButton editBtn   = UITheme.primaryButton("Edit");
        JButton deleteBtn = UITheme.dangerButton("Delete");
        JButton refreshBtn= UITheme.grayButton("Refresh");
        addBtn.addActionListener(e    -> showSlotDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e-> loadSchedule());
        btnBar.add(refreshBtn); btnBar.add(addBtn); btnBar.add(editBtn); btnBar.add(deleteBtn);

        add(topBar,                 BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnBar,                 BorderLayout.SOUTH);
    }

    private void loadSchedule() {
        model.setRowCount(0);
        scheduleService.getInstructorSchedule(instructor.getId()).forEach(s ->
            model.addRow(new Object[]{
                s.getId(), s.getCourseCode() + " — " + s.getCourseTitle(),
                s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), s.getRoom()
            }));
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a slot first."); return; }
        // Build a minimal Schedule from the table row for editing
        Schedule s = new Schedule();
        s.setId((int) model.getValueAt(row, 0));
        s.setDayOfWeek((String) model.getValueAt(row, 2));
        s.setStartTime((String) model.getValueAt(row, 3));
        s.setEndTime((String)   model.getValueAt(row, 4));
        s.setRoom((String)      model.getValueAt(row, 5));
        showSlotDialog(s);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a slot first."); return; }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this schedule slot?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { scheduleService.deleteSlot(id); loadSchedule(); }
    }

    private void showSlotDialog(Schedule existing) {
        boolean isEdit = existing != null && existing.getId() > 0;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Slot" : "Add Schedule Slot", true);
        dlg.setSize(400, 380);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_WHITE);
        form.setBorder(UITheme.paddingBorder(20,24));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6,4,6,4);

        JComboBox<Course>  courseCb  = new JComboBox<>();
        JComboBox<String>  dayCb     = new JComboBox<>(DAYS);
        JTextField         startF    = UITheme.styledTextField(8);
        JTextField         endF      = UITheme.styledTextField(8);
        JTextField         roomF     = UITheme.styledTextField(10);

        instructorService.getMyCourses(instructor.getId()).forEach(courseCb::addItem);

        if (isEdit) {
            dayCb.setSelectedItem(existing.getDayOfWeek());
            startF.setText(existing.getStartTime());
            endF.setText(existing.getEndTime());
            roomF.setText(existing.getRoom());
        }

        String[] labels = {"Course *","Day *","Start (HH:mm) *","End (HH:mm) *","Room *"};
        JComponent[] fields = {courseCb, dayCb, startF, endF, roomF};
        for (int i=0;i<labels.length;i++) {
            g.gridwidth=1; g.weightx=0; g.gridx=0; g.gridy=i;
            JLabel l = new JLabel(labels[i]); l.setFont(UITheme.FONT_SMALL); form.add(l,g);
            g.gridx=1; g.weightx=1; form.add(fields[i],g);
        }

        JLabel status = new JLabel(" ");
        status.setForeground(UITheme.DANGER); status.setFont(UITheme.FONT_SMALL);
        g.gridx=0; g.gridy=5; g.gridwidth=2; form.add(status,g);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        btnP.setBackground(UITheme.BG_LIGHT);
        JButton cancel = UITheme.grayButton("Cancel");
        JButton save   = UITheme.primaryButton(isEdit ? "Save" : "Add Slot");
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            Schedule s = isEdit ? existing : new Schedule();
            Course c = (Course) courseCb.getSelectedItem();
            if (c == null) { status.setText("Select a course."); return; }
            s.setCourseId(c.getId());
            s.setDayOfWeek((String) dayCb.getSelectedItem());
            s.setStartTime(startF.getText().trim());
            s.setEndTime(endF.getText().trim());
            s.setRoom(roomF.getText().trim());
            boolean ok = isEdit
                ? scheduleService.updateSlot(s)
                : scheduleService.addSlot(s);
            if (ok) { loadSchedule(); dlg.dispose(); }
            else status.setText("Failed to save slot. Check time format HH:mm.");
        });
        btnP.add(cancel); btnP.add(save);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
