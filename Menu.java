package jeu;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

@SuppressWarnings("deprecation")
public class Menu {

	public Texture selectTex;
	public Texture cadreSelectionTex;
	public Texture selectionTileTex = null;
	public Texture dialogueBox = null;
	private TrueTypeFont font;
	private Font menuFont = new Font("Times New Roman", Font.BOLD, 28);
	private boolean antiAlias = true;
	public Controleur control;
	public int selectionTile = Tile.WALL;

	public Menu(Controleur control) {

		this.control = control;
		font = new TrueTypeFont(menuFont, antiAlias);

	}

	public void drawSelect(int mousex, int mousey) {

		// Calcule les coordonnées de la sourie en tile de 32*32
		int x = mousex * 32;
		int y = (mousey * 32) + 32;

		// Si la texture na pas deja été charger ..on le fait.
		if (selectTex == null)
			selectTex = Resource.selectionImg.getTexture();

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

	public void drawMenuEdit() {

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
			cadreSelectionTex = Resource.cadreSelectionImg.getTexture();

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
			selectionTileTex = Resource.wallImg.getTexture();
			break;
		case Tile.GRASS:
			selectionTileTex = Resource.grassImg.getTexture();
			break;
		case Tile.WATER:
			selectionTileTex = Resource.waterImg.getTexture();
			break;
		case Tile.TREE:
			selectionTileTex = Resource.treeImg.getTexture();
			break;
		case Tile.ROCK:
			selectionTileTex = Resource.rockImg.getTexture();
			break;
		}

		// et on dessine le preview.
		if (selectionTileTex == null)
			selectionTileTex = Resource.wallImg.getTexture();

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

	public void drawMenu() {

		// Menu Principal.
		font.drawString(52, 22, "MENU !         Echap : Quit G : Jeu",
				Color.black);
		font.drawString(50, 20, "MENU !         Echap : Quit G : Jeu",
				Color.white);
		font.drawString(50, 550, "WOOOOOOOOO Du Text !!!!", Color.white);
		// Le reset des couleurs.
		GraphicCall.resetColor();

	}

}
