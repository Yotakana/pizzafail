package jeu;

import java.util.Random;

import org.lwjgl.opengl.GL11;

public class Enemy extends MovingEntity {
	private long timeInterval = 2000;
	private long Time = System.currentTimeMillis();
	public World world;

	public Enemy(double x, double y, double width, double height,String textureName, World world) {
		super(x, y, width, height, textureName, world);
	}

	@Override
	public void movingBehavior() {
		if (System.currentTimeMillis() > Time + timeInterval) {
			dx = 0;
			dy = 0;

			Random random = new Random();
			int r = random.nextInt(4);

			System.out.println(r);

			switch (r) {
			case 0:
				dx = .05;
				break;

			case 1:
				dx = -.05;
				break;

			case 2:
				dy = .05;
				break;

			case 3:
				dy = -.05;
				break;
			}

			Time = System.currentTimeMillis();
		}
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

		System.out.println(dx + " " + dy);
		System.out.println(delta);

	}

}
