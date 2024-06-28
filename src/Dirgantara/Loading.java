package Dirgantara;

import javax.swing.*;

public class Loading extends JFrame{
    private JPanel panel1;
    private JProgressBar progressBar1;
    private JLabel lblLoading;

//    public static void main(String[] args) {
//        new Loading();
//    }
    public Loading(){
        FrameConfig();
        lblLoading.setText("Loading... 0%");
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(50);
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int value = chunks.get(chunks.size() - 1);
                progressBar1.setValue(value);
                lblLoading.setText("Loading... " + value + "%");
            }

            protected void done() {
                try {
                    Thread.sleep(400); // Menambahkan jeda selama 2 detik (2000 milidetik)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Proses loading selesai
                MenuAwal menuAwal = new MenuAwal();
                menuAwal.setVisible(true);
                dispose();
            }
        };
        worker.execute();
    }
    public void FrameConfig() {
        add(panel1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
