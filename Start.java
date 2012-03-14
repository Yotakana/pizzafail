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
	
	// Taille de la fenetre du jeu.
	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	// Nombre d'images pas secondes.
	private static int FPS = 60;
	// State de depart du jeu.
	private State state = State.MENU;
	// Boolean pour la Main loop
	public boolean running = true;
	// Textures pour le mode edition.
	private Texture selectTex;
	private Texture cadreSelectionTex;
	private Texture selectionTileTex = null;
	// Selection par default pour le mode edition
	private int selectionTile = Tile.WALL;
	// Layer par default du mode edition.
	private int drawLayer = 0;
	// Variable pour la musique du jeu.
	private Audio music = Sound.intro;
	// Variable pour les textes
	private TrueTypeFont font;
	private Font menuFont = new Font("Times New Roman", Font.BOLD, 24);
	private boolean antiAlias = true;
	public int mouseX, mouseY;
	public Joueur joueur;
	public World world;
	private long lastFrame;
	private int delta;

	public Start() {

		// Initialistion de la Fenetre.
		GraphicCall.setUpDisplay(WIDTH, HEIGHT);
		// Initialistion de OpenGL.
		GraphicCall.setUpOpenGL(WIDTH, HEIGHT);
		// Creation des Ojbets de depart ( joueur, world ..etc).
		setupEntity();
		// Stock l'heure system pour le calcule de delta.
		setUpTimer();
		// Main Loop.
		while (running) {
			render();
			getInput();
			// Synchronisation affichage à "FPS" images/sec.
			Display.update();
			Display.sync(FPS);
			if (Display.isCloseRequested())
				running = false;
		}
		// On kill OpenAL.
		AL.destroy();
		// On kill la fenetre.
		Display.destroy();
		System.exit(0);
	}

	private void render() {

		// Method render avec switch sur le state du jeu ( menu, edit..etc )
		// pour determiner les objets à afficher.
		switch (state) {
		case GAME:
			// On efface l'ecran,
			GraphicCall.clearScreen();
			// on calcule les changements / deplacements,
			gameUpdate();
			// et on affiche.
			world.draw();
			joueur.draw();
			world.drawLayer();
			break;
		case EDIT:
			// On efface l'ecran,
			GraphicCall.clearScreen();
			// et on affiche.
			world.draw();
			joueur.draw();
			world.drawLayer();
			// Affichage du curseur de selection.
			drawSelect((int) Math.round(mouseX / Tile.WIDTH),
					(int) Math.round(mouseY / Tile.HEIGHT));
			// Affichage du Menu d'edition.
			drawMenuEdit();
			break;
		case MENU:
			// On affiche,
			world.draw();
			joueur.draw();
			world.drawLayer();
			// et on rajoute le menu principal.
			drawMenu();
			break;
		}

	}

	public void gameUpdate() {

		/*
		 * Method qui recroupe tout les calculs de mouvement joueur, mobs,
		 * changement de maps ....
		 */
		
		// Check si notre map n'est pas deja la dernière sur l'axe X,
		if (joueur.getxPos() >= WIDTH - joueur.width && world.xCoord + 1 < world.MAX_COL) {
			
			// Change les coordonnées de la map à charger.
			world.setMapCoord(world.xCoord + 1, world.yCoord);
			// Change la map à charger.
			world.setMap(world.mapID);
			// Repop le joueur à gauche de l'ecran à x = 1 pour ne pas activer,
			// un autre changement de map (qui aurait lieu si x = 0 ).
			joueur.setxPos(1);
			// debug - à degager, s'affiche a chaque changement de map.
			System.out.println("MapID : " + world.mapID + " at : "
					+ world.xCoord + " , " + world.yCoord);
			
		// sinon check si notre map n'est pas deja la première sur l'axe X.
		} else if (joueur.getxPos() <= 0 && world.xCoord > 0) {

			world.setMapCoord(world.xCoord - 1, world.yCoord);
			world.setMap(world.mapID);
			joueur.setxPos(WIDTH - joueur.width - 1);
			// debug - à degager, s'affiche a chaque changement de map.
			System.out.println("MapID : " + world.mapID + " at : "
					+ world.xCoord + " , " + world.yCoord);

		}
		
		// Check si notre map n'est pas deja la dernière sur l'axe Y,
		if (joueur.getyPos() >= HEIGHT - joueur.height && world.yCoord + 1 < world.MAX_ROW) {

			world.setMapCoord(world.xCoord, world.yCoord + 1);
			world.setMap(world.mapID);
			joueur.setyPos(1);
			// debug - à degager, s'affiche a chaque changement de map.
			System.out.println("MapID : " + world.mapID + " at : "
					+ world.xCoord + " , " + world.yCoord);
			
		// sinon check si notre map n'est pas deja la première sur l'axe Y.
		} else if (joueur.getyPos() <= 0 && world.yCoord > 0) {

			world.setMapCoord(world.xCoord, world.yCoord - 1);
			world.setMap(world.mapID);
			joueur.setyPos(HEIGHT - joueur.height - 1);
			// debug - à degager, s'affiche a chaque changement de map.
			System.out.println("MapID : " + world.mapID + " at : "
					+ world.xCoord + " , " + world.yCoord);

		}

		// Calcule du delta.
		delta = getDelta();
		// Envoye du delta a joueur pour le calcule de la vitesse de
		// deplacement.
		joueur.updateDelta(delta);
		// Update de la position du joueur.
		joueur.move();
		// Update des Elements du world ( mobs, map en cours d'affichage...etc )
		world.update();

	}

	private void getInput() {

		// Method qui gere tout les Inputs clavier et sourie
		// en fonction des states ( avec un switch ).

		// calcule des coordonnées de la souris sur la fenetre de jeu.
		mouseX = Mouse.getX();
		mouseY = 600 - Mouse.getY(); // calcul con à cause de l'axe Y inversé.

		switch (state) {

		case GAME:
			// Double la vitesse du joueur.
			if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
				joueur.boost = 2;
			}
			// Change le state pour que le menu s'affiche et stop la musique.
			if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				state = State.MENU;
				music.stop();
			}
			// Change le state pour que le mode edit s'active et stop la musique.
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				state = State.EDIT;
				music.stop();
			}
			// Bouge le joueur de 0.15 en fonction de la direction Y.
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				joueur.setDy(-.15);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				joueur.setDy(.15);
			} else {
			// Si pas de touche appuyer on bouge plus.
				joueur.setDy(0); // si pas de touche appuyer on bouge plus
			}
			// Bouge le joueur de 0.15 en fonction de la direction Y.
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				joueur.setDx(.15);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				joueur.setDx(-.15);
			} else {
			// Si pas de touche appuyer on bouge plus.
				joueur.setDx(0);

			}
			break;
		case EDIT:
			// On sauve le monde !!! (voir class WorldLoader).
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				world.wl.save(world);
			}
			// On charge le monde depuis une sauvegarde.
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
				world.wl.load(world);
			}
			// Selection des Tiles pour le mode édition avec touche 1, 2, 3...
			// drawlayer en fonction du type d'objet à placer.
			if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				selectionTile = 1; // 1 = WAll.
				drawLayer = 0; 
			} else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				selectionTile = 2; // 2 = GRASS.
				drawLayer = 0;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
				selectionTile = 3; // 3 = WATER.
				drawLayer = 0;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
				selectionTile = 4; // 4 = TREE.
				drawLayer = 1; 
			} else if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
				selectionTile = 5; // 5 = STONE.
				drawLayer = 1;
			}
			// Echap -> retour au jeu avec la musique.
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				state = State.GAME;
				music.playAsMusic(1.0f, 1.0f, true);
			}
			// Si click bouton 1 de la sourie on essaye de placer un bloc
			// uniquement si le layer correspondant est vide.
			if (Mouse.isButtonDown(0)) {
			// calcule des coordonnées du block à la position de  la sourie.
				int x = (int) Math.round(mouseX / 32);
				int y = (int) Math.round(mouseY / 32);
				if (world.currentGrid[x][y][drawLayer] == null) {
					world.currentGrid[x][y][drawLayer] = new Tile(x
							* Tile.WIDTH, (y * Tile.HEIGHT), selectionTile);
			// Affichage pour debug, à degager plus tard.		
					System.out.println("New Block at : " + x + " , " + y);
				}
			}
			// Si click bouton 2 de la sourie on enleve le bloc,
			// en mettant la valeur du tableau egale à null.
			if (Mouse.isButtonDown(1)) {
				int x = (int) Math.round(mouseX / 32);
				int y = (int) Math.round(mouseY / 32);
				world.currentGrid[x][y][drawLayer] = null;
			// Affichage pour debug, à degager plus tard.		
				System.out.println(x + " " + y);
			}

			break;
		case MENU:
			// Touche G pour passer du menu au jeu.
			if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
				state = State.GAME;
				music.playAsMusic(1.0f, 1.0f, true);
			}
			
			// Si echap on casse la Main loop.
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				running = false;
			}

			break;

		}
	}

	private void setupEntity() {

		// Initialise tout les objets necessaire au jeu.
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

	private void drawSelect(int mousex, int mousey) {

		int x = mousex * 32;
		int y = (mousey * 32) + 32;

		if (selectTex == null)
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

		font.drawString(20, 20, "MODE EDITION :", Color.black);
		font.drawString(20, 50, "S : Save ", Color.black);
		font.drawString(20, 80, "L : load ", Color.black);
		GraphicCall.resetColor();

		if (cadreSelectionTex == null)
			cadreSelectionTex = Image.cadreSelection;

		cadreSelectionTex.bind();

		GL11.glBegin(GL11.GL_QUADS); // BackGroung pour le preview des Tiles.
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2i(48, 516);
		GL11.glTexCoord2d(cadreSelectionTex.getWidth(), 0);
		GL11.glVertex2i(84, 516);
		GL11.glTexCoord2d(cadreSelectionTex.getWidth(),
				cadreSelectionTex.getHeight());
		GL11.glVertex2i(84, 552);
		GL11.glTexCoord2d(0, cadreSelectionTex.getHeight());
		GL11.glVertex2i(48, 552);
		GL11.glEnd();

		switch (selectionTile) {

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
		GL11.glTexCoord2d(selectionTileTex.getWidth(),
				selectionTileTex.getHeight());
		GL11.glVertex2i(82, 550);
		GL11.glTexCoord2d(0, selectionTileTex.getHeight());
		GL11.glVertex2i(50, 550);
		GL11.glEnd();

	}

	private void drawMenu() {

		font.drawString(52, 22, "MENU !         Echap : Quit G : Jeu",
				Color.black);
		font.drawString(50, 20, "MENU !         Echap : Quit G : Jeu",
				Color.white);
		font.drawString(50, 550, "WOOOOOOOOO Du Text !!!!", Color.white);
		GraphicCall.resetColor();

	}

	public static void main(String[] args) {

		new Start();

	}

	public enum State {
		MENU, GAME, EDIT;
	}

}
