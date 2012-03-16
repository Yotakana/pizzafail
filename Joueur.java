package jeu;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;

public class Joueur extends MovingEntity {
	
	
	public World world;
	public Joueur(double x, double y, double width, double height,String textureName, World world) {
		super(x, y, width, height, textureName, world);
		
		
		animUp = new Animation();
		animDown = new Animation();
		animLeft = new Animation();
		animRight = new Animation();
		
		animUp.addFrame(Resource.link_up1, 150);
		animUp.addFrame(Resource.link_up2, 150);
		
		animDown.addFrame(Resource.link_down1, 150);
		animDown.addFrame(Resource.link_down2, 150);
		
		animLeft.addFrame(Resource.link_left1, 150);
		animLeft.addFrame(Resource.link_left2, 150);
		
		animRight.addFrame(Resource.link_right1, 150);
		animRight.addFrame(Resource.link_right2, 150);
		
		System.out.println("Player animation loaded");
		
		animUp.stop();
		animDown.stop();
		animRight.stop();
		animLeft.stop();
		
		currentAnim = animRight;

	}

	@Override
	public void draw() {
		
		currentAnim.update(delta);
		texture = currentAnim.getCurrentFrame().getTexture();
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex2d(xPos, yPos);
		
			GL11.glTexCoord2d(texture.getWidth(), 0);
			GL11.glVertex2d(xPos + width, yPos);
		
			GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
			GL11.glVertex2d(xPos + width, yPos + height);
		
			GL11.glTexCoord2d(0, texture.getHeight());
			GL11.glVertex2d(xPos, yPos + height);
		GL11.glEnd();
		
	}

}
