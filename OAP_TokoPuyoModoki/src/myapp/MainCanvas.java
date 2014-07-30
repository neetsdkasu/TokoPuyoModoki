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

	private Sprite puyoSprite = null;
	private TiledLayer fieldTLayer = null;
	private Graphics g;
	private int[] field;

	private int[] ranking;

	private int gamemode = 0;
	private int score = 0;

	private int drop1c = 1;
	private int drop1x = 0;
	private int drop1y = 0;
	private int drop2c = 2;
	private int drop2x = 0;
	private int drop2y = -1;
	
	private int next1 = 1;
	private int next2 = 2;
	private int next3 = 3;
	private int next4 = 4;
	
	private int dropCount = 0;

	/**
	 * @throws IOException 
	 * 
	 */
	public MainCanvas() throws IOException {
		super(false);
		
		Image image = Image.createImage("/puyo.png");
		
		puyoSprite = new Sprite(image, 24, 20);
		
		fieldTLayer = new TiledLayer(10, 15, image, 24, 20);
		fieldTLayer.setPosition(0, -16);
		fieldTLayer.fillCells(2, 1, 6, 12, 1); // MainField
		fieldTLayer.fillCells(0, 0, 2, 15, 8); // Left Wall
		fieldTLayer.fillCells(8, 0, 2, 15, 8); // Right Wall
		fieldTLayer.fillCells(9, 1, 1, 4, 1);  // Next Puyo
		fieldTLayer.fillCells(2, 0, 6, 1, 8);  // Upper Wall
		fieldTLayer.fillCells(2, 13, 6, 2, 8); // Under Wall
		
		score = 0; // Test
		ranking = new int[10];
		field = new int[128];
		
		for (int i = 0; i < 8; i++) {
			field[i] = field[i + 120] = -1;
		}
		
		for (int i = 0; i < 120; i += 8) {
			field[i] = field[i + 7] = -1;
		}
		
	}
	
	private void drawScore() {
		String scoreStr = Integer.toString(score); 
		g.setColor(0x000000);
		g.drawString(scoreStr, 121, 252, Graphics.TOP | Graphics.HCENTER);
		g.setColor(0xFFFFFF);
		g.drawString(scoreStr, 120, 251, Graphics.TOP | Graphics.HCENTER);
	}
	
	private void drawNextPuyo() {
		
		puyoSprite.setFrame(next1);
		puyoSprite.setPosition(216, 4);
		puyoSprite.paint(g);
		puyoSprite.setFrame(next2);
		puyoSprite.setPosition(216, 24);
		puyoSprite.paint(g);		

		puyoSprite.setFrame(next3);
		puyoSprite.setPosition(230, 44);
		puyoSprite.paint(g);
		puyoSprite.setFrame(next4);
		puyoSprite.setPosition(230, 64);
		puyoSprite.paint(g);		
	}
	
	private void drawRanking() {
		g.setColor(0xFFFFFF);
		
		g.drawString("RANKING", 120, 4, Graphics.TOP | Graphics.HCENTER);
		
		for (int i = 0; i < ranking.length; i++) {
			g.drawString(" 1 2 3 4 5 6 7 8 910".substring(i << 1, (i + 1) << 1)
					, 72, 24 + i * 20, Graphics.TOP | Graphics.RIGHT);
			g.drawString(Integer.toString(ranking[i]), 180, 24 + i * 20, Graphics.TOP | Graphics.RIGHT);
		}
	}
	
	private void drawDropPuyo() {
		
		if (drop1y >= 0) {
			puyoSprite.setFrame(drop1c);
			puyoSprite.setPosition(drop1x * 24 + 48, drop1y * 20 + 4);
			puyoSprite.paint(g);
		}
		
		if (drop2y >= 0) {
			puyoSprite.setFrame(drop2c);
			puyoSprite.setPosition(drop2x * 24 + 48, drop2y * 20 + 4);
			puyoSprite.paint(g);
		}
				
	}
	
	private boolean movePuyo(int dx, int dy) {
		int d1 = ((drop1y + dy + 3) << 3) + drop1x + dx + 1;
		int d2 = ((drop2y + dy + 3) << 3) + drop2x + dx + 1;
		if (field[d1] == 0 && field[d2] == 0) {
			drop1x += dx;
			drop1y += dy;
			drop2x += dx;
			drop2y += dy;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkPlayKey(int keyState) {
		switch (keyState) {
		case LEFT_PRESSED:
			movePuyo(-1, 0);
			break;
		case RIGHT_PRESSED:
			movePuyo(1, 0);
			break;
		case DOWN_PRESSED:
			movePuyo(0, 1);
			break;
		case UP_PRESSED: // Rotate Left
			break;
		case FIRE_PRESSED: // Rotate Right
			break;
		default:
			return false;
		}
		return true;
	}
	
	private void checkDrop() {
		dropCount++;
		if (dropCount > 12) {
			dropCount = 0;
			if (movePuyo(0, 1) == false) {
				gamemode = 4;
			}
		}
	}
	
	private void droped() {
		int d1 = ((drop1y + 3) << 3) + drop1x + 1;
		int d2 = ((drop2y + 3) << 3) + drop2x + 1;
		field[d1] = drop1c;
		field[d2] = drop2c;
		fieldTLayer.setCell(drop1x + 2, drop1y + 1, drop1c + 1);
		fieldTLayer.setCell(drop2x + 2, drop2y + 1, drop2c + 1);
	}
	
	public void run() {
		g = getGraphics();
		
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
		
		gamemode = 0;
		int keyState;
		long wait0, wait1;
		
		for(;;) {
			wait0 = System.currentTimeMillis();
			
			keyState = getKeyStates();
			
			switch (gamemode) {
			case 0: // Title
				if (keyState == FIRE_PRESSED) {
					gamemode = 3;
				}
				break;
			case 1: // Game Over
				if (keyState == FIRE_PRESSED) {
					gamemode = 2;
				}
				break;
			case 2: // View Ranking
				if (keyState == FIRE_PRESSED) {
					gamemode = 0;
				}
				break;
			case 3: // Playing
				if (checkPlayKey(keyState)) {
					dropCount = 0;
				}
				checkDrop();
				break;
			case 4: // 
				if (checkPlayKey(keyState)) {
					dropCount = 0;
					gamemode = 3;
				} else {
					droped();
					gamemode = 5;
				}
				break;
			}
		
			fieldTLayer.paint(g);
			drawNextPuyo();
			drawScore();
			
			switch (gamemode) {
			case 0:
				g.setColor(0xFFFFFF);
				g.drawString("PUSH [5] TO START", 120, 120, Graphics.TOP | Graphics.HCENTER);
				break;
			case 1:
				g.setColor(0xFFFFFF);
				g.drawString("GAME OVER", 120, 120, Graphics.TOP | Graphics.HCENTER);
				break;
			case 2:
				drawRanking();
				break;
			case 3:
			case 4:
				drawDropPuyo();
				break;

			}
			
			flushGraphics();
			
			// Wait
			do {
				wait1 = System.currentTimeMillis() - wait0;
			} while (wait1 < 100L);
			
		}
	}
}
