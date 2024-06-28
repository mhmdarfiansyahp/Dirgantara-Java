package Dirgantara.Laporan;

import Connect.Connect;
import Dirgantara.Dashboard.ProfilManager;
import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalManager;
import Riwayat.RiwayatManager;
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
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class laporanPembatalan extends JFrame{
    private JLabel lbl_user;
    private JLabel lbl_jabatan;
    private JButton dashboardButton;
    private JButton riwayatTransaksiButton;
    private JButton laporanPembelianButton;
    private JButton logoutButton;
    private JButton laporanPembatalanButton;
    private JTextField txtTahun;
    private JComboBox cmbBulan;
    private JTable tbdataPembelian;
    private JButton cariButton;
    private JButton cetakButton;
    private JButton resetButton;
    private JPanel pembatalan;

    Connect connection = new Connect();
    SimpleDateFormat dateFormatTampil = new SimpleDateFormat("dd/MM/yyyy");
    Date tanggal = new Date();
    private DefaultTableModel model;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);


    public laporanPembatalan(String username, String nama_akses){
        FrameConfig();
        lbl_user.setText(username);
        lbl_jabatan.setText(nama_akses);
        clear();

        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tahun = txtTahun.getText();
                int bulan = cmbBulan.getSelectedIndex();
                if (tahun.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Masukkan tahun terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                loadTable(bulan, tahun);
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        cetakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tbdataPembelian.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Tidak ada data untuk dicetak.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                panggilJasper();
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
        laporanPembelianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                laporanPembelian laporanPembelian = new laporanPembelian(username, nama_akses);
                laporanPembelian.setVisible(true);
                dispose();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(laporanPembatalan.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        riwayatTransaksiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RiwayatManager riwayatManager = new RiwayatManager(username, nama_akses);
                riwayatManager.setVisible(true);
                dispose();
            }
        });
    }

    public void addColumn(){
        model.addColumn("ID Pemesanan");
        model.addColumn("Nama Pemesan");
        model.addColumn("Tanggal Pemesanan");
        model.addColumn("Tanggal Pembatalan");
        model.addColumn("Alasan Pembatalan");
        model.addColumn("Total (Rp)");
    }

    public void FrameConfig() {
        add(pembatalan);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void dataTable(int idxBulan, String tahun){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        String query = "SELECT * FROM view_laporanPembatalan";

        if (idxBulan > 0 && !(tahun.isEmpty())){
            query = "SELECT * FROM view_laporanPembatalan WHERE YEAR(tanggal_pembatalan) = '" + tahun + "' AND MONTH(tanggal_pembatalan) = '" + idxBulan +"'";
        }

        try {
            connection.pstat = connection.conn.prepareStatement(query);
            connection.result = connection.pstat.executeQuery();
            while (connection.result.next()){
                Object[] obj = new Object[6];
                obj[0] = connection.result.getString(1);
                obj[1] = connection.result.getString(2);
                obj[2] = connection.result.getString(3);
                obj[3] = connection.result.getString(4);
                obj[4] = connection.result.getString(5);
                String totalString = connection.result.getString(6);
                double totalValue = Double.parseDouble(totalString);
                String formattedTotal = numberFormat.format(totalValue);
                obj[5] = formattedTotal;
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        }catch (Exception ex){
            System.out.println("Error saat load data table "+ ex);
        }
    }

    public void loadTable(int idxBulan, String tahun){
        model = new DefaultTableModel();
        tbdataPembelian.setModel(model);
        addColumn();
        dataTable(idxBulan, tahun);
    }

    public void clear(){
        txtTahun.setText("");
        cmbBulan.setSelectedIndex(0);
        //loadTable(cmbBulan.getSelectedIndex(), txtTahun.getText());
    }

    public void panggilJasper(){
        try {
            //System.out.println(kembalian);
            //System.out.println(metodeBayar);
            //Mengambil data dari table detailPenjualan
            Connect connection = new Connect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM view_laporanPembatalan";
            connection.result = connection.stat.executeQuery(query);

            DefaultTableModel dataTransansi = new DefaultTableModel();
            dataTransansi.addColumn("nomor");
            dataTransansi.addColumn("id_pemesanan");
            dataTransansi.addColumn("tanggal_pemesanan");
            dataTransansi.addColumn("tanggal_pembatalan");
            dataTransansi.addColumn("alasan_batal");
            dataTransansi.addColumn("total");
            int nomor = 1;

            while (connection.result.next()) {
                Object[] obj = new Object[6];
                obj[0] = nomor;
                obj[1] = connection.result.getString("id_pemesanan");
                obj[2] = connection.result.getString("tanggal_pemesanan");
                obj[3] = connection.result.getString("tanggal_pembatalan");
                obj[4] = connection.result.getString("alasan_batal");
                double total_double = Double.parseDouble(connection.result.getString("total"));
                int total_int = (int) total_double;
                obj[5] = total_int;
                dataTransansi.addRow(obj);
                nomor++;
            }

            connection.stat.close();
            connection.result.close();


            HashMap<String, Object> parameter = new HashMap<>();
            JRDataSource dataSource = new JRTableModelDataSource(dataTransansi);
            JasperDesign jasperDesign = JRXmlLoader.load("Jasper/MyReports/laporanPembatalan.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            parameter.put("Tanggal", dateFormatTampil.format(tanggal));
            parameter.put("Bulan", Calendar.getInstance().get(Calendar.MONTH));
            parameter.put("Tahun", Calendar.getInstance().get(Calendar.YEAR));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, dataSource);

            // Simpan laporan ke file PDF
            String outputFile = "Jasper/MyReports/laporanPembatalan.pdf";

            // Cek apakah direktori sudah ada atau belum, jika belum buat direktori
            File directory = new File("Jasper/MyReports/laporanPembatalan/");
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

    private String formatTotal(int total) {
        // Format angka di sini, misalnya dengan menambahkan titik di setiap ribuan
        return String.format("%,d", total);
    }

}
