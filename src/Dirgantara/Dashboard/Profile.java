package Dirgantara.Dashboard;

import Connect.Connect;
import Dirgantara.Master.Master_maskapai;
import Dirgantara.MasterBandara.menuBandara;
import Dirgantara.MasterJadwal.MenuUtama;
import Dirgantara.MasterKursi.Halaman_utama;
import Dirgantara.MasterPesawat.MenuPesawat;
import Dirgantara.MenuAwal;
import Dirgantara.Rute.menuRute;
import Dirgantara.Transaksi.ConfirmPembayaran;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Profile extends JFrame{
    private JLabel label_user;
    private JLabel label_jbtan;
    private JButton btnbandara;
    private JButton btnmaskapai;
    private JButton btnpesawat;
    private JButton btnrute;
    private JButton btnkursi;
    private JButton btnlogout;
    private JTextField txtId;
    private JTextField txtUmur;
    private JTextField txtEmail;
    private JTextField txtNIK;
    private JTextField txtUsrName;
    private JTextField txtPassword;
    private JButton btnUbah;
    private JButton btnHapus;
    private JPanel profile;
    private JTextField txtNama;
    private JButton btndashboard;
    private JButton btnJadwalpenerbangan;
    private JButton btnConfirmPembayaran;

    public static void main(String[] args) {
        String username = null, nama_akses = null;
        new Profile (username, nama_akses);
    }
    Connect connect = new Connect();
    public Profile(String username, String nama_akses){
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);
        txtNama.setEditable(false);
        txtEmail.setEditable(false);
        txtNIK.setEditable(false);
        txtUmur.setEditable(false);
        txtPassword.setEditable(false);
        txtUsrName.setEditable(false);

        loadData(username);
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
                        JOptionPane.showMessageDialog(profile, "Pengguna berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(profile, "Gagal menghapus pengguna", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editProfile editProfile = new editProfile(username,nama_akses);
                editProfile.setVisible(true);
                dispose();
            }
        });
        btnmaskapai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Master_maskapai masterMaskapai = new Master_maskapai(username,nama_akses);
                masterMaskapai.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuAwal menuAwal = new MenuAwal();
                menuAwal.setVisible(true);
                dispose();
            }
        });
        btnbandara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuBandara menuBandara = new menuBandara(username, nama_akses);
                menuBandara.setVisible(true);
                dispose();
            }
        });
        btnkursi.addActionListener(new ActionListener() {
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
                int choice = JOptionPane.showConfirmDialog(Profile.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(Profile.this, "Terima Kasih telah login");
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true); // Menampilkan menu awal setelah keluar
                    dispose();
                }
            }
        });
        btnrute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuRute menuRute = new menuRute(username, nama_akses);
                menuRute.setVisible(true);
                dispose();
            }
        });
        btnpesawat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuPesawat menuPesawat = new MenuPesawat(username, nama_akses);
                menuPesawat.setVisible(true);
                dispose();
            }
        });
        btnConfirmPembayaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfirmPembayaran confirmPembayaran = new ConfirmPembayaran(username, nama_akses);
                confirmPembayaran.setVisible(true);
                dispose();
            }
        });
        btnJadwalpenerbangan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuUtama menuUtama = new MenuUtama(username, nama_akses);
                menuUtama.setVisible(true);
                dispose();
            }
        });
    }

    public void FrameConfig() {
        add(profile);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void loadData(String username) {
        try {
            String query = "SELECT * FROM [dbo].[User] WHERE id_akses = '1' AND username = ?";
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
