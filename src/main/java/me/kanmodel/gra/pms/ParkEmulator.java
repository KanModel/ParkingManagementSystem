package me.kanmodel.gra.pms;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class ParkEmulator {
    private static boolean use = true;

    public static void main(String[] args) {
        JFrame window = new JFrame("Park Emulator");
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JButton enterBtn = new JButton("入库");
        JButton exitBtn = new JButton("出库");
        panel1.add(enterBtn);
        panel1.add(exitBtn);
        JButton parkBtn = new JButton("停车");
        JLabel label = new JLabel("使用:");
        JCheckBox checkBox = new JCheckBox();
        JTextField textField = new JTextField(5);
        textField.setText("1");
        textField.setHorizontalAlignment(JTextField.RIGHT);
        checkBox.setSelected(use);
        panel2.add(textField);
        panel2.add(label);
        panel2.add(checkBox);
        panel2.add(parkBtn);

        window.setSize(new Dimension(250, 100));
        window.setResizable(false);
        window.setLayout(new BorderLayout());
        window.add(panel1, BorderLayout.NORTH);
        window.add(panel2, BorderLayout.SOUTH);

        //入库
        enterBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.showOpenDialog(window);
            if (fileChooser.getSelectedFile() != null) {
                File pic = fileChooser.getSelectedFile();
                try {
                    String base64Str = pic2Base64(pic);
                    String res = sendPost("http://127.0.0.1:8086/reg", "data=" + base64Str);
                    System.out.println(sendPost("http://127.0.0.1:8088/api/park/enter?carID=" + URLEncoder.encode(res, "UTF-8"), ""));
                } catch (Exception ex) {
//                    ex.printStackTrace();
                }
            }
        });

        //出库
        exitBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.showOpenDialog(window);
            if (fileChooser.getSelectedFile() != null) {
                File pic = fileChooser.getSelectedFile();
                try {
                    String base64Str = pic2Base64(pic);
                    String res = sendPost("http://127.0.0.1:8086/reg", "data=" + base64Str);
                    System.out.println(sendPost("http://127.0.0.1:8088/api/park/exit?carID=" + URLEncoder.encode(res, "UTF-8"), ""));
                } catch (Exception ex) {
//                    ex.printStackTrace();
                }
            }
        });

        parkBtn.addActionListener(e -> {
            try {
                System.out.println(sendPost("http://127.0.0.1:8088/api/scatter/use?parkID=" + textField.getText() + "&use=" + use, ""));
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
        });

        checkBox.addActionListener(e -> use = checkBox.isSelected());

        textField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if(!(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)){
                    e.consume(); //关键，屏蔽掉非法输入
                }
            }
        });

        window.setVisible(true);
    }

    private static String sendPost(String url, String data) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//        con.setRequestProperty("X-Content-Type-Options", "nosniff");
//        con.setRequestProperty("", "");

        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + data);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }


    private static String pic2Base64(File file) throws IOException {
        byte[] bFile = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return Base64.getEncoder().encodeToString(bFile);
    }
}
