package Dirgantara.MasterJadwal;

import Connect.Connect;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpdateDeleteJadwal extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JButton btnHapus;
    private JButton btnUbah;
    private JTextField txtId;
    private JTable tbJadwal;
    private JButton btnCari;
    private JTextField txtCariID;
    private JButton btnSemua;
    private JButton btncleardetail;
    private JComboBox cbTujuan;
    private JComboBox cbRute;
    private JComboBox cbPesawat;
    private JTextField txtBerangkat;
    private JTextField txtSampai;
    private JPanel updateJ;
    private JTextField txtStatus;
    private JComboBox cbAsal;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();

    public static void main(String[] args) {
        String username = null;
        String nama_akses = null;

        new UpdateDeleteJadwal(username,nama_akses);
    }

    String id, status, idrute, idpesawat, idasal, idtujuan, waktuBerangkat, waktuSampai;

    int statusText;

    public UpdateDeleteJadwal(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);

        modelmodel = new DefaultTableModel();
        addKolom();
        tbJadwal.setModel(modelmodel);
        txtId.setEditable(false);

        rute();
        pesawat();
        Asalbandara();
        TujuanBandara();
        cbRute.setSelectedIndex(-1);
        cbAsal.setSelectedIndex(-1);
        cbPesawat.setSelectedIndex(-1);
        cbTujuan.setSelectedIndex(-1);

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idrute = "";
                idpesawat = "";
                idasal = "";
                idtujuan = "";
                id = txtId.getText();
                waktuBerangkat = txtBerangkat.getText();
                waktuSampai = txtSampai.getText();
                status = txtStatus.getText();

