package newUI;

import javax.swing.*;

import org.json.JSONObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditKendaraanUI extends JFrame {
    private JTextField jumlahField;
    private JTextField hargaField;
    private JComboBox<String> idField;

    public EditKendaraanUI() {
        setTitle("Edit Kendaraan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel jumlahLabel = new JLabel("Jumlah:");
        jumlahField = new JTextField();
        JLabel hargaLabel = new JLabel("Harga:");
        hargaField = new JTextField();
        JLabel idLabel = new JLabel("ID Kendaraan:");
        idField = new JComboBox<>(new String[]{"sedan", "hatchback", "suv", "crossover"});

        JButton editButton = new JButton("Edit");
        JButton backButton = new JButton("Back");

        panel.add(jumlahLabel);
        panel.add(jumlahField);
        panel.add(hargaLabel);
        panel.add(hargaField);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(backButton);
        panel.add(editButton);

        add(panel, BorderLayout.CENTER);

        backButton.addActionListener(e -> {
            dispose(); // Close the EditKendaraanUI window
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jumlah = jumlahField.getText();
                String harga = hargaField.getText();
                String jenis = idField.getSelectedItem().toString();

                try {
                    // Create the JSON object for editkendaraan data
                    JSONObject editData = new JSONObject();
                    editData.put("jumlah", jumlah);
                    editData.put("harga", harga);
                    editData.put("jenis", jenis);

                    // Create the URL for editkendaraan endpoint
                    URL url = new URL("http://localhost:7000/editkendaraan");

                    // Create the HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Send the editkendaraan data
                    OutputStream os = connection.getOutputStream();
                    os.write(editData.toString().getBytes());
                    os.flush();

                    // Check the response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        JOptionPane.showMessageDialog(null, "Berhasil mengedit data kendaraan!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal mengedit data kendaraan. Response code: " + responseCode);
                    }

                    connection.disconnect();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error occurred while connecting to the JSON server.");
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EditKendaraanUI().setVisible(true);
            }
        });
    }
}
