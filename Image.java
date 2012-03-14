package jeu;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Image {
	
	
	static Texture grass = loadTexture("grass");
	static Texture wall = loadTexture("wall");
	static Texture water = loadTexture("water");
	static Texture tree = loadTexture("tree");
	static Texture rock = loadTexture("rock");
	static Texture selection = loadTexture("selection");
	static Texture cadreSelection = loadTexture("cadreselection");
	
	public static Texture loadTexture(String key) {
		try {
			return TextureLoader.getTexture("PNG",
					ResourceLoader.getResourceAsStream("img/" + key + ".png"));
		} catch (FileNotFoundException e) {
			System.out.println("not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