//                LocalDateTime dateTime = LocalDateTime.now();
//
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                String formattedDateTime = dateTime.format(formatter);

                if (txtId.getText().isEmpty() || cbRute.getSelectedItem().equals(0) || cbPesawat.getSelectedItem().equals(0) ||
                        cbAsal.getSelectedItem().equals(0) || cbTujuan.getSelectedItem().equals(0) || txtBerangkat.getText().isEmpty() || txtSampai.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(updateJ, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String tanggalPattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

                if (!waktuBerangkat.matches(tanggalPattern) || !waktuSampai.matches(tanggalPattern)) {
                    JOptionPane.showMessageDialog(updateJ, "Format tanggal atau waktu tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_rute FROM [dbo].[Rute] WHERE kode_rute = '" + cbRute.getSelectedItem() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idrute = (String) connect.result.getString("id_rute");
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_pesawat FROM [dbo].[Pesawat] WHERE kode_pesawat = '" + cbPesawat.getSelectedItem() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idpesawat = (String) connect.result.getString("id_pesawat");
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_bandara FROM [dbo].[Bandara] WHERE nama_bandara = '" + cbTujuan.getSelectedItem() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idtujuan = (String) connect.result.getString("id_bandara");
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_bandara FROM [dbo].[Bandara] WHERE nama_bandara = '" + cbAsal.getSelectedItem() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idasal = (String) connect.result.getString("id_bandara");
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                try {
                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }

                    connect.stat = connect.conn.createStatement();
                    String selectQuery = "SELECT id_rute, id_pesawat, waktu_berangkat, perkiraan_sampai, id_bandara_tujuan, id_bandara_asal, status " +
                            "FROM [dbo].[Jadwal_Penerbangan] WHERE id_jadwal = ?";
                    connect.pstat = connect.conn.prepareStatement(selectQuery);
                    connect.pstat.setString(1, id);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        String previousIdRute = connect.result.getString("id_rute");
                        String previousIdPesawat = connect.result.getString("id_pesawat");
                        Timestamp previousWaktuBerangkat = connect.result.getTimestamp("waktu_berangkat");
                        Timestamp previousWaktuSampai = connect.result.getTimestamp("perkiraan_sampai");
                        String previousIdTujuan = connect.result.getString("id_bandara_tujuan");
                        String previousIdAsal = connect.result.getString("id_bandara_asal");
                        int previousStatus = connect.result.getInt("status");

                        LocalDateTime dateTimeBerangkat = LocalDateTime.parse(waktuBerangkat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime dateTimeSampai = LocalDateTime.parse(waktuSampai, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        Timestamp newWaktuBerangkat = Timestamp.valueOf(dateTimeBerangkat);
                        Timestamp newWaktuSampai = Timestamp.valueOf(dateTimeSampai);

                        if (previousIdRute.equals(idrute) && previousIdPesawat.equals(idpesawat) &&
                                previousWaktuBerangkat.equals(newWaktuBerangkat) && previousWaktuSampai.equals(newWaktuSampai) &&
                                previousIdTujuan.equals(idtujuan) && previousIdAsal.equals(idasal) && previousStatus == statusText) {
                            JOptionPane.showMessageDialog(null, "Tidak ada perubahan pada data Jadwal", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();


                    String query = "UPDATE [dbo].[Jadwal_Penerbangan] SET id_rute=?, id_pesawat=?," +
                            "waktu_berangkat =?, perkiraan_sampai=?, id_bandara_tujuan=?, id_bandara_asal=?, status=? WHERE id_jadwal=?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, idrute);
                    connect.pstat.setString(2,idpesawat);
                    LocalDateTime dateTimeBerangkat = LocalDateTime.parse(waktuBerangkat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime dateTimeSampai = LocalDateTime.parse(waktuSampai, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    // Ubah menjadi objek Timestamp sebelum menyimpan ke database
                    Timestamp timestampBerangkat = Timestamp.valueOf(dateTimeBerangkat);
                    Timestamp timestampSampai = Timestamp.valueOf(dateTimeSampai);

                    connect.pstat.setTimestamp(3, timestampBerangkat);
                    connect.pstat.setTimestamp(4, timestampSampai);
                    connect.pstat.setString(5, idtujuan);
                    connect.pstat.setString(6, idasal);
                    connect.pstat.setInt(7, statusText);
                    connect.pstat.setString(8, id);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Data Jadwal berhasil diubah");
                    connect.pstat.close();


                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }
                loaddata();
                clear();
            }
        });
        tbJadwal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbJadwal.getSelectedRow();
                if (i == -1){
                    return;
                }

                txtId.setText((String) modelmodel.getValueAt(i,0));
                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT kode_rute FROM [dbo].[Rute] WHERE id_rute = '" + (String) modelmodel.getValueAt(i, 1) + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idrute = connect.result.getString("kode_rute");
                        cbRute.setSelectedItem(idrute);
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }
                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT kode_pesawat FROM [dbo].[Pesawat] WHERE id_pesawat = '" + (String) modelmodel.getValueAt(i, 2) + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idpesawat = connect.result.getString("kode_pesawat");
                        cbPesawat.setSelectedItem(idpesawat);
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                String waktuBerangkat = modelmodel.getValueAt(i, 3).toString();
                String waktuSampai = modelmodel.getValueAt(i, 4).toString();

                // Menampilkan nilai datetime di komponen teks
                txtBerangkat.setText(waktuBerangkat);
                txtSampai.setText(waktuSampai);
                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT nama_bandara FROM [dbo].[Bandara] WHERE id_bandara = '" + (String) modelmodel.getValueAt(i, 5) + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idtujuan = connect.result.getString("nama_bandara");
                        cbTujuan.setSelectedItem(idtujuan);
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }
                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT nama_bandara FROM [dbo].[Bandara] WHERE id_bandara = '" + (String) modelmodel.getValueAt(i, 6) + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idasal = connect.result.getString("nama_bandara");
                        cbAsal.setSelectedItem(idasal);
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }
                status = (String) modelmodel.getValueAt(i, 7);
                statusText = Integer.parseInt(status);
                if (statusText == 1) {
                    txtStatus.setText("Tersedia");
                } else if (statusText == 0) {
                    txtStatus.setText("Tidak Tersedia");
                }
            }
        });
        btnSemua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loaddata();
            }
        });
        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtCariID.getText();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ID harus diisi", "Validasi", JOptionPane.WARNING_MESSAGE);
                } else {
                    showByID();
                }
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idjadwal = txtId.getText();
                if (idjadwal.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Harus memilih data Jadwal terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String checkQuery = "SELECT status FROM [dbo].[Jadwal_Penerbangan] WHERE id_jadwal = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, idjadwal);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int status = connect.result.getInt("status");
                        if (status == 0) {
                            JOptionPane.showMessageDialog(updateJ, "Data Jadwal sudah tidak tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa status Jadwal: " + ex);
                }

                int selectedOption = JOptionPane.showConfirmDialog(updateJ, "Apakah Anda yakin ingin mengubah data Jadwal menjadi tidak tersedia?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    try {
                        String query = "UPDATE [dbo].[Jadwal_Penerbangan] SET status = 0 WHERE id_jadwal = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, idjadwal);
                        int rowsUpdated = connect.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(updateJ, "Jadwal berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(updateJ, "Gagal menghapus Jadwal", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }

                        connect.pstat.close();
                    } catch (SQLException ex) {
                        System.out.println("Terjadi error saat menghapus data Jadwal: " + ex);
                    }
                    loaddata();
                    clear();
                }

            }
        });
        btncleardetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuUtama menuUtama = new MenuUtama(username, nama_akses);
                menuUtama.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(UpdateDeleteJadwal.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
    }

    public void FrameConfig() {
        add(updateJ);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void addKolom(){
        modelmodel.addColumn("ID");
        modelmodel.addColumn("Nama Rute");
        modelmodel.addColumn("Nama Pesawat");
        modelmodel.addColumn("Waktu Berangkat");
        modelmodel.addColumn("Waktu Sampai");
        modelmodel.addColumn("Bandara Tujuan");
        modelmodel.addColumn("Bandara Asal");
        modelmodel.addColumn("Status");
    }

    public void loaddata(){
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        Thread thread = new Thread(() -> {
            try {
                connect.stat = connect.conn.createStatement();
                //String query = "SELECT * FROM [dbo].[Pesawat]";
                String query = "SELECT * FROM [dbo].[Jadwal_Penerbangan]";
                connect.result = connect.stat.executeQuery(query);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                while (connect.result.next()) {
                    Object[] obj = new Object[8];

                    obj[0] = connect.result.getString("id_jadwal");
                    obj[1] = connect.result.getString("id_rute");
                    obj[2] = connect.result.getString("id_pesawat");
                    obj[3] = dateFormat.format(connect.result.getDate("waktu_berangkat")) + " " + timeFormat.format(connect.result.getTime("waktu_berangkat"));
                    obj[4] = dateFormat.format(connect.result.getDate("perkiraan_sampai")) + " " + timeFormat.format(connect.result.getTime("perkiraan_sampai"));
                    obj[5] = connect.result.getString("id_bandara_tujuan");
                    obj[6] = connect.result.getString("id_bandara_asal");
                    obj[7] = connect.result.getString("status");

                    SwingUtilities.invokeLater(() -> modelmodel.addRow(obj));

                    Thread.sleep(150);
                }
                connect.stat.close();
                connect.result.close();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat load data Rute dan Detail rute: " + ex);
            }
        });
        thread.start();
    }

    public void rute() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_rute, kode_rute from [dbo].[Rute] WHERE status = 1";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbRute.addItem(connect.result.getString("kode_rute"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Rute" + ex);
        }
    }

    public void pesawat() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_pesawat, kode_pesawat from [dbo].[Pesawat] WHERE status = 1";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbPesawat.addItem(connect.result.getString("kode_pesawat"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Pesawat" + ex);
        }
    }

    public void Asalbandara() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_bandara, nama_bandara from [dbo].[Bandara] WHERE status = 1";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbAsal.addItem(connect.result.getString("nama_bandara"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Bandara" + ex);
        }
    }

    public void TujuanBandara() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_bandara, nama_bandara from [dbo].[Bandara] WHERE status = 1";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbTujuan.addItem(connect.result.getString("nama_bandara"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Bandara" + ex);
        }
    }

    public void showByID(){
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();
            String query = "SELECT * FROM [dbo].[Jadwal_Penerbangan] where id_jadwal LIKE'%" +txtCariID.getText()+ "%'";
            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()){
                Object[] obj = new Object[8];
                obj[0] = connect.result.getString("id_jadwal");
                obj[1] = connect.result.getString("id_rute");
                obj[2] = connect.result.getString("id_pesawat");
                obj[3] = connect.result.getDate("waktu_berangkat");
                obj[4] = connect.result.getDate("perkiraan_sampai");
                obj[5] = connect.result.getString("id_bandara_tujuan");
                obj[6] = connect.result.getString("id_bandara_asal");
                obj[7] = connect.result.getString("status");
                modelmodel.addRow(obj);
            }
            if (modelmodel.getRowCount() == 0){
                JOptionPane.showMessageDialog(null,"Data Jadwal tidak ditemukan");
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception e){
            System.out.println("Terjadi eror saat load data Kelas Kursi : " +e);
        }
    }

