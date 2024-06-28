package Dirgantara.Transaksi;

import Connect.Connect;
import Dirgantara.Dashboard.ProfilPenumpang;
import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalPenumpang;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Pembayaran extends JFrame{
    private JLabel label_user;
    private JButton dashboardButton;
    private JButton btnPembatalan;
    private JButton logoutButton;
    private JButton btnPemesanan;
    private JButton riwayatButton;
    private JButton btnPembayaran;
    private JPanel pembayaran;
    private JTextField txttglPesan;
    private JComboBox cbIdpemesanan;
    private JTextField txttglbayar;
    private JTextField txtrute;
    private JTextField txtjumlah;
    private JTextField txttotal;
    private JButton lanjutKePembayaranButton;
    private JTextField txtnama;
    private JTextField txtBook;
    private JCheckBox checkvirtual;
    private JCheckBox checkGerai;
    private JRadioButton virtualAccountRadioButton;
    private JRadioButton geraiRetailRadioButton;
    private JComboBox cbPemilihan;
    private JRadioButton cbCash;

    String selectedPaymentMethod;
    private String[] virtualAccountOptions = {"BNI", "Mandiri", "Permata"};
    private String[] geraiRetaiOptions = {"Alfamart", "Indomart"};
    Connect connect = new Connect();

    public Pembayaran(String username){
        FrameConfig();
        label_user.setText(username);
        String nama_user = getNamaUser(username);
        txtnama.setText(nama_user);

        String id_user = getIdUser(username);
        populateIdPemesanan(id_user);
        cbIdpemesanan.setSelectedIndex(-1);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuAwal menuAwal = new MenuAwal();
                menuAwal.setVisible(true);
                dispose();
            }
        });
        cbIdpemesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tanggal(cbIdpemesanan.getSelectedItem().toString());
            }
        });
        lanjutKePembayaranButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean virtualSelected = virtualAccountRadioButton.isSelected();
                boolean geraiSelected = geraiRetailRadioButton.isSelected();
                boolean cashSelected = cbCash.isSelected();

                if (virtualSelected) {
                    String selectedIdPemesanan = cbIdpemesanan.getSelectedItem().toString();
                    int totalPemesanan = getTotalPemesanan(selectedIdPemesanan);

                    // Ambil pilihan virtual account dari combo box cbPemilihan
                    String selectedVirtualAccount = cbPemilihan.getSelectedItem().toString();

                    dispose(); // Menutup form Pembayaran saat ini

                    // Berdasarkan pilihan virtual account, buat dan tampilkan form yang sesuai
                    if (selectedVirtualAccount.equals("BNI")) {
                        VirtualAccount virtualAccount = new VirtualAccount(selectedIdPemesanan, username, totalPemesanan, selectedVirtualAccount, selectedPaymentMethod);
                        virtualAccount.setVisible(true);
                    } else if (selectedVirtualAccount.equals("Mandiri")) {
                        Mandiri mandiri = new Mandiri(selectedIdPemesanan, username, totalPemesanan, selectedVirtualAccount,selectedPaymentMethod);
                        mandiri.setVisible(true);
                    } else if (selectedVirtualAccount.equals("Permata")) {
                        Permata permata = new Permata(selectedIdPemesanan, username, totalPemesanan, selectedVirtualAccount,selectedPaymentMethod);
                        permata.setVisible(true);
                    } else {
                        // Tampilkan pesan jika pemilihan virtual account tidak valid
                        JOptionPane.showMessageDialog(Pembayaran.this, "Pilihan virtual account tidak valid!");
                    }
                } else if (geraiSelected) {
                    String selectedIdPemesanan = cbIdpemesanan.getSelectedItem().toString();
                    int totalPemesanan = getTotalPemesanan(selectedIdPemesanan);

                    String selectgeraiRetai = cbPemilihan.getSelectedItem().toString();
                    dispose();
                    if (selectgeraiRetai.equals("Alfamart")) {
                        Alfamart alfamart = new Alfamart(selectedIdPemesanan, username, totalPemesanan, selectgeraiRetai, selectedPaymentMethod);
                        alfamart.setVisible(true);
                    } else if (selectgeraiRetai.equals("Indomart")) {
                        Indomart indomart = new Indomart(selectedIdPemesanan, username, totalPemesanan, selectgeraiRetai, selectedPaymentMethod);
                        indomart.setVisible(true);
                    }

                } else if (cashSelected) {
                    String selectedIdPemesanan = cbIdpemesanan.getSelectedItem().toString();
                    String randomCode = generateRandomCode(16);
                    // Tampilkan kode random dalam kotak pesan dialog JOptionPane.showMessageDialog
                    JOptionPane.showMessageDialog(Pembayaran.this, randomCode);
                    updatePemesananData(selectedIdPemesanan, "Cash");

                }else {
                    // Jika tidak ada CheckBox yang dipilih, tampilkan pesan error atau lakukan tindakan lainnya
                    JOptionPane.showMessageDialog(Pembayaran.this, "Pilih salah satu jenis pembayaran!");
                }

            }
        });
        geraiRetailRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (geraiRetailRadioButton.isSelected()) {
                    // Menghapus seluruh isi dari cbPemilihan
                    cbPemilihan.removeAllItems();

                    // Menambahkan opsi gerai retail ke cbPemilihan
                    for (String option : geraiRetaiOptions) {
                        cbPemilihan.addItem(option);
                    }
                    // Nonaktifkan virtualAccountRadioButton dan cbPemilihan
                    virtualAccountRadioButton.setEnabled(false);
                    cbCash.setEnabled(false);
                    cbPemilihan.setEnabled(true);

                    selectedPaymentMethod = "Gerai Retail";

                } else {
                    // Jika geraiRetailRadioButton tidak dipilih, aktifkan kembali virtualAccountRadioButton dan cbPemilihan
                    virtualAccountRadioButton.setEnabled(true);
                    cbCash.setEnabled(true);
                    cbPemilihan.setEnabled(false);
                }
            }
        });
        virtualAccountRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (virtualAccountRadioButton.isSelected()) {
                    // Menghapus seluruh isi dari cbPemilihan
                    cbPemilihan.removeAllItems();

                    // Menambahkan opsi virtual account ke cbPemilihan
                    for (String option : virtualAccountOptions) {
                        cbPemilihan.addItem(option);
                    }

                    // Nonaktifkan geraiRetailRadioButton
                    geraiRetailRadioButton.setEnabled(false);
                    cbCash.setEnabled(false);
                    cbPemilihan.setEnabled(true);

                    selectedPaymentMethod = "Virtual Account";

                } else {
                    // Jika virtualAccountRadioButton tidak dipilih, aktifkan kembali geraiRetailRadioButton dan nonaktifkan cbPemilihan
                    geraiRetailRadioButton.setEnabled(true);
                    cbCash.setEnabled(true);
                    cbPemilihan.setEnabled(false);
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
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilPenumpang profilPenumpang = new ProfilPenumpang(username);
                profilPenumpang.setVisible(true);
                dispose();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Logout button clicked.");
                int option = JOptionPane.showConfirmDialog(Pembayaran.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
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
        btnPembatalan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembatalan pembatalan = new Pembatalan(username);
                pembatalan.setVisible(true);
                dispose();
            }
        });
        cbCash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbCash.isSelected()) {
                    geraiRetailRadioButton.setEnabled(false);
                    virtualAccountRadioButton.setEnabled(false);
                    cbPemilihan.setEnabled(true);
                } else {
                    geraiRetailRadioButton.setEnabled(true);
                    virtualAccountRadioButton.setEnabled(true);
                    cbPemilihan.setEnabled(false);
                }
            }
        });
    }

    public void FrameConfig() {
        add(pembayaran);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
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

    public void populateIdPemesanan(String id_user) {
        try {
            String query = "SELECT id_pemesanan FROM [dbo].[Pemesanan] WHERE id_user = ? AND status_pesanan = 0"; // Menambahkan filter status = 0
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, id_user);
            ResultSet result = pstat.executeQuery();

            cbIdpemesanan.removeAllItems(); // Menghapus semua item pada ComboBox sebelum memuat yang baru

            while (result.next()) {
                String id_pemesanan = result.getString("id_pemesanan");
                cbIdpemesanan.addItem(id_pemesanan); // Menambahkan id_pemesanan ke dalam ComboBox
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    private void Tanggal(String selectedIdPemesanan) {
        try {
            String query = "SELECT tanggal_booking, tanggal_pemesanan FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, selectedIdPemesanan);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                // Ambil nilai tanggal dari database
                Date txtBookValue = result.getDate("tanggal_booking");
                Date txttglPesanValue = result.getDate("tanggal_pemesanan");

                // Tambahkan 2 hari ke tanggal menggunakan Calendar
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(txtBookValue);
                calendar.add(Calendar.DATE, 2);
                Date txtBookPlus2Days = calendar.getTime();

                calendar.setTime(txttglPesanValue);
                calendar.add(Calendar.DATE, 2);
                Date txttglPesanPlus2Days = calendar.getTime();

                // Format tanggal sesuai kebutuhan tampilan pada komponen txttglPesan
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedTxtBookValue = dateFormat.format(txtBookPlus2Days);
                String formattedTxttglPesanValue = dateFormat.format(txttglPesanPlus2Days);

                txtBook.setText(formattedTxtBookValue);
                txttglPesan.setText(formattedTxttglPesanValue);

                String kodeRute = getKodeRute(selectedIdPemesanan);
                txtrute.setText(kodeRute);

                int jumlahPemesanan = Jumlah(selectedIdPemesanan);
                txtjumlah.setText(String.valueOf(jumlahPemesanan));

                int totalPemesanan = getTotalPemesanan(selectedIdPemesanan);

                // Format angka total pemesanan dengan tanda titik sebagai pemisah ribuan
                DecimalFormat decimalFormat = new DecimalFormat("#,###.###");
                String formattedTotal = decimalFormat.format(totalPemesanan);

                // Ganti tanda koma (,) dengan tanda titik (.)
                formattedTotal = formattedTotal.replace(",", ".");

                txttotal.setText(formattedTotal);
                Date currentDate = new Date();

                // Format tanggal sesuai kebutuhan tampilan pada komponen txttglbayar
                SimpleDateFormat tanggal = new SimpleDateFormat("dd/MM/yyyy");
                String formattedCurrentDate = tanggal.format(currentDate);

                // Set nilai tanggal pada komponen txttglbayar
                txttglbayar.setText(formattedCurrentDate);
            } else {
                // Handle the case if the selected id_pemesanan is not found
                // You can clear the fields or show an error message
                txtBook.setText("");
                txttglPesan.setText("");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    private String getKodeRute(String idPemesanan) {
        String kodeRute = "";

        try {
            String query = "SELECT Rute.kode_rute " +
                    "FROM [dbo].[Rute] " +
                    "JOIN [dbo].[Jadwal_Penerbangan] ON Rute.id_rute = Jadwal_Penerbangan.id_rute " +
                    "JOIN [dbo].[Pemesanan] ON Jadwal_Penerbangan.id_jadwal = Pemesanan.id_jadwal " +
                    "WHERE Pemesanan.id_pemesanan = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, idPemesanan);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                kodeRute = result.getString("kode_rute");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return kodeRute;
    }

    private int Jumlah(String idPemesanan) {
        int jumlahPemesanan = 0;

        try {
            String query = "SELECT jumlah FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, idPemesanan);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                jumlahPemesanan = result.getInt("jumlah");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return jumlahPemesanan;
    }

    private int getTotalPemesanan(String idPemesanan) {
        int total = 0;

        try {
            String query = "SELECT total FROM [dbo].[Pemesanan] WHERE id_pemesanan = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, idPemesanan);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                total = result.getInt("total");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return total;
    }

    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length + (length / 4) - 1); // Menambahkan ruang untuk spasi

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));

            // Tambahkan spasi setiap 4 angka
            if ((i + 1) % 4 == 0 && i != length - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    private void updatePemesananData(String selectedIdPemesanan, String metodeBayar) {
        try {
            // Buka koneksi database
            Connection connection = connect.conn;

            // Buat perintah SQL untuk melakukan update pada tabel pemesanan
            String updateQuery = "UPDATE [dbo].[Pemesanan] SET metode_bayar = ? WHERE id_pemesanan = ?";
            PreparedStatement pstat = connection.prepareStatement(updateQuery);

            // Set metode bayar sesuai dengan selectedPaymentMethod
            pstat.setString(1, metodeBayar);

            // Set nilai id_pemesanan yang akan di-update
            pstat.setString(2, selectedIdPemesanan);

            // Eksekusi perintah update
            pstat.executeUpdate();

            // Tutup perintah dan koneksi
            pstat.close();
            connection.close();

            // Tampilkan pesan sukses jika pembaruan berhasil
            JOptionPane.showMessageDialog(Pembayaran.this, "Data pemesanan berhasil diperbarui!");
        } catch (Exception ex) {
            // Tampilkan pesan error jika terjadi kesalahan
            JOptionPane.showMessageDialog(Pembayaran.this, "Terjadi kesalahan saat memperbarui data pemesanan: " + ex.getMessage());
        }
    }


}
