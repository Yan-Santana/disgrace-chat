package src.screens.ServerLogs;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Color;

import java.io.IOException;

import src.components.Assets;
import src.infrastructure.server.ChatServer;

public class ServerLogsScreen {
    private ChatServer server;
    private JFrame frame;

    public ServerLogsScreen(ChatServer server) {
        this.server = server;
        int frameHeight = 608;
        int frameWidth = 853;

        this.frame = new JFrame("Disgrace - " + this.server.getName());
		this.frame.setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setIconImage(new Assets().getAppIcon());

        Color commonBorderColor = new Color(195, 207, 217);

        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, commonBorderColor));
        leftPanel.setMaximumSize(new Dimension(250, frameHeight));

        JPanel rightPanel = new JPanel();
        rightPanel.setMaximumSize(new Dimension(frameWidth - 250, frameHeight));

        frame.add(leftPanel);
        frame.add(rightPanel);

		this.frame.setSize(frameWidth, frameHeight);
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void show() {
        this.frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer("S.I da depressão", 7777);
        ServerLogsScreen serverLogsScreen = new ServerLogsScreen(server);
        serverLogsScreen.show();
    }
}