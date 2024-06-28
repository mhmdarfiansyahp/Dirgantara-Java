package Dirgantara.Rute;

import Connect.Connect;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class UpdateDeleteRute extends JFrame {
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JPanel UpdateRute;
    private JButton btnUbah;
    private JTextField txtId;
    private JTextField txtasal;
    private JTextField txttujuan;
    private JComboBox cbidpesawat;
    private JTextField txtnamapesawat;
    private JButton btnhapusDetail;
    private JButton btnubahdatail;
    private JButton btncleardetail;
    private JTable tbRute;
    private JButton btnCari;
    private JTextField txtCariID;
    private JButton btnSemua;
    private JTextField txtkode;
    private JTextField txtHarga;
    private JTextField txtstatus;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();

    String id, kode, status, tujuan, asal, harga, namaPesawat, idpesawat;
    int statusText, hargatxt,statusharga;

    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public UpdateDeleteRute(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        modelmodel = new DefaultTableModel();
        addKolom();

        tbRute.setModel(modelmodel);
        pesawat();
        cbidpesawat.setSelectedIndex(-1);

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtId.getText();
                kode = txtkode.getText();
                tujuan = txttujuan.getText();
                asal = txtasal.getText();
                status = txtstatus.getText();
                harga = txtHarga.getText();

                if (txtkode.getText().isEmpty() || txtHarga.getText().isEmpty() || txttujuan.getText().isEmpty() || txtasal.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(UpdateRute, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    hargatxt = Integer.parseInt(harga);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UpdateRute, "Harga kursi harus berupa angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (status.equalsIgnoreCase("Tersedia")) {
                    statusText = 1;
                } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                    statusText = 0;
                } else {
                    JOptionPane.showMessageDialog(UpdateRute, "Status tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String query = "SELECT * FROM [dbo].[Rute] WHERE [id_rute] = ?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        String oldKode = connect.result.getString("kode_rute");
                        String oldTujuan = connect.result.getString("daerah_tujuan");
                        String oldAsal = connect.result.getString("daerah_asal");
                        int oldHarga = connect.result.getInt("harga_rute");
                        int oldStatus = connect.result.getInt("status");

                        if (oldKode.equals(kode) && oldTujuan.equals(tujuan) && oldAsal.equals(asal)
                                && oldHarga == hargatxt && oldStatus == statusText) {
                            JOptionPane.showMessageDialog(UpdateRute, "Tidak ada data yang berbeda dengan data sebelumnya", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(UpdateRute, "Data tidak ditemukan", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    connect.stat = connect.conn.createStatement();
                    query = "UPDATE [dbo].[Rute] SET kode_rute=?, daerah_tujuan=?," +
                            "daerah_asal =?, harga_rute=?, status=? WHERE [id_rute]=?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, kode);
                    connect.pstat.setString(2, tujuan);
                    connect.pstat.setString(3, asal);
                    connect.pstat.setInt(4, hargatxt);
                    connect.pstat.setInt(5, statusText);
                    connect.pstat.setString(6, id);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Rute berhasil diubah");
                    connect.pstat.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat mengubah data: " + ex);
                }
                loaddata();
            }
        });
        tbRute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbRute.getSelectedRow();
                if (i == -1) {
                    return;
                }
                txtId.setText((String) modelmodel.getValueAt(i, 0));
                txtkode.setText((String) modelmodel.getValueAt(i, 1));
                txttujuan.setText((String) modelmodel.getValueAt(i, 2));
                txtasal.setText((String) modelmodel.getValueAt(i, 3));
                String hargaPesawatText = (String) modelmodel.getValueAt(i, 4);
                if (hargaPesawatText.contains(",")) {
                    hargaPesawatText = hargaPesawatText.replaceAll("[,\\s]", "");
                }
                int hargaKursi = Integer.parseInt(hargaPesawatText);
                txtHarga.setText(String.valueOf(hargaKursi));
                statusharga = (int) modelmodel.getValueAt(i, 5);
                if (statusharga == 1) {
                    txtstatus.setText("Tersedia");
                } else if (statusharga == 0) {
                    txtstatus.setText("Tidak Tersedia");
                }

                idpesawat = (String) modelmodel.getValueAt(i, 6); // Get the id_pesawat from the table
                try {
                    connect.stat = connect.conn.createStatement();
                    String sql = "SELECT id_pesawat, nama_pesawat FROM [dbo].[Pesawat] WHERE nama_pesawat = ?";
                    PreparedStatement pstmt = connect.conn.prepareStatement(sql);
                    pstmt.setString(1, idpesawat);
                    connect.result = pstmt.executeQuery();

                    while (connect.result.next()) {
                        idpesawat = (String) connect.result.getString("id_pesawat");
                        namaPesawat = (String) connect.result.getString("nama_pesawat");
                    }

                    cbidpesawat.setSelectedItem(idpesawat); // Select the id_pesawat in the ComboBox
                    txtnamapesawat.setText(namaPesawat);
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat mendapatkan Kode pesawat: " + ex);
                }
            }
        });
        btnSemua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loaddata();
            }
        });
        cbidpesawat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedId = (String) cbidpesawat.getSelectedItem();
                if (selectedId != null) {
                    String selectedNamaPesawat = kodePesawat(selectedId);
                    txtnamapesawat.setText(selectedNamaPesawat);
                }
            }
        });
        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showData();
            }
        });
        btnubahdatail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tbRute.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(UpdateRute, "Pilih baris data rute terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String idRute = txtId.getText();
                    String idPesawat = cbidpesawat.getSelectedItem().toString();

                    // Query untuk mendapatkan data detil rute sebelumnya
                    String selectQuery = "SELECT id_pesawat FROM [dbo].[DetilRute] WHERE id_rute = ?";
                    connect.pstat = connect.conn.prepareStatement(selectQuery);
                    connect.pstat.setString(1, idRute);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        String previousIdPesawat = connect.result.getString("id_pesawat");
                        if (previousIdPesawat.equals(idPesawat)) {
                            JOptionPane.showMessageDialog(UpdateRute, "Tidak ada perubahan pada data detail rute", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    connect.stat = connect.conn.createStatement();
                    String updateQuery = "UPDATE [dbo].[DetilRute] SET id_pesawat = ? WHERE id_rute = ?";
                    connect.pstat = connect.conn.prepareStatement(updateQuery);
                    connect.pstat.setString(1, idPesawat);
                    connect.pstat.setString(2, idRute);
                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Detail Rute berhasil diperbarui");
                    connect.pstat.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat memperbarui Detail Rute: " + ex);
                }
                loaddata();
                clear();
                clear2();
            }
        });
        btncleardetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                clear2();
            }
        });
        btnhapusDetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idrute = txtId.getText();
                if (idrute.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Harus memilih data Rute terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String checkQuery = "SELECT status FROM [dbo].[Rute] WHERE id_rute = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, idrute);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int status = connect.result.getInt("status");
                        if (status == 0) {
                            JOptionPane.showMessageDialog(UpdateRute, "Data Detail Rute sudah tidak tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
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

                int selectedOption = JOptionPane.showConfirmDialog(UpdateRute, "Apakah Anda yakin ingin mengubah data Rute menjadi tidak tersedia?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    try {
                        String query = "UPDATE [dbo].[Rute] SET status = 0 WHERE id_rute = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, idrute);
                        int rowsUpdated = connect.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(UpdateRute, "Detail Rute berhasil diubah ", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(UpdateRute, "Gagal menghapus Detail Rute", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }

                        connect.pstat.close();
                    } catch (SQLException ex) {
                        System.out.println("Terjadi error saat menghapus data Rute: " + ex);
                    }
                    loaddata();
                    clear2();
                    clear();
                }
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuRute menuRute = new menuRute(username, nama_akses);
                menuRute.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(UpdateDeleteRute.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
    }

    public void FrameConfig() {
        add(UpdateRute);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void clear2(){
        cbidpesawat.setSelectedIndex(-1);
        txtnamapesawat.setText("");
    }

    public void clear(){
        txtId.setText("");
        txtkode.setText("");
        txtasal.setText("");
        txttujuan.setText("");
        txtHarga.setText("");
        txtstatus.setText("");
    }

    public void addKolom(){
        modelmodel.addColumn("ID");
        modelmodel.addColumn("Kode Rute");
        modelmodel.addColumn("Tujuan");
        modelmodel.addColumn("Asal");
        modelmodel.addColumn("Harga");
        modelmodel.addColumn("Status");
        modelmodel.addColumn("ID Pesawat");
    }

    public void loaddata(){
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();
            //String query = "SELECT * FROM [dbo].[Pesawat]";
            String query = "EXEC sp_viewRutePesawat";
            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()){
                Object[] obj = new Object[7];

                obj[0] = connect.result.getString("id_rute");
                obj[1] = connect.result.getString("kode_rute");
                obj[2] = connect.result.getString("daerah_tujuan");
                obj[3] = connect.result.getString("daerah_asal");
                int hargaKursi = connect.result.getInt("harga_rute");
                obj[4] = numberFormat.format(hargaKursi);
                obj[5] = connect.result.getInt("status");
                obj[6] = connect.result.getString("nama_pesawat");

                modelmodel.addRow(obj);
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception ex){
            System.out.println("Terjadi error saat load data Rute dan Detail rute: " + ex);
        }
    }

    public void pesawat() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_pesawat FROM [dbo].[Pesawat] WHERE status = 1"; // Add WHERE clause to filter by status = 1
            connect.result = connect.stat.executeQuery(sql);

            cbidpesawat.removeAllItems(); // Clear existing items

            while (connect.result.next()) {
                cbidpesawat.addItem(connect.result.getString("id_pesawat"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Pesawat" + ex);
        }
    }

    private String kodePesawat(String idPesawat) {
        namaPesawat = "";
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT nama_pesawat FROM [dbo].[Pesawat] WHERE id_pesawat = '" + idPesawat + "'";
            connect.result = connect.stat.executeQuery(sql);

            if (connect.result.next()) {
                namaPesawat = connect.result.getString("nama_pesawat");
            }

            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat mendapatkan Kode Pesawat: " + ex);
        }
        return namaPesawat;
    }

    public void showData() {
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();

            String query = "SELECT R.id_rute, R.kode_rute, R.daerah_tujuan, R.daerah_asal, R.harga_rute, R.status, D.id_pesawat " +
                    "FROM [dbo].[Rute] R " +
                    "INNER JOIN [dbo].[DetilRute] D ON R.id_rute = D.id_rute " +
                    "WHERE R.id_rute LIKE '%" + txtCariID.getText() + "%' OR D.id_rute LIKE '%" + txtCariID.getText() + "%'";

            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()) {
                Object[] obj = new Object[7];
                obj[0] = connect.result.getString("id_rute");
                obj[1] = connect.result.getString("kode_rute");
                obj[2] = connect.result.getString("daerah_tujuan");
                obj[3] = connect.result.getString("daerah_asal");
                obj[4] = connect.result.getInt("harga_rute");
                obj[5] = connect.result.getInt("status");
                obj[6] = connect.result.getString("id_pesawat");
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
}
