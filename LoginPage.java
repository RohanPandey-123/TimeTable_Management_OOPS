import javax.swing.*;
import org.mindrot.jbcrypt.BCrypt;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTabbedPane roleTabs;
    private JButton signupButton;

    public LoginPage() {
        setTitle("TTB - Time Table Builder");
        setSize(500, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Custom font
        Font titleFont = new Font("SansSerif", Font.BOLD, 32);
        Font subtitleFont = new Font("SansSerif", Font.ITALIC, 14);
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);

        // Header
        JLabel title = new JLabel("📅  T T B", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setForeground(new Color(50, 50, 130));

        JLabel subtitle = new JLabel("Time Table Builder", SwingConstants.CENTER);
        subtitle.setFont(subtitleFont);
        subtitle.setForeground(Color.DARK_GRAY);

        // Role Tabs with icons
        roleTabs = new JTabbedPane(JTabbedPane.TOP);
        roleTabs.addTab("Student", new ImageIcon("icons/student.png"), new JPanel());
        roleTabs.addTab("Admin", new ImageIcon("icons/admin.png"), new JPanel());
        roleTabs.addTab("Teacher", new ImageIcon("icons/teacher.png"), new JPanel());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Color color1 = new Color(230, 240, 255);
                Color color2 = new Color(200, 220, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("🔓 LOGIN");
        signupButton = new JButton("📝 SIGN UP");

        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        signupButton.setBackground(new Color(60, 179, 113));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);

        JLabel userLabel = new JLabel("👤 Username:");
        JLabel passLabel = new JLabel("🔑 Password:");
        userLabel.setFont(labelFont);
        passLabel.setFont(labelFont);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        formPanel.add(signupButton, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(roleTabs, BorderLayout.NORTH);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(subtitle, BorderLayout.SOUTH);

        // Hide signup button for admin
        roleTabs.addChangeListener(e -> {
            String selectedRole = roleTabs.getTitleAt(roleTabs.getSelectedIndex()).toLowerCase();
            signupButton.setVisible(!selectedRole.equals("admin"));
        });

        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> new SignupPage());

        // Initial visibility
        signupButton.setVisible(!roleTabs.getTitleAt(roleTabs.getSelectedIndex()).equalsIgnoreCase("admin"));

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleTabs.getTitleAt(roleTabs.getSelectedIndex()).toLowerCase();

        if (role.equals("admin")) {
            if (username.equals("admin") && password.equals("123")) {
                JOptionPane.showMessageDialog(this, "Login Successful as admin 🎉");
                dispose();
                new AdminDashboard("admin");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        File file = new File("users/" + role + "s.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (username.equals(data[0]) && BCrypt.checkpw(password, data[1])) {
                    JOptionPane.showMessageDialog(this, "Login Successful as " + role + " 🎉");
                    dispose();

                    if (role.equals("teacher")) {
                        new TeacherDashboard(username);
                    } else if (role.equals("student")) {
                        new StudentDashboard(username);
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading user data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
