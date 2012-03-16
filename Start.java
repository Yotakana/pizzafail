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
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	// Nombre d'images pas secondes.
	private final int FPS = 60;
	// State de depart du jeu.
	private State state = State.MENU;
	// Boolean pour la Main loop
	public boolean running = true;
	// Textures pour le mode edition.
	private Texture selectTex;
	private Texture cadreSelectionTex;
	private Texture selectionTileTex = null;
	private Texture dialogueBox = null;
	// Selection par default pour le mode edition
	private int selectionTile = Tile.WALL;
	// Layer par default du mode edition.
	private int drawLayer = 0;
	// Variable pour la musique du jeu.
	private Audio music;
	// Variable pour les textes
	private TrueTypeFont font;
	private TrueTypeFont fontDialogue;
	private Font menuFont = new Font("Times New Roman", Font.BOLD, 28);
	private Font dialogueFont = new Font("Times New Roman", Font.BOLD, 14);
	private boolean antiAlias = true;
	// position de la sourie
	public int mouseX, mouseY;
	//variables pour le calcaule de delta
	private long lastFrame;
	private int delta;
	// le joueur et le monde.
	public Joueur joueur;
	public World world;

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
		case DIALOGUE:
			GraphicCall.clearScreen();
			world.draw();
			joueur.draw();
			world.drawLayer();
			drawDialogueBox();
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

	private void gameUpdate() {
		//LoadResource.animFireCamp.update(16);

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
		
		/*
		 * Method qui gere tout les Inputs clavier et sourie
		 * en fonction des states ( avec un switch ).
		 */

		// calcule des coordonnées de la souris sur la fenetre de jeu.
		mouseX = Mouse.getX();
		mouseY = 600 - Mouse.getY(); // calcul con à cause de l'axe Y inversé.

		switch (state) {

		case GAME:
			// Double la vitesse du joueur.
			if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
				joueur.boost = 2;
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
				int x = (int) Math.round((joueur.xPos / 32));
				int y = (int) Math.round((joueur.yPos / 32));
				if(joueur.currentAnim == joueur.animUp)
					y-=1;
				else if(joueur.currentAnim == joueur.animDown)
					y+=1;
				else if(joueur.currentAnim == joueur.animLeft)
					x-=1;
				else if(joueur.currentAnim == joueur.animRight)
					x+=1;
				if(world.currentGrid[x][y][1] != null && world.currentGrid[x][y][1].type == 4)
					state = State.DIALOGUE;
				
			}
			// Change le state pour que le menu s'affiche et stop la musique.
			else if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				state = State.MENU;
				music.stop();
			}
			// Change le state pour que le mode edit s'active et stop la musique.
			else if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				state = State.EDIT;
				music.stop();
			}
			// Bouge le joueur de 0.15 en fonction de la direction Y.
			// ou 0.11 si on va en diagonal.
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)
					|| Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					joueur.setDy(-.115);
				} else
					joueur.currentAnim = joueur.animUp;
					joueur.currentAnim.start();
					joueur.setDy(-.15);
				
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)
					|| Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					joueur.setDy(.115);
				} else
					joueur.currentAnim = joueur.animDown;
					joueur.currentAnim.start();
					joueur.setDy(.15);
			} else {
			// Ni UP ni DOWN appuyé on bouge plus sur l'axe Y.
				joueur.setDy(0);
				if(joueur.currentAnim == joueur.animDown
						|| joueur.currentAnim == joueur.animUp){
					joueur.currentAnim.stop();
					joueur.currentAnim.setCurrentFrame(0);
					}
			}
			
			// Bouge le joueur de 0.15 en fonction de la direction X,
			// ou 0.11 si on va en diagonal.
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_UP)
					|| Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					joueur.currentAnim = joueur.animRight;
					joueur.currentAnim.start();
					joueur.setDx(.115);
				} else
					joueur.currentAnim = joueur.animRight;
					joueur.currentAnim.start();
					joueur.setDx(.15);
				
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_UP)
					|| Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					joueur.currentAnim = joueur.animLeft;
					joueur.currentAnim.start();
					joueur.setDx(-.115);
				} else
					joueur.currentAnim = joueur.animLeft;
					joueur.currentAnim.start();
					joueur.setDx(-.15);
			} else {
			// Ni RIGHT ni LEFT appuyé on bouge plus sur l'axe X.
				joueur.setDx(0);
				if(joueur.currentAnim == joueur.animLeft
					|| joueur.currentAnim == joueur.animRight){
				joueur.currentAnim.stop();
				joueur.currentAnim.setCurrentFrame(0);
				}
				
			}
			
			break;
		case EDIT:
			// On sauve le monde !!! (voir class WorldLoader).
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				world.wl.save(world);
			}
			// On charge le monde depuis une sauvegarde.
			else if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
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
			else if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				running = false;
			}

			break;
		case DIALOGUE:
			if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
				state = State.GAME;
			}
			break;
		}
		
	}

	private void setupEntity() {

		// Creation du monde.
		world = new World();
		// Creation du joueur.
		joueur = new Joueur(200, 200, 32, 32, "link", world);
		// initialisation de la font.
		font = new TrueTypeFont(menuFont, antiAlias);
		fontDialogue = new TrueTypeFont(dialogueFont, antiAlias);
		// initialisation de la music.
		music = LoadResource.introTheme;
		
	}

	private long getTime() {
		
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private int getDelta() {
		// Calcule le delta.
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}

	private void setUpTimer() {
		
		lastFrame = getTime();
	}

	private void drawSelect(int mousex, int mousey) {
	
		// Calcule les coordonnées de la sourie en tile de 32*32
		int x = mousex * 32;
		int y = (mousey * 32) + 32;
		
		// Si la texture na pas deja été charger ..on le fait.
		if (selectTex == null)
			selectTex = LoadResource.selectionImg.getTexture();
		
		// On dessiner le Quad avec sa texture.
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
		
		// Affichage de texte
		font.drawString(22, 22, "MODE EDITION :", Color.black);
		font.drawString(20, 20, "MODE EDITION :", Color.white);
		font.drawString(22, 52, "S : Save ", Color.black);
		font.drawString(20, 50, "S : Save ", Color.white);
		font.drawString(22, 82, "L : Load ", Color.black);
		font.drawString(20, 80, "L : Load ", Color.white);
		// On remet les couleur à zero ( sinon tout deviens blanc apres ).
		GraphicCall.resetColor();
		
		// Check si texture deja chargé.
		if (cadreSelectionTex == null)
			cadreSelectionTex = LoadResource.cadreSelectionImg.getTexture();

		// Blabla...Quad + texture.
		// Ce Quad est le coutour du preview en mode edit.
		cadreSelectionTex.bind();
		GL11.glBegin(GL11.GL_QUADS); 
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2i(48, 516);
		GL11.glTexCoord2d(cadreSelectionTex.getWidth(), 0);
		GL11.glVertex2i(84, 516);
		GL11.glTexCoord2d(cadreSelectionTex.getWidth(),cadreSelectionTex.getHeight());
		GL11.glVertex2i(84, 552);
		GL11.glTexCoord2d(0, cadreSelectionTex.getHeight());
		GL11.glVertex2i(48, 552);
		GL11.glEnd();

		// Switch pour savoir Quel texture de Tile appliquer dans le preview,
		switch (selectionTile) {

		case Tile.WALL:
			selectionTileTex = LoadResource.wallImg.getTexture();
			break;
		case Tile.GRASS:
			selectionTileTex = LoadResource.grassImg.getTexture();
			break;
		case Tile.WATER:
			selectionTileTex = LoadResource.waterImg.getTexture();
			break;
		case Tile.TREE:
			selectionTileTex = LoadResource.treeImg.getTexture();
			break;
		case Tile.ROCK:
			selectionTileTex = LoadResource.rockImg.getTexture();
			break;
		}

		// et on dessine le preview.
		selectionTileTex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2i(50, 518);
		GL11.glTexCoord2d(selectionTileTex.getWidth(), 0);
		GL11.glVertex2i(82, 518);
		GL11.glTexCoord2d(selectionTileTex.getWidth(),selectionTileTex.getHeight());
		GL11.glVertex2i(82, 550);
		GL11.glTexCoord2d(0, selectionTileTex.getHeight());
		GL11.glVertex2i(50, 550);
		GL11.glEnd();

	}

	private void drawMenu() {

		// Menu Principal.
		font.drawString(52, 22, "MENU !         Echap : Quit G : Jeu",Color.black);
		font.drawString(50, 20, "MENU !         Echap : Quit G : Jeu",Color.white);
		font.drawString(50, 550, "WOOOOOOOOO Du Text !!!!", Color.white);
		// Le reset des couleurs.
		GraphicCall.resetColor();

	}
	
	private void drawDialogueBox() {
		if (dialogueBox == null)
			dialogueBox = LoadResource.getImg("dialogue").getTexture();
		
		dialogueBox.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2i(500, 480);
		GL11.glTexCoord2d(dialogueBox.getWidth(), 0);
		GL11.glVertex2i(780, 480);
		GL11.glTexCoord2d(dialogueBox.getWidth(),dialogueBox.getHeight());
		GL11.glVertex2i(780, 580);
		GL11.glTexCoord2d(0, dialogueBox.getHeight());
		GL11.glVertex2i(500, 580);
		GL11.glEnd();
		fontDialogue.drawString(520,500 , "Robert : Bonjour Mr. l'arbre....",Color.white);
		GraphicCall.resetColor();

	}

	public static void main(String[] args) {
		
		// La method Main ...normal ^^
		new Start();

	}

	public enum State {
		// L'enum pour les states du jeu.
		MENU, GAME, EDIT, DIALOGUE;
	}

}
