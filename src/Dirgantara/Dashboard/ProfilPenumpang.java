package Dirgantara.Dashboard;

import Connect.Connect;
import Dirgantara.MenuAwal;
import Dirgantara.Transaksi.Pembatalan;
import Dirgantara.Transaksi.Pembayaran;
import Dirgantara.Transaksi.Pemesanan;
import Dirgantara.Transaksi.Riwayat;
import Dirgantara.halamanAwalPenumpang;
import sun.security.util.Pem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilPenumpang extends JFrame{
    private JLabel label_user;
    private JButton dashboardButton;
    private JButton riwayatButton;
    private JButton logoutButton;
    private JTextField txtEmail;
    private JTextField txtNIK;
    private JTextField txtUsrName;
    private JTextField txtNama;
    private JTextField txtUmur;
    private JTextField txtPassword;
    private JButton btnHapus;
    private JButton btnUbah;
    private JPanel ProfilPenum;
    private JButton pemesananButton;
    private JButton pembayaranButton;
    private JButton pembatalanButton;

    Connect connect = new Connect();

    public ProfilPenumpang(String username){
        FrameConfig();
        label_user.setText(username);
        txtNama.setEditable(false);
        txtEmail.setEditable(false);
        txtNIK.setEditable(false);
        txtUmur.setEditable(false);
        txtPassword.setEditable(false);
        txtUsrName.setEditable(false);

        loadData(username);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(ProfilPenumpang.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPenum editPenum = new editPenum(username);
                editPenum.setVisible(true);
                dispose();
            }
        });
        pemesananButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pemesanan pemesanan = new Pemesanan(username);
                pemesanan.setVisible(true);
                dispose();
            }
        });
        pembayaranButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembayaran pembayaran = new Pembayaran(username);
                pembayaran.setVisible(true);
                dispose();
            }
        });
        riwayatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Riwayat riwayat = new Riwayat(username);
                riwayat.setVisible(true);
                dispose();
            }
        });
        pembatalanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembatalan pembatalan = new Pembatalan(username);
                pembatalan.setVisible(true);
                dispose();
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idUser = txtNama.getText();

                try {
                    String query = "UPDATE [dbo].[User] SET status = 0 WHERE nama_user = ?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, idUser );
                    int rowsUpdated = connect.pstat.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(ProfilPenum, "Pengguna berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ProfilPenum, "Gagal menghapus pengguna", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }

                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat menghapus pengguna: " + ex);
                }
                MenuAwal menuAwal = new MenuAwal();
                menuAwal.setVisible(true);
                dispose();
            }
        });
    }

    public void FrameConfig() {
        add(ProfilPenum);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void loadData(String username) {
        try {
            String query = "SELECT * FROM [dbo].[User] WHERE id_akses = '2' AND username = ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, username);
            ResultSet rs = connect.pstat.executeQuery();

            if (rs.next()) {

                String nama = rs.getString("nama_user");
                int umur = rs.getInt("umur");
                String email = rs.getString("email");
                String nik = rs.getString("no_ktp");
                String userName = rs.getString("username");
                String password = rs.getString("password");

                txtNama.setText(nama);
                txtUmur.setText(String.valueOf(umur));
                txtEmail.setText(email);
                txtNIK.setText(nik);
                txtUsrName.setText(userName);
                txtPassword.setText(password);
            }

            rs.close();
            connect.pstat.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat memuat data: " + ex);
        }
    }
}
