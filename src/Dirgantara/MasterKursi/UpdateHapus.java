package Dirgantara.MasterKursi;

import Connect.Connect;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class UpdateHapus extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JButton btnHapus;
    private JButton btnUbah;
    private JTextField txtId;
    private JTextField txtKode;
    private JTextField txtNama;
    private JTextField txtStatus;
    private JTable tbKelasKursi;
    private JButton btnCari;
    private JTextField txtCariID;
    private JButton btnSemua;
    private JPanel UpdateK;
    private JButton clearButton;


    DefaultTableModel model;
    Connect connect = new Connect();
    String id, kode, nama, status;
    int statusText;

    public UpdateHapus(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        model = new DefaultTableModel();

        addKolom();
        tbKelasKursi.setModel(model);
        txtId.setEditable(false);

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtId.getText();
                kode = txtKode.getText();
                nama = txtNama.getText();
                status = txtStatus.getText();

                if (txtKode.getText().isEmpty() || txtNama.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(UpdateK, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    if (status.equalsIgnoreCase("Tersedia")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak Tersedia")) {
                        statusText = 0;
                    }

                    String selectQuery = "SELECT kode_kelas, nama_kelas, status FROM Kelas_Kursi WHERE id_kelas = ?";
                    connect.pstat = connect.conn.prepareStatement(selectQuery);
                    connect.pstat.setString(1, id);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        String existingKode = connect.result.getString("kode_kelas");
                        String existingNama = connect.result.getString("nama_kelas");
                        int existingStatus = connect.result.getInt("status");

                        if (existingKode.equals(kode) && existingNama.equals(nama) && existingStatus == statusText) {
                            JOptionPane.showMessageDialog(null, "Data sudah ada dan tidak boleh sama", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }
                    connect.result.close();
                    connect.pstat.close();

                    String query = "UPDATE [dbo].[Kelas_Kursi] SET kode_kelas=?, nama_kelas=?," +
                            "status=? WHERE id_kelas=?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, kode);
                    connect.pstat.setString(2, nama);
                    connect.pstat.setInt(3, statusText);
                    connect.pstat.setString(4, id);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Data Kelas Kursi berhasil diubah");
                    connect.pstat.close();


                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }

                loaddata();
                clear();
            }
        });
        tbKelasKursi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                super.mouseClicked(e);
                int i = tbKelasKursi.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtId.setText((String) model.getValueAt(i,0));
                txtNama.setText((String)  model.getValueAt(i,2));
                txtKode.setText((String) model.getValueAt(i,1));
                statusText = (int) model.getValueAt(i,3);
                if (statusText == 1){
                    txtStatus.setText("Tersedia");
                }else if (statusText == 0){
                    txtStatus.setText("Tidak Tersedia");
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
                    showByID();
                }
            }
        });
        btnSemua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loaddata();
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idkelas = txtId.getText();
                if (idkelas.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Harus memilih data Kelas Kursi terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String checkQuery = "SELECT status FROM [dbo].[Kelas_Kursi] WHERE id_kelas = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, idkelas);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int status = connect.result.getInt("status");
                        if (status == 0) {
                            JOptionPane.showMessageDialog(UpdateK, "Data Kelas Kursi sudah tidak aktif", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa status Kelas Kursi: " + ex);
                }

                int selectedOption = JOptionPane.showConfirmDialog(UpdateK, "Apakah Anda yakin ingin mengubah data Kelas Kursi menjadi tidak aktif?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    try {
                        String query = "UPDATE [dbo].[Kelas_Kursi] SET status = 0 WHERE id_kelas = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, idkelas);
                        int rowsUpdated = connect.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(UpdateK, "Kelas Kursi berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(UpdateK, "Gagal menghapus Kelas Kursi", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }

                        connect.pstat.close();
                    } catch (SQLException ex) {
                        System.out.println("Terjadi error saat menghapus Kelas Kursi: " + ex);
                    }
                    loaddata();
                    clear();
                }
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Halaman_utama halamanUtama = new Halaman_utama(username, nama_akses);
                halamanUtama.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(UpdateHapus.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }

    public void FrameConfig() {
        add(UpdateK);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void clear(){
        txtId.setText("");
        txtKode.setText("");
        txtNama.setText("");
        txtStatus.setText("");
    }

    public void addKolom(){
        model.addColumn("ID");
        model.addColumn("Kode Kelas");
        model.addColumn("Nama Kelas");
        model.addColumn("Status");

    }

    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
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

                model.addRow(obj);
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception ex){
            System.out.println("Terjadi error saat load data kelas kursi: " + ex);
        }
    }

    public void showByID(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();
            String query = "SELECT * FROM [dbo].[Kelas_Kursi] where id_kelas LIKE'%" +txtCariID.getText()+ "%'";
            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()){
                Object[] obj = new Object[5];
                obj[0] = connect.result.getString("id_kelas");
                obj[1] = connect.result.getString("kode_kelas");
                obj[2] = connect.result.getString("nama_kelas");
                obj[3] = connect.result.getInt("status");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null,"Data Kelas Kursi tidak ditemukan");
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception e){
            System.out.println("Terjadi eror saat load data Kelas Kursi : " +e);
        }
    }
}
