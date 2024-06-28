package Dirgantara.Master;

import Connect.Connect;
import Dirgantara.MasterBandara.updatedelete;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Create_maskapai extends JFrame{
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtID;
    private JTextField txtNegara;
    private JTextField txtkode;
    private JTextField txtStatus;
    private JTable tblPenyewa;
    private JButton btnSimpan;
    private JButton btnBatal;
    private JPanel CreateM;
    private JTextField txtNama;
    private JLabel lbljbt;
    private JLabel lblnama;

    DefaultTableModel model;
    Connect connect = new Connect();

    String id, kode, nama, negara, status;
    int statusText;

    public Create_maskapai(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        model = new DefaultTableModel();

        addKolom();
        autoid();
        tblPenyewa.setModel(model);
        tblPenyewa.setEnabled(false);

        txtStatus.setText("Aktif");
        loaddata();

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtID.getText();
                kode = txtkode.getText();
                nama = txtNama.getText();
                negara = txtNegara.getText();
                status = txtStatus.getText();

                if (txtkode.getText().isEmpty() || txtNama.getText().isEmpty() || txtNegara.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(CreateM, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    if (status.equalsIgnoreCase("Aktif")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak aktif")) {
                        statusText = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[Maskapai] VALUES (?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, kode);
                    connect.pstat.setString(3, nama);
                    connect.pstat.setString(4, negara);
                    connect.pstat.setInt(5, statusText);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Maskapai berhasil ditambahkan");
                    connect.pstat.close();

                    loaddata();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }
                autoid();
                clear();

            }
        });
        tblPenyewa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tblPenyewa.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtID.setText((String) model.getValueAt(i,0));
                txtNama.setText((String) model.getValueAt(i,2));
                txtkode.setText((String) model.getValueAt(i,1));
                txtNegara.setText((String) model.getValueAt(i, 3));
                statusText = (int) model.getValueAt(i,4);
                if (statusText == 1){
                    txtStatus.setText("Aktif");
                }else if (statusText == 0){
                    txtStatus.setText("Tidak aktif");
                }
                txtStatus.setEditable(true);
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Master_maskapai masterMaskapai = new Master_maskapai(username, nama_akses);
                masterMaskapai.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Create_maskapai.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }

    public void FrameConfig() {
        add(CreateM);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void autoid() {
        try {
            String sql = "SELECT MAX(id_maskapai) FROM [dbo].[Maskapai]";
            connect.pstat = connect.conn.prepareStatement(sql);
            ResultSet result = connect.pstat.executeQuery();

            if (result.next()) {
                String maxId = result.getString(1);
                if (maxId != null) {
                    int number = Integer.parseInt(maxId.substring(2)) + 1;
                    String formattedNumber = String.format("%03d", number);
                    txtID.setText("MS" + formattedNumber);
                } else {
                    txtID.setText("MS001");
                }
            }

            txtID.setEnabled(false);
            txtStatus.setEnabled(false);
            connect.pstat.close();
            result.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode data Maskapai: " + ex);
        }
    }

    public void clear() {
        txtkode.setText("");
        txtNama.setText("");
        txtNegara.setText("");
    }

    public void addKolom(){
        model.addColumn("ID");
        model.addColumn("Kode");
        model.addColumn("Nama Maskapai");
        model.addColumn("Negara Asal");
        model.addColumn("Status");
    }

    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        Thread thread = new Thread(() -> {
            try {
                connect.stat = connect.conn.createStatement();
                String query = "SELECT * FROM [dbo].[Maskapai]";
                connect.result = connect.stat.executeQuery(query);

                while (connect.result.next()) {
                    Object[] obj = new Object[5];

                    obj[0] = connect.result.getString("id_maskapai");
                    obj[1] = connect.result.getString("kode_maskapai");
                    obj[2] = connect.result.getString("nama_maskapai");
                    obj[3] = connect.result.getString("negara_asal");
                    obj[4] = connect.result.getInt("status");

                    SwingUtilities.invokeLater(() -> model.addRow(obj));

                    Thread.sleep(100);
                }
                connect.stat.close();
                connect.result.close();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat load data maskapai: " + ex);
            }
        });

        thread.start();
    }
}
