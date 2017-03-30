package com.emmanuelmess.serial_io;

import javax.swing.UIManager;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main {
	static SerialPort serialPort, serialPort1;
	static ChatGUI c;
	
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        	 e.printStackTrace();
        }
        
        c = new ChatGUI("Comunicación serial by EmmanuelMess, Alexey 'scream3r' Sokolov and hasherr", (msg)->{
        	try {
				serialPort.writeString(msg);
				c.receive("Tx", msg);
			} catch (SerialPortException e) {
				c.error("Falló al enviar (" + e.getLocalizedMessage() + ")");
			}
        });
        c.display();
        initCOMMs();
	}
    
    private static void initCOMMs() {
    	// getting serial ports list into the array
    	String[] portNames = SerialPortList.getPortNames();
    	        
    	if (portNames.length == 0) {
    	    c.error("There are no serial ports");
    	} else {
	    	StringBuilder s = new StringBuilder("Puertos abiertos: ");
	    	for (int i = 0; i < portNames.length; i++)
	    	    s.append(portNames[i] + ", ");
		    c.msg(s.delete(s.length()-2, s.length()).toString());
    	}
    	
    	serialPort = new SerialPort("COM1");
	    try {
	        serialPort.openPort();
	
	        serialPort.setParams(SerialPort.BAUDRATE_9600,
	                             SerialPort.DATABITS_8,
	                             SerialPort.STOPBITS_1,
	                             SerialPort.PARITY_NONE);
	
	        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
	                                      SerialPort.FLOWCONTROL_RTSCTS_OUT);
	    } catch (SerialPortException ex) {
	    	c.error("There are an error on writing string to port: \n\t" + ex);
	    }
	    
    	serialPort1 = new SerialPort("COM2");
	    try {
	    	serialPort1.openPort();
	
	    	serialPort1.setParams(SerialPort.BAUDRATE_9600,
	                             SerialPort.DATABITS_8,
	                             SerialPort.STOPBITS_1,
	                             SerialPort.PARITY_NONE);
	
	    	serialPort1.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
	                                      SerialPort.FLOWCONTROL_RTSCTS_OUT);
	
	    	serialPort1.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
	    } catch (SerialPortException ex) {
	    	c.error("There are an error on writing string to port: \n\t" + ex);
	    }
    }
	
	private static class PortReader implements SerialPortEventListener {
	    @Override
	    public void serialEvent(SerialPortEvent event) {
	        if(event.isRXCHAR() && event.getEventValue() > 0) {
	            try {
	                String receivedData = serialPort1.readString(event.getEventValue());
	                c.receive("Rx", receivedData);
	            } catch (SerialPortException ex) {
	                System.out.println("Error in receiving string from COM-port:\n\t" + ex);
	            }
	        }
	    }
	
	}
}