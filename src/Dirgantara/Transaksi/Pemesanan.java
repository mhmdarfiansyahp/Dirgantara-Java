package Dirgantara.Transaksi;

import Connect.Connect;
import Dirgantara.Dashboard.ProfilPenumpang;
import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalPenumpang;
import com.toedter.calendar.JDateChooser;
import sun.security.util.Pem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Pemesanan extends JFrame{
    private JLabel label_user;
    private JButton dashboardButton;
    private JButton btnPembatalan;
    private JButton logoutButton;
    private JButton btnPemesanan;
    private JButton riwayatButton;
    private JButton btnPembayaran;
    private JPanel TransaksiPemesanan;
    private JTable tbDatapesawat;
    private JComboBox cbasal;
    private JComboBox cbtujuan;
    private JComboBox cbKursi;
    private JButton cariButton;
    private JPanel Jptanggal;
    private String selectedIdJadwal;
    private Object[][] tableData;

    private String selectedAsal;
    private String selectedTujuan;
    private String selectedKursi, previousSelectedKursi;
    private Date selectedDate;



    Connect connect = new Connect();
    DefaultTableModel modelmodel;
    JDateChooser dateChooser = new JDateChooser();

    String idJadwal,kodeRute, kodePesawat, waktuSampai, waktuBerangkat, selectedKelas;

    public static void main(String[] args) {
        String username = null;
        new Pemesanan(username);
    }

    public Pemesanan(String username) {
        FrameConfig();
        label_user.setText(username);
        modelmodel = new DefaultTableModel();
        tbDatapesawat.setModel(modelmodel);
        addKolom();

        if (tableData != null) {
            for (Object[] row : tableData) {
                modelmodel.addRow(row);
            }
        }
        DefaultTableModel modelDetail = new DefaultTableModel();

        Dari();
        Tujuan();
        Kursi();
        cbKursi.setSelectedIndex(-1);
        cbtujuan.setSelectedIndex(-1);
        cbasal.setSelectedIndex(-1);

        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showdatapesawat();
            }
        });
        tbDatapesawat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbDatapesawat.getSelectedRow();
                if (i == -1){
                    return;
                }

                // Store the selected values
                selectedAsal = cbasal.getSelectedItem().toString();
                selectedTujuan = cbtujuan.getSelectedItem().toString();
                previousSelectedKursi = cbKursi.getSelectedItem().toString();
                selectedDate = dateChooser.getDate();

                idJadwal = tbDatapesawat.getValueAt(i, 0).toString();
                kodeRute = tbDatapesawat.getValueAt(i, 1).toString();
                kodePesawat = tbDatapesawat.getValueAt(i, 2).toString();
                waktuBerangkat = tbDatapesawat.getValueAt(i, 3).toString();
                waktuSampai = tbDatapesawat.getValueAt(i, 4).toString();

                String idPesawat = getIdPesawat(kodePesawat);

                dispose();
                String username = label_user.getText();
                simpanPemesanan simpanPemesanan = new simpanPemesanan(modelDetail, username, selectedKursi, kodeRute, waktuBerangkat, idJadwal, selectedAsal, selectedTujuan, selectedDate, idPesawat, Pemesanan.this);
                simpanPemesanan.addRowToDetailTable(idJadwal, kodeRute, kodePesawat, waktuBerangkat, waktuSampai);
                simpanPemesanan.setPreviousSelectedKursi(previousSelectedKursi);
                simpanPemesanan.setVisible(true);

                showdatapesawat();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Pemesanan.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        cbKursi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedKursi = cbKursi.getSelectedItem().toString();
            }
        });
        btnPembayaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembayaran pembayaran = new Pembayaran(username);
                pembayaran.setVisible(true);
                dispose();
            }
        });
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilPenumpang profilPenumpang = new ProfilPenumpang(username);
                profilPenumpang.setVisible(true);
                dispose();
            }
        });
        riwayatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Riwayat riwayat = new Riwayat(username);
                riwayat.setVisible(true);
                dispose();
            }
        });
        btnPembatalan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembatalan pembatalan = new Pembatalan(username);
                pembatalan.setVisible(true);
                dispose();
            }
        });
    }

    public void FrameConfig() {
        add(TransaksiPemesanan);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        Jptanggal.add(dateChooser);

        // Menetapkan format tanggal
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        dateChooser.setDateFormatString("yyyy-MM-dd");
        // Mengatur tanggal pertama kali menjadi null
        dateChooser.setDate(null);

    }

    public void Dari() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_bandara, nama_bandara from [dbo].[Bandara]";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbasal.addItem(connect.result.getString("nama_bandara"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Bandara asal" + ex);
        }
    }

    public void Tujuan() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_bandara, nama_bandara from [dbo].[Bandara]";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbtujuan.addItem(connect.result.getString("nama_bandara"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Bandara tujuan" + ex);
        }
    }

    public void Kursi() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_kelas, nama_kelas from [dbo].[Kelas_Kursi]";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbKursi.addItem(connect.result.getString("nama_kelas"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Kelas" + ex);
        }
    }


    public void showdatapesawat() {
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        Date selectedDate = dateChooser.getDate();

        // Check if the selected date is in the past
        if (isDateInPast(selectedDate)) {
            JOptionPane.showMessageDialog(null, "Maaf, tanggal penerbangan sudah lewat.");
            return;
        }

        try {
            connect.stat = connect.conn.createStatement();
            selectedDate = dateChooser.getDate();
            String formattedDate = dateFormat.format(selectedDate);

            // Mendapatkan bandara asal dan tujuan yang dipilih
            String selectedAsal = cbasal.getSelectedItem().toString();
            String selectedTujuan = cbtujuan.getSelectedItem().toString();
            selectedKelas = cbKursi.getSelectedItem().toString();

            String query = "SELECT j.id_jadwal, r.kode_rute, p.kode_pesawat, j.waktu_berangkat, j.perkiraan_sampai, " +
                    "ba.nama_bandara AS bandara_tujuan, bb.nama_bandara AS bandara_asal " +
                    "FROM [dbo].[Jadwal_Penerbangan] j " +
                    "JOIN [dbo].[Rute] r ON j.id_rute = r.id_rute " +
                    "JOIN [dbo].[Pesawat] p ON j.id_pesawat = p.id_pesawat " +
                    "JOIN [dbo].[Bandara] ba ON j.id_bandara_tujuan = ba.id_bandara " +
                    "JOIN [dbo].[Bandara] bb ON j.id_bandara_asal = bb.id_bandara " +
                    "JOIN [dbo].[DetilKursi] dk ON p.id_pesawat = dk.id_pesawat " +
                    "JOIN [dbo].[Kelas_Kursi] k ON dk.id_kelas = k.id_kelas " +
                    "WHERE CONVERT(DATE, j.waktu_berangkat) = '"+ formattedDate +"' " +
                    "AND bb.nama_bandara = '"+ selectedAsal +"' " +
                    "AND ba.nama_bandara = '"+ selectedTujuan +"' " +
                    "AND k.nama_kelas = '"+ selectedKelas +"'";

            connect.result = connect.stat.executeQuery(query);
            boolean hasData = false; // Menyimpan informasi apakah ada data yang ditemukan

            while (connect.result.next()) {
                hasData = true;

                Object[] obj = new Object[5];
                obj[0] = connect.result.getString("id_jadwal");
                obj[1] = connect.result.getString("kode_rute");
                obj[2] = connect.result.getString("kode_pesawat");
                obj[3] = dateFormat.format(connect.result.getDate("waktu_berangkat")) + " " + timeFormat.format(connect.result.getTime("waktu_berangkat"));
                obj[4] = dateFormat.format(connect.result.getDate("perkiraan_sampai")) + " " + timeFormat.format(connect.result.getTime("perkiraan_sampai"));
                modelmodel.addRow(obj);
            }

            storeTableData();

            if (!hasData) {
                JOptionPane.showMessageDialog(null, "Data Jadwal tidak ditemukan");
            }

            connect.stat.close();
            connect.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi eror saat load data Jadwal : " + e);
        }
        tbDatapesawat.setModel(modelmodel);
    }


    public void addKolom(){
        modelmodel.addColumn("ID");
        modelmodel.addColumn("Nama Rute");
        modelmodel.addColumn("Nama Pesawat");
        modelmodel.addColumn("Waktu Berangkat");
        modelmodel.addColumn("Waktu Sampai");
    }

    public void storeTableData() {
        int rowCount = tbDatapesawat.getRowCount();
        int columnCount = tbDatapesawat.getColumnCount();
        tableData = new Object[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                tableData[i][j] = tbDatapesawat.getValueAt(i, j);
            }
        }
    }

    public void restoreTableData(DefaultTableModel model) {
        tbDatapesawat.setModel(model);
    }

    public void setSelectedValues(String selectedAsal, String selectedTujuan, Date selectedDate) {
        cbasal.setSelectedItem(selectedAsal);
        cbtujuan.setSelectedItem(selectedTujuan);
        dateChooser.setDate(selectedDate);
    }

    public String getIdPesawat(String kodePesawat) {
        String idPesawat = "";

        try {
            connect.stat = connect.conn.createStatement();
            String query = "SELECT id_pesawat FROM [dbo].[Pesawat] WHERE kode_pesawat = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, kodePesawat);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                idPesawat = result.getString("id_pesawat");
            }

            pstat.close();
            result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat mendapatkan id_pesawat: " + ex);
        }

        return idPesawat;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
        dateChooser.setDate(selectedDate);
    }

    public void setPreviousSelectedKursi(String kursi) {
        previousSelectedKursi = kursi;
        cbKursi.setSelectedItem(kursi);
    }

    private boolean isDateInPast(Date selectedDate) {
        Date currentDate = new Date();
        return selectedDate.before(currentDate);
    }


}
