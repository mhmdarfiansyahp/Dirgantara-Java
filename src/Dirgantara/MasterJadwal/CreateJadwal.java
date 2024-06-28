package Dirgantara.MasterJadwal;

import Connect.Connect;
import Dirgantara.MasterKursi.UpdateHapus;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class CreateJadwal extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtid;
    private JTextField txtBerangkat;
    private JButton btnidbaru;
    private JTextField txtSampai;
    private JButton btnSimpan;
    private JButton btnClear;
    private JTable tbJadwal;
    private JComboBox cbRute;
    private JTextField txtStatus;
    private JComboBox cbPesawat;
    private JComboBox cbTujuan;
    private JComboBox cbAsal;
    private JPanel CreateJ;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();

    String id, status, idrute, idpesawat, idasal, idtujuan, waktuBerangkat, waktuSampai,tanggal;

    int statusText;

    public CreateJadwal(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);

        modelmodel = new DefaultTableModel();
        addKolom();
        tbJadwal.setModel(modelmodel);
        loaddata();
        tbJadwal.setEnabled(false);

        rute();
        pesawat();
        Asalbandara();
        TujuanBandara();
        cbRute.setSelectedIndex(-1);
        cbAsal.setSelectedIndex(-1);
        cbPesawat.setSelectedIndex(-1);
        cbTujuan.setSelectedIndex(-1);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idrute = "";
                idpesawat = "";
                idasal = "";
                idtujuan = "";
                id = txtid.getText();
                waktuBerangkat = txtBerangkat.getText();
                waktuSampai = txtSampai.getText();
                status = txtStatus.getText();

                //LocalDate tanggalSekarang = LocalDate.now();


                if (txtid.getText().isEmpty() || cbRute.getSelectedItem().equals(0) || cbPesawat.getSelectedItem().equals(0) ||
                        cbAsal.getSelectedItem().equals(0) || cbTujuan.getSelectedItem().equals(0) || txtBerangkat.getText().isEmpty() || txtSampai.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(CreateJ, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String tanggalPattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

                if (!waktuBerangkat.matches(tanggalPattern) || !waktuSampai.matches(tanggalPattern)) {
                    JOptionPane.showMessageDialog(CreateJ, "Format tanggal atau waktu tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
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

                try{
                    connect.stat = connect.conn.createStatement();
                    String checkQuery = "SELECT COUNT(*) AS count FROM [dbo].[Jadwal_Penerbangan] WHERE id_jadwal = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, id);

                    connect.result = connect.pstat.executeQuery();
                    connect.result.next();
                    int count = connect.result.getInt("count");

                    if (count > 0) {
                        JOptionPane.showMessageDialog(null, "Data dengan ID Jadwal tersebut sudah ada", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[Jadwal_Penerbangan] VALUES (?,?,?,?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, idrute);
                    connect.pstat.setString(3,idpesawat);
                    LocalDateTime dateTimeBerangkat = LocalDateTime.parse(waktuBerangkat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime dateTimeSampai = LocalDateTime.parse(waktuSampai, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    // Ubah menjadi objek Timestamp sebelum menyimpan ke database
                    Timestamp timestampBerangkat = Timestamp.valueOf(dateTimeBerangkat);
                    Timestamp timestampSampai = Timestamp.valueOf(dateTimeSampai);

                    connect.pstat.setTimestamp(4, timestampBerangkat);
                    connect.pstat.setTimestamp(5, timestampSampai);
                    connect.pstat.setString(6, idtujuan);
                    connect.pstat.setString(7, idasal);
                    connect.pstat.setInt(8, statusText);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Jadwal Penerbangan berhasil ditambahkan");
                    connect.pstat.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }

                loaddata();
                clear();
            }
        });
        btnidbaru.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoid();
            }
        });
        btnClear.addActionListener(new ActionListener() {
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
                int option = JOptionPane.showConfirmDialog(CreateJadwal.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
    }

    public void FrameConfig() {
        add(CreateJ);
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
        try {
            connect.stat = connect.conn.createStatement();
            //String query = "SELECT * FROM [dbo].[Pesawat]";
            String query = "SELECT * FROM [dbo].[Jadwal_Penerbangan]";
            connect.result = connect.stat.executeQuery(query);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


            while (connect.result.next()){
                Object[] obj = new Object[8];

                obj[0] = connect.result.getString("id_jadwal");
                obj[1] = connect.result.getString("id_rute");
                obj[2] = connect.result.getString("id_pesawat");
                obj[3] = dateFormat.format(connect.result.getDate("waktu_berangkat")) + " " + timeFormat.format(connect.result.getTime("waktu_berangkat"));
                obj[4] = dateFormat.format(connect.result.getDate("perkiraan_sampai")) + " " + timeFormat.format(connect.result.getTime("perkiraan_sampai"));
                obj[5] = connect.result.getString("id_bandara_tujuan");
                obj[6] = connect.result.getString("id_bandara_asal");
                obj[7] = connect.result.getString("status");

                modelmodel.addRow(obj);
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception ex){
            System.out.println("Terjadi error saat load data Rute dan Detail rute: " + ex);
        }
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

    public void autoid(){
        try {
            String sql = "SELECT MAX(id_jadwal) FROM [dbo].[Jadwal_Penerbangan]";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()){
                String maxID = connect.result.getString(1);
                if (maxID != null){
                    int num = Integer.parseInt(maxID.substring(3)) + 1;
                    String formattedNumber = String.format("%03d", num);
                    txtid.setText("JD" + formattedNumber);
                }else {
                    txtid.setText("JD001");
                }
            }

            txtStatus.setText("Tersedia");
            txtid.setEditable(false);
            txtStatus.setEnabled(false);
            connect.pstat.close();
            connect.result.close();
        }
        catch (Exception ex){
            System.out.println("Error "+ex);
        }
    }

    public void clear() {
        txtid.setText("");
        cbRute.setSelectedIndex(-1);
        cbAsal.setSelectedIndex(-1);
        cbPesawat.setSelectedIndex(-1);
        cbTujuan.setSelectedIndex(-1);
        txtBerangkat.setText("");
        txtSampai.setText("");
        txtStatus.setText("");
    }
}