//    public void showData() {
//        modelmodel.getDataVector().removeAllElements();
//        modelmodel.fireTableDataChanged();
//        try {
//            connect.stat = connect.conn.createStatement();
//
//            String query = "SELECT id_rute, id_pesawat, waktu_berangkat, perkiraan_sampai, id_bandara_tujuan, id_bandara_asal, status FROM Jadwal_Penerbangan";
//
//
//            connect.result = connect.stat.executeQuery(query);
//
//            while (connect.result.next()) {
//                Object[] obj = new Object[8];
//                obj[0] = connect.result.getString("id_jadwal");
//                obj[1] = connect.result.getString("id_rute");
//                obj[2] = connect.result.getString("id_pesawat");
//                obj[3] = connect.result.getDate("waktu_berangkat");
//                obj[4] = connect.result.getDate("perkiraan_sampai");
//                obj[5] = connect.result.getString("id_bandara_tujuan");
//                obj[6] = connect.result.getString("id_bandara_asal");
//                obj[7] = connect.result.getString("status");
//                modelmodel.addRow(obj);
//            }
//
//            if (modelmodel.getRowCount() == 0) {
//                JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
//            }
//
//            connect.stat.close();
//            connect.result.close();
//        } catch (Exception e) {
//            System.out.println("Terjadi eror saat load data: " + e);
//        }
//    }

    public void clear() {
        txtId.setText("");
        cbRute.setSelectedIndex(-1);
        cbAsal.setSelectedIndex(-1);
        cbPesawat.setSelectedIndex(-1);
        cbTujuan.setSelectedIndex(-1);
        txtBerangkat.setText("");
        txtSampai.setText("");
        txtStatus.setText("");
    }

}
