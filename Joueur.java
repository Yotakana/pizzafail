package jeu;
import org.lwjgl.opengl.GL11;

public class Joueur extends MovingEntity {
	
	public World world;
	public Joueur(double x, double y, double width, double height,String textureName, World world) {
		super(x, y, width, height, textureName, world);

	}

	@Override
	public void draw() {

		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex2d(xPos, yPos);
		
			GL11.glTexCoord2d(texture.getWidth(), 0);
			GL11.glVertex2d(xPos + width, yPos);
		
			GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
			GL11.glVertex2d(xPos + width, yPos - height);
		
			GL11.glTexCoord2d(0, texture.getHeight());
			GL11.glVertex2d(xPos, yPos - height);
		GL11.glEnd();
		
	}

}
