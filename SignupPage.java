import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.mindrot.jbcrypt.BCrypt;

public class SignupPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public SignupPage() {
        setTitle("Signup - TTB");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Signup", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleCombo = new JComboBox<>(new String[]{"student", "teacher"});

        JButton signupBtn = new JButton("SIGN UP");

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Role:"));
        form.add(roleCombo);
        form.add(new JLabel());
        form.add(signupBtn);

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        signupBtn.addActionListener(e -> handleSignup());

        setVisible(true);
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File("users/" + role + "s.csv");

        try {
            // Check if username exists
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    reader.close();
                    return;
                }
            }
            reader.close();

            // Hash and store password
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            FileWriter fw = new FileWriter(file, true);
            fw.write(username + "," + hashed + "\n");
            fw.close();

            JOptionPane.showMessageDialog(this, "Signup successful! You can now login.");
            dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving user data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
