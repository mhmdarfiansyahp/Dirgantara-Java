package Dirgantara.MasterPesawat;

import Connect.Connect;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class UpdatePesawat extends  JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JButton btnUbah;
    private JTextField txtId;
    private JTextField txtKode;
    private JTextField txtKapasitas;
    private JTextField txtStatus;
    private JTable tbPesawat;
    private JButton btnCari;
    private JTextField txtCariID;
    private JButton btnSemua;
    private JPanel updateP;
    private JComboBox cbidkelas;
    private JTextField txtkapasitaskursi;
    private JTextField txthargakursi;
    private JTextField txtstatusKursi;
    private JButton btnhapusDetail;
    private JButton btnubahdatail;
    private JButton btncleardetail;
    private JTextField txtmaskapai;
    private JTextField txtNamapesawat;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();


    String id, kode, status, idmaskapai,idkursi, namapesawat,kapasitasKursiText,hargaKursiText,statusKursi;
    int statusText, kapasitas, statuskursi,kapasitaskursi, hargakursi;

    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public UpdatePesawat(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        modelmodel = new DefaultTableModel();

        addKolom();
        tbPesawat.setModel(modelmodel);
        txtId.setEditable(false);
        //maskapai();
        detailkursi();
        cbidkelas.setSelectedIndex(-1);

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idmaskapai = "";
                id = txtId.getText();
                kode = txtKode.getText();
                namapesawat = txtNamapesawat.getText();
                String kapasitasText = txtKapasitas.getText();
                status = txtStatus.getText();

                if (txtKode.getText().isEmpty() || txtKapasitas.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(updateP, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    kapasitas = Integer.parseInt(kapasitasText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(updateP, "Kapasitas harus berupa angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_maskapai FROM [dbo].[Maskapai] WHERE nama_maskapai = '" + txtmaskapai.getText() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idmaskapai = connect.result.getString("id_maskapai");
                    }
                    connect.result.close();
                    connect.stat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }


                try {
                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }

                    String selectQuery = "SELECT kode_pesawat, id_maskapai, kapasitas, status FROM Pesawat WHERE id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(selectQuery);
                    connect.pstat.setString(1, id);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        String existingKode = connect.result.getString("kode_pesawat");
                        String existingIdMaskapai = connect.result.getString("id_maskapai");
                        int existingKapasitas = connect.result.getInt("kapasitas");
                        int existingStatus = connect.result.getInt("status");

                        if (existingKode.equals(kode) && existingIdMaskapai.equals(idmaskapai) && existingKapasitas == kapasitas && existingStatus == statusText) {
                            JOptionPane.showMessageDialog(null, "Data sudah ada dan tidak boleh sama", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();

                    String query = "UPDATE [dbo].[Pesawat] SET kode_pesawat=?,nama_pesawat=?, id_maskapai=?," +
                            "kapasitas =?, status=? WHERE id_pesawat=?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, kode);
                    connect.pstat.setString(2, namapesawat);
                    connect.pstat.setString(3,idmaskapai);
                    connect.pstat.setInt(4, kapasitas);
                    connect.pstat.setInt(5, statusText);
                    connect.pstat.setString(6, id);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Data Pesawat berhasil diubah");
                    connect.pstat.close();


                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }
                //loaddata();
            }
        });
        tbPesawat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbPesawat.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtId.setText((String) modelmodel.getValueAt(i,0));
                txtNamapesawat.setText(String.valueOf(modelmodel.getValueAt(i, 2)));
                txtKapasitas.setText(String.valueOf(modelmodel.getValueAt(i, 4)));
                txtKode.setText((String) modelmodel.getValueAt(i,1));
                txtkapasitaskursi.setText(String.valueOf(modelmodel.getValueAt(i, 7)));
                String hargaKursiText = (String) modelmodel.getValueAt(i, 8);
                hargaKursiText = hargaKursiText.replaceAll(",", ""); // Menghapus koma dari angka
                int hargaKursi = Integer.parseInt(hargaKursiText);
                txthargakursi.setText(hargaKursiText);
                statuskursi = (int) modelmodel.getValueAt(i,9);
                if (statuskursi == 1){
                    txtstatusKursi.setText("Terisi");
                }else if (statuskursi == 0){
                    txtstatusKursi.setText("Kosong");
                }

                statusText = (int) modelmodel.getValueAt(i,5);
                if (statusText == 1){
                    txtStatus.setText("Tersedia");
                }else if (statusText == 0){
                    txtStatus.setText("Tidak Tersedia");
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT nama_maskapai FROM [dbo].[Maskapai] WHERE nama_maskapai = '" + (String) modelmodel.getValueAt(i, 3) + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idmaskapai = connect.result.getString("nama_maskapai");
                    }
                    txtmaskapai.setText(idmaskapai);
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    idkursi = (String) modelmodel.getValueAt(i, 6);
                    String sql1 = "select nama_kelas from [dbo].[Kelas_Kursi] where nama_kelas = '" + idkursi + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        String namakelas = (String) connect.result.getString("nama_kelas");
                        cbidkelas.setSelectedItem(namakelas);
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                txtStatus.setEditable(true);
            }
        });
        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtCariID.getText();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nama harus diisi", "Validasi", JOptionPane.WARNING_MESSAGE);
                } else {
                    showData();
                }
            }
        });
        btnSemua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loaddata();
            }
        });
