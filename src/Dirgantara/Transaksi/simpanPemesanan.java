package Dirgantara.Transaksi;

import Connect.Connect;
import Dirgantara.halamanAwalPenumpang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.Date;

import static java.sql.Types.DATE;
import static java.sql.Types.VARCHAR;

public class simpanPemesanan extends JFrame{
    private JPanel DetailP;
    private JTextField txtid;
    private JTextField txtTglPesan;
    private JTextField txtNamapenum;
    private JTextField txtJumlah;
    private JTextField txtkursi;
    private JTextField txtstatus;
    private JTable tbDetailpesanan;
    private JTextField txttotal;
    private JButton btnsimpan;
    private JTextField txtBook;
    private JButton btnkembali;
    private JCheckBox cekBayi;
    private JCheckBox cekanak;
    private JCheckBox cekdewasa;
    private JTextField txtbayi;
    private JTextField txtanak;
    private JTextField txtdewasa;

    String selectedAsal,selectedTujuan;
    Date selectedDate;

    String previousSelectedKursi;

    String selectedKursi, kodeRute, id, tanggal, waktuBerangkat,selectedIdJadwal, book, status, namaKelas,selectedIdPesawat;
    int hargaKursi, hargaRute, Jmlh, statusText, bayi, anak, dewasa;

    BigDecimal hargaTotal;

    Pemesanan pemesanan;
    DefaultTableModel modelDetail;

    Connect connect = new Connect();

