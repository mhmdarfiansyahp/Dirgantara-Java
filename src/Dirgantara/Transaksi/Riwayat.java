package Dirgantara.Transaksi;

import Connect.Connect;
import Dirgantara.Dashboard.ProfilPenumpang;
import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalPenumpang;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class Riwayat extends JFrame{
    private JLabel label_user;
    private JButton dashboardButton;
    private JButton btnPembatalan;
    private JButton logoutButton;
    private JButton btnPemesanan;
    private JButton riwayatButton;
    private JButton btnPembayaran;
    private JTable tbriwayat;
    private JComboBox cbfilter;
    private JPanel riwayat;

    String userIdentifer;
    Connect connect = new Connect();
    DefaultTableModel modelmodel;
    JDateChooser dateChooser = new JDateChooser();

    public Riwayat(String username) {
        FrameConfig();
        label_user.setText(username);
        modelmodel = new DefaultTableModel();
        tbriwayat.setModel(modelmodel);
        addKolom();

        this.userIdentifer = getUserIdFromUsername(username);


        isiPilihanComboBox();

        cbfilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataRiwayat(cbfilter.getSelectedItem().toString(), userIdentifer);
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Riwayat.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnPemesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pemesanan pemesanan = new Pemesanan(username);
                pemesanan.setVisible(true);
                dispose();
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
                ProfilPenumpang penumpang = new ProfilPenumpang(username);
                penumpang.setVisible(true);
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
        add(riwayat);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void addKolom(){
        modelmodel.addColumn("ID Pemesanan");
        modelmodel.addColumn("Tanggal Pemesanan");
        modelmodel.addColumn("Tanggal Pembayaran");
        modelmodel.addColumn("Tanggal Pembatalan");
        modelmodel.addColumn("Jumlah Penumpang");
        modelmodel.addColumn("Total Harga");
    }

    public void isiPilihanComboBox() {
        // Tambahkan pilihan-pilihan yang diinginkan ke dalam ComboBox di sini
        String[] pilihan = {"Semua Riwayat", "Pembayaran", "Pembatalan", "Refund"};

        // Hapus item yang sudah ada di dalam ComboBox
        cbfilter.removeAllItems();

        // Tambahkan pilihan-pilihan ke dalam ComboBox
        for (String pilih : pilihan) {
            cbfilter.addItem(pilih);
        }
    }

    public void loadDataRiwayat(String selectedOption, String userIdentifer) {
        // Clear the existing data in the table
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();

        // Write the logic to load data from the "Pemesanan" table based on the selected option and userIdentifer
        try {
            connect.stat = connect.conn.createStatement();
            String query = "";

            if (selectedOption.equals("Semua Riwayat")) {
                // Load all data from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, tanggal_pembayaran, tanggal_pembatalan, jumlah, total FROM Pemesanan WHERE id_user = ?";
            } else if (selectedOption.equals("Pembayaran")) {
                // Load data for the "Pembayaran" option from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, tanggal_pembayaran, NULL as tanggal_pembatalan, jumlah, total FROM Pemesanan WHERE status_pesanan = 2 AND id_user = ?";
            } else if (selectedOption.equals("Pembatalan")) {
                // Load data for the "Pembatalan" option from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, NULL as tanggal_pembayaran, tanggal_pembatalan, jumlah, total FROM Pemesanan WHERE status_pesanan = 1 AND id_user = ?";
            } else if (selectedOption.equals("Refund")) {
                // Load data for the "Refund" option from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, tanggal_pembayaran, tanggal_pembatalan, jumlah, total FROM Pemesanan WHERE status_pesanan = 3 AND id_user = ?";
            }

            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, userIdentifer);
            connect.result = connect.pstat.executeQuery();

            // Populate the table with data
            while (connect.result.next()) {
                Object[] rowData = new Object[6];
                rowData[0] = connect.result.getString("id_pemesanan");
                rowData[1] = connect.result.getString("tanggal_pemesanan");
                rowData[2] = connect.result.getString("tanggal_pembayaran");
                rowData[3] = connect.result.getString("tanggal_pembatalan");
                rowData[4] = connect.result.getString("jumlah");

                // Format the "total" column using NumberFormat
                double total = connect.result.getDouble("total");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                numberFormat.setMinimumFractionDigits(0); // To avoid decimal places
                rowData[5] = numberFormat.format(total);

                modelmodel.addRow(rowData);
            }

            connect.stat.close();
            connect.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Riwayat: " + e);
        }
    }

    // Metode untuk mengambil id_user berdasarkan username
    private String getUserIdFromUsername(String username) {
        try {
            connect.stat = connect.conn.createStatement();
            String query = "SELECT id_user FROM [dbo].[User] WHERE username = ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, username);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()) {
                return connect.result.getString("id_user");
            }

            connect.stat.close();
            connect.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat mengambil data id_user: " + e);
        }

        return null;
    }


}
