package Dirgantara.MasterBandara;

import Dirgantara.MenuAwal;
import Dirgantara.halamanAwalAdmin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menuBandara extends JFrame{
    private JLabel label_user;
    private JLabel label_jbtan;
    private JButton btnmwnuadmin;
    private JButton btnlogout;
    private JMenuItem Delete;
    private JPanel MenuBandara;
    private JMenuItem Create;

    public menuBandara(String username, String nama_akses) {
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);

        Create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreateBandara createBandara = new CreateBandara(username, nama_akses);
                createBandara.setVisible(true);
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
                int option = JOptionPane.showConfirmDialog(menuBandara.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
            }
        });
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                updatedelete updatedelete = new updatedelete(username, nama_akses);
                updatedelete.setVisible(true);
            }
        });
    }

    public void FrameConfig() {
        add(MenuBandara);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
