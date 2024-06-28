package Dirgantara;

import Dirgantara.Dashboard.ProfilManager;
import Dirgantara.Laporan.laporanPembatalan;
import Dirgantara.Laporan.laporanPembelian;
import Riwayat.RiwayatManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class halamanAwalManager extends JFrame{
    private JButton dashboardButton;
    private JButton riwayatTransaksiButton;
    private JButton laporanPembelianButton;
    private JButton logoutButton;
    private JPanel Manager;
    private JLabel lbl_user;
    private JLabel lbl_jabatan;
    private JButton laporanPembatalanButton;

    public halamanAwalManager(String username, String nama_akses){
        FrameConfig();
        lbl_user.setText(username);
        lbl_jabatan.setText(nama_akses);
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilManager profilManager = new ProfilManager(username, nama_akses);
                profilManager.setVisible(true);
                dispose();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(halamanAwalManager.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
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
        laporanPembatalanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                laporanPembatalan laporanPembatalan = new laporanPembatalan(username, nama_akses);
                laporanPembatalan.setVisible(true);
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
        add(Manager);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
