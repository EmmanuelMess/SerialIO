package com.emmanuelmess.serial_io;

import javax.swing.UIManager;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main {
	public static final int[] BAUDRATES = {110, 300, 600,
			1200, 4800, 9600, 14400, 19200, 38400, 57600, 
			115200, 128000, 256000};
	public static final String[] BAUDRATES_NAMES = {"110", "300", "600",
			"1200", "4800", "9600", "14400", "19200", "38400", "57600", 
			"115200", "128000", "256000"};
	
	public static final int[] BITS = {5, 6, 7, 8};
	public static final String[] BITS_NAMES = {"5", "6", "7", "8"};
	
	public static final int[] STOPBITS = {1, 2, 3};
	public static final String[] STOPBITS_NAMES = {"1", "2", "1,5"};
	
	public static final int[] PARITY = {0, 1, 2, 3, 4};
	public static final String[] PARITY_NAMES = {"NINGUNA", "PAR", "IMPAR",
			"MARCADA", "ESPACIADA"};
	
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
        c.preDisplay((baudrateIndex, databitsIndex, stopbitsIndex, parityIndex, 
        		com1, com2) -> {
        	c.display();
        	initCOMMs(BAUDRATES[baudrateIndex], BITS[databitsIndex], 
        			STOPBITS[stopbitsIndex], PARITY[parityIndex], com1, com2);
        });
	}
    
    private static void initCOMMs(int baudrate, int databits, int stopbits, int parity, 
    		String com1, String com2) {
    	// getting serial ports list into the array
    	String[] portNames = SerialPortList.getPortNames();
    	        
    	if (portNames.length == 0) {
    	    c.error("There are no serial ports");
    	}
    	
    	serialPort = new SerialPort(com1);
	    try {
	        serialPort.openPort();
	
	        serialPort.setParams(baudrate, databits, stopbits, parity);
	
	        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
	                                      SerialPort.FLOWCONTROL_RTSCTS_OUT);
	    } catch (SerialPortException ex) {
	    	c.error("There are an error on writing string to port: \n\t" + ex);
	    }
	    
    	serialPort1 = new SerialPort(com2);
	    try {
	    	serialPort1.openPort();
	
	        serialPort.setParams(baudrate, databits, stopbits, parity);
	
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