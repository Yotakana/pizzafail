package jeu;
import java.io.IOException;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;


public class LoadResource {
	
	static Audio introTheme = loadSound("zelda");
	
	static Image grassImg = getImg("grass");
	static Image wallImg = getImg("wall");
	static Image waterImg = getImg("water");
	static Image treeImg = getImg("tree");
	static Image rockImg = getImg("rock");
	static Image selectionImg = getImg("selection");
	static Image cadreSelectionImg = getImg("cadreselection");
	
	static Image link_right1 = getImg("link_right1");
	static Image link_right2 = getImg("link_right2");
	static Image link_left1 = getImg("link_left1");
	static Image link_left2 = getImg("link_left2");
	
	static Texture grass = grassImg.getTexture();
	static Texture wall = wallImg.getTexture();
	static Texture water = waterImg.getTexture();
	static Texture tree = treeImg.getTexture();
	static Texture rock = rockImg.getTexture();
	static Texture selection = selectionImg.getTexture();
	static Texture cadreSelection = cadreSelectionImg.getTexture();
	
	public static Audio loadSound(String name) {
		try {
			return AudioLoader.getAudio("OGG",
					ResourceLoader.getResourceAsStream("snd/" + name + ".ogg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static Image getImg(String key) {

		try {
			return new Image("img/" + key + ".png");

		} catch (SlickException e) {
			e.printStackTrace();
		}
		return null;

	}


}
