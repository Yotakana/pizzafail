package jeu;

import java.awt.Font;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.opengl.Texture;

@SuppressWarnings("deprecation")
public class Start {

	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	private static int FPS = 60;
	private long lastFrame;
	private State state = State.MENU;
	public boolean running = true;
	private Texture selectTex;
	private Texture cadreSelectionTex;
	public int mouseX, mouseY;
	public Joueur joueur;
	public World world;
	private int selectionTile = Tile.WALL;
	private Texture selectionTileTex = null;
	private int drawLayer = 0;
	private int delta;
	private Audio music = Sound.intro;
	private TrueTypeFont font;
	private Font menuFont = new Font("Times New Roman", Font.BOLD, 24);
	private boolean antiAlias = true;
	
	public Start () {

		GraphicCall.setUpDisplay(WIDTH, HEIGHT);
		GraphicCall.setUpOpenGL(WIDTH, HEIGHT);
		setupEntity();
		setUpTimer();
		
		while (running) {
			render();
			getInput();
			Display.update();
			Display.sync(FPS);
			if(Display.isCloseRequested())
				running = false;
		}
		AL.destroy();
		Display.destroy();
		System.exit(0);
	}

	private void render() {

		switch (state) {
		
		case EDIT:
			GraphicCall.clearScreen();
			gameUpdate();
			world.draw();
			joueur.draw();
			world.drawLayer();
			drawSelect((int) Math.round(mouseX / Tile.WIDTH) ,(int) Math.round(mouseY / Tile.HEIGHT));
			drawMenuEdit();
			break;
		case MENU:
			world.draw();
			joueur.draw();
			gameUpdate();
			world.drawLayer();
			drawMenu();
			break;
		case GAME:
			music.stop();
			GraphicCall.clearScreen();
			gameUpdate();
			world.draw();
			joueur.draw();
			world.drawLayer();		
		}

	}

	public void gameUpdate() {
		
		if(joueur.getxPos() >= WIDTH - joueur.width && world.xCoord +1  < world.MAX_COL){
			
			world.setMapCoord(world.xCoord +1, world.yCoord);
			world.setMap(world.mapID);
			joueur.setxPos(1);
			//debug - à degager
			System.out.println("MapID : " + world.mapID + " at : " + world.xCoord + " , " + world.yCoord);
			
		}else if(joueur.getxPos() <= 0 && world.xCoord > 0){
			
			world.setMapCoord(world.xCoord -1, world.yCoord);
			world.setMap(world.mapID);
			joueur.setxPos(WIDTH - joueur.width - 1);
			//debug - à degager
			System.out.println("MapID : " + world.mapID + " at : " + world.xCoord + " , " + world.yCoord);
			
		}

		if(joueur.getyPos() >= HEIGHT - joueur.height && world.yCoord +1 < world.MAX_ROW ){
			
			world.setMapCoord(world.xCoord , world.yCoord+1);
			world.setMap(world.mapID);
			joueur.setyPos(1);
			//debug - à degager
			System.out.println("MapID : " + world.mapID + " at : " + world.xCoord + " , " + world.yCoord);
			
		}else if(joueur.getyPos() <= 0 && world.yCoord > 0 ){
			
			world.setMapCoord(world.xCoord , world.yCoord-1);
			world.setMap(world.mapID);
			joueur.setyPos(HEIGHT - joueur.height -1);
			//debug - à degager
			System.out.println("MapID : " + world.mapID + " at : " + world.xCoord + " , " + world.yCoord);
			
		}
		
		delta = getDelta();
		joueur.updateDelta(delta);
		joueur.move();
		
		switch (state) {
		case GAME:
			world.update();
		}
		
	}

	private void getInput() {
		
		mouseX = Mouse.getX();
		mouseY = 600 - Mouse.getY();

		switch (state) {

		case GAME:
			if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
				joueur.boost = 2;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
					state = State.MENU;
					music.playAsMusic(1.0f, 1.0f, true);
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				state = State.EDIT;
			}
				
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				joueur.setDy(-.15);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				joueur.setDy(.15);
			} else {
				joueur.setDy(0);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				joueur.setDx(.15);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				joueur.setDx(-.15);
			} else {
				joueur.setDx(0);

			}
			break;
			
		case EDIT:
			
