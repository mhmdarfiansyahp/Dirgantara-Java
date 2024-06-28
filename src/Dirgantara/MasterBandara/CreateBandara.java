package Dirgantara.MasterBandara;

import Connect.Connect;
import Dirgantara.MasterJadwal.UpdateDeleteJadwal;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class CreateBandara extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTable tbBandara;
    private JButton btnSimpan;
    private JButton btnBatal;
    private JTextField txtId;
    private JTextField txtKode;
    private JTextField txtNama;
    private JTextField txtLokasi;
    private JPanel CreateBandara;

    DefaultTableModel model;
    Connect connect = new Connect();

    String Id, Kode, Nama, Lokasi;

    public CreateBandara(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        model = new DefaultTableModel();

        addColumn();
        txtId.setEditable(false);
        id_otomatis();

        tbBandara.setModel(model);
        tbBandara.setEnabled(false);

        loaddata();

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Id = txtId.getText();
                Kode = txtKode.getText();
                Nama = txtNama.getText();
                Lokasi = txtLokasi.getText();

                if (txtKode.getText().isEmpty() || txtNama.getText().isEmpty() || txtLokasi.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(CreateBandara, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!Nama.matches("^[a-zA-Z\\s]+$")) {
                    JOptionPane.showMessageDialog(CreateBandara, "Nama hanya boleh terdiri dari huruf", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try{
                    String query="INSERT INTO Bandara VALUES (?,?,?,?,1)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, Id);
                    connect.pstat.setString(2, Kode);
                    connect.pstat.setString(3, Nama);
                    connect.pstat.setString(4, Lokasi);

                    connect.pstat.executeUpdate();
                    connect.pstat.close();
                }catch (SQLException ex){
                    System.out.println("Terjadi error saat insert data bandara!: "+ex);
                }
                JOptionPane.showMessageDialog(null,"Insert data bandara berhasil");
                id_otomatis();
                batal();
                loaddata();
            }
        });
        tbBandara.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbBandara.getSelectedRow();
                if (i == -1) {
                    return;
                }
                txtId.setText((String) model.getValueAt(i,0));
                txtKode.setText((String) model.getValueAt(i,1));
                txtNama.setText((String) model.getValueAt(i,2));
                txtLokasi.setText((String) model.getValueAt(i, 3));
                //txtStatus.setText(model.getValueAt(i,4).toString());
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuBandara menuBandara = new menuBandara(username, nama_akses);
                menuBandara.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(CreateBandara.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                batal();
            }
        });
    }

    public void FrameConfig() {
        add(CreateBandara);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void id_otomatis(){
        try {
            String sql = "SELECT MAX(id_bandara) FROM Bandara";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()){
                String maxID = connect.result.getString(1);
                if (maxID != null){
                    int num = Integer.parseInt(maxID.substring(4)) + 1;
                    String formattedNumber = String.format("%03d", num);
                    txtId.setText("BD" + formattedNumber);
                }else {
                    txtId.setText("BD001");
                }
            }
            connect.pstat.close();
            connect.result.close();
        }
        catch (Exception ex){
            System.out.println("Error "+ex);
        }
    }

    public void batal(){
        txtKode.setText("");
        txtNama.setText("");
        txtLokasi.setText("");
    }

    public void addColumn(){
        model.addColumn("ID Bandara");
        model.addColumn("Kode Bandara");
        model.addColumn("Nama");
        model.addColumn("Lokasi");
        //model.addColumn("Status");
    }

    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        Thread thread = new Thread(() -> {
            try {
                connect.stat = connect.conn.createStatement();
                String query = "SELECT * FROM [dbo].[Bandara]";
                connect.result = connect.stat.executeQuery(query);

                while (connect.result.next()) {
                    Object[] obj = new Object[5];

                    obj[0] = connect.result.getString("id_bandara");
                    obj[1] = connect.result.getString("kode_bandara");
                    obj[2] = connect.result.getString("nama_bandara");
                    obj[3] = connect.result.getString("lokasi_bandara");
                    obj[4] = connect.result.getInt("status");

                    SwingUtilities.invokeLater(() -> model.addRow(obj));

                    Thread.sleep(150);
                }
                connect.stat.close();
                connect.result.close();

            } catch (Exception ex) {
                System.out.println("Terjadi error saat load data Bandara: " + ex);
            }
        });

        thread.start();
    }

}
