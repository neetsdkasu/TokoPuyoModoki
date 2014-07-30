/**
 * 
 */
package myapp;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author Leonardone
 *
 */
public class TokoPuyoModokiMIDlet extends MIDlet implements CommandListener {

	private Command exitCommand = null; 
	private MainCanvas canvas = null;
	private Thread mainThread = null;
	
	/**
	 * 
	 * 
	 */
	public TokoPuyoModokiMIDlet()  {
		Displayable disp = null;
		exitCommand = new Command("EXIT", Command.EXIT, 1);
		try {
			canvas = new MainCanvas();
			disp = canvas;
			disp.addCommand(exitCommand);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			canvas = null;
			Alert alert = new Alert("ERROR", "起動に失敗しました", null, AlertType.ERROR);
			disp = alert;
		}
		disp.setCommandListener(this);
		Display.getDisplay(this).setCurrent(disp);
	}

	/* (非 Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {

	}

	/* (非 Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {

	}

	/* (非 Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		if (mainThread == null && canvas != null) {
			mainThread = new Thread(canvas);
			mainThread.start();
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (c.equals(exitCommand) || c.equals(Alert.DISMISS_COMMAND)) {
			notifyDestroyed();
		}
	}

}
