package Dirgantara.MasterKursi;

import Connect.Connect;
import Dirgantara.MasterPesawat.MenuPesawat;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;

public class CreateKelasKursi extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtKode;
    private JTextField txtStatus;
    private JTextField txtNama;
    private JButton btnSimpan;
    private JPanel CreateK;
    private JTextField txtid;
    private JTable tbkelaskursi;
    private JButton btnClear;

    DefaultTableModel modelmodel;
    Connect connect = new Connect();


    String id, kode, nama, status;
    int statusText;
    public CreateKelasKursi(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        modelmodel = new DefaultTableModel();

        addKolom();
        autoid();
        tbkelaskursi.setModel(modelmodel);
        tbkelaskursi.setEnabled(false);

//        txtStatus.setText("Tersedia");
        loaddata();
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtid.getText();
                kode = txtKode.getText();
                nama = txtNama.getText();
                status = txtStatus.getText();

                if (txtKode.getText().isEmpty() || txtNama.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(CreateK, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[Kelas_Kursi] VALUES (?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, kode);
                    connect.pstat.setString(3, nama);
                    connect.pstat.setInt(4, statusText);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Kelas Kursi berhasil ditambahkan");
                    connect.pstat.close();

                    loaddata();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }
                autoid();
                clear();
            }
        });
        tbkelaskursi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbkelaskursi.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtid.setText((String) modelmodel.getValueAt(i,0));
                txtNama.setText((String)  modelmodel.getValueAt(i,2));
                txtKode.setText((String) modelmodel.getValueAt(i,1));
                statusText = (int) modelmodel.getValueAt(i,3);
                if (statusText == 1){
                    txtStatus.setText("Tersedia");
                }else if (statusText == 0){
                    txtStatus.setText("Tidak Tersedia");
                }
                txtStatus.setEditable(true);
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
                Halaman_utama halamanUtama =new Halaman_utama(username, nama_akses);
                halamanUtama.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(CreateKelasKursi.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
    }
    public void FrameConfig() {
        add(CreateK);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void loaddata(){
        modelmodel.getDataVector().removeAllElements();
        modelmodel.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();
            String query = "SELECT * FROM [dbo].[Kelas_Kursi]";
            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()){
                Object[] obj = new Object[4];

                obj[0] = connect.result.getString("id_kelas");
                obj[1] = connect.result.getString("kode_kelas");
                obj[2] = connect.result.getString("nama_kelas");
                obj[3] = connect.result.getInt("status");

                modelmodel.addRow(obj);
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception ex){
            System.out.println("Terjadi error saat load data kelas kursi: " + ex);
        }
   }

    public void autoid(){
        try {
            String sql = "SELECT MAX(id_kelas) FROM [dbo].[Kelas_Kursi]";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()){
                String maxID = connect.result.getString(1);
                if (maxID != null){
                    int num = Integer.parseInt(maxID.substring(3)) + 1;
                    String formattedNumber = String.format("%03d", num);
                    txtid.setText("KK" + formattedNumber);
                }else {
                    txtid.setText("KK001");
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
        txtKode.setText("");
        txtNama.setText("");
    }

    public void addKolom(){
        modelmodel.addColumn("ID");
        modelmodel.addColumn("Kode Kelas");
        modelmodel.addColumn("Nama Kelas");
        modelmodel.addColumn("Status");

    }
}
