package app.helpers;

import app.Main;
import app.server.AgentConnect;
import app.views.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
// import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
// import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
// import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * This class facilitate switching between views (pages)
 * in the application. Provides multi-panel design
 * pattern in the application. Is a singleton.
 */
public class MultiPanel extends CardLayout {

    public static final MultiPanel SELF = new MultiPanel(); // Reference to singleton

    private final Map<String, AbstractView> VIEWS =
        new HashMap<String, AbstractView>();        // Map of view name to views
    private JFrame parent = null;                   // Reference to parent container

    GpioController gpio;

    // provision gpio pin #01 & #03 as an output pins and blink
    GpioPinDigitalOutput led1;
    GpioPinDigitalOutput led2;

    // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
    GpioPinDigitalInput button1;
    
    private MultiPanel() { } // private constructor

    /**
     * Set the parent (wrapping) container.
     *
     * @param parent    the wrapping container
     * @return          this object (for chaining)
     */
    public MultiPanel setParent(JFrame parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Adds the given view and associates it with the given naem.
     * Returns true if successfully added, otherwise false.
     * Condition to add views: name unique (does not already exist) and
     * parent is set.
     *
     * @param name      the lookup (view name) string
     * @param av        the view to add
     * @return          true if successfully added, otherwise false.
     */
    public boolean add(String name, AbstractView av) {
        if (parent != null && !VIEWS.containsKey(name)) {
            addLayoutComponent(av, name);
            VIEWS.put(name, av);
            parent.add(av);
            return true;
        }
        return false;
    }
    public boolean add(AbstractView av) {
        return add(av.getName(), av);
    }

    /**
     * Display the view associated with the first name.
     * Returns true if successfully added, otherwise false.
     * Condition to add views: name exists and parent is set.
     *
     * @param name      of the view to display
     * @param args      arguments need to display the view
     * @return          true if successfully added, otherwise false.
     */
    public boolean show(String name, Object... args) {
        if (parent != null && VIEWS.containsKey(name)) {
            System.out.println("SHOW VIEW: " + name + " :: " + Arrays.toString(args));
            show(parent.getContentPane(), name);
            VIEWS.get(name).prepareView(args);

			if (name == "RECEIPT") {
				gpioController();
				serverIoTSendPost();
			}

			return true;
		}
		return false;
	}

	/**
	 *    Raspberry Pi Pinout
	 *      3V3  (1)  (2) 5V
	 *    GPIO2  (3)  (4) 5V
	 *    GPIO3  (5)  (6) GND
	 *    GPIO4  (7)  (8) GPIO14
	 *      GND  (9) (10) GPIO15
	 *   GPIO17 (11) (12) GPIO18
	 *   GPIO27 (13) (14) GND
	 *   GPIO22 (15) (16) GPIO23
	 *      3V3 (17) (18) GPIO24
	 *   GPIO10 (19) (20) GND
	 *    GPIO9 (21) (22) GPIO25
	 *   GPIO11 (23) (24) GPIO8
	 *      GND (25) (26) GPIO7
	 *    GPIO0 (27) (28) GPIO1
	 *    GPIO5 (29) (30) GND
	 *    GPIO6 (31) (32) GPIO12
	 *   GPIO13 (33) (34) GND
	 *   GPIO19 (35) (36) GPIO16
	 *   GPIO26 (37) (38) GPIO20
	 *      GND (39) (40) GPIO21
	 *
	 * BerryClip+ - 6 LED - 2 Switch - 1 Buzzer Board
	 * Hardware Reference
	 * =============================
	 * The components are connected to the main Pi GPIO header (P1)
	 * LED 1    - P1-07 - GPIO4
	 * LED 2    - P1-11 - GPIO17
	 * LED 3    - P1-15 - GPIO22
	 * LED 4    - P1-19 - GPIO10
	 * LED 5    - P1-21 - GPIO9
	 * LED 6    - P1-23 - GPIO11
	 * Buzzer   - P1-24 - GPIO8
	 * Switch 1 - P1-26 - GPIO7
	 * Swtich 2 - P1-22 - GPIO25
	 *
	 * Jam HAT - 6 LED - 2 Switch - 1 Buzzer Board
	 * The table below shows the pin numbers for BCM, Board and the matching GPIO Zero objects.
	 * |Component |GPIO.BCM | BOARD  |GPIO Zero object | Notes |
	 * |----------|---------|--------|-----------------|-------|
	 * | LED1     | GPIO 5  | Pin 29 | lights_1.red    |   	   |
	 * | LED2     | GPIO 6  | Pin 31 | lights_2.red    |   	   |
	 * | LED3     | GPIO 12 | Pin 32 | lights_1.yellow |       |
	 * | LED4     | GPIO 13 | Pin 33 | lights_2.yellow |       |
	 * | LED5     | GPIO 16 | Pin 36 | lights_1.green  |       |
	 * | LED6     | GPIO 17 | Pin 11 | lights_2.green  |       |
	 * | Button 1 | GPIO 19 | Pin 35 | button_1        | Connected to R8/R10 |
	 * | Button 2 | GPIO 18 | Pin 12 | button_2        | Connected to R7/R9 |
	 * | Buzzer   | GPIO 20 | Pin 38 | buzzer          |       |
	 *
	 * Wiring Pi - GPIO Interface library for the Raspberry Pi
	 * +-----+-----+---------+------+---+---Pi 4B--+---+------+---------+-----+-----+
	 * | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 	 * +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
	 * |     |     |    3.3v |      |   |  1 || 2  |   |      | 5v      |     |     |
	 * |   2 |   8 |   SDA.1 |   IN | 1 |  3 || 4  |   |      | 5v      |     |     |
	 * |   3 |   9 |   SCL.1 |   IN | 1 |  5 || 6  |   |      | 0v      |     |     |
	 * |   4 |   7 | GPIO. 7 |   IN | 1 |  7 || 8  | 1 | IN   | TxD     | 15  | 14  |
	 * |     |     |      0v |      |   |  9 || 10 | 1 | IN   | RxD     | 16  | 15  |
	 * |  17 |   0 | GPIO. 0 |  OUT | 0 | 11 || 12 | 0 | OUT  | GPIO. 1 | 1   | 18  |
	 * |  27 |   2 | GPIO. 2 |   IN | 0 | 13 || 14 |   |      | 0v      |     |     |
	 * |  22 |   3 | GPIO. 3 |  OUT | 0 | 15 || 16 | 0 | IN   | GPIO. 4 | 4   | 23  |
	 * |     |     |    3.3v |      |   | 17 || 18 | 0 | OUT  | GPIO. 5 | 5   | 24  |
	 * |  10 |  12 |    MOSI |   IN | 0 | 19 || 20 |   |      | 0v      |     |     |
	 * |   9 |  13 |    MISO |   IN | 0 | 21 || 22 | 1 | OUT  | GPIO. 6 | 6   | 25  |
	 * |  11 |  14 |    SCLK |   IN | 0 | 23 || 24 | 1 | IN   | CE0     | 10  | 8   |
	 * |     |     |      0v |      |   | 25 || 26 | 1 | IN   | CE1     | 11  | 7   |
	 * |   0 |  30 |   SDA.0 |   IN | 1 | 27 || 28 | 1 | IN   | SCL.0   | 31  | 1   |
	 * |   5 |  21 | GPIO.21 |  OUT | 0 | 29 || 30 |   |      | 0v      |     |     |
	 * |   6 |  22 | GPIO.22 |  OUT | 0 | 31 || 32 | 0 | OUT  | GPIO.26 | 26  | 12  |
	 * |  13 |  23 | GPIO.23 |  OUT | 0 | 33 || 34 |   |      | 0v      |     |     |
	 * |  19 |  24 | GPIO.24 |   IN | 0 | 35 || 36 | 1 | OUT  | GPIO.27 | 27  | 16  |
	 * |  26 |  25 | GPIO.25 |   IN | 0 | 37 || 38 | 0 | IN   | GPIO.28 | 28  | 20  |
	 * |     |     |      0v |      |   | 39 || 40 | 0 | IN   | GPIO.29 | 29  | 21  |
	 * +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
	 * | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
	 * +-----+-----+---------+------+---+---Pi 4B--+---+------+---------+-----+-----+
	 */
    
	/**
	 * This code demonstrates how to perform simple blinking LED logic of a
	 * GPIO pin on the Raspberry Pi using the Pi4J library.
	 */
	void gpioController() {
		if ((Main.gpio == "") || (Main.gpio.indexOf("none") != -1)) {
			System.err.println(
					"Note: create gpio controller e.g. gpio=GPIO_01 not defined in iotbpm.properties file.");
			return;
		}

		if (gpio == null) {
			// create gpio controller
			gpio = GpioFactory.getInstance();

			// provision gpio pin #01 & #03 as an output pins and blink
			led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21);
			led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22);

			// provision gpio pin #02 as an input pin with its internal pull down resistor enabled
			button1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN);
		}
		// continuously blink the led every 1/2 second for 15 seconds
		led1.blink(500, 15000);

		// continuously blink the led every 1 second
		led2.blink(1000, 15000);

		// stop all GPIO activity/threads
		// (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		// gpio.shutdown(); // <--- implement this method call if you wish to terminate the
		// Pi4J GPIO controller
	}

	void serverIoTSendPost() {
		String postURL = Main.server;
		if ((postURL == "") || (postURL.indexOf("0.0.0.0") != -1)) {
			System.err.println("Note: IoT Kiosk server " + postURL
					+ " in server=http://10.0.0.2/... not defined in iotbpm.properties file.");
			return;
		}

		String postMsg = "/?id=" + Main.id;

		java.util.Date date = new Date();
		long fixtime = date.getTime();
		fixtime = (long) (fixtime * 0.001);
		postMsg = postMsg + "&timestamp=" + Long.toString(fixtime);

		postMsg = postMsg + "&event=" + Main.USER.getID();
		postMsg = postMsg + "&process=" + Main.process;
		postMsg = postMsg + "&name=" + Main.name;
		postMsg = postMsg + "&keypress=1.0";

		AgentConnect agentConnect = new AgentConnect();
		agentConnect.sendPost(postURL, postMsg);
		// agentConnect.sendGet(postURL, postMsg);
	}
}
