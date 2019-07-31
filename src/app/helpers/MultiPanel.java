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
			led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
			led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);

			// provision gpio pin #02 as an input pin with its internal pull down resistor enabled
			button1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		}
		// continuously blink the led every 1/2 second for 15 seconds
		led1.blink(500, 15000);

		// continuously blink the led every 1 second
		led2.blink(1000);
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
