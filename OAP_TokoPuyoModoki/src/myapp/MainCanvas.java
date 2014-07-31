/**
 * 
 */
package myapp;

import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * @author Leonardone
 *
 */
class MainCanvas extends GameCanvas implements Runnable {
	
	private static final int MODE_TITLE = 0;
	private static final int MODE_GAMEOVER = 1;
	private static final int MODE_RANKING = 2;
	private static final int MODE_NEXTPUYO = 3;
	private static final int MODE_PLAYING = 4;
	private static final int MODE_DROP = 5;
	private static final int MODE_ERASE = 6;
	private static final int MODE_CHKGAMEOVER = 7;
	
	private Random rand;
	
	private Sprite puyoSprite;
	private TiledLayer fieldTLayer;
	private TiledLayer wallTLayer;
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
	
	private int old1x = 0;
	private int old1y = 0;
	private int old2x = 0;
	private int old2y = 0;
	
	private int spindle = 2;
	private int rotation = 0;
	
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
		
		fieldTLayer = new TiledLayer(8, 14, image, 24, 20);
		fieldTLayer.setPosition(48, 4);
		fieldTLayer.fillCells(0, 13, 6, 1, 8); // Under Wall
		
		wallTLayer = new TiledLayer(10, 15, image, 24, 20);
		wallTLayer.setPosition(0, -16);
		wallTLayer.fillCells(0, 0, 2, 15, 8); // Left Wall
		wallTLayer.fillCells(8, 0, 2, 15, 8); // Right Wall
		wallTLayer.fillCells(9, 1, 1, 4, 1);  // Next Puyo
		wallTLayer.fillCells(2, 0, 6, 1, 8);  // Upper Wall
		
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
			g.drawSubstring(" 1 2 3 4 5 6 7 8 910", i << 1, 2
					, 72, 24 + i * 20, Graphics.TOP | Graphics.RIGHT);
			g.drawString(Integer.toString(ranking[i]), 180, 24 + i * 20, Graphics.TOP | Graphics.RIGHT);
		}
	}
	
	private void drawDropPuyo() {
		int x, y;
		int minX = 240, minY = 268;
		int maxX = 0, maxY = 0;
		
		if (old1y >= 0) {
			x = old1x * 24 + 48;
			y = old1y * 20 + 4;
			
			if (x < minX) minX = x;
			if (x > maxX) maxX = x;
			if (y < minY) minY = y;
			if (y > maxY) maxY = y;
		
			puyoSprite.setFrame(0);
			puyoSprite.setPosition(x, y);
			puyoSprite.paint(g);
		}
		
		if (old2y >= 0) {
			x = old2x * 24 + 48;
			y = old2y * 20 + 4;

			if (x < minX) minX = x;
			if (x > maxX) maxX = x;
			if (y < minY) minY = y;
			if (y > maxY) maxY = y;
			
			puyoSprite.setFrame(0);
			puyoSprite.setPosition(x, y);
			puyoSprite.paint(g);
		}

		if (drop1y >= 0) {
			x = drop1x * 24 + 48;
			y = drop1y * 20 + 4;
			
			if (x < minX) minX = x;
			if (x > maxX) maxX = x;
			if (y < minY) minY = y;
			if (y > maxY) maxY = y;

			puyoSprite.setFrame(drop1c);
			puyoSprite.setPosition(x, y);
			puyoSprite.paint(g);
			
			if (spindle == 1 && (dropCount & 0x1) == 0) { // ‰ñ“]Ž²
				g.setColor(0x777733);
				g.drawRect(x, y, 23, 19);
			}
		}
		
		if (drop2y >= 0) {
			x = drop2x * 24 + 48;
			y = drop2y * 20 + 4;
			
			if (x < minX) minX = x;
			if (x > maxX) maxX = x;
			if (y < minY) minY = y;
			if (y > maxY) maxY = y;
			
			puyoSprite.setFrame(drop2c);
			puyoSprite.setPosition(x, y);
			puyoSprite.paint(g);
			
			if (spindle == 2 && (dropCount & 0x1) == 0) { // ‰ñ“]Ž²
				g.setColor(0x777733);
				g.drawRect(x, y, 23, 19);
			}
		}
		
		if (maxX != 0) {
			flushGraphics(minX, minY, maxX - minX + 24, maxY - minY + 20);
		}
	}
	
	private int p2i(int x, int y) {
		return ((y + 3) << 3) + x + 1;
	}
	
	private void backup() {
		old1x = drop1x;
		old1y = drop1y;
		old2x = drop2x;
		old2y = drop2y;
	}
	
	private boolean movePuyo(int dx, int dy) {
		int d1 = p2i(drop1x + dx, drop1y + dy);
		int d2 = p2i(drop2x + dx, drop2y + dy);
		if (field[d1] == 0 && field[d2] == 0) {
			backup();
			drop1x += dx;
			drop1y += dy;
			drop2x += dx;
			drop2y += dy;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean rotate(boolean rightturn) {
		int rx, ry, sx, sy, d, r, dr;
		
		if (rightturn) {
			r = rotation;
			dr = 1;
		} else {
			r = (rotation + 2) & 0x3;
			dr = 3;
		}
		
		backup();
		
		if (spindle == 1) {
			rx = sx = drop1x;
			ry = sy = drop1y;
		} else {
			rx = sx = drop2x;
			ry = sy = drop2y;
		}
		
		d = p2i(sx, sy);
		
		do {
			if (r == 0) {
				if (field[d + 1] == 0) {
					rx++; 
					rotation = 1;
				} else if (field[d - 1] == 0) {
					sx--; 
					rotation = 1;
				} else {
					r = (r + dr) & 0x3;
				}
			}
			if (r == 1) {
				if (field[d + 8] == 0) {
					ry++;
					rotation = 2;
				} else if (field[d - 8] == 0) {
					sy--;
					rotation = 2;
				} else {
					r = (r + dr) & 0x3;
				}
			}
			if (r == 2) {
				if (field[d - 1] == 0) {
					rx--;
					rotation = 3;
				} else if (field[d + 1] == 0) {
					sx++;
					rotation = 3;
				} else {
					r = (r + dr) & 0x3;
				}
			}
			if (r == 3) {
				if (field[d - 8] == 0) {
					ry--;
					rotation = 0;
				} else if (field[d + 8] == 0) {
					sy++;
					rotation = 0;
				} else {
					r = (r + dr) & 0x3;
				}
			}
		} while (rx == sx && ry == sy); 
		
		if (spindle == 1) {
			drop1x = sx;
			drop1y = sy;
			drop2x = rx;
			drop2y = ry;
		} else {
			drop1x = rx;
			drop1y = ry;
			drop2x = sx;
			drop2y = sy;
		}
		
		return true;
	}
	
	private boolean operate(int keyState) {
		switch (keyState) {
		case LEFT_PRESSED:
			return movePuyo(-1, 0);
		case RIGHT_PRESSED:
			return movePuyo(1, 0);
		case DOWN_PRESSED:
			return movePuyo(0, 1);
		case UP_PRESSED: // Rotate Left
			return rotate(false);
		case FIRE_PRESSED: // Rotate Right
			return rotate(true);
		}
		return false;
	}
	
	private boolean landing() {
		dropCount++;
		if (dropCount > 6) {
			dropCount = 0;
			if (movePuyo(0, 1) == false) {
				return true;
			}
		}
		return false;
	}
	
	private void landed() {
		int d1 = ((drop1y + 3) << 3) + drop1x + 1;
		int d2 = ((drop2y + 3) << 3) + drop2x + 1;
		old1y = old2y = -1;
		field[d1] = drop1c;
		field[d2] = drop2c;
		if (drop1y >= 0) {
			fieldTLayer.setCell(drop1x, drop1y, drop1c + 1);
		}
		if (drop2y >= 0) {
			fieldTLayer.setCell(drop2x, drop2y, drop2c + 1);
		}
		fieldTLayer.paint(g);
		drawScore();
		flushGraphics();
	}
	
	private boolean checkAllDrops() {
		int drops = 0;
		int x, y;
		for (int i = 120; i > 8; i--) {
			if (field[i] == 0 && field[i - 8] > 0) {
				drops++;
				field[i] = field[i - 8];
				field[i - 8] = 0;
				x = (i & 0x7) - 1; // x = (i % 8) - 1
				y = (i >> 3) - 3; // y = (i / 8) - 3;
				if (y >= 0) {
					fieldTLayer.setCell(x, y, field[i] + 1);
				}
				y--;
				if (y >= 0) {
					fieldTLayer.setCell(x, y, 1);
				}
			}
		}
		
		return drops > 0;
	}
	
	private boolean checkErasePuyo() {
		
		return false;
	}
	
	private void initGame() {
		score = 0;
		
		for (int i = 8; i < 120; i++) {
			if (field[i] > 0) {
				field[i] = 0;
			}
		}
		
		fieldTLayer.fillCells(0, 0, 6, 12, 1);
		fieldTLayer.fillCells(0, 12, 6, 2, 8); // Under Wall

		g.setColor(0x000000);
		g.fillRect(48, 4, 144, 264);

		fieldTLayer.paint(g);
		drawScore();
		
		flushGraphics();
	}
	
	private void setNextPuyo() {
		drop1c = next1;
		drop1x = 2;
		drop1y = -2;
		
		drop2c = next2;
		drop2x = 2;
		drop2y = -1;
		
		spindle = 2;
		rotation = 0;
		
		next1 = next3;
		next2 = next4;
		
		next3 = rand.nextInt(5) + 1;
		next4 = rand.nextInt(5) + 1;
	}
	
	private void switchTitle() {
		gamemode = MODE_TITLE;
		g.setColor(0x000000);
		g.fillRect(48, 4, 144, 264);
		g.setColor(0xFFFFFF);
		g.drawString("PUSH [5] TO START", 120, 120, Graphics.TOP | Graphics.HCENTER);
		drawScore();
		flushGraphics(48, 4, 144, 264);
	}
	
	private void switchGameOver() {
		gamemode = MODE_GAMEOVER;
		g.setColor(0x000000);
		g.fillRect(48, 4, 144, 264);
		g.setColor(0xFFFFFF);
		g.drawString("GAME OVER", 120, 120, Graphics.TOP | Graphics.HCENTER);
		drawScore();
		flushGraphics(48, 4, 144, 264);
	}
	
	private void switchRanking() {
		gamemode = MODE_RANKING;
		g.setColor(0x000000);
		g.fillRect(48, 4, 144, 264);
		drawRanking();
		drawScore();
		flushGraphics(48, 4, 144, 264);
	}
	
	private void switchPlaying() {
		gamemode = MODE_PLAYING;
	}
	
	private void switchNextPuyo() {
		gamemode = MODE_NEXTPUYO;
	}
	
	private void switchDrop() {
		gamemode = MODE_DROP;
	}
	
	public void run() {
		g = getGraphics();
		
		g.setColor(0x000000);
		g.fillRect(0, 0, 240, 268);
		wallTLayer.paint(g);
		flushGraphics();
		
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
	
		next1 = rand.nextInt(5) + 1;
		next2 = rand.nextInt(5) + 1;
		next3 = rand.nextInt(5) + 1;
		next4 = rand.nextInt(5) + 1;

		int keyState;
		long wait0, wait1;
		
		switchTitle();
		
		for(;;) {
			wait0 = System.currentTimeMillis() + 50L; // add Wait count
			
			keyState = getKeyStates();
			
			switch (gamemode) {
			case MODE_TITLE: // Title
				if (keyState == FIRE_PRESSED) {
					initGame();
					switchNextPuyo();
				}
				break;
			case MODE_GAMEOVER: // Game Over
				if (keyState == FIRE_PRESSED) {
					switchRanking();
				}
				break;
			case MODE_RANKING: // View Ranking
				if (keyState == FIRE_PRESSED) {
					switchTitle();
				}
				break;
			case MODE_NEXTPUYO:
				setNextPuyo();
				drawNextPuyo();
				flushGraphics(216, 4, 24, 80);
				switchPlaying();
				break;
			case MODE_PLAYING: // Playing
				if (operate(keyState)) {
					drawDropPuyo();
				} else if (landing()) {
					landed();
					switchDrop();
				} else {
					drawDropPuyo();
				}
				break;
			case MODE_DROP:
				if (checkAllDrops()) {
					fieldTLayer.paint(g);
					drawScore();
					flushGraphics(48, 4, 144, 264);
				} else {
					gamemode = MODE_ERASE;
					gamemode = MODE_CHKGAMEOVER; // test
				}
				break;
			case MODE_ERASE:
				break;
			case MODE_CHKGAMEOVER:
				if (field[p2i(2, 0)] != 0) {
					switchGameOver();
				} else {
					switchNextPuyo();
				}
				break;
			}
			
			// Wait
			do {
				wait1 = System.currentTimeMillis();
			} while (wait1 < wait0);
			
			String num = Long.toString(wait1 -wait0);
			g.setColor(0xFFFFFF);
			g.fillRect(0, 0, 48, 20);
			g.setColor(0x000000);
			g.drawString(num, 0, 0, Graphics.TOP | Graphics.LEFT);
			flushGraphics();
		}
	}
}
