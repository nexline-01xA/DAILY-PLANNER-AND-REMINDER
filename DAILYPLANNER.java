// ==============================================================
// PROJECT 2: DAILY PLANENR AND REMINDR APP (Java Swing)
// ==============================================================

// --- FILE: Task.java ---
import java.time.LocalDateTime;

public class Task {
    private String title;
    private String notes;
    private LocalDateTime when;

    public Task(String title, String notes, LocalDateTime when) {
        this.title = title;
        this.notes = notes;
        this.when = when;
    }

    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public LocalDateTime getWhen() { return when; }
}

// --- FILE: ReminderManager.java ---
import javax.swing.*;
import java.time.*;
import java.util.*;

public class ReminderManager {
    private final Timer timer = new Timer();

    public void schedule(Task t) {
        long delay = Duration.between(LocalDateTime.now(), t.getWhen()).toMillis();
        if (delay <= 0) return;
        timer.schedule(new TimerTask(){
            public void run(){ SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,"Reminder: "+t.getTitle()+"\n"+t.getNotes())); }
        }, delay);
    }
}

// --- FILE: DailyPlannerApp.java ---
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DailyPlannerApp extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Title","When","Notes"},0);
    private final java.util.List<Task> tasks = new ArrayList<>();
    private final ReminderManager reminders = new ReminderManager();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DailyPlannerApp() {
        super("Daily Planenr & Remindr");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700,400);
        setLocationRelativeTo(null);

        JTable table = new JTable(model);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Task");
        JButton del = new JButton("Remove");
        top.add(add); top.add(del);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        add.addActionListener(e -> addTask());
        del.addActionListener(e -> {int r=table.getSelectedRow(); if(r!=-1){model.removeRow(r); tasks.remove(r);}});
    }

    private void addTask() {
        JTextField title = new JTextField();
        JTextField time = new JTextField();
        JTextArea notes = new JTextArea(4,20);
        JPanel p = new JPanel(new GridLayout(0,2));
        p.add(new JLabel("Title:")); p.add(title);
        p.add(new JLabel("When (yyyy-MM-dd HH:mm):")); p.add(time);
        p.add(new JLabel("Notes:")); p.add(new JScrollPane(notes));

        if (JOptionPane.showConfirmDialog(this,p,"Add Task",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            try {
                Task t = new Task(title.getText(), notes.getText(), LocalDateTime.parse(time.getText(), fmt));
                tasks.add(t);
                model.addRow(new Object[]{t.getTitle(), t.getWhen().format(fmt), t.getNotes()});
                reminders.schedule(t);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DailyPlannerApp().setVisible(true));
    }
}
