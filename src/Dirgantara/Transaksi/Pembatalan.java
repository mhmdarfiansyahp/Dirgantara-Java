package Dirgantara.Transaksi;

import Dirgantara.Dashboard.ProfilPenumpang;
import Dirgantara.MenuAwal;
import com.toedter.calendar.JDateChooser;
import Connect.Connect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.Format;
import java.util.Date;
import java.math.BigDecimal;



public class Pembatalan extends JFrame {
    private JLabel label_user;
    private JButton dashboardButton;
    private JButton btnPembatalan;
    private JButton logoutButton;
    private JButton btnPemesanan;
    private JButton riwayatButton;
    private JButton btnPembayaran;
    private JPanel jpPembatalan;
    private JTable tbPemesanan;
    private JPanel Jptanggal;
    private JTextField txtAsal;
    private JTextField txtTujuan;
    private JTextArea txtAPembatalan;
    private JTextField txtTglBerangkat;
    private JButton btnSimpan;
    private JButton btnBatal;
    private JTextField txtNamaPenum;
    private JComboBox cbIdPemesanan;
    private JTextField txtTotal;
    private JButton btnRefund;
    private JTextField txtRefund;
    private JPanel pembatalan;


    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private Connect connect = new Connect();
   // private DefaultTableModel modelmodel;
    private JDateChooser dateChooser = new JDateChooser();

    private DefaultTableModel model;

    private String username;

    public static void main(String[] args) {
        String username = null;
        new Pembatalan(username);
    }

