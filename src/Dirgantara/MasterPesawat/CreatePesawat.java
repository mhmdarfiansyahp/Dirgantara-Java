package Dirgantara.MasterPesawat;

import Connect.Connect;
import Dirgantara.MenuAwal;
import Dirgantara.Rute.UpdateDeleteRute;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class CreatePesawat extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtid;
    private JTextField txtKode;
    private JTextField txtkapasitas;
    private JTextField txtStatus;
    private JButton btnSimpan;
    private JButton btnClear;
    private JTable tbpesawat;
    private JPanel CreateP;
    private JComboBox cbmaskapai;
    private JComboBox cbidkelas;
    private JTextField txtkapasitaskursi;
    private JTextField txthargakursi;
    private JTextField txtstatusKursi;
    private JButton btnsimpankursi;
    private JButton btnidbaru;
    private JButton btnclearkursi;
    private JTextField txtNamapesawat;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();


    String id, kode, status, idmaskapai,idkursi,kapasitasKursiText,hargaKursiText,statusKursi, namapesawat;
    int statusText, kapasitas, statuskursi,kapasitaskursi, hargakursi;

    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public CreatePesawat(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);

        modelmodel = new DefaultTableModel();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        numberFormat.setMaximumFractionDigits(0);

        addKolom();
        tbpesawat.setModel(modelmodel);
        maskapai();
        detailkursi();
        loaddata();
        cbmaskapai.setSelectedIndex(-1);
        cbidkelas.setSelectedIndex(-1);
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idmaskapai = "";
                id = txtid.getText();
                kode = txtKode.getText();
                namapesawat = txtNamapesawat.getText();
                String kapasitasText = txtkapasitas.getText();
                status = txtStatus.getText();

                if (txtKode.getText().isEmpty() || txtkapasitas.getText().isEmpty() || cbmaskapai.getSelectedItem().equals(0)) {
                    JOptionPane.showMessageDialog(CreateP, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    kapasitas = Integer.parseInt(kapasitasText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CreateP, "Kapasitas harus berupa angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_maskapai FROM [dbo].[Maskapai] WHERE nama_maskapai = '" + cbmaskapai.getSelectedItem() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idmaskapai = (String) connect.result.getString("id_maskapai");
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat insert " + ex);
                }

                try {
                    connect.stat = connect.conn.createStatement();
                    String checkQuery = "SELECT COUNT(*) AS count FROM [dbo].[Pesawat] WHERE id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, id);

                    connect.result = connect.pstat.executeQuery();
                    connect.result.next();
                    int count = connect.result.getInt("count");

                    if (count > 0) {
                        JOptionPane.showMessageDialog(null, "Data dengan ID pesawat tersebut sudah ada", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[Pesawat] VALUES (?,?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, kode);
                    connect.pstat.setString(3, namapesawat);
                    connect.pstat.setString(4,idmaskapai);
                    connect.pstat.setInt(5, kapasitas);
                    connect.pstat.setInt(6, statusText);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Pesawat berhasil ditambahkan");
                    connect.pstat.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }

                loaddata();
                clear();
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
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
                int option = JOptionPane.showConfirmDialog(CreatePesawat.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        tbpesawat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbpesawat.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtid.setText((String) modelmodel.getValueAt(i,0));
                txtNamapesawat.setText(String.valueOf(modelmodel.getValueAt(i, 2)));
                txtkapasitas.setText(String.valueOf(modelmodel.getValueAt(i, 4)));
                txtKode.setText((String) modelmodel.getValueAt(i,1));
                txtkapasitaskursi.setText(String.valueOf(modelmodel.getValueAt(i, 7)));
                String hargaKursiText = (String) modelmodel.getValueAt(i, 8);
                hargaKursiText = hargaKursiText.replace(",", ""); // Menghilangkan koma
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
                    String sql1 = "select nama_maskapai from [dbo].[Maskapai] where nama_maskapai = '" + (String) modelmodel.getValueAt(i,3)+ "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idmaskapai = (String) connect.result.getString("nama_maskapai");
                    }
                    cbmaskapai.setSelectedItem(idmaskapai);
                }catch (SQLException ex) {
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

                txtid.setEnabled(false);
                txtNamapesawat.setEnabled(false);
                txtkapasitas.setEnabled(false);
                txtKode.setEnabled(false);
                txtStatus.setEnabled(false);
                cbmaskapai.setEnabled(false);
            }
        });
        btnidbaru.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtid.getText().isEmpty()) {
                    autoid();
                }
            }
        });
        btnclearkursi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear2();
                clear();
            }
        });
        btnsimpankursi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idkursi = "";
                kapasitasKursiText = txtkapasitaskursi.getText();
                hargaKursiText = txthargakursi.getText();
                statusKursi = txtstatusKursi.getText();

                if (kapasitasKursiText.isEmpty() || hargaKursiText.isEmpty() || txtstatusKursi.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(CreateP, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    kapasitaskursi = Integer.parseInt(kapasitasKursiText);
                    hargakursi = Integer.parseInt(hargaKursiText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CreateP, "Kapasitas kursi, harga kursi harus berupa angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String formattedHargaKursi = numberFormat.format(hargakursi);

                try {
                    connect.stat = connect.conn.createStatement();
                    String sql1 = "SELECT id_kelas FROM [dbo].[Kelas_Kursi] WHERE nama_kelas = '" + cbidkelas.getSelectedItem() + "'";
                    connect.result = connect.stat.executeQuery(sql1);

                    while (connect.result.next()) {
                        idkursi = connect.result.getString("id_kelas");
                    }
                    connect.stat.close();
                    connect.result.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat mengambil ID Kelas: " + ex);
                }

                try {
                    if (statusKursi.equalsIgnoreCase("Terisi")) {
                        statuskursi = 1;
                    } else if (statusKursi.equalsIgnoreCase("Kosong")) {
                        statuskursi = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "SELECT kapasitas FROM [dbo].[Pesawat] WHERE id_pesawat = '" + txtid.getText() + "'";
                    connect.result = connect.stat.executeQuery(query);
                    int kapasitasMaksimal = 0;

                    if (connect.result.next()) {
                        kapasitasMaksimal = connect.result.getInt("kapasitas");
                    }
                    connect.result.close();

                    if (kapasitaskursi >= kapasitasMaksimal || kapasitaskursi < (kapasitasMaksimal - 10)) {
                        JOptionPane.showMessageDialog(CreateP, "Kapasitas kursi melebihi batas atau kurang dari batas maksimal yang diperbolehkan", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    query = "SELECT * FROM [dbo].[DetilKursi] WHERE id_kelas = ? AND id_pesawat = ?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, idkursi);
                    connect.pstat.setString(2, txtid.getText());
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        JOptionPane.showMessageDialog(CreateP, "Data detail kursi sudah ada, tidak dapat melakukan pembaruan", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        connect.pstat.close();
                        connect.result.close();
                        return;
                    }
                    connect.result.close();
                    connect.pstat.close();

                    query = "INSERT INTO [dbo].[DetilKursi] VALUES (?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, idkursi);
                    connect.pstat.setString(2, txtid.getText());
                    connect.pstat.setInt(3, kapasitaskursi);
                    connect.pstat.setInt(4, hargakursi);
                    connect.pstat.setInt(5, statuskursi);

                    int rowsAffected = connect.pstat.executeUpdate();
                    connect.conn.commit();
                    connect.pstat.close();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Data detail kursi berhasil disimpan");
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan data detail kursi");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat menyimpan data detail kursi: " + ex);
                }

                clear();
                clear2();
                loaddata();
            }
        });
    }

    public void FrameConfig() {
        add(CreateP);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void maskapai() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_maskapai, nama_maskapai FROM [dbo].[Maskapai] WHERE status = 1";
            connect.result = connect.stat.executeQuery(sql);

            while (connect.result.next()) {
                cbmaskapai.addItem(connect.result.getString("nama_maskapai"));
            }
            connect.stat.close();
            connect.result.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat load data Maskapai: " + ex);
        }
    }

    public void detailkursi() {
        try {
            connect.stat = connect.conn.createStatement();
            String sql = "SELECT id_kelas, nama_kelas from [dbo].[Kelas_Kursi] WHERE status = 1";
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

    public void autoid(){
        try {
            String sql = "SELECT MAX([id_pesawat]) FROM [dbo].[Pesawat]";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()){
                String maxID = connect.result.getString(1);
                if (maxID != null){
                    int num = Integer.parseInt(maxID.substring(3)) + 1;
                    String formattedNumber = String.format("%03d", num);
                    txtid.setText("PS" + formattedNumber);
                }else {
                    txtid.setText("PS001");
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
    public void clear(){
        txtid.setText("");
        txtKode.setText("");
        txtNamapesawat.setText("");
        txtkapasitas.setText("");
        cbmaskapai.setSelectedIndex(-1);
        txtStatus.setText("");
        txtid.setEnabled(true);
        txtNamapesawat.setEnabled(true);
        txtkapasitas.setEnabled(true);
        txtKode.setEnabled(true);
        txtStatus.setEnabled(true);
        cbmaskapai.setEnabled(true);
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

    public void loaddata() {
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
}
