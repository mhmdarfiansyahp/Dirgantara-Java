package Dirgantara;

import Dirgantara.Dashboard.ProfilPenumpang;
import Dirgantara.Transaksi.Pembatalan;
import Dirgantara.Transaksi.Pembayaran;
import Dirgantara.Transaksi.Pemesanan;
import Dirgantara.Transaksi.Riwayat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class halamanAwalPenumpang extends JFrame{
    private JButton dashboardButton;
    private JButton btnPembatalan;
    private JButton riwayatButton;
    private JButton logoutButton;
    private JPanel Penumpang;
    private JLabel label_user;
    private JButton btnPembayaran;
    private JButton btnPemesanan;

    public halamanAwalPenumpang(String username){
        FrameConfig();
        label_user.setText(username);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(halamanAwalPenumpang.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilPenumpang profilPenumpang = new ProfilPenumpang(username);
                profilPenumpang.setVisible(true);
                dispose();
            }
        });
        btnPemesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pemesanan pemesanan = new Pemesanan(username);
                pemesanan.setVisible(true);
                dispose();
            }
        });

        btnPembayaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembayaran pembayaran = new Pembayaran(username);
                pembayaran.setVisible(true);
                dispose();
            }
        });
        btnPembatalan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembatalan pembatalan = new Pembatalan(username);
                pembatalan.setVisible(true);
                dispose();
            }
        });
        riwayatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Riwayat riwayat =new Riwayat(username);
                riwayat.setVisible(true);
                dispose();
            }
        });
    }
    public void FrameConfig() {
        add(Penumpang);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
