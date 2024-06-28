package Riwayat;

import Connect.Connect;
import Dirgantara.Dashboard.ProfilManager;
import Dirgantara.Laporan.laporanPembatalan;
import Dirgantara.Laporan.laporanPembelian;
import Dirgantara.MenuAwal;
import Dirgantara.Transaksi.Riwayat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

public class RiwayatManager extends JFrame{
    private JLabel lbl_user;
    private JLabel lbl_jabatan;
    private JButton dashboardButton;
    private JButton riwayatTransaksiButton;
    private JButton laporanPembelianButton;
    private JButton logoutButton;
    private JButton laporanPembatalanButton;
    private JTable tbriwayat;
    private JComboBox cbfilter;
    private JPanel riwayatmana;

    Connect connect = new Connect();
    DefaultTableModel modelmodel;

    public RiwayatManager (String username, String nama_akses){
        FrameConfig();
        lbl_user.setText(username);
        lbl_jabatan.setText(nama_akses);
        modelmodel = new DefaultTableModel();
        tbriwayat.setModel(modelmodel);
        tbriwayat.setEnabled(false);
        addKolom();

        isiPilihanComboBox();
        cbfilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataRiwayat(cbfilter.getSelectedItem().toString());
            }
        });
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilManager profilManager = new ProfilManager(username, nama_akses);
                profilManager.setVisible(true);
                dispose();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(RiwayatManager.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        laporanPembelianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                laporanPembelian laporanPembelian = new laporanPembelian(username, nama_akses);
                laporanPembelian.setVisible(true);
                dispose();
            }
        });
        laporanPembatalanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                laporanPembatalan laporanPembatalan = new laporanPembatalan(username, nama_akses);
                laporanPembatalan.setVisible(true);
                dispose();
            }
        });
    }
    public void FrameConfig() {
        add(riwayatmana);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void addKolom(){
        modelmodel.addColumn("ID Pemesan");
        modelmodel.addColumn("Tanggal Pemesanan");
        modelmodel.addColumn("Tanggal Pembayaran");
        modelmodel.addColumn("Tanggal Pembatalan");
        modelmodel.addColumn("Alasan Pembatalan");
        modelmodel.addColumn("Jumlah Penumpang");
        modelmodel.addColumn("Total Harga");
    }

    public void loadDataRiwayat(String selectedOption) {
        // Clear the existing data in the table
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();

        // Write the logic to load data from the "Pemesanan" table based on the selected option and userIdentifer
        try {
            connect.stat = connect.conn.createStatement();
            String query = "";

            if (selectedOption.equals("Semua Riwayat")) {
                // Load all data from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, tanggal_pembayaran, tanggal_pembatalan, jumlah, alasan_batal, total FROM Pemesanan";
            } else if (selectedOption.equals("Pembayaran")) {
                // Load data for the "Pembayaran" option from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, tanggal_pembayaran, NULL as tanggal_pembatalan, jumlah, alasan_batal, total FROM Pemesanan WHERE status_pesanan = 2";
            } else if (selectedOption.equals("Pembatalan")) {
                // Load data for the "Pembatalan" option from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, NULL as tanggal_pembayaran, tanggal_pembatalan, jumlah, alasan_batal, total FROM Pemesanan  WHERE status_pesanan = 1";
            } else if (selectedOption.equals("Refund")) {
                // Load data for the "Refund" option from the "Pemesanan" table for the specific user
                query = "SELECT id_pemesanan, tanggal_pemesanan, tanggal_pembayaran, tanggal_pembatalan, alasan_batal, jumlah, total FROM Pemesanan WHERE status_pesanan = 3";
            }

            connect.pstat = connect.conn.prepareStatement(query);
            connect.result = connect.pstat.executeQuery();

            // Populate the table with data
            while (connect.result.next()) {
                Object[] rowData = new Object[7];
                rowData[0] = connect.result.getString("id_pemesanan");
                rowData[1] = connect.result.getString("tanggal_pemesanan");
                rowData[2] = connect.result.getString("tanggal_pembayaran");
                rowData[3] = connect.result.getString("tanggal_pembatalan");
                rowData[4] = connect.result.getString("alasan_batal");
                rowData[5] = connect.result.getInt("jumlah");

                // Format the "total" column using NumberFormat
                double total = connect.result.getDouble("total");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                numberFormat.setMinimumFractionDigits(0); // To avoid decimal places
                rowData[6] = numberFormat.format(total);

                modelmodel.addRow(rowData);
            }

            connect.stat.close();
            connect.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Riwayat: " + e);
        }
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

}
