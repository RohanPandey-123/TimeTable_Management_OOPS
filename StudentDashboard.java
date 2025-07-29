import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private JPanel leftPanel, rightPanel;
    private DefaultListModel<String> courseListModel;
    private JList<String> courseList;
    private JTable timetable;
    private DefaultTableModel timetableModel;
    private List<ClassInfo> availableClasses;
    private Map<String, ClassInfo> selectedCourses;
    private String studentName;
    private File studentFile;

    public StudentDashboard(String studentName) {
        this.studentName = studentName;
        this.availableClasses = new ArrayList<>();
        this.selectedCourses = new HashMap<>();

        File userDir = new File("users");
        if (!userDir.exists()) userDir.mkdir();
        this.studentFile = new File(userDir, studentName.replaceAll("\\s+", "_") + "_classes.csv");

        loadAvailableCourses();

        setTitle("Student Dashboard");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(1000, 40));
        topBar.setBackground(new Color(52, 152, 219));

        JLabel nameLabel = new JLabel("  👤 STUDENT: " + studentName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        topBar.add(nameLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });
        topBar.add(logoutButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBackground(new Color(44, 62, 80));

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        courseList.setBackground(new Color(236, 240, 241));
        JScrollPane scrollPane = new JScrollPane(courseList);

        JPanel courseControlPanel = new JPanel(new FlowLayout());
        courseControlPanel.setBackground(new Color(44, 62, 80));

        JButton searchAddButton = new JButton("➕ Search / Add");
        searchAddButton.setBackground(new Color(39, 174, 96));
        searchAddButton.setForeground(Color.WHITE);

        JButton removeButton = new JButton("❌ Remove Selected");
        removeButton.setBackground(new Color(192, 57, 43));
        removeButton.setForeground(Color.WHITE);

        courseControlPanel.add(searchAddButton);
        courseControlPanel.add(removeButton);

        leftPanel.add(courseControlPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel(new BorderLayout());
        String[] columns = {"", "M", "T", "W", "Th", "F"};
        timetableModel = new DefaultTableModel(columns, 9);
        String[] times = {"8 - 9", "9 - 10", "10 - 11", "11 - 12", "12 - 1", "1 - 2", "2 - 3", "3 - 4", "4 - 5"};
        for (int i = 0; i < 9; i++) {
            timetableModel.setValueAt(times[i], i, 0);
        }

        timetable = new JTable(timetableModel);
        timetable.setRowHeight(50);
        timetable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        timetable.setGridColor(Color.GRAY);
        timetable.setShowGrid(true);
        timetable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        rightPanel.add(new JScrollPane(timetable), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        searchAddButton.addActionListener(e -> showAvailableClasses());
        removeButton.addActionListener(e -> removeSelectedCourse());

        loadSelectedCourses();
        setVisible(true);
    }

    private void loadAvailableCourses() {
        File adminCourseFile = new File("users/admin_courses.csv");
        if (!adminCourseFile.exists()) {
            System.out.println("Admin course file not found!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(adminCourseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String subject = parts[0];
                    String timeSlot = parts[1];
                    String days = parts[2];
                    String roomNo = parts[3];
                    String teacherName = parts[4];

                    ClassInfo classInfo = new ClassInfo(subject, teacherName, days, roomNo, timeSlot);
                    availableClasses.add(classInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAvailableClasses() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (ClassInfo c : availableClasses) {
            String displayText = c.code + " (" + c.instructor + ") - " + c.days + " - " + c.timeSlot + " - Room: " + c.roomNo;
            JButton classBtn = new JButton("➕ " + displayText);
            classBtn.setBackground(new Color(149, 165, 166));
            classBtn.setFocusPainted(false);
            classBtn.addActionListener(e -> {
                if (hasClash(c)) {
                    JOptionPane.showMessageDialog(this, "Time clash detected! Cannot add " + c.code, "Clash", JOptionPane.ERROR_MESSAGE);
                } else {
                    String displayName = c.code + " (" + c.instructor + ")";
                    if (!selectedCourses.containsKey(displayName)) {
                        courseListModel.addElement(displayName);
                        selectedCourses.put(displayName, c);
                        addToTimetable(c);
                        saveSelectedCourses();
                    }
                }
            });
            panel.add(classBtn);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Available Classes", JOptionPane.PLAIN_MESSAGE);
    }

    private boolean hasClash(ClassInfo newClass) {
        int[] timeRange = parseTimeSlot(newClass.timeSlot);
        int startHour = timeRange[0];
        int endHour = timeRange[1];

        for (String day : newClass.days.split(" ")) {
            int col = dayToColumn(day);
            if (col == -1) continue;

            for (int hour = startHour; hour < endHour; hour++) {
                int row = hour - 8;
                if (row >= 0 && row < 9 && timetableModel.getValueAt(row, col) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private int[] parseTimeSlot(String timeSlot) {
        String[] parts = timeSlot.trim().split(" - ");
        int startHour = parseHour(parts[0]);
        int endHour = parseHour(parts[1]);
        return new int[]{startHour, endHour};
    }

    private int parseHour(String time) {
        time = time.toLowerCase();
        int hour;
        if (time.contains("am")) {
            hour = Integer.parseInt(time.replace("am", "").trim());
        } else {
            hour = Integer.parseInt(time.replace("pm", "").trim());
            if (hour < 12) hour += 12;
        }
        return hour;
    }

    private void addToTimetable(ClassInfo c) {
        int[] timeRange = parseTimeSlot(c.timeSlot);
        int startHour = timeRange[0];
        int endHour = timeRange[1];

        for (String day : c.days.split(" ")) {
            int col = dayToColumn(day);
            if (col == -1) continue;

            for (int hour = startHour; hour < endHour; hour++) {
                int row = hour - 8;
                if (row >= 0 && row < 9) {
                    timetableModel.setValueAt(c.code + " (" + c.instructor + ")", row, col);
                }
            }
        }
    }

    private void removeSelectedCourse() {
        String selectedCourse = courseList.getSelectedValue();
        if (selectedCourse != null && selectedCourses.containsKey(selectedCourse)) {
            ClassInfo c = selectedCourses.get(selectedCourse);
            int[] timeRange = parseTimeSlot(c.timeSlot);
            int startHour = timeRange[0];
            int endHour = timeRange[1];

            for (String day : c.days.split(" ")) {
                int col = dayToColumn(day);
                if (col == -1) continue;

                for (int hour = startHour; hour < endHour; hour++) {
                    int row = hour - 8;
                    if (row >= 0 && row < 9) {
                        timetableModel.setValueAt(null, row, col);
                    }
                }
            }
            courseListModel.removeElement(selectedCourse);
            selectedCourses.remove(selectedCourse);
            saveSelectedCourses();
        }
    }

    private void loadSelectedCourses() {
        if (!studentFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String code = parts[0];
                    String instructor = parts[1];
                    String days = parts[2];
                    String roomNo = parts[3];
                    String timeSlot = parts[4];

                    ClassInfo c = new ClassInfo(code, instructor, days, roomNo, timeSlot);
                    String displayName = code + " (" + instructor + ")";
                    selectedCourses.put(displayName, c);
                    courseListModel.addElement(displayName);
                    addToTimetable(c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSelectedCourses() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(studentFile))) {
            for (ClassInfo c : selectedCourses.values()) {
                pw.println(c.code + "," + c.instructor + "," + c.days + "," + c.roomNo + "," + c.timeSlot);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        new StudentDashboard("Rohan Pandey");
    }
}