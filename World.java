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
	private Enemy robert;

	public World() {
		
		// defini la map de depart
		mapCoord [0][0] = 1;
		mapCoordToMapID();
		setMap(mapID);
		
		// Map x: 0 y: 0
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid1[x][y][0] = new Tile(x * Tile.WIDTH, y * Tile.HEIGHT ,Tile.GRASS);
			}
		}
		
		
		// Map x: 1 y: 0
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid2[x][y][0] = new Tile(x * Tile.WIDTH, y * Tile.HEIGHT ,Tile.GRASS);
			}
		}
		
		
		
		// Map x: 0 y: 1
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid3[x][y][0] = new Tile(x * Tile.WIDTH,y * Tile.HEIGHT ,Tile.GRASS);
			}
		}
		
				
		// Map x: 1 y: 1
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1; y++) {
				grid4[x][y][0] = new Tile(x * Tile.WIDTH,y * Tile.HEIGHT ,Tile.GRASS);
			}
		}
		
		robert = new Enemy(400, 400, 32, 32, "soldier", this);

	}
	
	public void update(){
		
		// totallement bancale ! mais ca marche... a revoir
		if(robert !=null){
			robert.updateDelta(16);
			robert.move();
		}
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
		
		// totallement bancale ! mais ca marche... a revoir
		
		if(robert !=null)
			robert.draw();
		if(robert == null && mapID == 0)
			robert = new Enemy(400, 400, 32, 32, "soldier", this);
		
		for (int x = 0; x < WIDTH - 1; x++) {
			for (int y = 0; y < HEIGHT - 1 ; y++) {
				if(currentGrid[x][y][1] != null)
					currentGrid[x][y][1].draw();
			}
		}
		
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
			robert = null; // a modif
			xCoord = 1;
			yCoord = 0;
			break;
		case 2:
			currentGrid = grid3;
			robert = null; // a modif
			xCoord = 0;
			yCoord = 1;
			break;
		case 3:
			currentGrid = grid4;
			robert = null; // a modif
			xCoord = 1;
			yCoord = 1;
			break;
		}
		
	}
	
	private void mapCoordToMapID(){
		
		for(int i = 0; i < 2; i++){
			for (int j = 0; j < 2; j++){
				if(mapCoord[i][j] == 1){
					mapID = i  + (j * 2);
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
