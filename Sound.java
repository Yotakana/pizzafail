package jeu;

import java.io.IOException;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sound {

	static Audio intro = loadSound("zelda");

	public static Audio loadSound(String name) {
		try {
			return AudioLoader.getAudio("OGG",
					ResourceLoader.getResourceAsStream("snd/" + name + ".ogg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
