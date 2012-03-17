package jeu;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.openal.Audio;

public class Controleur {
	
	public int mouseY;
	public int mouseX;
	public Audio music;
	public int drawLayer;
	private Joueur joueur;
	private World world;
	private Menu menu;
	private State state;
	private long inputInterval = 300;
	private long lastInput = System.currentTimeMillis();
	
	public Controleur(Joueur joueur,World world,State state,Menu menu){
		
		this.joueur = joueur;
		this.world = world;
		this.state = state;
		this.menu = menu;
		
	}
	
	public void getMousePos(){
		
		mouseX = Mouse.getX();
		mouseY = 600 - Mouse.getY(); 
		
	}
	
	
	public void getInput(){
		
		/*
		 * Method qui gere tout les Inputs clavier et sourie
		 * en fonction des states ( avec un switch ).
		 */
		getMousePos();
		
		switch (state.getStatue()) {

		case GAME:
			// Double la vitesse du joueur.
			if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
				joueur.boost = 2;
			}
			else if(System.currentTimeMillis() > lastInput + inputInterval && Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
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
					state.set(State.GameState.DIALOGUE);
					lastInput = System.currentTimeMillis();
				
			}
			// Change le state pour que le menu s'affiche et stop la musique.
			else if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				state.set(State.GameState.MENU);
				Resource.introTheme.stop();
			}
			// Change le state pour que le mode edit s'active et stop la musique.
			else if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				state.set(State.GameState.EDIT);
				Resource.introTheme.stop();
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
			// Ni UP ni DOWN appuy� on bouge plus sur l'axe Y.
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
			// Ni RIGHT ni LEFT appuy� on bouge plus sur l'axe X.
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
			// Selection des Tiles pour le mode �dition avec touche 1, 2, 3...
			// drawlayer en fonction du type d'objet � placer.
			if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				menu.selectionTile = 1; // 1 = WAll.
				drawLayer = 0; 
				System.out.println("fdfds");
			} else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				menu.selectionTile = 2; // 2 = GRASS.
				drawLayer = 0;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
				menu.selectionTile = 3; // 3 = WATER.
				drawLayer = 0;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
				menu.selectionTile = 4; // 4 = TREE.
				drawLayer = 1; 
			} else if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
				menu.selectionTile = 5; // 5 = STONE.
				drawLayer = 1;
			}
			// Echap -> retour au jeu avec la musique.
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				state.set(State.GameState.GAME);
				Resource.introTheme.playAsMusic(1.0f, 1.0f, true);
			}
			// Si click bouton 1 de la sourie on essaye de placer un bloc
			// uniquement si le layer correspondant est vide.
			if (Mouse.isButtonDown(0)) {
			// calcule des coordonn�es du block � la position de  la sourie.
				int x = (int) Math.round(mouseX / 32);
				int y = (int) Math.round(mouseY / 32);
				if (world.currentGrid[x][y][drawLayer] == null) {
					world.currentGrid[x][y][drawLayer] = new Tile(x
							* Tile.WIDTH, (y * Tile.HEIGHT), menu.selectionTile);
			// Affichage pour debug, � degager plus tard.		
					System.out.println("New Block at : " + x + " , " + y);
				}
			}
			// Si click bouton 2 de la sourie on enleve le bloc,
			// en mettant la valeur du tableau egale � null.
			if (Mouse.isButtonDown(1)) {
				int x = (int) Math.round(mouseX / 32);
				int y = (int) Math.round(mouseY / 32);
				world.currentGrid[x][y][drawLayer] = null;
			// Affichage pour debug, � degager plus tard.		
				System.out.println(x + " " + y);
			}

			break;
		case MENU:
			// Touche G pour passer du menu au jeu.
			if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
				state.set(State.GameState.GAME);
				Resource.introTheme.playAsMusic(1.0f, 1.0f, true);
			}
			
			// Si echap on casse la Main loop.
			else if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				state.setRunning(false);
			}

			break;
		case DIALOGUE:
			if (System.currentTimeMillis() > lastInput + inputInterval && Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
				state.set(State.GameState.GAME);
				lastInput = System.currentTimeMillis();
			}
			break;
		}
		
		
		
	}

}