    public Pembatalan(String username){
        this.username = username;
        FrameConfig();
        label_user.setText(username);
        //modelmodel = new DefaultTableModel();


        model = new DefaultTableModel();

        //menambahkan table model ke table
        tbPemesanan.setModel(model);
        tbPemesanan.setEnabled(false);
        addColumn();
        loadDataPemesanan();
        loadIdPemesananToComboBox();
        ///loadData(username);
        String nama_user = getNamaUser(username);
        txtNamaPenum.setText(nama_user);
        cbIdPemesanan.setSelectedIndex(-1);
        txtRefund.setEditable(false);

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mendapatkan data dari komponen input
                String namaPenumpang = txtNamaPenum.getText();
                String asalPenerbangan = txtAsal.getText();
                String tujuanPenerbangan = txtTujuan.getText();
                Date tanggalPembatalan = dateChooser.getDate();
                String alasanPembatalan = txtAPembatalan.getText();
                String selectedIdPemesanan = cbIdPemesanan.getSelectedItem().toString();

                // Mengecek status pesanan dari database berdasarkan id_pemesanan
                int currentStatus = 0;
                try {
                    currentStatus = getCurrentStatus(selectedIdPemesanan);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    if (selectedIdPemesanan.isEmpty() || namaPenumpang.isEmpty() || asalPenerbangan.isEmpty() || tujuanPenerbangan.isEmpty() || tanggalPembatalan == null || alasanPembatalan.isEmpty()) {
                        JOptionPane.showMessageDialog(jpPembatalan, "Data pembatalan harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (currentStatus == 1) {
                        JOptionPane.showMessageDialog(jpPembatalan, "Pesanan sudah dibatalkan sebelumnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirmDialogResult = JOptionPane.showConfirmDialog(jpPembatalan, "Apakah Anda yakin ingin membatalkan pesanan?", "Konfirmasi Pembatalan", JOptionPane.YES_NO_OPTION);
                    if (confirmDialogResult == JOptionPane.YES_OPTION) {
                        // User confirmed the cancellation, proceed with the cancellation process

                        // Tindakan sesuai dengan status pesanan
                        int newStatus = 0;
                        if (currentStatus == 0) {
                            // Pesanan bisa dibatalkan, mengubah status_pesanan menjadi 1 = "Dibatalkan"
                            newStatus = 1;
                            System.out.println("Status pembatalan berhasil diubah menjadi Dibatalkan.");
                            JOptionPane.showMessageDialog(jpPembatalan, "Status pembatalan berhasil diubah", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                            //updateKapasitasKursi(selectedIdPemesanan);

                        } else if (currentStatus == 2) {
                            // Pesanan telah dibayar, tidak dapat dibatalkan, namun bisa dilakukan refund
                            System.out.println("Pesanan telah dibayar dan tidak dapat dibatalkan. Lakukan refund jika diperlukan.");
                            JOptionPane.showMessageDialog(jpPembatalan, "Pesanan telah dibayar dan tidak dapat dibatalkan. Lakukan refund jika diperlukan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                            Clear();
                            return;
                        } else if (currentStatus == 3) {
                            // Pesanan telah di-refund
                            System.out.println("Pesanan telah di-refund.");
                            JOptionPane.showMessageDialog(jpPembatalan, "Pesanan telah di-refund.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                            Clear();
                            return;
                        } else {
                            // Tidak ada tindakan sesuai status yang ditemukan
                            System.out.println("Tidak ada tindakan yang sesuai dengan status pesanan saat ini.");
                            JOptionPane.showMessageDialog(jpPembatalan, "Tidak ada tindakan yang sesuai dengan status pesanan saat ini.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // Mengubah status pemesanan berdasarkan "id_pemesanan" dan memperbarui informasi pembatalan pada database
                        String query = "UPDATE [dbo].[Pemesanan] SET status_pesanan = ?, tanggal_pembatalan = ?, alasan_batal = ? WHERE id_pemesanan = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setInt(1, newStatus);
                        connect.pstat.setDate(2, new java.sql.Date(tanggalPembatalan.getTime()));
                        connect.pstat.setString(3, alasanPembatalan);
                        connect.pstat.setString(4, selectedIdPemesanan);
                        int rowsAffected = connect.pstat.executeUpdate();

                        if (rowsAffected > 0) {
                            // Berhasil memperbarui informasi pembatalan pada database
                            System.out.println("Informasi pembatalan berhasil diperbarui di database.");
                        } else {
                            // Gagal memperbarui informasi pembatalan pada database
                            System.out.println("Gagal memperbarui informasi pembatalan di database.");
                        }

                        connect.pstat.close();
                    } else {
                        // User chose not to cancel the order
                        System.out.println("Pembatalan pesanan dibatalkan oleh pengguna.");
                        return;
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat mengubah status pembatalan: " + ex);
                }
                Clear();
                loadDataPemesanan();
            }
        });

        cbIdPemesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected id_pemesanan
                String selectedIdPemesanan = cbIdPemesanan.getSelectedItem().toString();

                // Display the selected id_pemesanan in the combobox itself
                cbIdPemesanan.setSelectedItem(selectedIdPemesanan);
                try {

                    // Retrieve the total value using the getTotalValue method
                    BigDecimal total = getTotalValue(selectedIdPemesanan);

                    int totalAsInt = total.intValue();
                    txtTotal.setText(decimalFormat.format(totalAsInt));

                    // Retrieve other data and display them accordingly
                    String query = "SELECT tanggal_booking, id_jadwal FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, selectedIdPemesanan);
                    ResultSet rs3 = connect.pstat.executeQuery();

                    if (rs3.next()) {
                        String tanggalBooking = rs3.getString("tanggal_booking");
                        String idJadwal = rs3.getString("id_jadwal");

                        // Display the tanggal_booking, e.g., set it to a label
                        txtTglBerangkat.setText(tanggalBooking);

                        // Retrieve the id_bandara_asal based on the selected id_jadwal
                        query = "SELECT b.nama_bandara " +
                                "FROM Bandara b " +
                                "JOIN Jadwal_Penerbangan jp ON jp.id_bandara_asal = b.id_bandara " +
                                "JOIN Pemesanan p ON p.id_jadwal = jp.id_jadwal " +
                                "WHERE p.id_pemesanan = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, selectedIdPemesanan);
                        ResultSet rs4 = connect.pstat.executeQuery();

                        if (rs4.next()) {
                            String idBandaraAsal = rs4.getString("nama_bandara");
                            // Display the id_bandara_asal, e.g., set it to a textField
                            txtAsal.setText(idBandaraAsal);
                        } else {
                            System.out.println("Tidak ditemukan bandara asal untuk pemesanan dengan ID_Pemesanan: " + selectedIdPemesanan);
                        }

                        rs4.close();
                        connect.pstat.close();

                        query = "SELECT b.nama_bandara " +
                                "FROM Bandara b " +
                                "JOIN Jadwal_Penerbangan jp ON jp.id_bandara_tujuan = b.id_bandara " +
                                "JOIN Pemesanan p ON p.id_jadwal = jp.id_jadwal " +
                                "WHERE p.id_pemesanan = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, selectedIdPemesanan);
                        ResultSet rs5 = connect.pstat.executeQuery();

                        if (rs5.next()) {
                            String idBandaraAsal = rs5.getString("nama_bandara");
                            // Display the id_bandara_asal, e.g., set it to a textField
                            txtTujuan.setText(idBandaraAsal);
                        } else {
                            System.out.println("Tidak ditemukan bandara tujuan untuk pemesanan dengan ID_Pemesanan: " + selectedIdPemesanan);
                        }

                        rs5.close();
                        connect.pstat.close();

                    } else {
                        System.out.println("Tidak ditemukan pemesanan dengan ID_Pemesanan: " + selectedIdPemesanan);
                    }

                    rs3.close();
                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memuat data: " + ex);
                }
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
        tbPemesanan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

                // Mendapatkan baris yang terseleksi di JTable
                int selectedRow = tbPemesanan.getSelectedRow();

                // Memastikan baris yang terseleksi tidak -1 (kosong)
                if (selectedRow == -1) {
                    return;
                }

                // Mendapatkan nilai dari kolom-kolom yang diinginkan
                String idPemesanan = tbPemesanan.getValueAt(selectedRow, 0).toString();
                String tanggalPemesanan = tbPemesanan.getValueAt(selectedRow, 1).toString();
                String tanggalPembayaran = tbPemesanan.getValueAt(selectedRow, 2).toString();
                String tanggalPembatalan = tbPemesanan.getValueAt(selectedRow, 3).toString();
                String idUser = tbPemesanan.getValueAt(selectedRow, 4).toString();
                String idJadwal = tbPemesanan.getValueAt(selectedRow, 5).toString();
                String idKelas = tbPemesanan.getValueAt(selectedRow, 6).toString();
                String statusPemesanan = tbPemesanan.getValueAt(selectedRow, 7).toString();
                String total = tbPemesanan.getValueAt(selectedRow, 8).toString();
            }
        });
        btnRefund.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String alasanPembatalan = txtAPembatalan.getText();
                String selectedIdPemesanan = cbIdPemesanan.getSelectedItem().toString();

                try {
                    if (selectedIdPemesanan.isEmpty() || alasanPembatalan.isEmpty()) {
                        JOptionPane.showMessageDialog(jpPembatalan, "Alasan batal harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // mendapatkan status terkini
                    int currentStatus = getCurrentStatus(selectedIdPemesanan);

                    if (currentStatus == 0) {
                        System.out.println("Anda tidak dapat melakukan refund karna status pesanan belum dibayar.");
                        JOptionPane.showMessageDialog(jpPembatalan, "Anda tidak dapat melakukan refund karena status pesanan belum dibayar.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    } else if (currentStatus == 1) {
                        System.out.println("Anda tidak dapat melakukan refund karna pesanan telah dibatalkan.");
                        JOptionPane.showMessageDialog(jpPembatalan, "Anda tidak dapat melakukan refund karena pesanan telah dibatalkan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    } else if (currentStatus == 2) {
                        // Dapatkan nilai "total" menggunakan metode getTotalValue
                        BigDecimal total = getTotalValue(selectedIdPemesanan);

                        // Hitung 50% dari total sebagai jumlah pengembalian (refund).
                        BigDecimal refundAmount = total.multiply(new BigDecimal("0.5"));
                        int confirmDialogResult = JOptionPane.showConfirmDialog(jpPembatalan, "Apakah Anda yakin ingin melakukan refund?", "Konfirmasi Refund", JOptionPane.YES_NO_OPTION);
                        if (confirmDialogResult == JOptionPane.YES_OPTION) {
                            // User confirmed the refund, proceed with the refund process

                            // Setel status menjadi "Refunded" (kode status: 3)
                            updateStatus(selectedIdPemesanan, 3);

                            // Simpan nilai refundAmount dalam tabel Pemesanan pada kolom "total"
                            String query = "UPDATE [dbo].[Pemesanan] SET total = ? WHERE id_pemesanan = ?";
                            connect.pstat = connect.conn.prepareStatement(query);
                            connect.pstat.setBigDecimal(1, refundAmount);
                            connect.pstat.setString(2, selectedIdPemesanan);
                            connect.pstat.executeUpdate();

                            connect.pstat.close();

                            // Muat ulang data pemesanan pada tabel
                            loadDataPemesanan();

                            int refundAsInt = refundAmount.intValue();

                            // Tampilkan jumlah pengembalian (refund) dalam textField txtRefund
                            txtRefund.setText(decimalFormat.format(refundAsInt));

                            // Tampilkan pesan validasi
                            System.out.println("Refund telah berhasil dilakukan.");
                            JOptionPane.showMessageDialog(jpPembatalan, "Refund telah berhasil dilakukan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                            Clear();
                        } else {
                            // User chose not to proceed with the refund
                            System.out.println("Refund dibatalkan oleh pengguna.");
                            return;
                        }
                    } else if (currentStatus == 3) {
                        System.out.println("Pesanan ini telah di-refund sebelumnya.");
                        JOptionPane.showMessageDialog(jpPembatalan, "Pesanan ini telah di-refund sebelumnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    } else {
                        System.out.println("Tidak ada tindakan yang sesuai dengan status pesanan saat ini.");
                        JOptionPane.showMessageDialog(jpPembatalan, "Tidak ada tindakan yang sesuai dengan status pesanan saat ini.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi kesalahan ketika melakukan refund: " + ex);
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
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Pembatalan.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
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
    }

    // Method untuk mengubah status pemesanan berdasarkan "id_pemesanan"
    private void updateStatus(String selectedIdPemesanan, int newStatus) throws SQLException {
        String query = "UPDATE [dbo].[Pemesanan] SET status_pesanan = ? WHERE id_pemesanan = ?";
        connect.pstat = connect.conn.prepareStatement(query);
        connect.pstat.setInt(1, newStatus);
        connect.pstat.setString(2, selectedIdPemesanan);
        connect.pstat.executeUpdate();

        connect.pstat.close();
        loadDataPemesanan();
    }


    // Method untuk mendapatkan status terkini berdasarkan "id_pemesanan"
    private int getCurrentStatus(String selectedIdPemesanan) throws SQLException {
        String query = "SELECT status_pesanan FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
        connect.pstat = connect.conn.prepareStatement(query);
        connect.pstat.setString(1, selectedIdPemesanan);
        ResultSet rs = connect.pstat.executeQuery();

        int status = 0; // Nilai default jika tidak ada status yang ditemukan untuk "id_pemesanan" tertentu
        if (rs.next()) {
            status = rs.getInt("status_pesanan");
        }

        rs.close();
        connect.pstat.close();
        return status;
    }

    public String getNamaUser(String username) {
        String nama_user = "";

        try {
            String query = "SELECT nama_user FROM [dbo].[User] WHERE username = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, username);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                nama_user = result.getString("nama_user");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return nama_user;
    }

    private BigDecimal getTotalValue(String selectedIdPemesanan) throws SQLException {
        String query = "SELECT total FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
        connect.pstat = connect.conn.prepareStatement(query);
        connect.pstat.setString(1, selectedIdPemesanan);
        ResultSet rs = connect.pstat.executeQuery();

        BigDecimal total = BigDecimal.ZERO; // Default value if no total is found for the given id_pemesanan
        if (rs.next()) {
            total = rs.getBigDecimal("total");
        }

        rs.close();
        connect.pstat.close();
        return total;
    }



    public void FrameConfig() {
        add(pembatalan);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        Jptanggal.add(dateChooser);

        // Menetapkan format tanggal
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        dateChooser.setDateFormatString("yyyy-MM-dd");
        // Mengatur tanggal pertama kali menjadi null
        dateChooser.setDate(new Date());
    }

    public String getIdUser(String username) {
        String id_user = "";

        try {
            String query = "SELECT id_user FROM [dbo].[User] WHERE username = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, username);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                id_user = result.getString("id_user");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return id_user;
    }
//    public void dataIdPesanan(String idPemesanan) {
//        try {
//            String query = "SELECT tanggal_booking FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
//            connect.pstat = connect.conn.prepareStatement(query);
//            connect.pstat.setString(1, idPemesanan);
//            ResultSet rs = connect.pstat.executeQuery();
//
//            if (rs.next()) {
//                String tanggalBooking = rs.getString("tanggal_booking");
//                // Display the tanggal_booking, e.g., set it to a label
//                txtTglBerangkat.setText(tanggalBooking);
//            }
//
//            rs.close();
//            connect.pstat.close();
//        } catch (SQLException ex) {
//            System.out.println("Terjadi error saat memuat data: " + ex);
//        }
//    }
    private void Clear() {
        txtAPembatalan.setText("");
        dateChooser.setDate(new Date());
        // Clear the selection of cbIdPemesanan
        cbIdPemesanan.setSelectedItem(-1);

        // Clear all text fields and other components
        txtTotal.setText("");
        txtTglBerangkat.setText("");
        txtAsal.setText("");
        txtTujuan.setText("");
        txtRefund.setText("");

    }

    private void addColumn(){
        model.addColumn("ID Pemesanan");
        model.addColumn("Tanggal Pemesanan");
        model.addColumn("Tanggal Pembayaran");
        model.addColumn("Tanggal Pembatalan");
        model.addColumn("ID User");
        model.addColumn("ID Jadwal");
        model.addColumn("ID Kelas");
        model.addColumn("Status Pemesanan");
        model.addColumn("Total");
    }

    public void loadDataPemesanan(){
        // Menghapus seluruh data ditampilkan (jika ada) untuk tampilan pertama
        model.getDataVector().removeAllElements();

        // Memberi tahu data telah kosong
        model.fireTableDataChanged();

        try {
            // Mendapatkan id_user berdasarkan username
            String id_user = getIdUser(username);

            // Menggunakan PreparedStatement untuk menghindari SQL injection
            String query = "SELECT * FROM Pemesanan WHERE id_user = ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, id_user);
            connect.result = connect.pstat.executeQuery();

            // Lakukan perbaris data
            while (connect.result.next()) {
                Object[] rowData = new Object[9];
                rowData[0] = connect.result.getString("id_pemesanan");
                rowData[1] = connect.result.getString("tanggal_pemesanan");
                rowData[2] = connect.result.getString("tanggal_pembayaran");
                rowData[3] = connect.result.getString("tanggal_pembatalan");
                rowData[4] = connect.result.getString("id_user");
                rowData[5] = connect.result.getString("id_jadwal");
                rowData[6] = connect.result.getString("id_kelas");
                rowData[7] = connect.result.getString("status_pesanan");
                String totalStr = connect.result.getString("total");
                double totalDouble = Double.parseDouble(totalStr);
                String totalFormatted = decimalFormat.format(totalDouble);
                rowData[8] = totalFormatted;
                model.addRow(rowData);
            }
            connect.pstat.close();
            connect.result.close();

        } catch (Exception e) {
            System.out.println("Terjadi error saat load data Pemesanan: " + e);
        }

    }
    // Fungsi untuk mengambil total pembayaran dari database berdasarkan id_pemesanan
//    private double getTotalPembayaranFromDatabase(String idPemesanan) {
//        double totalPembayaran = 0.0;
//
//        try {
//            String query = "SELECT total_pembayaran FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
//            connect.pstat = connect.conn.prepareStatement(query);
//            connect.pstat.setString(1, idPemesanan);
//            ResultSet rs = connect.pstat.executeQuery();
//
//            if (rs.next()) {
//                totalPembayaran = rs.getDouble("total_pembayaran");
//            }
//
//            rs.close();
//            connect.pstat.close();
//        } catch (SQLException ex) {
//            System.out.println("Terjadi error saat mengambil total pembayaran dari database: " + ex);
//        }
//
//        return totalPembayaran;
//    }
//    // Fungsi untuk mengambil status pembayaran dari database berdasarkan id_pemesanan
//    private int getStatusPembayaranFromDatabase(String idPemesanan) {
//        int statusPembayaran = 0;
//
//        try {
//            String query = "SELECT status_pembayaran FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
//            connect.pstat = connect.conn.prepareStatement(query);
//            connect.pstat.setString(1, idPemesanan);
//            ResultSet rs = connect.pstat.executeQuery();
//
//            if (rs.next()) {
//                statusPembayaran = rs.getInt("status_pembayaran");
//            }
//
//            rs.close();
//            connect.pstat.close();
//        } catch (SQLException ex) {
//            System.out.println("Terjadi error saat mengambil status pembayaran dari database: " + ex);
//        }
//
//        return statusPembayaran;
//    }

    private void loadIdPemesananToComboBox() {
        try {
            String idUser = getIdUser(username);

            // Ambil data ID pemesanan dari database berdasarkan id_user yang sedang login
            String query = "SELECT id_pemesanan, tanggal_booking FROM [dbo].[Pemesanan] WHERE id_user = ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, idUser);
            ResultSet rs = connect.pstat.executeQuery();

            // Hapus semua item di ComboBox sebelum menambahkan data baru
            cbIdPemesanan.removeAllItems();

            // Tambahkan ID pemesanan yang sesuai ke dalam cbIdPemesanan
            while (rs.next()) {
                String idPemesanan = rs.getString("id_pemesanan");
                Date tanggalBooking = rs.getDate("tanggal_booking");

                // Filter only the bookings with tanggal_booking greater than or equal to the current date
                if (tanggalBooking != null && tanggalBooking.compareTo(new Date()) >= 0) {
                    cbIdPemesanan.addItem(idPemesanan);
                }
            }

            rs.close();
            connect.pstat.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat memuat data: " + ex);
        }
    }
}
