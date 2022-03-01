package vutbr.feec.eccProjekt.core;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {

    static ArrayList<MyFile> myFiles = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        int fileId = 0;

        JFrame frame = new JFrame("Receiver");
        frame.setSize(450, 450);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(jScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jTitle = new JLabel("File Receiver");
        jTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jTitle.setBorder(new EmptyBorder(20,0,10,0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        frame.add(jTitle);
        frame.add(jScrollPane);
        frame.setVisible(true);

        ServerSocket serverSocket = new ServerSocket(5000);

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength]; //aky velky subor to bude
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                        JPanel jPFile = new JPanel();
                        jPFile.setLayout(new BoxLayout(jPFile, BoxLayout.Y_AXIS));
                        JLabel jPFileName = new JLabel(fileName);
                        jPFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jTitle.setBorder(new EmptyBorder(10,0,10,0));

                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jPFile.setName(String.valueOf(fileId));
                            jPFile.addMouseListener(getMyMouseListener());

                            jPFile.add(jPFileName);
                            jPanel.add(jPFile);
                            frame.validate();
                        } else {
                            jPFile.setName(String.valueOf(fileId));
                            jPFile.addMouseListener(getMyMouseListener());
                            jPFile.add(jPFileName);
                            jPanel.add(jPFile);

                            frame.validate();
                        }
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        fileId++;
                    }
                }
            } catch (IOException er) {
                er.printStackTrace();
            }
        }
        //}
    }



    public static  String getFileExtension(String fileName) { //je mozne posielat iba simple files, ako .txt, .pdf
        int i = fileName.lastIndexOf('.');
        if(i> 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
        JFrame jFrame = new JFrame("Downloader");
        jFrame.setSize(400,400);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JLabel jTitle = new JLabel("Downloader");
        jTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jTitle.setBorder(new EmptyBorder(20,0,10,0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jPrompt = new JLabel("Are you sure you want to download?" + fileName);
        jPrompt.setFont(new Font("Arial", Font.BOLD, 15));
        jPrompt.setBorder(new EmptyBorder(20,0,10,0));
        jPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jBYes = new JButton("Yes");
        jBYes.setPreferredSize(new Dimension(150,75));
        jBYes.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jBNo = new JButton("No");
        jBNo.setPreferredSize(new Dimension(150,75));
        jBNo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel jFileContent = new JLabel();
        jFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(20,0,10,0));
        jpButtons.add(jBYes);
        jpButtons.add(jBNo);

        //check, ci sa zobrazi preview, text...

        jBYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToDownload = new File(fileName);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();
                    jFrame.dispose();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        });
        jBNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });
        jPanel.add(jTitle);
        jPanel.add(jPrompt);
        jPanel.add(jFileContent);
        jPanel.add(jpButtons);

        jFrame.add(jPanel);

        return jFrame;

    }

    public static MouseListener getMyMouseListener(){
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { //otvor subor ktory bol kliknuty
                JPanel jPanel = (JPanel) e.getSource();
                int fileId = Integer.parseInt(jPanel.getName());
                for (MyFile myFile: myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }
}
