package Dirgantara.Master;

import Connect.Connect;
import Dirgantara.MenuAwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Update_maskapai extends JFrame{
    private JLabel lblnama;
    private JLabel lbljbt;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtID;
    private JTextField txtNegara;
    private JTextField txtkode;
    private JTextField txtStatus;
    private JTextField txtNama;
    private JTable tblMaskapai;
    private JButton btnUbah;
    private JButton btnBatal;
    private JButton btnCari;
    private JTextField txtCariID;
    private JPanel UpdateM;
    private JButton btnSemua;
    private JButton btnDelete;

    DefaultTableModel model;
    Connect connect = new Connect();
    String id, kode, nama, negara, status;
    int statusText;


    public Update_maskapai(String username, String nama_akses){
        FrameConfig();
        lblnama.setText(username);
        lbljbt.setText(nama_akses);
        model = new DefaultTableModel();

        addKolom();
        tblMaskapai.setModel(model);

        txtID.setEditable(false);
        //txtStatus.setText("Aktif");
        //loaddata();
        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtID.getText();
                kode = txtkode.getText();
                nama = txtNama.getText();
                negara = txtNegara.getText();
                status = txtStatus.getText();

                if (txtkode.getText().isEmpty() || txtNama.getText().isEmpty() || txtNegara.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(UpdateM, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean hasChanges = false;
                try {
                    if (status.equalsIgnoreCase("Aktif")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak aktif")) {
                        statusText = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "SELECT kode_maskapai, nama_maskapai, negara_asal, status FROM Maskapai WHERE id_maskapai=?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.result = connect.pstat.executeQuery();
                    if (connect.result.next()) {
                        String existingKode = connect.result.getString("kode_maskapai");
                        String existingNama = connect.result.getString("nama_maskapai");
                        String existingNegara = connect.result.getString("negara_asal");
                        int existingStatus = connect.result.getInt("status");

                        if (!existingKode.equals(kode) || !existingNama.equals(nama) || !existingNegara.equals(negara) || existingStatus != statusText) {
                            hasChanges = true;
                        }
                    }
                    connect.pstat.close();
                    connect.result.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat memeriksa perubahan data: " + ex);
                    return;
                }

                if (!hasChanges) {
                    JOptionPane.showMessageDialog(null, "Data masih sama. Ubah data terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                try {
                    if (status.equalsIgnoreCase("Aktif")) {
                        statusText = 1;
                    } else if (status.equalsIgnoreCase("Tidak aktif")) {
                        statusText = 0;
                    }
                    connect.stat = connect.conn.createStatement();
                    String query = "UPDATE Maskapai SET kode_maskapai=?, nama_maskapai=?, negara_asal=?, status=? WHERE id_maskapai=?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, kode);
                    connect.pstat.setString(2, nama);
                    connect.pstat.setString(3, negara);
                    connect.pstat.setInt(4, statusText);
                    connect.pstat.setString(5, id);

                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Data Maskapai berhasil diubah");
                    connect.pstat.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat memperbarui data: " + ex);
                }

                clear();
                loaddata();
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
        tblMaskapai.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tblMaskapai.getSelectedRow();
                if (i == -1){
                    return;
                }
                txtID.setText((String) model.getValueAt(i,0));
                txtNama.setText((String) model.getValueAt(i,2));
                txtkode.setText((String) model.getValueAt(i,1));
                txtNegara.setText((String) model.getValueAt(i, 3));
                statusText = (int) model.getValueAt(i,4);
                if (statusText == 1){
                    txtStatus.setText("Aktif");
                }else if (statusText == 0){
                    txtStatus.setText("Tidak aktif");
                }
                txtStatus.setEditable(true);
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Master_maskapai masterMaskapai = new Master_maskapai(username, nama_akses);
                masterMaskapai.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Update_maskapai.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnSemua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loaddata();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtID.getText();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Harus memilih data Maskapai terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String checkQuery = "SELECT status FROM [dbo].[Maskapai] WHERE id_maskapai = ?";
                    connect.pstat = connect.conn.prepareStatement(checkQuery);
                    connect.pstat.setString(1, id);
                    connect.result = connect.pstat.executeQuery();

                    if (connect.result.next()) {
                        int status = connect.result.getInt("status");
                        if (status == 0) {
                            JOptionPane.showMessageDialog(UpdateM, "Data Maskapai sudah tidak aktif", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            connect.result.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    int selectedOption = JOptionPane.showConfirmDialog(UpdateM, "Apakah Anda yakin ingin mengubah data Maskapai menjadi tidak aktif?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (selectedOption == JOptionPane.YES_OPTION) {
                        String updateQuery = "UPDATE [dbo].[Maskapai] SET status = ? WHERE id_maskapai = ?";
                        connect.pstat = connect.conn.prepareStatement(updateQuery);
                        connect.pstat.setInt(1, 0);
                        connect.pstat.setString(2, id);
                        int rowsUpdated = connect.pstat.executeUpdate();

                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(UpdateM, "Maskapai berhasil dinonaktifkan", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(UpdateM, "Gagal mengubah status Maskapai", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    connect.result.close();
                    connect.pstat.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error saat mengubah status data maskapai: " + ex);
                }

                clear();
                loaddata();
            }
        });
    }

    public void clear() {
        txtkode.setText("");
        txtNama.setText("");
        txtNegara.setText("");
        txtID.setText("");
        txtCariID.setText("");
        txtStatus.setText("");
    }

    public void addKolom(){
        model.addColumn("ID");
        model.addColumn("Kode");
        model.addColumn("Nama Maskapai");
        model.addColumn("Negara Asal");
        model.addColumn("Status");
    }
    public void FrameConfig() {
        add(UpdateM);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        Thread thread = new Thread(() -> {
            try {
                connect.stat = connect.conn.createStatement();
                String query = "SELECT * FROM [dbo].[Maskapai]";
                connect.result = connect.stat.executeQuery(query);

                while (connect.result.next()) {
                    Object[] obj = new Object[5];

                    obj[0] = connect.result.getString("id_maskapai");
                    obj[1] = connect.result.getString("kode_maskapai");
                    obj[2] = connect.result.getString("nama_maskapai");
                    obj[3] = connect.result.getString("negara_asal");
                    obj[4] = connect.result.getInt("status");

                    SwingUtilities.invokeLater(() -> model.addRow(obj));

                    Thread.sleep(100);
                }
                connect.stat.close();
                connect.result.close();
            } catch (Exception ex) {
                System.out.println("Terjadi error saat load data maskapai: " + ex);
            }
        });

        thread.start();
    }

    public void showByID(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            connect.stat = connect.conn.createStatement();
            String query = "SELECT * FROM Maskapai where id_maskapai LIKE'%" +txtCariID.getText()+ "%'";
            connect.result = connect.stat.executeQuery(query);

            while (connect.result.next()){
                Object[] obj = new Object[5];
                obj[0] = connect.result.getString("id_maskapai");
                obj[1] = connect.result.getString("kode_maskapai");
                obj[2] = connect.result.getString("nama_maskapai");
                obj[3] = connect.result.getString("negara_asal");
                obj[4] = connect.result.getInt("status");
                model.addRow(obj);
            }
            if (model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null,"Data Maskapai tidak ditemukan");
            }
            connect.stat.close();
            connect.result.close();
        }catch (Exception e){
            System.out.println("Terjadi eror saat load data Maskapai : " +e);
        }
    }
}
