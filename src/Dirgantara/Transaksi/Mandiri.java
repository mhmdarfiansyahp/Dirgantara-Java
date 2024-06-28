package Dirgantara.Transaksi;

import Connect.Connect;
import Dirgantara.halamanAwalPenumpang;

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
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Mandiri extends JFrame{
    private JTextField txtGerai;
    private JTextField txtkoderedeem;
    private JTextField txttotal;
    private JButton selesaiButton;
    private JPanel mandiri;
    private JButton btnkembali;

    SimpleDateFormat dateFormatTampil = new SimpleDateFormat("dd/MM/yyyy");

    Date tanggal = new Date();

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    String jamBerangkat = null;
    String jamSampai = null;

    Connect connect = new Connect();
    String idPemesanan,selectedVirtualAccount,selectedPaymentMethod, TanggalKebrangkatan, tujuan, asal;
    int totalPemesanan;
    public Mandiri(String idPemesanan, String username, int totalPemesanan, String selectedVirtualAccount, String selectedPaymentMethod) {
        FrameConfig();
        this.idPemesanan = idPemesanan;
        this.totalPemesanan = totalPemesanan;
        this.selectedVirtualAccount = selectedVirtualAccount;
        this.selectedPaymentMethod = selectedPaymentMethod;
        String randomCode = generateRandomCode(16);
        txtkoderedeem.setText(randomCode);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        txttotal.setText(decimalFormat.format(totalPemesanan));
        txtGerai.setText(selectedVirtualAccount);


        selesaiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePemesananData(selectedPaymentMethod);
                halamanAwalPenumpang halamanAwalPenumpang = new halamanAwalPenumpang(username);
                halamanAwalPenumpang.setVisible(true);
                dispose();
                panggilJasper("Mandiri");
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembayaran pembayaran = new Pembayaran(username);
                pembayaran.setVisible(true);
                dispose();
            }
        });
    }


    public void FrameConfig() {
        add(mandiri);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
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

    private void updatePemesananData(String metodeBayar) {
        try {
            // Buka koneksi database
            Connection connection = connect.conn;

            // Buat perintah SQL untuk melakukan update pada tabel pemesanan
            String updateQuery = "UPDATE [dbo].[Pemesanan] SET tanggal_pembayaran = ?, metode_bayar = ?, status_pesanan = ? WHERE id_pemesanan = ?";
            PreparedStatement pstat = connection.prepareStatement(updateQuery);

            // Ambil tanggal pembayaran dari komponen txttglbayar
            Date currentDate = new Date();
            pstat.setDate(1, new java.sql.Date(currentDate.getTime()));

            // Set metode bayar sesuai dengan selectedVirtualAccount
            pstat.setString(2, selectedPaymentMethod);

            // Set status menjadi 'Sudah Dibayar' atau '2'
            pstat.setInt(3, 2); // Or you can set the status code directly: pstat.setString(3, "2");

            // Set nilai id_pemesanan yang akan di-update
            pstat.setString(4, idPemesanan);

            // Eksekusi perintah update
            pstat.executeUpdate();

            // Tutup perintah dan koneksi
            pstat.close();
            connection.close();

            // Tampilkan pesan sukses jika pembaruan berhasil
            JOptionPane.showMessageDialog(Mandiri.this, "Data pemesanan berhasil diperbarui!");
        } catch (Exception ex) {
            // Tampilkan pesan error jika terjadi kesalahan
            JOptionPane.showMessageDialog(Mandiri.this, "Terjadi kesalahan saat memperbarui data pemesanan: " + ex.getMessage());
        }
    }

    public void panggilJasper(String metodeBayar){
        try {
            //System.out.println(kembalian);
            System.out.println(metodeBayar);
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

}
