package Dirgantara.Dashboard;

import Connect.Connect;
import Dirgantara.MenuAwal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class editPenum extends JFrame {
    private JLabel lblnama;
    private JButton btnkembali;
    private JButton btnlogout;
    private JTextField txtEmail;
    private JTextField txtNIK;
    private JTextField txtUsrName;
    private JTextField txtNama;
    private JTextField txtUmur;
    private JTextField txtPassword;
    private JButton btnUbah;
    private JPanel EditP;

    Connect connect = new Connect();

    public editPenum(String username){
        FrameConfig();
        lblnama.setText(username);
        loadData(username);

        btnUbah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = txtNama.getText();
                String umur = txtUmur.getText();
                String email = txtEmail.getText();
                String nik = txtNIK.getText();
                String userName = txtUsrName.getText();
                String password = txtPassword.getText();

                if (nama.isEmpty() || umur.isEmpty() || email.isEmpty() || nik.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(EditP, "Data harus terisi semua", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String query = "SELECT * FROM [dbo].[User] WHERE id_akses = '2' AND username = ?";
                    connect.pstat = connect.conn.prepareStatement(query);
                    connect.pstat.setString(1, userName);
                    ResultSet rs = connect.pstat.executeQuery();

                    if (rs.next()) {
                        String namaSebelumnya = rs.getString("nama_user");
                        int umurSebelumnya = rs.getInt("umur");
                        String emailSebelumnya = rs.getString("email");
                        String nikSebelumnya = rs.getString("no_ktp");
                        String userNameSebelumnya = rs.getString("username");
                        String passwordSebelumnya = rs.getString("password");

                        boolean dataChanged = false;

                        if (!nama.equals(namaSebelumnya)) {
                            dataChanged = true;
                        }
                        if (!umur.equals(String.valueOf(umurSebelumnya))) {
                            dataChanged = true;
                        }
                        if (!email.equals(emailSebelumnya)) {
                            dataChanged = true;
                        }
                        if (!nik.equals(nikSebelumnya)) {
                            dataChanged = true;
                        }
                        if (!userName.equals(userNameSebelumnya)) {
                            dataChanged = true;
                        }
                        if (!password.equals(passwordSebelumnya)) {
                            dataChanged = true;
                        }

                        if (!dataChanged) {
                            JOptionPane.showMessageDialog(EditP, "Tidak ada perubahan data yang dilakukan", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            rs.close();
                            connect.pstat.close();
                            return;
                        }
                    }

                    rs.close();
                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa perubahan data: " + ex);
                }

                if (!nama.matches("^[a-zA-Z]+$")) {
                    JOptionPane.showMessageDialog(EditP, "Nama hanya boleh terdiri dari huruf", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!umur.matches("\\d+")) {
                    JOptionPane.showMessageDialog(EditP, "Umur hanya boleh diisi dengan angka", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int Umur = Integer.parseInt(umur);

                String regexPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

                if (!email.matches(regexPattern)) {
                    JOptionPane.showMessageDialog(EditP, "Format email tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (nik.length() != 16) {
                    JOptionPane.showMessageDialog(EditP, "NIK harus terdiri dari 16 digit", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String updateQuery = "UPDATE [dbo].[User] SET umur = ?, email = ?, no_ktp = ?, username = ?, password = ? WHERE nama_user = ?";
                    connect.pstat = connect.conn.prepareStatement(updateQuery);
                    connect.pstat.setInt(1, Umur);
                    connect.pstat.setString(2, email);
                    connect.pstat.setString(3, nik);
                    connect.pstat.setString(4, userName);
                    connect.pstat.setString(5, password);
                    connect.pstat.setString(6, nama);

                    int rowsUpdated = connect.pstat.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(EditP, "Data pengguna berhasil diubah", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(EditP, "Gagal mengubah data pengguna", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }

                    connect.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat mengubah data pengguna: " + ex);
                }
            }
        });
        btnkembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfilPenumpang profilPenumpang = new ProfilPenumpang(username);
                profilPenumpang.setVisible(true);
                dispose();
            }
        });
        btnlogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(editPenum.this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(editPenum.this, "Terima kasih telah menggunakan aplikasi ini");
                    MenuAwal menuAwal = new MenuAwal();
                    menuAwal.setVisible(true); // Menampilkan menu awal setelah keluar
                    dispose();
                }
            }
        });
        txtNIK.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String nik = txtNIK.getText();
                if (nik.length() >= 16) {
                    e.consume(); // Menghentikan input jika panjang NIK sudah mencapai 16 digit
                }
            }
        });
        txtNIK.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();

                // Mengecek apakah karakter bukan angka
                if (!Character.isDigit(character)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                }
            }
        });
        txtUmur.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();

                // Mengecek apakah karakter bukan angka
                if (!Character.isDigit(character)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                }
            }
        });
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume(); // Mengabaikan karakter yang bukan huruf
                }
            }
        });
    }
    public void FrameConfig() {
        add(EditP);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void loadData(String username) {
        try {
            String query = "SELECT * FROM [dbo].[User] WHERE id_akses = '2' AND username = ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, username);
            ResultSet rs = connect.pstat.executeQuery();

            if (rs.next()) {

                String nama = rs.getString("nama_user");
                int umur = rs.getInt("umur");
                String email = rs.getString("email");
                String nik = rs.getString("no_ktp");
                String userName = rs.getString("username");
                String password = rs.getString("password");

                txtNama.setText(nama);
                txtUmur.setText(String.valueOf(umur));
                txtEmail.setText(email);
                txtNIK.setText(nik);
                txtUsrName.setText(userName);
                txtPassword.setText(password);
            }

            rs.close();
            connect.pstat.close();
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat memuat data: " + ex);
        }
    }
}
