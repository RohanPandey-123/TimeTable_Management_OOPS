import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private String teacherName;
    private JTable timetable;
    private DefaultTableModel timetableModel;
    private java.util.List<ClassInfo> teacherClasses;

    public TeacherDashboard(String teacherName) {
        this.teacherName = teacherName;
        this.teacherClasses = new ArrayList<>();

        setTitle("Teacher Dashboard");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ==== Top Bar ====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(1000, 40));
        topBar.setBackground(Color.LIGHT_GRAY);

        JLabel nameLabel = new JLabel("  TEACHER: " + teacherName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topBar.add(nameLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });
        topBar.add(logoutButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ==== Timetable ====
        String[] columns = {"", "M", "T", "W", "Th", "F"};
        timetableModel = new DefaultTableModel(columns, 9);
        String[] times = {"8 - 9", "9 - 10", "10 - 11", "11 - 12", "12 - 1", "1 - 2", "2 - 3", "3 - 4", "4 - 5"};
        for (int i = 0; i < 9; i++) {
            timetableModel.setValueAt(times[i], i, 0);
        }

        timetable = new JTable(timetableModel);
        timetable.setRowHeight(50);
        add(new JScrollPane(timetable), BorderLayout.CENTER);

        loadTeacherClasses();      // 🔄 Load classes from admin_courses.csv
        populateTimetable();       // 📋 Render into table

        setVisible(true);
    }

    private void loadTeacherClasses() {
        File adminCoursesFile = new File("users/admin_courses.csv");
        if (!adminCoursesFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(adminCoursesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String courseName = parts[0];
                    String timeRange = parts[1]; // e.g., "9am - 11am"
                    String days = parts[2];      // e.g., "M F"
                    List<String> dayList = Arrays.asList(days.trim().split("\\s+"));
                    String roomNo = parts[3];
                    String courseTeacher = parts[4];

                    if (courseTeacher.equalsIgnoreCase(teacherName)) {
                        int timeSlot = parseStartHour(timeRange); // Convert "9am - 11am" → 9
                        // Using the new overloaded constructor for ClassInfo
                        teacherClasses.add(new ClassInfo(courseName, teacherName, dayList, Integer.toString(timeSlot)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateTimetable() {
        for (ClassInfo c : teacherClasses) {
            for (String day : c.getDays()) {
                int col = dayToColumn(day);
                int row = Integer.parseInt(c.getTimeSlot()) - 8; // 8 AM is row 0
                if (col != -1 && row >= 0 && row < 9) {
                    timetableModel.setValueAt(c.getCourse() + " - " + String.join(" ", c.getDays()), row, col);
                }
            }
        }
    }
    
    private int parseStartHour(String timeRange) {
        try {
            String start = timeRange.split("-")[0].trim().toLowerCase();
            int hour = Integer.parseInt(start.replaceAll("[^0-9]", ""));
            if (start.contains("pm") && hour != 12) hour += 12;
            return hour;
        } catch (Exception e) {
            e.printStackTrace();
            return 8; // fallback to 8 AM
        }
    }

    private int dayToColumn(String day) {
        return switch (day) {
            case "M" -> 1;
            case "T" -> 2;
            case "W" -> 3;
            case "Th" -> 4;
            case "F" -> 5;
            default -> -1;
        };
    }

    public static void main(String[] args) {
        new TeacherDashboard("abdulla"); // test login
    }
}