			if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				selectionTile = 1;
				drawLayer = 0;
			}else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				selectionTile = 2;
				drawLayer = 0;
			}else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
				selectionTile = 3;
				drawLayer = 0;
			}else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
				selectionTile = 4;
				drawLayer = 1;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
				selectionTile = 5;
				drawLayer = 1;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				state = State.GAME;
			}
			
			if(Mouse.isButtonDown(0)){
				int x = (int) Math.round(mouseX / 32);
				int y = (int) Math.round(mouseY / 32);
				if(world.currentGrid[x][y][drawLayer] == null){
					world.currentGrid[x][y][drawLayer] = new Tile(x * Tile.WIDTH, ( y * Tile.HEIGHT) ,selectionTile);
					System.out.println("New Block at : "+ x + " , " + y);
				}
			}
			
			if(Mouse.isButtonDown(1)){
				int x = (int) Math.round(mouseX / 32);
				int y = (int) Math.round(mouseY / 32);
				System.out.println(x + " " + y);
					world.currentGrid[x][y][drawLayer] = null;
			}
			
			break;
			
		case MENU:
			if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
				state = State.GAME;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				running = false;
			}

			break;

		}
	}

	private void setupEntity() {

		world = new World();
		joueur = new Joueur(200, 200, 32, 32, "link", world);
		font = new TrueTypeFont(menuFont, antiAlias);

	}

	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}

	private void setUpTimer() {
		lastFrame = getTime();
	}

	private void drawSelect(int mousex ,int mousey){
		
		int x = mousex * 32;
		int y = (mousey * 32 ) + 32;
		
		if(selectTex == null)
			selectTex = Image.selection;
		
		selectTex.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex2d(x, y);
			GL11.glTexCoord2d(selectTex.getWidth(), 0);
			GL11.glVertex2d(x + Tile.WIDTH, y);
			GL11.glTexCoord2d(selectTex.getWidth(), selectTex.getHeight());
			GL11.glVertex2d(x + Tile.WIDTH, y - Tile.HEIGHT);
			GL11.glTexCoord2d(0, selectTex.getHeight());
			GL11.glVertex2d(x, y - Tile.HEIGHT);
		GL11.glEnd();
		
	}
	
	
	private void drawMenuEdit() {
		
		font.drawString(20, 20, "MODE EDITION", Color.black);
		GraphicCall.resetColor();
		
		
		if(cadreSelectionTex == null)
			cadreSelectionTex = Image.cadreSelection;
		
		cadreSelectionTex.bind();
		
		GL11.glBegin(GL11.GL_QUADS); //BackGroung pour le preview des Tiles.
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex2i(48, 516);
			GL11.glTexCoord2d(cadreSelectionTex.getWidth(), 0);
			GL11.glVertex2i(84, 516);
			GL11.glTexCoord2d(cadreSelectionTex.getWidth(), cadreSelectionTex.getHeight());
			GL11.glVertex2i(84, 552);
			GL11.glTexCoord2d(0, cadreSelectionTex.getHeight());
			GL11.glVertex2i(48, 552);
		GL11.glEnd();
		
		switch(selectionTile){
		
		case Tile.WALL:
			selectionTileTex = Image.wall;
			break;
		case Tile.GRASS:
			selectionTileTex = Image.grass;
			break;
		case Tile.WATER:
			selectionTileTex = Image.water;
			break;
		case Tile.TREE:
			selectionTileTex = Image.tree;
			break;
		case Tile.ROCK:
			selectionTileTex = Image.rock;
			break;
		}
		
		selectionTileTex.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2i(50, 518);
		GL11.glTexCoord2d(selectionTileTex.getWidth(), 0);
		GL11.glVertex2i(82, 518);
		GL11.glTexCoord2d(selectionTileTex.getWidth(), selectionTileTex.getHeight());
		GL11.glVertex2i(82, 550);
		GL11.glTexCoord2d(0, selectionTileTex.getHeight());
		GL11.glVertex2i(50, 550);
		GL11.glEnd();
	
	}
	
	private void drawMenu() {
		
		font.drawString(52, 22, "MENU !         Echap : Quit G : Jeu", Color.black);
		font.drawString(50, 20, "MENU !         Echap : Quit G : Jeu", Color.white);
		font.drawString(50, 550, "WOOOOOOOOO Du Text !!!!", Color.white);
		GraphicCall.resetColor();
		
	}
	
	public static void main (String[] args) {

		new Start();

		}

	public enum State {
		MENU, GAME, EDIT;
	}

}
