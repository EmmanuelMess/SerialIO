package com.emmanuelmess.serial_io;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author hasherr edited by EmmanuelMess
 *
 */
public class ChatGUI {

    private Sender l;
    private JFrame firstFrame, newFrame;
    private JButton sendMessage;
    private JTextField messageBox;
    private JTextArea chatBox;
    
    public ChatGUI(String appName, Sender l) {
    	firstFrame = new JFrame("Puertos [COMx]");
    	newFrame = new JFrame(appName);
    	this.l = l;
    }
    
    public void preDisplay(OnEnd end) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel lCom1 = new JLabel("Puerto para enviar:"), lCom2 = new JLabel("Puerto a escuchar:");
        JTextField com1 = new JTextField("COM1", 5), com2 = new JTextField("COM2", 5);
        JPanel center = new JPanel(); 
        center.setLayout(new GridLayout(2, 2, 5, 5));
        center.add(lCom1, 0, 0);
        center.add(com2, 0, 1);
        center.add(lCom2, 1, 0);
        center.add(com1, 1, 1);
        center.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JButton start = new JButton("Iniciar");
        start.addActionListener((event) -> {
        	firstFrame.dispose();
            end.ended(com1.getText(), com2.getText());
        });
        
        mainPanel.add(center, BorderLayout.CENTER);
        mainPanel.add(start, BorderLayout.SOUTH);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        firstFrame.add(mainPanel);
        firstFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        firstFrame.pack();
        firstFrame.setVisible(true);
        
        com1.requestFocusInWindow();
    }
    
    interface OnEnd{
    	public void ended(String com1, String com2);
    }

    public void display() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.addActionListener((actionEvent)->{
        	newChatMsg();
        });

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener((event) -> {
        	newChatMsg();
        });

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);
        southPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        mainPanel.add(BorderLayout.SOUTH, southPanel);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(470, 300);
        newFrame.setVisible(true);
        
        messageBox.requestFocusInWindow();
    }
    
    private void newChatMsg() {
	    if (messageBox.getText().length() != 0) {
	        if (messageBox.getText().equals("/clear")) {
	            chatBox.setText("");
	            messageBox.setText("");
	        } else {
	        	l.send(messageBox.getText());
	        	messageBox.setText("");
	        }
    	}
    }
    
    public void receive(String p, String msg) {
        msg("<" + p + ">  " + msg);
    }
    
    public void error(String e) {
        msg("ERROR: " + e);
    }
    
    public void msg(String e) {
        chatBox.append(e + "\n");
        messageBox.setText("");
        messageBox.requestFocus();
    }
    
    interface Sender {
    	public void send(String msg);
    }
}