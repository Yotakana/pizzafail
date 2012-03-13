package jeu;

import org.newdawn.slick.opengl.Texture;

public class MovingEntity {

	double xPos, yPos;
	double width, height;
	double dx, dy;
	public String textureName;
	public Texture texture;
	private int delta = 0; 
	public World world;
	public int boost = 1; // ca va degager, juste pour des tests

	public MovingEntity(double x, double y, double width, double height,
			String textureName, World world) {

		this.xPos = x;
		this.yPos = y;
		this.height = height;
		this.width = width;
		this.textureName = textureName;
		this.world = world;

		texture = Image.loadTexture(textureName);

	}

	public void updateDelta(int delta) {
		this.delta = delta;

	}

	public void draw() {

	}

	public void move() {

		for (int i = 0; i < world.WIDTH - 1; i++) {
			for (int j = 0; j < world.HEIGHT - 1; j++) {
				for (int z = 0; z < world.LAYER; z++) {
					if (world.currentGrid[i][j][z] != null) {
						if (xPos + (width / 2) >= world.currentGrid[i][j][z].getxPos()
								&& xPos + (width / 2) <= world.currentGrid[i][j][z].getxPos() + Tile.WIDTH
								&& !world.currentGrid[i][j][z].isWalkable
								&& yPos >= world.currentGrid[i][j][z].getyPos()
								&& yPos <= world.currentGrid[i][j][z].getyPos() + Tile.HEIGHT) {
							System.out.println("colide at south");
							if (dy < 0)
								dy = 0;

						}
						if (xPos + (width / 2) >= world.currentGrid[i][j][z]
								.getxPos()
								&& xPos + (width / 2) <= world.currentGrid[i][j][z].getxPos() + Tile.WIDTH
								&& !world.currentGrid[i][j][z].isWalkable
								&& yPos + height >= world.currentGrid[i][j][z].getyPos()
								&& yPos + height <= world.currentGrid[i][j][z].getyPos() + Tile.HEIGHT) {
							System.out.println("colide at north");
							if (dy > 0)
								dy = 0;

						}
						if (xPos >= world.currentGrid[i][j][z].getxPos()
								&& xPos <= world.currentGrid[i][j][z].getxPos()+ Tile.WIDTH
								&& !world.currentGrid[i][j][z].isWalkable
								&& yPos + (height / 2) >= world.currentGrid[i][j][z].getyPos()
								&& yPos + (height / 2) <= world.currentGrid[i][j][z].getyPos() + Tile.HEIGHT) {
							System.out.println("colide at west");
							if (dx < 0)
								dx = 0;

						}
						if (xPos + width >= world.currentGrid[i][j][z].getxPos()
								&& xPos + width <= world.currentGrid[i][j][z].getxPos() + Tile.WIDTH
								&& !world.currentGrid[i][j][z].isWalkable
								&& yPos + (height / 2) >= world.currentGrid[i][j][z].getyPos()
								&& yPos + (height / 2) <= world.currentGrid[i][j][z].getyPos() + Tile.HEIGHT) {
							System.out.println("colide at est");
							if (dx > 0)
								dx = 0;

						}
					}

				}
			}
		}

		if (xPos >= 800 - width && dx > 0)
			dx = 0;
		if (xPos <= 0 && dx < 0)
			dx = 0;
		if (yPos >= 600 && dy > 0)
			dy = 0;
		if (yPos <= 0 + height && dy < 0)
			dy = 0;

		xPos += (dx * delta * boost); // boost ca va degager
		yPos += (dy * delta * boost); // c'est juste pour des tests

	}

	public double getxPos() {
		return xPos;
	}

	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public void setyPos(double yPos) {
		this.yPos = yPos;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

}
