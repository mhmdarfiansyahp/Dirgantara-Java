package Dirgantara;

import Connect.Connect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame{
    private JPasswordField txtpass;
    private JTextField txtussername;
    private JButton exitButton;
    private JButton loginButton;
    private JPanel Login;
    private JCheckBox Liatpass;

    Connect connect = new Connect();

    public Login(){
        FrameConfig();
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuAwal form = new MenuAwal();
                form.setVisible(true);
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] value = validasi();
                Boolean valid = Boolean.parseBoolean(value[0]);
                if (valid) {
                    int hakAkses = Integer.parseInt(value[1]);
                    String username = value[2];
                    String nama_akses = value[3];
                    switch (hakAkses) {
                        case 1:
                            // Tampilkan form untuk hak akses 1 (misalnya form admin)
                            JOptionPane.showMessageDialog(Login, "Hai! Selamat datang kembali, Admin");
                            halamanAwalAdmin adminform = new halamanAwalAdmin(username, nama_akses);
                            adminform.setVisible(true);
                            dispose();
                            break;
                        case 2:
                            // Tampilkan form untuk hak akses 3 (misalnya form penumpang)
                            JOptionPane.showMessageDialog(Login, "Hai! Selamat datang kembali");
                            halamanAwalPenumpang penumpangForm = new halamanAwalPenumpang(username);
                            penumpangForm.setVisible(true);
                            dispose();
                            break;
                        case 3:
                            JOptionPane.showMessageDialog(Login, "Hai! Selamat datang kembali, Manager");
                            halamanAwalManager managerForm = new halamanAwalManager(username, nama_akses);
                            managerForm.setVisible(true);
                            dispose();
                            break;
                        default:
                            JOptionPane.showMessageDialog(Login, "Hak akses tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            break;
                    }
                }
            }
        });
        char passwordEchoChar = txtpass.getEchoChar(); // Menyimpan karakter echo sebelumnya

        Liatpass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Liatpass.isSelected()) {
                    txtpass.setEchoChar((char) 0); // Mengubah karakter echo menjadi 0 (tidak ada karakter echo)
                } else {
                    txtpass.setEchoChar(passwordEchoChar); // Mengembalikan karakter echo sebelumnya
                }
            }
        });
    }

    public void FrameConfig() {
        add(Login);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        // Tombol "Login" akan berfungsi ketika tombol "Enter" ditekan
        getRootPane().setDefaultButton(loginButton);
    }


    public String[] validasi() {
        if (txtussername.getText().isEmpty() || txtpass.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Login, "username/ Password Kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                connect.stat = connect.conn.createStatement();
                String query = "SELECT * FROM [dbo].[User] u INNER JOIN [dbo].[hak_akses] ha ON u.id_akses = ha.id_akses WHERE username = '" + txtussername.getText() +
                        "' AND password = '" + txtpass.getText() + "'";
                connect.result = connect.stat.executeQuery(query);
                if (connect.result.next()) {
                    int status = connect.result.getInt("status");
                    if (status == 0) {
                        JOptionPane.showMessageDialog(Login, "Akun tidak aktif", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return new String[]{"false"};
                    } else {
                        int hakAkses = connect.result.getInt("id_akses");
                        String username = connect.result.getString("username");
                        String password = connect.result.getString("password");
                        String nama_akses = connect.result.getString("nama_akses");
                        return new String[]{"true", Integer.toString(hakAkses), username, nama_akses};
                    }
                } else {
                    JOptionPane.showMessageDialog(Login, "Username atau Password Salah", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return new String[]{"false"};
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(Login, ex.getMessage(), "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        }
        return new String[]{"false"};
    }
}