//        btnHapus.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String idpesawat = txtId.getText();
//                if (idpesawat.isEmpty()) {
//                    JOptionPane.showMessageDialog(null, "Harus memilih data Pesawat terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
//                    return;
//                }
//
//                try {
//                    String checkQuery = "SELECT status FROM [dbo].[Pesawat] WHERE id_pesawat = ?";
//                    connect.pstat = connect.conn.prepareStatement(checkQuery);
//                    connect.pstat.setString(1, idpesawat);
//                    connect.result = connect.pstat.executeQuery();
//
//                    if (connect.result.next()) {
//                        int status = connect.result.getInt("status");
//                        if (status == 0) {
//                            JOptionPane.showMessageDialog(updateP, "Data Pesawat sudah tidak aktif", "Peringatan", JOptionPane.WARNING_MESSAGE);
//                            connect.result.close();
//                            connect.pstat.close();
//                            return;
//                        }
//                    }
//
//                    connect.result.close();
//                    connect.pstat.close();
//                } catch (SQLException ex) {
//                    System.out.println("Terjadi error saat memeriksa status Pesawat: " + ex);
//                }
//
//                int selectedOption = JOptionPane.showConfirmDialog(updateP, "Apakah Anda yakin ingin mengubah data Pesawat menjadi tidak tersedia?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
//                if (selectedOption == JOptionPane.YES_OPTION) {
//                    try {
//                        String query = "UPDATE [dbo].[Pesawat] SET status = 0 WHERE id_pesawat = ?";
//                        connect.pstat = connect.conn.prepareStatement(query);
//                        connect.pstat.setString(1, idpesawat);
//                        int rowsUpdated = connect.pstat.executeUpdate();
//
//                        if (rowsUpdated > 0) {
//                            JOptionPane.showMessageDialog(updateP, "Pesawat berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
//                        } else {
//                            JOptionPane.showMessageDialog(updateP, "Gagal menghapus Pesawat", "Peringatan", JOptionPane.WARNING_MESSAGE);
//                        }
//
//                        connect.pstat.close();
//                    } catch (SQLException ex) {
//                        System.out.println("Terjadi error saat menghapus data Pesawat: " + ex);
//                    }
//                    loaddata();
//                    clear2();
//                    clear();
//                }
//            }
//        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuPesawat menuPesawat = new MenuPesawat(username, nama_akses);
                menuPesawat.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(UpdatePesawat.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnubahdatail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idPesawat = txtId.getText();
                String namaKelas = cbidkelas.getSelectedItem().toString();
                String kapasitasKursiText = txtkapasitaskursi.getText();
                String hargaKursiText = txthargakursi.getText();
                String statusKursi = txtstatusKursi.getText();

                if (kapasitasKursiText.isEmpty() || hargaKursiText.isEmpty() || statusKursi.isEmpty()) {
                    JOptionPane.showMessageDialog(UpdatePesawat.this, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int kapasitasKursi;
                int hargaKursi;
                int statusKursiInt;


                if (statusKursi.equalsIgnoreCase("Terisi")) {
                    statusKursiInt = 1;
                } else if (statusKursi.equalsIgnoreCase("Kosong")) {
                    statusKursiInt = 0;
                } else {
                    JOptionPane.showMessageDialog(UpdatePesawat.this, "Status kursi harus diisi dengan 'Terisi' atau 'Kosong'", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    try {
                        kapasitasKursi = Integer.parseInt(kapasitasKursiText);
                        hargaKursi = Integer.parseInt(hargaKursiText);
                        //statusKursiInt = statusKursi.equalsIgnoreCase("Terisi") ? 1 : 0;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(UpdatePesawat.this, "Kapasitas kursi dan harga kursi harus berupa angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_kelas FROM [dbo].[Kelas_Kursi] WHERE nama_kelas = '" + namaKelas + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    String idKelas = "";
                    if (connect.result.next()) {
                        idKelas = connect.result.getString("id_kelas");
                    }
                    connect.stat.close();
                    connect.result.close();

                    connect.stat = connect.conn.createStatement();
                    String selectQuery = "SELECT id_kelas, kapasitas_kursi, harga_kursi, status_kursi FROM DetilKursi WHERE id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(selectQuery);
                    connect.pstat.setString(1, idPesawat);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        String existingIdKelas = connect.result.getString("id_kelas");
                        int existingKapasitasKursi = connect.result.getInt("kapasitas_kursi");
                        int existingHargaKursi = connect.result.getInt("harga_kursi");
                        int existingStatusKursi = connect.result.getInt("status_kursi");

                        if (existingIdKelas.equals(idKelas) && existingKapasitasKursi == kapasitasKursi && existingHargaKursi == hargaKursi && existingStatusKursi == statusKursiInt) {
                            JOptionPane.showMessageDialog(null, "Data sudah ada dan tidak boleh sama", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }

                    }

                    connect.stat = connect.conn.createStatement();
                    String query = "SELECT kapasitas FROM [dbo].[Pesawat] WHERE id_pesawat = '" + idPesawat + "'";
                    connect.result = connect.stat.executeQuery(query);
                    int kapasitasMaksimal = 0;

                    if (connect.result.next()) {
                        kapasitasMaksimal = connect.result.getInt("kapasitas");
                    }
                    connect.result.close();

                    if (kapasitasKursi >= kapasitasMaksimal || kapasitasKursi < (kapasitasMaksimal - 10)) {
                        JOptionPane.showMessageDialog(UpdatePesawat.this, "Kapasitas kursi melebihi batas atau kurang dari batas maksimal yang diperbolehkan", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    query = "UPDATE [dbo].[DetilKursi] SET id_kelas = ?, kapasitas_kursi = ?, harga_kursi = ?, status_kursi = ? WHERE id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, idKelas);
                    connect.pstat.setInt(2, kapasitasKursi);
                    connect.pstat.setInt(3, hargaKursi);
                    connect.pstat.setInt(4, statusKursiInt);
                    connect.pstat.setString(5, idPesawat);

                    int rowsAffected = connect.pstat.executeUpdate();
                    connect.conn.commit();
                    connect.pstat.close();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(UpdatePesawat.this, "Data detail kursi berhasil diperbarui");
                    } else {
                        JOptionPane.showMessageDialog(UpdatePesawat.this, "Gagal memperbarui data detail kursi");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(UpdatePesawat.this, "Terjadi error saat memperbarui data detail kursi: " + ex);
                }
                clear();
                clear2();
                loaddata();
            }
        });

        btnhapusDetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idpesawat = txtId.getText();
                if (idpesawat.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Harus memilih data Detail Kursi terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String checkPesawatQuery = "SELECT status FROM [dbo].[Pesawat] WHERE id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(checkPesawatQuery);
                    connect.pstat.setString(1, idpesawat);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int statusPesawat = connect.result.getInt("status");
                        if (statusPesawat == 0) {
                            JOptionPane.showMessageDialog(updateP, "Data Pesawat sudah tidak Tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa status Pesawat: " + ex);
                }

                try {
                    String checkQuery = "SELECT status_kursi FROM [dbo].[DetilKursi] WHERE id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, idpesawat);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int status = connect.result.getInt("status_kursi");
                        if (status == 1) {
                            JOptionPane.showMessageDialog(updateP, "Data Detail Kursi sudah Terisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa status Detail Kursi: " + ex);
                }

                int selectedOption = JOptionPane.showConfirmDialog(updateP, "Apakah Anda yakin ingin mengubah data Rute menjadi tidak tersedia?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    try {
                        String query = "UPDATE [dbo].[DetilKursi] SET status_kursi = 1 WHERE id_pesawat = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, idpesawat);
                        int rowsUpdated = connect.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(updateP, "Detail Kursi berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(updateP, "Gagal menghapus Detail Kursi", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }

                        connect.pstat.close();
                    } catch (SQLException ex) {
                        System.out.println("Terjadi error saat menghapus data Pesawat: " + ex);
                    }

                    // Mengubah status Pesawat menjadi Tidak Tersedia
                    try {
                        String updatePesawatQuery = "UPDATE [dbo].[Pesawat] SET status = 0 WHERE id_pesawat = ?";
                        connect.pstat = connect.conn.prepareStatement(updatePesawatQuery);
                        connect.pstat.setString(1, idpesawat);
                        int rowsUpdated = connect.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(updateP, "Status Pesawat berhasil diubah", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(updateP, "Gagal mengubah status Pesawat", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }

                        connect.pstat.close();
                    } catch (SQLException ex) {
                        System.out.println("Terjadi error saat mengubah status Pesawat: " + ex);
                    }
                    loaddata();
                    clear();
                    clear2();
                }
            }
        });

        btncleardetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                clear2();
            }
        });
    }

    public void FrameConfig() {
        add(updateP);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void clear(){
        txtKode.setText("");
        txtKapasitas.setText("");
        txtmaskapai.setText("");
        txtNamapesawat.setText("");
        txtId.setText("");
        txtStatus.setText("");
        txtCariID.setText("");
    }

    public void clear2(){
        cbidkelas.setSelectedIndex(-1);
        txtkapasitaskursi.setText("");
        txthargakursi.setText("");
        txtstatusKursi.setText("");
    }

    public void addKolom(){
        modelmodel.addColumn("ID");
        modelmodel.addColumn("Kode Pesawat");
        modelmodel.addColumn("Nama Pesawat");
        modelmodel.addColumn("Nama Maskapai");
        modelmodel.addColumn("Kapasitas Pesawat");
        modelmodel.addColumn("Status");
        modelmodel.addColumn("ID Kelas");
        modelmodel.addColumn("Kapasitas Kursi");
        modelmodel.addColumn("Harga");
        modelmodel.addColumn("Status Kursi");
    }

    public void loaddata(){
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        Thread thread = new Thread(() -> {
            try {
                connect.stat = connect.conn.createStatement();
                String query = "EXEC sp_viewPesawatKursi";
                connect.result = connect.stat.executeQuery(query);

                while (connect.result.next()) {
                    Object[] obj = new Object[10];

                    obj[0] = connect.result.getString("id_pesawat");
                    obj[1] = connect.result.getString("kode_pesawat");
                    obj[2] = connect.result.getString("nama_pesawat");
                    obj[3] = connect.result.getString("nama_maskapai");
                    obj[4] = connect.result.getInt("kapasitas");
                    obj[5] = connect.result.getInt("status");
                    obj[6] = connect.result.getString("nama_kelas");
                    obj[7] = connect.result.getInt("kapasitas_kursi");
                    int hargaKursi = connect.result.getInt("harga_kursi");
                    obj[8] = numberFormat.format(hargaKursi);
                    obj[9] = connect.result.getInt("status_kursi");

                    SwingUtilities.invokeLater(() -> modelmodel.addRow(obj));

                    Thread.sleep(100);
                }
                connect.stat.close();
                connect.result.close();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat load data Pesawat: " + ex);
            }
        });

        thread.start();
    }

    public void showData() {
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();

            String query = "SELECT P.id_pesawat, P.kode_pesawat, P.nama_pesawat, P.id_maskapai, P.kapasitas, P.status, D.id_kelas, D.kapasitas_kursi, D.harga_kursi, D.status_kursi " +
                    "FROM [dbo].[Pesawat] P " +
                    "INNER JOIN [dbo].[DetilKursi] D ON P.id_pesawat = D.id_pesawat " +
                    "WHERE P.id_pesawat LIKE '%" + txtCariID.getText() + "%' OR D.id_pesawat LIKE '%" + txtCariID.getText() + "%'";

            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connect.result.getString("id_pesawat");
                obj[1] = connect.result.getString("kode_pesawat");
                obj[2] = connect.result.getString("nama_pesawat");
                obj[3] = connect.result.getString("nama_maskapai");
                obj[4] = connect.result.getInt("kapasitas");
                obj[5] = connect.result.getInt("status");
                obj[6] = connect.result.getString("nama_kelas");
                obj[7] = connect.result.getInt("kapasitas_kursi");
                obj[8] = connect.result.getInt("harga_kursi");
                obj[9] = connect.result.getInt("status_kursi");
                modelmodel.addRow(obj);
            }

            if (modelmodel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
            }

            connect.stat.close();
            connect.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi eror saat load data: " + e);
        }
    }



    public void maskapai() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_maskapai, nama_maskapai from [dbo].[Maskapai]";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                String namaMaskapai = connect.result.getString("nama_maskapai");
                txtmaskapai.setText(namaMaskapai);
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Maskapai" + ex);
        }
    }

    public void detailkursi() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_kelas, nama_kelas from [dbo].[Kelas_Kursi]";
            connect.result = connect.stat.executeQuery(sql);

            cbidkelas.removeAllItems(); // Menghapus item yang ada sebelumnya

            while (connect.result.next()) {
                cbidkelas.addItem(connect.result.getString("nama_kelas"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data kelas kursi" + ex);
        }
    }

}
