package Dirgantara.Master;

import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalAdmin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Master_maskapai extends JFrame{
    private JButton btnmwnuadmin;
    private JButton btnlogout;
    private JPanel Maskapai;
    private JMenuItem Create;
    private JLabel label_jbtan;
    private JLabel label_user;
    private JMenuItem update;
    private JMenuItem Delete;


    public Master_maskapai(String username, String nama_akses){
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);
        Create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Create_maskapai createMaskapai = new Create_maskapai(username, nama_akses);
                createMaskapai.setVisible(true);
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
                int option = JOptionPane.showConfirmDialog(Master_maskapai.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Update_maskapai updateMaskapai = new Update_maskapai(username, nama_akses);
                updateMaskapai.setVisible(true);
            }
        });
    }
    public void FrameConfig() {
        add(Maskapai);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
