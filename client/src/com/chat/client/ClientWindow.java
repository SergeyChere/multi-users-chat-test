package com.chat.client;

import com.chat.metwork.TCPConnection;
import com.chat.metwork.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDRESS = "172.17.44.161";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField nickname = new JTextField("admin");
    private final JTextField input = new JTextField();
    private TCPConnection tcpConnection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        input.addActionListener(this);
        add(input, BorderLayout.SOUTH);
        add(nickname, BorderLayout.NORTH);
        setVisible(true);
        try {
            tcpConnection = new TCPConnection(IP_ADDRESS, PORT, this);
        } catch (IOException exc) {
            printMessage("Connection exception: "+exc);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = input.getText();
        if (message.equals("")) return;
        input.setText(null);
        tcpConnection.sendString(nickname.getText()+": "+message);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exc) {
        printMessage("Connection exception: "+exc);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg+"\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
