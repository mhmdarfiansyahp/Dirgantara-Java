package Dirgantara.Transaksi;

import Connect.Connect;
import Dirgantara.Dashboard.Profile;
import Dirgantara.Master.Master_maskapai;
import Dirgantara.MasterBandara.menuBandara;
import Dirgantara.MasterJadwal.MenuUtama;
import Dirgantara.MasterKursi.Halaman_utama;
import Dirgantara.MasterPesawat.MenuPesawat;
import Dirgantara.MenuAwal;
import Dirgantara.Rute.CreateRute;
import Dirgantara.Rute.menuRute;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ConfirmPembayaran extends JFrame{
    private JLabel label_user;
    private JLabel label_jbtan;
    private JButton btndashboard;
    private JButton btnbandara;
    private JButton btnmaskapai;
    private JButton btnpesawat;
    private JButton btnrute;
    private JButton btnkursi;
    private JButton btnlogout;
    private JButton btnJadwalpenerbangan;
    private JTextField txttglPesan;
    private JTextField txtBook;
    private JButton konfirmasiButton;
    private JComboBox cbIdpemesanan;
    private JTextField txtnama;
    private JPanel confirm;
    private JTextField txttotal;
    private JTextField txtjumlah;
    private JTextField txtrute;
    private JTextField txttglbayar;

    Connect connect = new Connect();
    SimpleDateFormat dateFormatTampil = new SimpleDateFormat("dd/MM/yyyy");

    Date tanggal = new Date();
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    String jamBerangkat = null;
    String jamSampai = null;

    String tujuan, asal;


    public ConfirmPembayaran(String username, String nama_akses){
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);

        populateIdPemesanan();
        cbIdpemesanan.setSelectedIndex(-1);
        cbIdpemesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedItem = cbIdpemesanan.getSelectedItem();
                if (selectedItem != null) {
                    String selectedIdPemesanan = selectedItem.toString();
                    String namaUser = getNamaUser(selectedIdPemesanan);
                    txtnama.setText(namaUser);
                    Tanggal(selectedIdPemesanan);
                } else {
                    txtnama.setText("");
                    txtBook.setText("");
                    txttglPesan.setText("");
                    txttglbayar.setText("");
                    txtrute.setText("");
                    txtjumlah.setText("");
                    txttotal.setText("");
                }
            }
        });
        konfirmasiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedItem = cbIdpemesanan.getSelectedItem();
                if (selectedItem != null) {
                    String selectedIdPemesanan = selectedItem.toString();
                    // Lanjutkan dengan logika Anda setelah ini
                    updatePemesananData(selectedIdPemesanan);
                    panggilJasper(selectedIdPemesanan);
                } else {
                    // Tampilkan pesan atau lakukan aksi yang sesuai jika tidak ada item yang terpilih
                    JOptionPane.showMessageDialog(null, "Pilih ID Pemesanan terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(ConfirmPembayaran.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnJadwalpenerbangan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuUtama menuUtama = new MenuUtama(username, nama_akses);
                menuUtama.setVisible(true);
                dispose();
            }
        });
        btnkursi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Halaman_utama halamanUtama = new Halaman_utama(username, nama_akses);
                halamanUtama.setVisible(true);
                dispose();
            }
        });
        btnrute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuRute menuRute = new menuRute(username, nama_akses);
                menuRute.setVisible(true);
                dispose();
            }
        });
        btnpesawat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuPesawat menuPesawat = new MenuPesawat(username, nama_akses);
                menuPesawat.setVisible(true);
                dispose();
            }
        });
        btnmaskapai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Master_maskapai masterMaskapai = new Master_maskapai(username,nama_akses);
                masterMaskapai.setVisible(true);
                dispose();
            }
        });
        btnbandara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuBandara bandara = new menuBandara(username, nama_akses);
                bandara.setVisible(true);
                dispose();
            }
        });
        btndashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Profile profile = new Profile(username,nama_akses);
                profile.setVisible(true);
                dispose();
            }
        });
    }

    public void FrameConfig() {
        add(confirm);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void populateIdPemesanan() {
        try {
            String query = "SELECT id_pemesanan FROM [dbo].[Pemesanan] WHERE metode_bayar = 'Cash' AND status_pesanan <> 2"; // Exclude entries with status 2
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            ResultSet result = pstat.executeQuery();

            cbIdpemesanan.removeAllItems(); // Menghapus semua item pada ComboBox sebelum memuat yang baru

            // Tambahkan semua id_pemesanan yang sesuai dengan kondisi ke dalam ComboBox
            while (result.next()) {
                String id_pemesanan = result.getString("id_pemesanan");
                cbIdpemesanan.addItem(id_pemesanan);
            }

            // Setelah selesai, atur index ComboBox ke -1 (tidak ada yang terpilih)
            cbIdpemesanan.setSelectedIndex(-1);

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    public String getNamaUser(String idPemesanan) {
        String nama_user = "";

        try {
            String query = "SELECT [User].nama_user " +
                    "FROM [dbo].[User] " +
                    "JOIN [dbo].[Pemesanan] ON [User].id_user = [Pemesanan].id_user " +
                    "WHERE [Pemesanan].id_pemesanan = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, idPemesanan);
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

    private void updatePemesananData(String idPemesanan) {
        try {
            // Buka koneksi database
            Connection connection = connect.conn;

            // Buat perintah SQL untuk melakukan update pada tabel pemesanan
            String updateQuery = "UPDATE [dbo].[Pemesanan] SET tanggal_pembayaran = ?, status_pesanan = ? WHERE id_pemesanan = ?";
            PreparedStatement pstat = connection.prepareStatement(updateQuery);

            // Ambil tanggal pembayaran dari komponen txttglbayar
            Date currentDate = new Date();
            pstat.setDate(1, new java.sql.Date(currentDate.getTime()));

            // Set metode bayar sesuai dengan selectedVirtualAccount
            pstat.setInt(2, 2); // Or you can set the status code directly: pstat.setString(2, "2");

            // Set status menjadi 'Sudah Dibayar' atau '2'
            pstat.setString(3, idPemesanan);

            // Eksekusi perintah update
            pstat.executeUpdate();

            // Tutup perintah dan koneksi
            pstat.close();
            connection.close();

            // Tampilkan pesan sukses jika pembaruan berhasil
            JOptionPane.showMessageDialog(ConfirmPembayaran.this, "Data pemesanan berhasil diperbarui!");

            // Panggil metode untuk memperbarui tampilan atau komponen lain yang perlu di-refresh
            // misalnya panggil metode Tanggal(selectedIdPemesanan) untuk mengupdate tanggal dan data lain
            Tanggal(idPemesanan);
        } catch (Exception ex) {
            // Tampilkan pesan error jika terjadi kesalahan
            JOptionPane.showMessageDialog(ConfirmPembayaran.this, "Terjadi kesalahan saat memperbarui data pemesanan: " + ex.getMessage());
        }
    }

    public void panggilJasper(String idPemesanan){
        try {
            //System.out.println(kembalian);
            //Mengambil data dari table detailPenjualan
            Connect connection = new Connect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT p.id_pemesanan, p.tanggal_booking, p.jumlah, " +
                    "u.nama_user, " +
                    "ba.nama_bandara AS nama_bandara_asal, " +
                    "bt.nama_bandara AS nama_bandara_tujuan, " +
                    "jp.waktu_berangkat, " +
                    "jp.perkiraan_sampai " +
                    "FROM Pemesanan p " +
                    "JOIN Jadwal_Penerbangan jp ON p.id_jadwal = jp.id_jadwal " +
                    "JOIN Bandara ba ON jp.id_bandara_asal = ba.id_bandara " +
                    "JOIN Bandara bt ON jp.id_bandara_tujuan = bt.id_bandara " +
                    "JOIN [dbo].[User] u ON p.id_user = u.id_user " +
                    "WHERE p.id_pemesanan = '" + idPemesanan + "'";



            connection.result = connection.stat.executeQuery(query);

            DefaultTableModel dataTransansi = new DefaultTableModel();
            dataTransansi.addColumn("nama_user");
            dataTransansi.addColumn("jumlah");

            while (connection.result.next()) {
                Object[] obj = new Object[2];
                obj[0] = connection.result.getString("nama_user");
                obj[1] = connection.result.getString("jumlah");

                dataTransansi.addRow(obj);

                asal = connection.result.getString("nama_bandara_asal");
                tujuan = connection.result.getString("nama_bandara_tujuan");

                String waktuBerangkatStr = connection.result.getString("waktu_berangkat");
                String waktuSampaiStr = connection.result.getString("perkiraan_sampai");


                try {
                    // Ubah format waktu_berangkat menjadi hanya jamnya saja
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date waktuBerangkat = sdf.parse(waktuBerangkatStr);
                    Date waktuSampai = sdf.parse(waktuSampaiStr);


                    // Format waktu_berangkat menjadi hanya jamnya saja
                    jamBerangkat = timeFormat.format(waktuBerangkat);
                    jamSampai = timeFormat.format(waktuSampai);
                } catch (java.text.ParseException e) {
                    // Handle the exception if there is an issue with parsing the date
                    e.printStackTrace();
                }
            }

            connection.stat.close();
            connection.result.close();



            HashMap<String, Object> parameter = new HashMap<>();
            JRDataSource dataSource = new JRTableModelDataSource(dataTransansi);
            JasperDesign jasperDesign = JRXmlLoader.load("Jasper/MyReports/Online.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            parameter.put("IDPemesanan", idPemesanan);
            parameter.put("TanggalKebrangkatan", dateFormatTampil.format(tanggal) );
            parameter.put("asal", asal);
            parameter.put("tujuan", tujuan);
            parameter.put("waktu_berangkat",jamBerangkat);
            parameter.put("waktusampai", jamSampai);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, dataSource);

            // Simpan laporan ke file PDF
            String outputFile = "Jasper/MyReports/Online/" + idPemesanan + ".pdf";

            // Cek apakah direktori sudah ada atau belum, jika belum buat direktori
            File directory = new File("Jasper/MyReports/Online/");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);

            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);

        } catch ( JRException ex) {
            ex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear(){
        cbIdpemesanan.setSelectedIndex(-1);
        txtnama.setText("");
        txtBook.setText("");
        txttglPesan.setText("");
        txttglbayar.setText("");
        txtrute.setText("");
        txtjumlah.setText("");
        txttotal.setText("");
    }
}
