package app;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.*;

import app.helpers.*;
import app.model.*;
import app.views.*;

/**
 * Executive Order Corporation we make Things Smart
 *
 * Arduino Tron AI-IoTBPM :: Internet of Things Drools-jBPM Expert System using Arduino Tron AI-IoTBPM Processing
 * Arduino Tron Drools-jBPM :: Executive Order Arduino Tron Sensor Processor MQTT AI-IoTBPM Client using AI-IoTBPM Drools-jBPM
 * Executive Order Corporation - Arduino Tron ESP8266 MQTT Telemetry Transport Machine-to-Machine(M2M)/Internet of Things(IoT)
 *
 * Executive Order Corporation
 * Copyright (c) 1978, 2019: Executive Order Corporation, All Rights Reserved
 */

/**
 * This is the main class for Raspberry Pi IoT Tron AI-IoTBPM Drools-jBPM Expert System
 */

public class Main {

	public static User USER;

	public static String id = ""; // 123456
	public static String name = ""; // IoT_Parking_Kiosk
	public static String process = ""; // com.IoTParkingKiosk
	public static String server = ""; // http://10.0.0.2:5055

	public static String gpio = ""; // create gpio controller
	
	public static void readProperties() {
		try {
			File file = new File("iotbpm.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				if (key.indexOf("id") != -1) {
					id = value;
				}
				if (key.indexOf("name") != -1) {
					name = value;
				}
				if (key.indexOf("process") != -1) {
					process = value;
				}
				if (key.indexOf("server") != -1) {
					server = value;
				}
				if (key.indexOf("gpio") != -1) {
					gpio = value;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createAndShowGUI() {
		readProperties();
		UITheme.setLookAndFeel();
		JFrame frame = new JFrame();
  		UIToolbox.fullscreen(frame);
  		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DBManager.SELF.destroy();
			}
		});
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					DBManager.SELF.destroy();
					System.exit(0);
				}
				return false;
			}
		});
		frame.setLayout(MultiPanel.SELF.setParent(frame));
		MultiPanel.SELF.add(new WelcomePage());
		MultiPanel.SELF.add(new LoginPage());
		MultiPanel.SELF.add(new HomePage());
		MultiPanel.SELF.add(new UserPage());
		MultiPanel.SELF.add(new ChangePINPage());
		MultiPanel.SELF.add(new VehiclesPage());
		MultiPanel.SELF.add(new EditVehiclePage());
		MultiPanel.SELF.add(new InsurancePage());
		MultiPanel.SELF.add(new SubscriptionPage());
		MultiPanel.SELF.add(new NewPermitPage());
		MultiPanel.SELF.add(new PermitDeniedPage());
		MultiPanel.SELF.add(new CreateVehiclePage());
		MultiPanel.SELF.add(new LicensePage());
		MultiPanel.SELF.add(new PayNowPage());
		MultiPanel.SELF.add(new ReceiptPage());
		MultiPanel.SELF.add(new HistoryPage());
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] arg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