    public simpanPemesanan(DefaultTableModel modelDetail, String username, String selectedKursi, String kodeRute, String waktuBerangkat, String idJadwal, String selectedAsal, String selectedTujuan, Date selectedDate, String selectedIdPesawat, Pemesanan pemesanan) {
        autoid();
        this.pemesanan = pemesanan;
        this.modelDetail = modelDetail;
        this.selectedKursi = selectedKursi;
        this.previousSelectedKursi = selectedKursi;
        this.kodeRute = kodeRute;
        this.waktuBerangkat = waktuBerangkat;
        this.selectedIdJadwal = idJadwal;
        this.selectedAsal = selectedAsal;
        this.selectedTujuan = selectedTujuan;
        this.selectedDate = selectedDate;
        this.selectedIdPesawat = selectedIdPesawat; // Inisialisasi selectedIdPesawat
        tbDetailpesanan.setModel(modelDetail); // Tambahkan baris ini untuk mengatur model pada JTable
        FrameConfig();
        addKolom();
        tglpesan();

        txtstatus.setText("Belum Selesai");
        txtstatus.setEditable(false);
        txtbayi.setEnabled(false);
        txtanak.setEnabled(false);
        txtdewasa.setEnabled(false);
        String nama_user = getNamaUser(username);
        txtNamapenum.setText(nama_user);
        txtkursi.setText(selectedKursi);
        hargaKursi = getHargaKursi(selectedIdPesawat,selectedKursi);
        txtBook.setText(getFormattedDate(waktuBerangkat));

        // Mengambil harga rute berdasarkan kode rute
        hargaRute = getHargaRute(kodeRute);
        txtJumlah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungTotal();
            }
        });
        btnsimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtid.getText();
                tanggal = txtTglPesan.getText();
                book = txtBook.getText();
                Jmlh = Integer.parseInt(txtJumlah.getText());
                status = txtstatus.getText();
                String formattedTotal = txttotal.getText().replace(".", "");
                hargaTotal = new BigDecimal(formattedTotal);
                namaKelas = txtkursi.getText();

                if (txtid.getText().isEmpty() || txtTglPesan.getText().isEmpty() || txtBook.getText().isEmpty() || txtNamapenum.getText().isEmpty()
                        ||txtJumlah.getText().isEmpty() || txtkursi.getText().isEmpty() || txtstatus.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(DetailP, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Jmlh = Integer.parseInt(txtJumlah.getText());
                int kapasitasKursi = getKapasitasKursi(selectedIdPesawat, getIdKelas(namaKelas));
                if (Jmlh > kapasitasKursi) {
                    JOptionPane.showMessageDialog(DetailP, "Jumlah kursi melebihi kapasitas yang tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (status.equalsIgnoreCase("Belum Selesai")) {
                    statusText = 0;
                } else if (status.equalsIgnoreCase("Dibatalkan")) {
                    statusText = 1;
                } else if (status.equalsIgnoreCase("Selesai")){
                    statusText = 2;
                }else if (status.equalsIgnoreCase("Direturn")){
                    statusText = 3;
                }

                try{
                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[Pemesanan] VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, tanggal);
                    connect.pstat.setString(3, book);
                    connect.pstat.setNull(4, DATE);
                    connect.pstat.setNull(5, DATE);
                    connect.pstat.setString(6, getIdUser(username));
                    connect.pstat.setString(7, Integer.toString(Jmlh));
                    connect.pstat.setString(8, selectedIdJadwal);
                    connect.pstat.setNull(9, VARCHAR);
                    connect.pstat.setString(10, getIdKelas(namaKelas));
                    connect.pstat.setNull(11, VARCHAR);
                    connect.pstat.setInt(12, statusText);
                    connect.pstat.setBigDecimal(13, hargaTotal);




                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Pemesanan berhasil disimpan");
                    connect.pstat.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }

                try {
                    // Ambil id_kelas untuk kursi yang dipesan
                    String idKelas = getIdKelas(selectedKursi);

                    // Lakukan update pada kapasitas_kursi
                    String updateQuery = "UPDATE [dbo].[DetilKursi] " +
                            "SET kapasitas_kursi = kapasitas_kursi - ? " +
                            "WHERE id_pesawat = ? AND id_kelas = ?";
                    PreparedStatement updateStat = connect.conn.prepareStatement(updateQuery);
                    updateStat.setInt(1, Jmlh); // Jumlah kursi yang dipesan
                    updateStat.setString(2, selectedIdPesawat);
                    updateStat.setString(3, idKelas);
                    updateStat.executeUpdate();
                    connect.conn.commit(); // Commit perubahan ke database

                    updateStat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat update kapasitas_kursi: " + ex);
                }

                halamanAwalPenumpang halamanAwalPenumpang = new halamanAwalPenumpang(username);
                halamanAwalPenumpang.setVisible(true);
                dispose();
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pemesanan.showdatapesawat();
                Pemesanan pemesanan = new Pemesanan(username);
                pemesanan.restoreTableData(modelDetail);
                pemesanan.setSelectedValues(selectedAsal, selectedTujuan, selectedDate);
                pemesanan.setSelectedDate(selectedDate); // Tambahkan ini untuk mengatur tanggal yang dipilih
                pemesanan.setPreviousSelectedKursi(previousSelectedKursi);
                pemesanan.setVisible(true);
                dispose();
            }
        });
        cekBayi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cekBayi.isSelected()) {
                    txtbayi.setEnabled(true);
                } else {
                    txtbayi.setEnabled(false);
                }
            }
        });
        cekanak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cekanak.isSelected()) {
                    txtanak.setEnabled(true);
                } else {
                    txtanak.setEnabled(false);
                }
            }
        });
        cekdewasa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cekdewasa.isSelected()) {
                    txtdewasa.setEnabled(true);
                } else {
                    txtdewasa.setEnabled(false);
                }
            }
        });
        txtbayi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungJumlah();
            }
        });
        txtanak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungJumlah();
            }
        });
        txtdewasa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungJumlah();
            }
        });
    }

    public void FrameConfig() {
        add(DetailP);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private String getFormattedDate(String dateTimeString) {
        String formattedDate = "";

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = sourceFormat.parse(dateTimeString);
            formattedDate = targetFormat.format(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return formattedDate;
    }

    public void addRowToDetailTable(String idJadwal, String kodeRute, String kodePesawat, String waktuBerangkat,
                                    String waktuSampai) {
        modelDetail.addRow(new Object[]{idJadwal, kodeRute, kodePesawat, waktuBerangkat, waktuSampai, status});
    }


    public void addKolom(){
        modelDetail.addColumn("ID");
        modelDetail.addColumn("Nama Rute");
        modelDetail.addColumn("Nama Pesawat");
        modelDetail.addColumn("Waktu Berangkat");
        modelDetail.addColumn("Waktu Sampai");
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
    public void autoid(){
        try {
            String sql = "SELECT MAX(id_pemesanan) FROM [dbo].[Pemesanan]";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()){
                String maxID = connect.result.getString(1);
                if (maxID != null){
                    int num = Integer.parseInt(maxID.substring(3)) + 1;
                    String formattedNumber = String.format("%03d", num);
                    txtid.setText("PSN" + formattedNumber);
                }else {
                    txtid.setText("PSN001");
                }
            }

            txtid.setEditable(false);
            connect.pstat.close();
            connect.result.close();
        }
        catch (Exception ex){
            System.out.println("Error "+ex);
        }
    }

    public void tglpesan(){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(dateFormatter);
        txtTglPesan.setText(formattedDate);
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

        private void hitungTotal() {
            try {
                // Mendapatkan nilai jumlah dari txtJumlah
                int jumlah = Integer.parseInt(txtJumlah.getText());

                // Menghitung total
                int total = (hargaKursi + hargaRute) * jumlah;

                // Mengurangi harga jika bayi diinput
                if (!txtbayi.getText().isEmpty()) {
                    bayi = Integer.parseInt(txtbayi.getText());
                    double diskon = 0.8; // Mengurangi 80% dari harga rute ditambah harga kursi
                    int hargaBayi = (int) (diskon * (hargaRute + hargaKursi) * bayi);
                    total -= hargaBayi;
                }

                if (!txtanak.getText().isEmpty()) {
                    anak = Integer.parseInt(txtanak.getText());
                    double diskon = 0.1; // Mengurangi 80% dari harga rute ditambah harga kursi
                    int hargaanak = (int) (diskon * (hargaRute + hargaKursi) * anak);
                    total -= hargaanak;
                }

                // Convert total to BigDecimal with three decimal places
                BigDecimal totalDecimal = new BigDecimal(total);

                // Format total as a string with the pattern "#,###.###"
                DecimalFormat decimalFormat = new DecimalFormat("#,###.###");
                String formattedTotal = decimalFormat.format(totalDecimal);

                // Ganti tanda koma (,) dengan tanda titik (.)
                formattedTotal = formattedTotal.replace(",", ".");

                // Menampilkan nilai total di txttotal
                txttotal.setText(formattedTotal);
            } catch (NumberFormatException ex) {
                // Menangani kesalahan jika input tidak valid
                System.out.println("Error: Invalid input for jumlah");
                ex.printStackTrace();
            }
        }

    public int getHargaKursi(String selectedIdPesawat, String selectedKursi) {
        int harga = 0;

        try {
            String query = "SELECT dk.harga_kursi " +
                    "FROM [dbo].[DetilKursi] dk " +
                    "JOIN [dbo].[Kelas_Kursi] k ON dk.id_kelas = k.id_kelas " +
                    "WHERE dk.id_pesawat = ? AND k.nama_kelas = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, selectedIdPesawat);
            pstat.setString(2, selectedKursi);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                harga = result.getInt("harga_kursi");
            }

            pstat.close();
            result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat mendapatkan harga kursi: " + ex);
        }

        return harga;
    }


    public int getHargaRute(String kodeRute) {
        int harga = 0;

        try {
            String query = "SELECT harga_rute FROM [dbo].[Rute] WHERE kode_rute = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, kodeRute);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                harga = result.getInt("harga_rute");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return harga;
    }

    public String getIdKelas(String namaKelas) {
        String idKelas = "";

        try {
            String query = "SELECT id_kelas FROM [dbo].[Kelas_Kursi] WHERE nama_kelas = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, namaKelas);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                idKelas = result.getString("id_kelas");
            }

            pstat.close();
            result.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }

        return idKelas;
    }

    public String getSelectedAsal() {
        return selectedAsal;
    }

    public String getSelectedTujuan() {
        return selectedTujuan;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    private void hitungJumlah() {
        bayi = txtbayi.getText().isEmpty() ? 0 : Integer.parseInt(txtbayi.getText());
        anak = txtanak.getText().isEmpty() ? 0 : Integer.parseInt(txtanak.getText());
        dewasa = txtdewasa.getText().isEmpty() ? 0 : Integer.parseInt(txtdewasa.getText());

        int jumlah = bayi + anak + dewasa;

        txtJumlah.setText(String.valueOf(jumlah));

        hitungTotal();
    }

    public void setPreviousSelectedKursi(String kursi) {
        previousSelectedKursi = kursi;
    }

    public int getKapasitasKursi(String idPesawat, String idKelas) {
        int kapasitas = 0;

        try {
            String query = "SELECT kapasitas_kursi FROM [dbo].[DetilKursi] WHERE id_pesawat = ? AND id_kelas = ?";
            PreparedStatement pstat = connect.conn.prepareStatement(query);
            pstat.setString(1, idPesawat);
            pstat.setString(2, idKelas);
            ResultSet result = pstat.executeQuery();

            if (result.next()) {
                kapasitas = result.getInt("kapasitas_kursi");
            }

            pstat.close();
            result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat mendapatkan kapasitas kursi: " + ex);
        }

        return kapasitas;
    }

}

