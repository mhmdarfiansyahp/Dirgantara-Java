package Dirgantara;

import Dirgantara.Dashboard.Profile;
import Dirgantara.Master.Master_maskapai;
import Dirgantara.MasterBandara.menuBandara;
import Dirgantara.MasterJadwal.MenuUtama;
import Dirgantara.MasterKursi.Halaman_utama;
import Dirgantara.MasterPesawat.MenuPesawat;
import Dirgantara.Rute.menuRute;
import Dirgantara.Transaksi.ConfirmPembayaran;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class halamanAwalAdmin extends JFrame{
    private JPanel panel1;
    private JButton btndashboard;
    private JButton btnbandara;
    private JButton btnmaskapai;
    private JButton btnpesawat;
    private JButton btnrute;
    private JButton btnkursi;
    private JButton btnlogout;
    private JLabel label_jbtan;
    private JLabel label_user;
    private JButton btnJadwalpenerbangan;
    private JButton btnConfirmPembayaran;

    public halamanAwalAdmin(String username, String nama_akses){
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);
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
                int choice = JOptionPane.showConfirmDialog(halamanAwalAdmin.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(halamanAwalAdmin.this, "Terima Kasih");
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true); // Menampilkan menu awal setelah keluar
                    dispose();
                }
            }
        });
        btndashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Profile profile = new Profile(username,nama_akses);
                profile.setVisible(true);
                dispose();
            }
        });
        btnbandara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuBandara bandara = new menuBandara(username, nama_akses);
                bandara.setVisible(true);
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
        btnpesawat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuPesawat menuPesawat = new MenuPesawat(username, nama_akses);
                menuPesawat.setVisible(true);
                dispose();
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
        btnJadwalpenerbangan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuUtama menuUtama = new MenuUtama(username, nama_akses);
                menuUtama.setVisible(true);
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
    }
    public void FrameConfig() {
        add(panel1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
