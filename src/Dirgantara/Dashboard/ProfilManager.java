package Dirgantara.Dashboard;

import Connect.Connect;
import Dirgantara.Laporan.laporanPembatalan;
import Dirgantara.Laporan.laporanPembelian;
import Dirgantara.MenuAwal;
import Riwayat.RiwayatManager;
import sun.java2d.cmm.ProfileDeferralMgr;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilManager extends JFrame{
    private JButton logoutButton;
    private JTextField txtEmail;
    private JTextField txtNIK;
    private JTextField txtUsrName;
    private JTextField txtNama;
    private JTextField txtUmur;
    private JTextField txtPassword;
    private JButton btnHapus;
    private JButton btnUbah;
    private JPanel ProfilMana;
    private JLabel lbl_user;
    private JLabel lbl_jabatan;
    private JButton dashboardButton;
    private JButton riwayatTransaksiButton;
    private JButton laporanPembelianButton;
    private JButton laporanPembatalanButton;

    Connect connect = new Connect();

    public ProfilManager(String username, String nama_akses){
        FrameConfig();
        lbl_user.setText(username);
        lbl_jabatan.setText(nama_akses);
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
                        JOptionPane.showMessageDialog(ProfilMana, "Pengguna berhasil dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ProfilMana, "Gagal menghapus pengguna", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(ProfilManager.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(ProfilManager.this, "Terima Kasih telah login");
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true); // Menampilkan menu awal setelah keluar
                    dispose();
                }
            }
        });
        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editMana editMana = new editMana(username, nama_akses);
               editMana.setVisible(true);
                dispose();
            }
        });
        laporanPembatalanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                laporanPembatalan laporanPembatalan = new laporanPembatalan(username, nama_akses);
                laporanPembatalan.setVisible(true);
                dispose();
            }
        });
        laporanPembelianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                laporanPembelian laporanPembelian = new laporanPembelian(username, nama_akses);
                laporanPembelian.setVisible(true);
                dispose();
            }
        });
        riwayatTransaksiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RiwayatManager riwayatManager = new RiwayatManager(username, nama_akses);
                riwayatManager.setVisible(true);
                dispose();
            }
        });
    }

    public void FrameConfig() {
        add(ProfilMana);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void loadData(String username) {
        try {
            String query = "SELECT * FROM [dbo].[User] WHERE id_akses = '3' AND username = ?";
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
