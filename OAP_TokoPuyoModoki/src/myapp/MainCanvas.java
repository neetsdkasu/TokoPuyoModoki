/**
 * 
 */
package myapp;

import java.io.IOException;
import java.util.Random;

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
	
	private Random rand;
	
	private Sprite puyoSprite = null;
	private TiledLayer fieldTLayer = null;
	private Graphics g;
	private int[] field;
	private int[] anime;
	private int[] eFlags;
	private int[] eFlagCount;
	
	private int[] ranking;

	private int gamemode = 0;
	private int score = 0;

	private int drop1c = 1;
	private int drop1x = 0;
	private int drop1y = 0;
	private int drop2c = 2;
	private int drop2x = 0;
	private int drop2y = -1;
	
	private int spindle = 2;
	
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
		
		rand = new Random();
		
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
		
		anime = new int[6];
		for (int i = 0; i < 6; i++) {
			anime[i] = fieldTLayer.createAnimatedTile(i + 9);
		}
		
		score = 0; // Test
		ranking = new int[10];
		field = new int[128];
		eFlags = new int[128];
		eFlagCount = new int[128];
		
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
			g.drawSubstring(" 1 2 3 4 5 6 7 8 910", i << 1, (i + 1) << 1
					, 72, 24 + i * 20, Graphics.TOP | Graphics.RIGHT);
			g.drawString(Integer.toString(ranking[i]), 180, 24 + i * 20, Graphics.TOP | Graphics.RIGHT);
		}
	}
	
	private void drawDropPuyo() {
		int x, y;
		
		if (drop1y >= 0) {
			x = drop1x * 24 + 48;
			y = drop1y * 20 + 4;
			
			puyoSprite.setFrame(drop1c);
			puyoSprite.setPosition(x, y);
			puyoSprite.paint(g);
			
			if (spindle == 1 && (dropCount & 0x1) == 0) {
				g.setColor(0x777733);
				g.drawRect(x, y, 24, 20);
			}
			
		}
		
		if (drop2y >= 0) {
			x = drop2x * 24 + 48;
			y = drop2y * 20 + 4;
			
			puyoSprite.setFrame(drop2c);
			puyoSprite.setPosition(x, y);
			puyoSprite.paint(g);
			
			if (spindle == 2 && (dropCount & 0x1) == 0) {
				g.setColor(0x777733);
				g.drawRect(x, y, 24, 20);
			}
			
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
			return movePuyo(-1, 0);
		case RIGHT_PRESSED:
			return movePuyo(1, 0);
		case DOWN_PRESSED:
			return movePuyo(0, 1);
		case UP_PRESSED: // Rotate Left
			break;
		case FIRE_PRESSED: // Rotate Right
			break;
		}
		return false;
	}
	
	private void checkDrop() {
		dropCount++;
		if (dropCount > 6) {
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
	
	private boolean checkAllDrops() {
		int drops = 0;
		for (int i = 120; i > 8; i--) {
			if (field[i] == 0 && field[i - 8] > 0) {
				drops++;
				field[i] = field[i - 8];
				field[i - 8] = 0;
			}
		}
		
		return drops > 0;
	}
	
	private boolean checkErasePuyo() {
		
		return false;
	}
	
	private void initGame() {
		score = 0;
	}
	
	private void setNextPuyo() {
		drop1c = next1;
		drop1x = 2;
		drop1y = -2;
		
		drop2c = next2;
		drop2x = 2;
		drop2y = -1;
		
		spindle = 2;
		
		next1 = next3;
		next2 = next4;
		
		next3 = rand.nextInt(5) + 1;
		next4 = rand.nextInt(5) + 1;
		
	}
	
	public void run() {
		g = getGraphics();
		
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
	
		next1 = rand.nextInt(5) + 1;
		next2 = rand.nextInt(5) + 1;
		next3 = rand.nextInt(5) + 1;
		next4 = rand.nextInt(5) + 1;

		gamemode = 0;
		int keyState;
		long wait0, wait1;
		
		for(;;) {
			wait0 = System.currentTimeMillis() + 120L; // add Wait count
			
			keyState = getKeyStates();
			
			switch (gamemode) {
			case 0: // Title
				if (keyState == FIRE_PRESSED) {
					initGame();
					setNextPuyo();
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
					gamemode = 6;
				}
				break;
			case 5: // 
				if (checkAllDrops() == false) {
					gamemode = 6;
				}
				break;
			case 6:
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
				wait1 = System.currentTimeMillis();
			} while (wait1 < wait0);
			
			g.setColor(0xFFFFFF);
			g.drawString(Long.toString(wait1 - wait0), 0, 0, Graphics.TOP | Graphics.LEFT);
			flushGraphics();
		}
	}
}
