/**
 * 
 */
package myapp;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * @author Leonardone
 *
 */
class MainCanvas extends GameCanvas implements Runnable {

	private Font mediumFont = null;
	private Sprite puyo = null;
	private TiledLayer field = null;
	private int score = 0;
	private Graphics g;
	private int gamemode = 0;
	
	/**
	 * @throws IOException 
	 * 
	 */
	public MainCanvas() throws IOException {
		super(false);
		
		Image image = Image.createImage("/puyo.png");
		
		puyo = new Sprite(image, 24, 20);
		
		field = new TiledLayer(10, 15, image, 24, 20);
		field.setPosition(0, -16);
		field.fillCells(2, 1, 6, 12, 1); // MainField
		field.fillCells(0, 0, 2, 15, 8); // Left Wall
		field.fillCells(8, 0, 2, 15, 8); // Right Wall
		field.fillCells(9, 1, 1, 4, 1);  // Next Puyo
		field.fillCells(2, 0, 6, 1, 8);  // Upper Wall
		field.fillCells(2, 13, 6, 2, 8); // Under Wall
		
		mediumFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		
		score = 1234500; // Test
	}
	
	private void drawScore() {
		String scoreStr = Integer.toString(score); 
		g.setFont(mediumFont);
		g.setColor(0x000000);
		g.drawString(scoreStr, 121, 252, Graphics.TOP | Graphics.HCENTER);
		g.setColor(0xFFFFFF);
		g.drawString(scoreStr, 120, 251, Graphics.TOP | Graphics.HCENTER);
	}
	
	private void drawNextPuyo() {
		
		puyo.setFrame(2);
		puyo.setPosition(216, 4);
		puyo.paint(g);
		puyo.setFrame(3);
		puyo.setPosition(216, 24);
		puyo.paint(g);		

		puyo.setFrame(4);
		puyo.setPosition(230, 44);
		puyo.paint(g);
		puyo.setFrame(5);
		puyo.setPosition(230, 64);
		puyo.paint(g);		
	}
	
	public void run() {
		g = getGraphics();
		

		
		field.paint(g);
		
		puyo.setFrame(1);
		puyo.setPosition(144, 44);
		puyo.paint(g);
		
		puyo.setFrame(4);
		puyo.setPosition(144, 64);
		puyo.paint(g);
		
		
		drawNextPuyo();
		drawScore();
		
		flushGraphics();
	}

}
