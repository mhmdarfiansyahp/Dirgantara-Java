package Dirgantara.MasterBandara;

import Connect.Connect;
import Dirgantara.MenuAwal;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class updatedelete extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JRadioButton rbNama;
    private JTextField txtRbNama;
    private JRadioButton rbLokasi;
    private JTextField txtRbAlamat;
    private JTable tbBandara;
    private JButton btnHapus;
    private JButton btnUbah;
    private JTextField txtId;
    private JTextField txtKode;
    private JTextField txtNama;
    private JTextField txtLokasi;
    private JPanel UpdateDelete;
    private JTextField txtStatus;
    private JButton btnclear;

    DefaultTableModel model;
    Connect connection = new Connect();

    String Id, Kode, Nama, Lokasi,status;
    int statusText;

    public updatedelete(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);

        model = new DefaultTableModel();
        tbBandara.setModel(model);

        ButtonGroup group = new ButtonGroup();
        group.add(rbNama);
        group.add(rbLokasi);
        addColumn();
        txtId.setEditable(false);
        tbBandara.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbBandara.getSelectedRow();
                if (i == -1) {
                    return;
                }
                txtId.setText((String) model.getValueAt(i,0));
                txtKode.setText((String) model.getValueAt(i,1));
                txtNama.setText((String) model.getValueAt(i,2));
                txtLokasi.setText((String) model.getValueAt(i, 3));
                statusText = (int) model.getValueAt(i,4);
                if (statusText == 1){
                    txtStatus.setText("Aktif");
                }else if (statusText == 0){
                    txtStatus.setText("Tidak aktif");
                }
                txtStatus.setEditable(true);
            }
        });
        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Id = txtId.getText();
                Kode = txtKode.getText();
                Nama = txtNama.getText();
                Lokasi = txtLokasi.getText();
                status = txtStatus.getText();

                if (txtKode.getText().isEmpty() || txtNama.getText().isEmpty() || txtLokasi.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(UpdateDelete, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean hasChanges = false;
                try {
                    if (status.equalsIgnoreCase("Aktif")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak aktif")) {
                        statusText = 0;
                    } else {
                        JOptionPane.showMessageDialog(null, "Status tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String selectQuery = "SELECT kode_bandara, nama_bandara, lokasi_bandara, status FROM Bandara WHERE id_bandara = ?";
                    connection.pstat = connection.conn.prepareStatement(selectQuery);
                    connection.pstat.setString(1, Id);
                    connection.result = connection.pstat.executeQuery();

                    if (connection.result.next()) {
                        String existingKode = connection.result.getString("kode_bandara");
                        String existingNama = connection.result.getString("nama_bandara");
                        String existingLokasi = connection.result.getString("lokasi_bandara");
                        int existingStatus = connection.result.getInt("status");

                        if (!existingKode.equals(Kode) || !existingNama.equals(Nama) || !existingLokasi.equals(Lokasi) || existingStatus != statusText) {
                            hasChanges = true;
                        }
                    }
                    connection.pstat.close();
                    connection.result.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat memeriksa perubahan data: " + ex);
                    return;
                }

                if (!hasChanges) {
                    JOptionPane.showMessageDialog(null, "Data masih sama. Ubah data terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String query = "UPDATE Bandara SET kode_bandara=?, nama_bandara=?, lokasi_bandara=?, status=? WHERE id_bandara=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1, Kode);
                    connection.pstat.setString(2, Nama);
                    connection.pstat.setString(3, Lokasi);
                    connection.pstat.setInt(4, statusText);
                    connection.pstat.setString(5, Id);

                    int rowsUpdated = connection.pstat.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Update data Bandara berhasil");
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal mengupdate data Bandara", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }

                    connection.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat update data Bandara!: " + ex);
                }

                loaddata();
                batal();
            }
        });
        rbNama.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showByNama();
            }
        });
        rbLokasi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showByLokasi();
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idBandara = txtId.getText();
                if (idBandara.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Harus memilih data Bandara terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String checkQuery = "SELECT status FROM Bandara WHERE id_bandara = ?";
                    connection.pstat = connection.conn.prepareStatement(checkQuery);
                    connection.pstat.setString(1, idBandara);
                    connection.result = connection.pstat.executeQuery();

                    if (connection.result.next()) {
                        int status = connection.result.getInt("status");
                        if (status == 0) {
                            JOptionPane.showMessageDialog(UpdateDelete, "Data Bandara sudah tidak aktif", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connection.result.close();
                            connection.pstat.close();
                            return;
                        }
                    }

                    connection.result.close();
                    connection.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa status Bandara: " + ex);
                }

                int selectedOption = JOptionPane.showConfirmDialog(UpdateDelete, "Apakah Anda yakin ingin mengubah data Bandara menjadi tidak aktif?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    try {
                        String query = "UPDATE Bandara SET status = 0 WHERE id_bandara = ?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, idBandara);
                        int rowsUpdated = connection.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(UpdateDelete, "Bandara berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(UpdateDelete, "Gagal menghapus data Bandara", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }

                        connection.pstat.close();
                    } catch (SQLException ex) {
                        System.out.println("Terjadi error saat menghapus Bandara: " + ex);
                    }
                    loaddata();
                    batal();
                }
            }
        });
        txtRbNama.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rbNama.setEnabled(true);
                rbLokasi.setEnabled(false);
            }
        });
        txtRbAlamat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rbLokasi.setEnabled(true);
                rbNama.setEnabled(false);
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuBandara menuBandara = new menuBandara(username, nama_akses);
                menuBandara.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(updatedelete.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnclear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                batal();
            }
        });
    }

    public void batal(){
        txtKode.setText("");
        txtNama.setText("");
        txtLokasi.setText("");
        txtId.setText("");
        txtStatus.setText("");
    }

    public void FrameConfig() {
        add(UpdateDelete);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void addColumn(){
        model.addColumn("ID Bandara");
        model.addColumn("Kode Bandara");
        model.addColumn("Nama");
        model.addColumn("Lokasi");
        model.addColumn("Status");
    }

    public void showByNama(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try{
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM Bandara WHERE nama_bandara LIKE '%" + txtRbNama.getText() + "%'";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()){
                    Object[] obj = new Object[5];
                    obj[0] = connection.result.getString("id_bandara");
                    obj[1] = connection.result.getString("kode_bandara");
                    obj[2] = connection.result.getString("nama_bandara");
                    obj[3] = connection.result.getString("lokasi_bandara");
                    obj[4] = connection.result.getInt("status");
                    model.addRow(obj);
            }
            //jika di table tidak ada yang dicari
            if (model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null,"Data Bandara tidak ditemukan!");
            }
            connection.stat.close();
            connection.result.close();
        }catch (Exception e){
            System.out.println("Terjadi error saat load data Bandara! " + e);
        }
    }
    public void showByLokasi(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try{
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM Bandara WHERE lokasi_bandara LIKE '%" + txtRbAlamat.getText() + "%'";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()){
                    Object[] obj = new Object[5];
                    obj[0] = connection.result.getString("id_bandara");
                    obj[1] = connection.result.getString("kode_bandara");
                    obj[2] = connection.result.getString("nama_bandara");
                    obj[3] = connection.result.getString("lokasi_bandara");
                    obj[4] = connection.result.getInt("status");
                    model.addRow(obj);
            }
            //jika di table tidak ada yang dicari
            if (model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null,"Data Bandara tidak ditemukan!");
            }
            connection.stat.close();
            connection.result.close();
        }catch (Exception e){
            System.out.println("Terjadi error saat load data Bandara! " + e);
        }
    }

    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        Thread thread = new Thread(() -> {
            try {
                connection.stat = connection.conn.createStatement();
                String query = "SELECT * FROM [dbo].[Bandara]";
                connection.result = connection.stat.executeQuery(query);

                while (connection.result.next()) {
                    Object[] obj = new Object[5];

                    obj[0] = connection.result.getString("id_bandara");
                    obj[1] = connection.result.getString("kode_bandara");
                    obj[2] = connection.result.getString("nama_bandara");
                    obj[3] = connection.result.getString("lokasi_bandara");
                    obj[4] = connection.result.getInt("status");

                    SwingUtilities.invokeLater(() -> model.addRow(obj));

                    Thread.sleep(150);
                }
                connection.stat.close();
                connection.result.close();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat load data Bandara: " + ex);
            }
        });
        thread.start();
    }
}
