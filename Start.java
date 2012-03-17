package jeu;

import java.awt.Font;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

@SuppressWarnings("deprecation")
public class Start {
	
	// Taille de la fenetre du jeu.
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	// Nombre d'images pas secondes.
	private final int FPS = 60;
	//variables pour le calcaule de delta
	private long lastFrame;
	private int delta;
	// le joueur et le monde.
	public Joueur joueur;
	public World world;
	public Controleur control;
	public State state;
	public Menu menu;
	//Dialoque qui va degager.
	private Texture dialogueBox = null;
	private TrueTypeFont fontDialogue;
	private Font dialogueFont = new Font("Times New Roman", Font.BOLD, 14);
	private boolean antiAlias = true;

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
		while (state.isRunning()) {
			render();
			getInput();
			// Synchronisation affichage à "FPS" images/sec.
			Display.update();
			Display.sync(FPS);
			if (Display.isCloseRequested())
				state.setRunning(false);
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
		switch (state.getStatue()) {
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
			menu.drawSelect((int) Math.round(control.mouseX / Tile.WIDTH),
					(int) Math.round(control.mouseY / Tile.HEIGHT));
			// Affichage du Menu d'edition.
			menu.drawMenuEdit();
			break;
		case MENU:
			// On affiche,
			world.draw();
			joueur.draw();
			world.drawLayer();
			// et on rajoute le menu principal.
			menu.drawMenu();
			break;
		}

	}

	private void gameUpdate() {

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
		
		control.getInput();
	
	}

	private void setupEntity() {

		world = new World();
		joueur = new Joueur(200, 200, 32, 32, "link", world);
		state = new State();
		menu = new Menu(control);
		control = new Controleur(joueur,world,state,menu);
		
		// Dialogue donc à degager.
		fontDialogue = new TrueTypeFont(dialogueFont, antiAlias);
		
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
	
	private void drawDialogueBox() {
		
		// DialogueBox à degager.
		if (dialogueBox == null)
			dialogueBox = Resource.getImg("dialogue").getTexture();
		
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

}
