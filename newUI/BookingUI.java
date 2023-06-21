package newUI;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookingUI extends JFrame {
    private JTextField usernameField;
    private JTextField teleponField;
    private JTextField waktuField;
    private JTextField jenisField;

    public BookingUI() {
        setTitle("Booking");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        JPanel buttonPanel = new JPanel();

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel teleponLabel = new JLabel("Telepon:");
        teleponField = new JTextField();
        JLabel waktuLabel = new JLabel("Waktu:");
        waktuField = new JTextField();
        JLabel jenisLabel = new JLabel("Jenis:");
        jenisField = new JTextField();
        JButton bookButton = new JButton("Book");
        JButton backButton = new JButton("Back");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(teleponLabel);
        panel.add(teleponField);
        panel.add(waktuLabel);
        panel.add(waktuField);
        panel.add(jenisLabel);
        panel.add(jenisField);
        panel.add(new JLabel());
        buttonPanel.add(backButton);
        buttonPanel.add(bookButton);
        
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String telepon = teleponField.getText();
                String waktu = waktuField.getText();
                String jenis = jenisField.getText();
                if (username.isEmpty() || telepon.isEmpty() || waktu.isEmpty() || jenis.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                }
                else {
                    try {
                        // Create the JSON object for booking data
                        JSONObject bookingData = new JSONObject();
                        bookingData.put("username", username);
                        bookingData.put("telepon", telepon);
                        bookingData.put("waktu", waktu);
                        bookingData.put("jenis", jenis);

                        // Create the URL for booking endpoint
                        URL url = new URL("http://localhost:7000/booking");

                        // Create the HttpURLConnection
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setDoOutput(true);

                        // Send the booking data
                        OutputStream os = connection.getOutputStream();
                        os.write(bookingData.toString().getBytes());
                        os.flush();

                        // Check the response code
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder responseBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                responseBuilder.append(line);
                            }
                            String response = responseBuilder.toString();
                            JOptionPane.showMessageDialog(null, "Booking Berhasil!. ID Booking: " + response);
                        } else {
                            JOptionPane.showMessageDialog(null, "Gagal booking. Response code: " + responseCode);
                        }

                        connection.disconnect();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error occurred while connecting to the JSON server.");
                        ex.printStackTrace();
                    }
                }
            }
        });

        backButton.addActionListener(e -> {
            dispose(); // Close the BookingUI window
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BookingUI().setVisible(true);
            }
        });
    }
}
