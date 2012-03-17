package jeu;
import java.io.IOException;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Resource {
	
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
	static Image link_up1 = getImg("link_up1");
	static Image link_up2 = getImg("link_up2");
	static Image link_down1 = getImg("link_down1");
	static Image link_down2 = getImg("link_down2");
	
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
