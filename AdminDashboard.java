import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;

public class AdminDashboard extends JFrame {
    private String AdminUsername;
    private DefaultTableModel tableModel;
    private File csvFile;
    private JTable table;

    public AdminDashboard(String AdminUsername) {
        this.AdminUsername = AdminUsername;
        this.csvFile = new File("users/" + AdminUsername + "_courses.csv");

        setTitle("My Classes - TTB");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ----------- Top Panel (User info + Logout + TTB) -----------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 70, 110));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel userLabel = new JLabel("Admin: " + AdminUsername.toUpperCase());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);

        JLabel ttbLabel = new JLabel("T T B");
        ttbLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        ttbLabel.setForeground(new Color(255, 215, 0));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(ttbLabel);
        rightPanel.add(logoutButton);

        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // ----------- Center Title -----------
        JLabel title = new JLabel("MY CLASSES", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // ----------- Table -----------
        String[] columns = {"S.N", "COURSE NAME", "TIME", "DAYS", "ROOM NO.", "TEACHER"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(200, 200, 220));
        table.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);

        // Load courses
        loadCourses();

        // ----------- Bottom Buttons -----------
        JButton addButton = new JButton("ADD COURSE");
        JButton removeButton = new JButton("REMOVE SELECTED");

        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.setFocusPainted(false);

        removeButton.setBackground(new Color(255, 193, 7));
        removeButton.setForeground(Color.BLACK);
        removeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        removeButton.setFocusPainted(false);

        addButton.addActionListener(e -> {
            addCourse();
            tableModel.fireTableDataChanged();
        });

        removeButton.addActionListener(e -> removeSelectedCourse());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(removeButton);

        // ----------- Add to Frame -----------
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.add(topPanel);
        headerPanel.add(title);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        if (!csvFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int sn = 1;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(sn++));
                row.addAll(Arrays.asList(data));
                tableModel.addRow(row);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading courses!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> getTeacherUsernames() {
        List<String> teachers = new ArrayList<>();
        File teacherFile = new File("users/teachers.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(teacherFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length >= 1 && !parts[0].trim().isEmpty()) {
                    teachers.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading teachers!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return teachers;
    }

    private void addCourse() {
        JTextField courseField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField timeFromField = new JTextField();
        JTextField timeToField = new JTextField();

        JCheckBox[] dayBoxes = {
            new JCheckBox("M"), new JCheckBox("T"), new JCheckBox("W"),
            new JCheckBox("Th"), new JCheckBox("F")
        };

        List<String> teachers = getTeacherUsernames();
        JComboBox<String> teacherDropdown = new JComboBox<>(teachers.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBackground(new Color(255, 255, 255));
        panel.add(new JLabel("Course Name:"));
        panel.add(courseField);
        panel.add(new JLabel("Room No:"));
        panel.add(roomField);
        panel.add(new JLabel("Time From (e.g. 9am):"));
        panel.add(timeFromField);
        panel.add(new JLabel("Time To (e.g. 11am):"));
        panel.add(timeToField);
        panel.add(new JLabel("Select Days:"));
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JCheckBox day : dayBoxes) {
            daysPanel.add(day);
        }
        panel.add(daysPanel);
        panel.add(new JLabel("Assign Teacher:"));
        panel.add(teacherDropdown);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String course = courseField.getText().trim();
            String room = roomField.getText().trim();
            String time = timeFromField.getText().trim() + " - " + timeToField.getText().trim();
            StringBuilder days = new StringBuilder();
            for (JCheckBox cb : dayBoxes) {
                if (cb.isSelected()) days.append(cb.getText()).append(" ");
            }
            String teacher = (String) teacherDropdown.getSelectedItem();

            if (course.isEmpty() || room.isEmpty() || time.trim().equals("-") || teacher == null || teacher.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields properly.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vector<String> row = new Vector<>();
            row.add(String.valueOf(tableModel.getRowCount() + 1));
            row.add(course);
            row.add(time);
            row.add(days.toString().trim());
            row.add(room);
            row.add(teacher);
            tableModel.addRow(row);

            saveCoursesToCSV();
        }
    }

    private void removeSelectedCourse() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(String.valueOf(i + 1), i, 0);
            }
            saveCoursesToCSV();
        }
    }

    private void saveCoursesToCSV() {
        try (PrintWriter pw = new PrintWriter(csvFile)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j < tableModel.getColumnCount(); j++) {
                    sb.append(tableModel.getValueAt(i, j));
                    if (j < tableModel.getColumnCount() - 1) sb.append(",");
                }
                pw.println(sb);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving courses!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
