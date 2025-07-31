import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class StudyTrackerApp {

    private ArrayList<StudyLog> studyLogs;
    private JFrame frame;
    private JTextField subjectField;
    private JTextField durationField;
    private JTextArea descriptionArea;
    private JList<StudyLog> studyLogList;
    private DefaultListModel<StudyLog> listModel;

    public StudyTrackerApp() {
        studyLogs = new ArrayList<>();
        // In a real app, load data from CSV or database here.
        // For now, let's add some initial dummy data.
        studyLogs.add(new StudyLog("Java", 3.5, "Inheritance"));
        studyLogs.add(new StudyLog("C++", 2.0, "Polymorphism"));
        studyLogs.add(new StudyLog("Python", 1.5, "Decorators"));
        studyLogs.add(new StudyLog("java",3.5,"dsa"));

        initializeGUI();
    }

    private void initializeGUI() {
        // --- Apply Look and Feel ---
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not set Nimbus Look and Feel. Using default.");
        }

        frame = new JFrame("Marvellous Study Tracker");

        // IMPORTANT: setUndecorated(true) MUST be called BEFORE setVisible(true)
        // or any attempts to set opacity, if you want to use frame.setOpacity().
        // Be aware that this removes the native window title bar and buttons.
        frame.setUndecorated(true); // Fix: Make frame undecorated for opacity to work

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // --- Styling for the Main Frame ---
        frame.getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Add New Study Log"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        inputPanel.setBackground(new Color(230, 230, 250)); // Lavender

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(labelFont);
        inputPanel.add(subjectLabel);
        subjectField = new JTextField(20);
        subjectField.setFont(textFieldFont);
        subjectField.setBackground(Color.WHITE);
        inputPanel.add(subjectField);

        JLabel durationLabel = new JLabel("Duration (hours):");
        durationLabel.setFont(labelFont);
        inputPanel.add(durationLabel);
        durationField = new JTextField(20);
        durationField.setFont(textFieldFont);
        durationField.setBackground(Color.WHITE);
        inputPanel.add(durationField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(labelFont);
        inputPanel.add(descriptionLabel);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(textFieldFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        inputPanel.add(descriptionScrollPane);

        JButton addButton = new JButton("Add Study Log");
        addButton.setFont(labelFont);
        addButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createRaisedBevelBorder());
        // Adding a hover effect (simple color change)
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(50, 205, 50)); // LimeGreen
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
            }
        });
        inputPanel.add(addButton);
        inputPanel.add(new JLabel(""));

        // --- List/Display Panel ---
        listModel = new DefaultListModel<>();
        studyLogList = new JList<>(listModel);
        studyLogList.setFont(new Font("Consolas", Font.PLAIN, 14));
        studyLogList.setBackground(new Color(255, 250, 240)); // FloralWhite
        studyLogList.setSelectionBackground(new Color(173, 216, 230)); // LightBlue
        studyLogList.setSelectionForeground(Color.BLACK);
        studyLogList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(studyLogList);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Recent Study Logs"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setBackground(new Color(240, 248, 255));


        // --- Update List ---
        updateStudyLogList();

        // --- Add Button Listener ---
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String subject = subjectField.getText().trim();
                    double duration = Double.parseDouble(durationField.getText().trim());
                    String description = descriptionArea.getText().trim();

                    if (subject.isEmpty() || description.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Subject and Description cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    StudyLog newLog = new StudyLog(subject, duration, description);
                    studyLogs.add(0, newLog);
                    updateStudyLogList();
                    clearInputFields();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for Duration.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.add(inputPanel, BorderLayout.WEST);
        frame.add(scrollPane, BorderLayout.CENTER);

        // --- Simple Fade-in Animation for the Frame ---
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        // Check if per-pixel translucency is supported
        if (gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
            frame.setOpacity(0.0f); // Start completely transparent
            Timer fadeInTimer = new Timer(30, new ActionListener() {
                float opacity = 0.0f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity += 0.05f;
                    if (opacity >= 1.0f) {
                        opacity = 1.0f;
                        ((Timer) e.getSource()).stop();
                    }
                    frame.setOpacity(opacity);
                }
            });
            fadeInTimer.start();
        } else {
            System.out.println("Window opacity not fully supported on this system or frame is decorated. Frame will appear instantly.");
            System.out.println("Translucency support: " + gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT));
            System.out.println("Per-pixel translucency support: " + gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT));
        }

        frame.setVisible(true);
    }

    private void updateStudyLogList() {
        listModel.clear();
        for (StudyLog log : studyLogs) {
            listModel.addElement(log);
        }
    }

    private void clearInputFields() {
        subjectField.setText("");
        durationField.setText("");
        descriptionArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudyTrackerApp();
            }
        });
    }
}