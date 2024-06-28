package Dirgantara.MasterPesawat;

import Dirgantara.MenuAwal;
import Dirgantara.Rute.UpdateDeleteRute;
import Dirgantara.halamanAwalAdmin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPesawat extends JFrame{
    private JLabel label_user;
    private JLabel label_jbtan;
    private JButton btnmwnuadmin;
    private JButton btnlogout;
    private JMenuItem Delete;
    private JMenuItem Create;
    private JPanel MenuP;

    public MenuPesawat(String username, String nama_akses){
        FrameConfig();
        label_user.setText(username);
        label_jbtan.setText(nama_akses);
        Create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreatePesawat createPesawat = new CreatePesawat(username, nama_akses);
                createPesawat.setVisible(true);
            }
        });
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                UpdatePesawat updatePesawat = new UpdatePesawat(username,nama_akses);
                updatePesawat.setVisible(true);
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(MenuPesawat.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true);
                    dispose();
                }
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
    }


    public void FrameConfig() {
        add(MenuP);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
