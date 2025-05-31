import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

/**
 * 客户端类，用于与服务器进行通信
 */
public class Client {
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField serverIPField;
    private JTextField usernameField;

    /**
     * 构造方法，初始化GUI并显示窗口
     */
    public Client() {
        initializeGUI();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 初始化GUI组件
     */
    private void initializeGUI() {
        frame = new JFrame("聊天室客户端");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1800, 1600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 创建连接面板
        JPanel connectPanel = new JPanel(new GridLayout(3, 2));
        JLabel serverIPLabel = new JLabel("服务器IP地址:");
        serverIPField = new JTextField();
        JLabel usernameLabel = new JLabel("用户名:");
        usernameField = new JTextField();
        JButton connectButton = new JButton("连接");

        connectPanel.add(serverIPLabel);
        connectPanel.add(serverIPField);
        connectPanel.add(usernameLabel);
        connectPanel.add(usernameField);
        connectPanel.add(new JLabel());
        connectPanel.add(connectButton);

        // 添加连接面板到主面板
        mainPanel.add(connectPanel, "Connect");

        // 创建聊天面板
        JPanel chatPanel = createChatPanel();
        mainPanel.add(chatPanel, "Chat");

        // 显示连接面板
        cardLayout.show(mainPanel, "Connect");

        frame.add(mainPanel);
        frame.pack();//自动调整界面尺寸

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIP = serverIPField.getText();
                String username = usernameField.getText();
                if (!serverIP.isEmpty() && !username.isEmpty()) {
                    connectToServer(serverIP, username);
                    cardLayout.show(mainPanel, "Chat");
                } else {
                    JOptionPane.showMessageDialog(frame, "请输入服务器IP和用户名", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 创建聊天面板
     * @return 聊天面板组件
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(e -> sendMessage());

        JButton fileButton = new JButton("发送文件");
        fileButton.addActionListener(e -> sendFile());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(sendButton);
        buttonPanel.add(fileButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        return chatPanel;
    }

    /**
     * 连接到服务器
     * @param serverIP 服务器IP地址
     * @param username 用户名
     */
    private void connectToServer(String serverIP, String username) {
        try {
            Socket socket = new Socket(serverIP, 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        if (serverMessage.startsWith("FILE_PORT:")) {
                            int port = Integer.parseInt(serverMessage.substring(10));
                            handleFileTransfer(port);
                        } else {
                            chatArea.append(serverMessage + "\n");
                        }
                    }
                } catch (IOException e) {
                    chatArea.append("与服务器的连接已断开\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "无法连接到服务器", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * 处理文件传输
     * @param port 文件传输端口
     */
    private void handleFileTransfer(int port) {
        // 文件传输逻辑
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            chatArea.append("我: " + message + "\n");
            out.println(message);
            inputField.setText("");
        }
    }

    /**
     * 发送文件
     */
    private void sendFile() {
        // 文件发送逻辑
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
