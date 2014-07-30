/**
 * 
 */
package myapp;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * @author Leonardone
 *
 */
class MainCanvas extends GameCanvas implements Runnable {

	private Graphics g = null;
	private Image puyoImage = null;
	private TiledLayer field = null;
	/**
	 * @throws IOException 
	 * 
	 */
	public MainCanvas() throws IOException {
		super(false);
		
		puyoImage = Image.createImage("/puyo.png");
		
		field = new TiledLayer(10, 15, puyoImage, 24, 20);
		field.setPosition(0, -15);
		field.fillCells(2, 1, 6, 12, 2); // MainField
		field.fillCells(0, 0, 2, 15, 8); // Left Wall
		field.fillCells(8, 0, 2, 15, 8); // Right Wall
		field.fillCells(2, 0, 6, 1, 8);  // Upper Wall
		field.fillCells(2, 13, 6, 2, 8); // Under Wall

		g = getGraphics();
	}
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		field.paint(g);
		flushGraphics();
	}

}
