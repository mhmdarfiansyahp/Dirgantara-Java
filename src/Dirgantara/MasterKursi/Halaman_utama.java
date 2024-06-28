package Dirgantara.MasterKursi;

import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalAdmin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Halaman_utama extends JFrame{
    private JLabel label_user;
    private JLabel label_jbtan;
    private JButton btnmwnuadmin;
    private JButton btnlogout;
    private JMenuItem Delete;
    private JMenuItem Create;
    private JPanel MenuK;

    public Halaman_utama(String username, String nama_akses){
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);
        Create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreateKelasKursi createKelasKursi = new CreateKelasKursi(username, nama_akses);
                createKelasKursi.setVisible(true);
            }
        });
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                UpdateHapus updateHapus = new UpdateHapus(username, nama_akses);
                updateHapus.setVisible(true);
            }
        });
        btnmwnuadmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                halamanAwalAdmin halamanAwalAdmin = new halamanAwalAdmin(username, nama_akses);
                halamanAwalAdmin.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Halaman_utama.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
    }

    public void FrameConfig() {
        add(MenuK);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
