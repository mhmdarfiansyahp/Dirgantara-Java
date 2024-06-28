package Dirgantara;

import Connect.Connect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Registrasi extends JFrame{
    private JTextField txtnama;
    private JTextField txtumur;
    private JTextField txtEmail;
    private JTextField txtNoktp;
    private JPasswordField txtconfirm;
    private JButton btnRegis;
    private JButton btncencel;
    private JPasswordField txtpassword;
    private JTextField txtidPenum;
    private JTextField txtusename;
    private JPanel JRegister;
    private JCheckBox checkBox1;

    Connect connect = new Connect();

    String id,name, email, umur, noktp, pass, confpass, username;

    public Registrasi(){
        FrameConfig();
        autoid();
//        txtStatus.setText("Aktif");
        btnRegis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtidPenum.getText();
                name = txtnama.getText();
                umur = txtumur.getText();
                noktp = txtNoktp.getText();
                email = txtEmail.getText();
                username = txtusename.getText();
                pass = String.valueOf(txtpassword.getPassword());
                confpass = String.valueOf(txtconfirm.getPassword());

                if (txtnama.getText().isEmpty() || txtNoktp.getText().isEmpty() || txtumur.getText().isEmpty()
                        || txtEmail.getText().isEmpty() || txtusename.getText().isEmpty() || txtpassword.getText().isEmpty() || txtconfirm.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(JRegister, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!umur.matches("\\d+")) {
                    JOptionPane.showMessageDialog(JRegister, "Umur hanya boleh diisi dengan angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!noktp.matches("\\d{1,16}")) {
                    JOptionPane.showMessageDialog(JRegister, "Noktp hanya boleh diisi dengan angka (maksimal 16 digit)", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (noktp.length() != 16) {
                    JOptionPane.showMessageDialog(JRegister, "Noktp harus diisi dengan 16 digit angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
                if (!Pattern.matches(emailRegex, email)) {
                    JOptionPane.showMessageDialog(JRegister, "Email tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!pass.equals(confpass)) {
                    JOptionPane.showMessageDialog(null,
                            "Confirm Password tidak sesuai", "coba lagi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    connect.stat = connect.conn.createStatement();
                    String query = "INSERT INTO [dbo].[User] (id_user, nama_user, umur, email, no_ktp, username, password, id_akses, status) VALUES (?,?,?,?,?,?,?,?,?)";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, id);
                    connect.pstat.setString(2, name);
                    connect.pstat.setString(3, umur);
                    connect.pstat.setString(4, email);
                    connect.pstat.setString(5, noktp);
                    connect.pstat.setString(6, username);
                    connect.pstat.setString(7, pass);
                    connect.pstat.setString(8, "2");
                    connect.pstat.setString(9, "1");


                    connect.pstat.executeUpdate();
                    connect.conn.commit();
                    JOptionPane.showMessageDialog(null, "Registrasi berhasil dilakukan");
                    clear();
                    connect.pstat.close();


                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi error saat data : " + ex);
                }
                autoid();
            }

        });
        btncencel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuAwal form = new MenuAwal();
                form.setVisible(true);
                dispose();
            }
        });
        checkBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox1.isSelected() == true){
                    txtpassword.setEchoChar('\0');
                }else {
                    txtpassword.setEchoChar('*');
                }
            }
        });
        txtNoktp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String nik = txtNoktp.getText();
                if (nik.length() >= 16) {
                    e.consume(); // Menghentikan input jika panjang NIK sudah mencapai 16 digit
                }
            }
        });
        txtNoktp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();

                // Mengecek apakah karakter bukan angka
                if (!Character.isDigit(character)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                }
            }
        });
        txtumur.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();

                // Mengecek apakah karakter bukan angka
                if (!Character.isDigit(character)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                }
            }
        });
        txtnama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume(); // Mengabaikan karakter yang bukan huruf
                }
            }
        });
    }

    public void clear() {

        txtnama.setText("");
        txtEmail.setText("");
        txtNoktp.setText("");
        txtumur.setText("");
        txtusename.setText("");
        txtpassword.setText("");
        txtconfirm.setText("");

    }


    public void FrameConfig() {
        add(JRegister);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void autoid() {
        try {
            String sql = "SELECT MAX(id_user) FROM [dbo].[User]";
            connect.pstat = connect.conn.prepareStatement(sql);
            ResultSet result = connect.pstat.executeQuery();

            if (result.next()) {
                String maxId = result.getString(1);
                if (maxId != null) {
                    int number = Integer.parseInt(maxId.substring(3)) + 1;
                    String formattedNumber = String.format("%03d", number);
                    txtidPenum.setText("USR" + formattedNumber);
                } else {
                    txtidPenum.setText("USR001");
                }
            }

            txtidPenum.setEnabled(false);
//            txtStatus.setEnabled(false);
            connect.pstat.close();
            result.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode data User: " + ex);
        }
    }
}
