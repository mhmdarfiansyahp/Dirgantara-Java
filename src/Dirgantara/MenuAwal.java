package Dirgantara;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuAwal extends JFrame{
    private JPanel btnmenu;
    private JButton Login;
    private JButton registrasiButton;
    private JButton keluarButton;

    public static void main(String[] args) {
        new Loading();
        //new MenuAwal().setVisible(true);
    }
    public MenuAwal(){
        FrameConfig();
        registrasiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Registrasi form = new Registrasi();
                form.setVisible(true);
                dispose();
            }
        });
        keluarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(MenuAwal.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(MenuAwal.this, "Terima kasih telah menggunakan aplikasi ini");
                    dispose();
                    System.exit(0); // Menutup aplikasi secara keseluruhan
                }
            }
        });
        Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login form = new Login();
                form.setVisible(true);
                dispose();
            }
        });
    }

    public void FrameConfig() {
        add(btnmenu);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}

