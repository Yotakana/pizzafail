package jeu;

public class World {

	public final int WIDTH = 26;
	public final int HEIGHT = 20;
	public final int LAYER = 2;
	private Tile[][][] grid1 = new Tile[WIDTH][HEIGHT][LAYER];
	private Tile[][][] grid2 = new Tile[WIDTH][HEIGHT][LAYER];
	private Tile[][][] grid3 = new Tile[WIDTH][HEIGHT][LAYER];
	private Tile[][][] grid4 = new Tile[WIDTH][HEIGHT][LAYER];
	public Tile[][][] currentGrid = null;
	public final int MAX_ROW = 2;
	public final int MAX_COL = 2;
	private int[][] mapCoord = new int[MAX_COL][MAX_ROW];
	public int mapID;
	public int xCoord, yCoord;
	public Enemy robert;

	public World() {
		
		// defini la map de depart
		mapCoord [0][0] = 1;
		mapCoordToMapID();
		setMap(mapID);
		
		// Map x: 0 y: 0
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid1[x][y][0] = new Tile(x * Tile.WIDTH, (y * Tile.HEIGHT) + 32 ,Tile.GRASS);
			}
		}
		
		
		// Map x: 1 y: 0
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid2[x][y][0] = new Tile(x * Tile.WIDTH, ( y * Tile.HEIGHT) +32 ,Tile.GRASS);
			}
		}
		
		
		
		// Map x: 0 y: 1
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid3[x][y][0] = new Tile(x * Tile.WIDTH, (y * Tile.HEIGHT) + 32 ,Tile.GRASS);
			}
		}
		
				
		// Map x: 1 y: 1
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid4[x][y][0] = new Tile(x * Tile.WIDTH, (y * Tile.HEIGHT) + 32 ,Tile.GRASS);
			}
		}
		
		robert = new Enemy(400, 400, 32, 32, "soldier", this);

	}
	
	public void update(){
			
		robert.updateDelta(16);
		robert.move();
	
	}

	public void draw() {

		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT -1 ; y++) {
				if(currentGrid[x][y][0] != null)
					currentGrid[x][y][0].draw();
			}
		}

	}
	
	public void drawLayer(){
		
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1 ; y++) {
				if(currentGrid[x][y][1] != null)
					currentGrid[x][y][1].draw();
			}
		}
		
		robert.draw();
		
	}
	
	public void setMap(int x){
		
		switch(x){
		
		case 0:
			currentGrid = grid1;
			xCoord = 0 ;
			yCoord = 0;
			break;
		case 1:
			currentGrid = grid2;
			xCoord = 1;
			yCoord = 0;
			break;
		case 2:
			currentGrid = grid3;
			xCoord = 0;
			yCoord = 1;
			break;
		case 3:
			currentGrid = grid4;
			xCoord = 1;
			yCoord = 1;
			break;
		}
		
	}
	
	private void mapCoordToMapID(){
		
		for(int p = 0; p < 2; p++){
			for (int q = 0; q < 2; q++){
				if(mapCoord[p][q] == 1){
					mapID = p  + (q * 2);
				}
			}
		}
	}
	
	public void setMapCoord(int x, int y){
		
		mapCoord[xCoord][yCoord] = 0;
		mapCoord[x][y] = 1;
		mapCoordToMapID();
	}
	
	
	public int getMapPos(){
		
		return mapID;
	}
}
