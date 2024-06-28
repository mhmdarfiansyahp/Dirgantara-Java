package Dirgantara.Rute;

import Connect.Connect;
import Dirgantara.MasterPesawat.MenuPesawat;
import Dirgantara.MenuAwal;
import Dirgantara.Transaksi.Riwayat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class CreateRute extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtid;
    private JTextField txtKode;
    private JTextField txtasal;
    private JButton btnidbaru;
    private JTextField txtStatus;
    private JButton btnSimpan;
    private JButton btnClear;
    private JTable tbRute;
    private JTextField txtnamapesawat;
    private JComboBox cbidpesawat;
    private JButton btndetail;
    private JButton btnclearkursi;
    private JPanel Createrute;
    private JTextField txtTujuan;
    private JTextField txtHarga;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();

    String id, kode, status, tujuan, asal, harga, namaPesawat, idpesawat;
    int statusText, hargatxt,statusharga;

    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public CreateRute(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);

        modelmodel = new DefaultTableModel();
        addKolom();
        tbRute.setModel(modelmodel);
        loaddata();

        pesawat();
        cbidpesawat.setSelectedIndex(-1);

        btnidbaru.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoid();
            }
        });
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtid.getText();
                kode = txtKode.getText();
                tujuan = txtTujuan.getText();
                asal = txtasal.getText();
                status = txtStatus.getText();
                harga = txtHarga.getText();

                if (txtKode.getText().isEmpty() || txtHarga.getText().isEmpty() || txtTujuan.getText().isEmpty() || txtasal.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(Createrute, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    hargatxt = Integer.parseInt(harga);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Createrute, "Harga kursi harus berupa angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }

                    String checkQuery = "SELECT COUNT(*) FROM [dbo].[Rute] WHERE kode_rute = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, kode);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int count = connect.result.getInt(1);
                        if (count > 0) {
                            JOptionPane.showMessageDialog(Createrute, "Data rute sudah ada dalam database", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[Rute] VALUES (?,?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, kode);
                    connect.pstat.setString(3,tujuan);
                    connect.pstat.setString(4, asal);
                    connect.pstat.setInt(5,hargatxt);
                    connect.pstat.setInt(6, statusText);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Rute berhasil ditambahkan");
                    connect.pstat.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }
                loaddata();
            }
        });
        tbRute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbRute.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtid.setText((String) modelmodel.getValueAt(i,0));
                txtKode.setText((String) modelmodel.getValueAt(i,1));
                txtTujuan.setText((String) modelmodel.getValueAt(i,2));
                txtasal.setText((String) modelmodel.getValueAt(i,3));
                String hargaPesawatText = (String) modelmodel.getValueAt(i, 4);
                if (hargaPesawatText.contains(",")) {
                    hargaPesawatText = hargaPesawatText.replaceAll("[,\\s]", "");
                }
                int hargaKursi = Integer.parseInt(hargaPesawatText);
                txtHarga.setText(String.valueOf(hargaKursi));
                statusharga = (int) modelmodel.getValueAt(i,5);
                if (statusharga == 1){
                    txtStatus.setText("Tersedia");
                }else if (statusharga == 0){
                    txtStatus.setText("Tidak Tersedia");
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

                txtid.setEnabled(false);
                txtKode.setEnabled(false);
                txtasal.setEnabled(false);
                txtTujuan.setEnabled(false);
                txtHarga.setEnabled(false);
                txtStatus.setEnabled(false);

            }
        });
        cbidpesawat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btndetail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbidpesawat.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(Createrute, "Pilih pesawat terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                idpesawat = cbidpesawat.getSelectedItem().toString();

                int selectedRow = tbRute.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(Createrute, "Pilih baris data rute terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    clear2();
                    return;
                }

                if (txtid.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(Createrute, "ID Rute harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String idRute = txtid.getText();

                    String checkQuery = "SELECT COUNT(*) FROM [dbo].[DetilRute] WHERE id_pesawat = ? AND id_rute = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, idpesawat);
                    connect.pstat.setString(2, idRute);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int count = connect.result.getInt(1);
                        if (count > 0) {
                            JOptionPane.showMessageDialog(Createrute, "Data detail rute sudah ada dalam database", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    connect.stat = connect.conn.createStatement();
                    String updateQuery = "INSERT INTO [dbo].[DetilRute] values (?,?)";
                    connect.pstat = connect.conn.prepareStatement(updateQuery);
                    connect.pstat.setString(1, cbidpesawat.getSelectedItem().toString());
                    connect.pstat.setString(2, txtid.getText());
                    int rowsAffected = connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Detail Rute berhasil diSimpan");
                    connect.pstat.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat Menyimpan Detail Rute: " + ex);
                }
                clear();
                clear2();
                loaddata();
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        btnclearkursi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear2();
                clear();
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
                int option = JOptionPane.showConfirmDialog(CreateRute.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        cbidpesawat.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedId = (String) cbidpesawat.getSelectedItem();
                if (selectedId != null) {
                    String selectedNamaPesawat = kodePesawat(selectedId);
                    txtnamapesawat.setText(selectedNamaPesawat);
                }
            }
        });
    }

    public void FrameConfig() {
        add(Createrute);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void autoid(){
        try {
            String sql = "SELECT MAX(id_rute) FROM [dbo].[Rute]";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()){
                String maxID = connect.result.getString(1);
                if (maxID != null){
                    int num = Integer.parseInt(maxID.substring(3)) + 1;
                    String formattedNumber = String.format("%03d", num);
                    txtid.setText("RT" + formattedNumber);
                }else {
                    txtid.setText("RT001");
                }
            }

            txtStatus.setText("Tersedia");
            txtid.setEditable(false);
            txtStatus.setEnabled(false);
            connect.pstat.close();
            connect.result.close();
        }
        catch (Exception ex){
            System.out.println("Error "+ex);
        }
    }

    public void pesawat() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_pesawat, nama_pesawat FROM [dbo].[Pesawat] WHERE status = 1";
            connect.result = connect.stat.executeQuery(sql);

            cbidpesawat.removeAllItems(); // Clear existing items

            while (connect.result.next()) {
                String idPesawat = connect.result.getString("id_pesawat");
                String namaPesawat = connect.result.getString("nama_pesawat");
                cbidpesawat.addItem(idPesawat); // Add id_pesawat to ComboBox
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Pesawat" + ex);
        }
    }


    public void clear2(){
        cbidpesawat.setSelectedIndex(-1);
        txtnamapesawat.setText("");
    }

    public void clear(){
        txtid.setText("");
        txtKode.setText("");
        txtasal.setText("");
        txtTujuan.setText("");
        txtHarga.setText("");
        txtStatus.setText("");
        txtid.setEnabled(true);
        txtKode.setEnabled(true);
        txtasal.setEnabled(true);
        txtTujuan.setEnabled(true);
        txtHarga.setEnabled(true);
        txtStatus.setEnabled(true);

    }

    public void addKolom(){
        modelmodel.addColumn("ID");
        modelmodel.addColumn("Kode Rute");
        modelmodel.addColumn("Tujuan");
        modelmodel.addColumn("Asal");
        modelmodel.addColumn("Harga");
        modelmodel.addColumn("Status");
        modelmodel.addColumn("Nama Pesawat");
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

}
