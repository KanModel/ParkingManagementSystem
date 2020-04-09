package me.kanmodel.gra.pms;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class PicSender {
    public static void main(String[] args) {
        JFrame window = new JFrame("图片发送");
        JButton button1 = new JButton("入库");
        JButton button2 = new JButton("出库");
        window.add(button1);
        window.add(button2);
        window.setSize(new Dimension(300, 100));
        window.setLayout(new FlowLayout());

        //入库
        button1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.showOpenDialog(window);
            if (fileChooser.getSelectedFile() != null) {
                File pic = fileChooser.getSelectedFile();
                try {
                    String base64Str = pic2Base64(pic);
                    String res = sendPost("http://127.0.0.1:8086/reg", "data=" + base64Str);
                    System.out.println(sendPost("http://127.0.0.1:8088/api/park/enter?carID=" + URLEncoder.encode(res, "UTF-8"), ""));;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //出库
        button2.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.showOpenDialog(window);
            if (fileChooser.getSelectedFile() != null) {
                File pic = fileChooser.getSelectedFile();
                try {
                    String base64Str = pic2Base64(pic);
                    String res = sendPost("http://127.0.0.1:8086/reg", "data=" + base64Str);
                    System.out.println(sendPost("http://127.0.0.1:8088/api/park/exit?carID=" + URLEncoder.encode(res, "UTF-8"), ""));;
                } catch (Exception ex) {
                    ex.printStackTrace();
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

//        System.out.println(response.toString());
        return response.toString();
    }


    private static String pic2Base64(File file) throws IOException {
        byte[] bFile = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return Base64.getEncoder().encodeToString(bFile);
    }
}
