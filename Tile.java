package jeu;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Tile {

	private int xPos = 0, yPos = 0 ;
	final static int WIDTH = 32, HEIGHT = 32;
	public String location;
	public boolean isWalkable = false;
	final static int WALL = 1, GRASS = 2, WATER = 3, TREE = 4, ROCK = 5;
	private Texture tex;
	public int type;

	public Tile(int x, int y, int type) {

		this.yPos = y;
		this.xPos = x;
		this.type = type;

		switch (type) {
		case 1:
			isWalkable = false;
			tex = Image.wall;
			break;
		case 2:
			isWalkable = true;
			tex = Image.grass;
			break;
		case 3:
			isWalkable = false;
			tex = Image.water;
			break;
		case 4:
			isWalkable = false;
			tex = Image.tree;
			break;
		case 5:
			isWalkable = false;
			tex = Image.rock;
			break;
		}

	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public void draw() {

		tex.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(xPos, yPos + 0);

		GL11.glTexCoord2d(tex.getWidth(), 0);
		GL11.glVertex2d(xPos + WIDTH, yPos + 0);

		GL11.glTexCoord2d(tex.getWidth(), tex.getHeight());
		GL11.glVertex2d(xPos + WIDTH, yPos+ HEIGHT);

		GL11.glTexCoord2d(0, tex.getHeight());
		GL11.glVertex2d(xPos, yPos+ HEIGHT);
		GL11.glEnd();
		
		
	}

}
