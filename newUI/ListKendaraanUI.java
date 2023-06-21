package newUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListKendaraanUI extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;

    public ListKendaraanUI() {
        setTitle("List Kendaraan UI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID Mobil");
        tableModel.addColumn("Jenis Mobil");
        tableModel.addColumn("Jumlah Mobil");
        tableModel.addColumn("Harga Mobil");

        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose(); // Close the ListKendaraanUI window
        });

        // Fetch vehicle data from the server
        try {
            URL url = new URL("http://localhost:7000/listkendaraan");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                String response = responseBuilder.toString();
                System.out.println("Server Response: " + response);

                // Parse the JSON response
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject vehicle = dataArray.getJSONObject(i);
                    int id = vehicle.getInt("idmobil");
                    String jenismobil = vehicle.getString("jenis_mobil");
                    int jumlahMobil = vehicle.getInt("jumlah_mobil");
                    int hargaMobil = vehicle.getInt("harga_mobil");

                    // Add the data to the table model
                    tableModel.addRow(new Object[]{id, jenismobil, jumlahMobil, hargaMobil});
                }
            } else {
                System.out.println("Failed to connect to the server. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception ex) {
            System.out.println("Error occurred while connecting to the server.");
            ex.printStackTrace();
        }

        // Create the JTable and set the model
        table = new JTable(tableModel);

        // Create a scroll pane and add the table to it
        scrollPane = new JScrollPane(table);

        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ListKendaraanUI().setVisible(true);
            }
        });
    }
}
