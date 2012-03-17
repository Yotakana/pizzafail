package jeu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class WorldLoader {

	public World world;
	public Tile[][][] grid = new Tile[26][20][2];

	public void loadLayer0(World world) {

		this.world = world;

		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build("maps/dumpL0.xml");
			Element root = document.getRootElement();
			for (Object block : root.getChildren()) {
				Element e = (Element) block;
				int x = Integer.parseInt(e.getAttributeValue("xPos"));
				int y = Integer.parseInt(e.getAttributeValue("yPos"));
				int type = Integer.parseInt(e.getAttributeValue("type"));
				world.currentGrid[(x / 32)][(y / 32)][0] = new Tile(x, y, type);

			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadLayer1(World world) {

		this.world = world;

		for (int i = 0; i < 26 - 1; i++) {
			for (int j = 0; j < 20 - 1; j++) {
				world.currentGrid[i][j][1] = null;
			}
		}

		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build("maps/dumpL1.xml");
			Element root = document.getRootElement();
			for (Object block : root.getChildren()) {
				Element e = (Element) block;
				int x = Integer.parseInt(e.getAttributeValue("xPos"));
				int y = Integer.parseInt(e.getAttributeValue("yPos"));
				int type = Integer.parseInt(e.getAttributeValue("type"));
				world.currentGrid[(x / 32)][(y / 32)][1] = new Tile(x, y, type);

			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void initWorld(World world) {

		this.world = world;

		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build("maps/map1layer0.xml");
			Element root = document.getRootElement();
			for (Object block : root.getChildren()) {
				Element e = (Element) block;
				int x = Integer.parseInt(e.getAttributeValue("xPos"));
				int y = Integer.parseInt(e.getAttributeValue("yPos"));
				int type = Integer.parseInt(e.getAttributeValue("type"));
				world.grid1[(x / 32)][(y / 32)][0] = new Tile(x, y, type);

			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build("maps/map1layer1.xml");
			Element root = document.getRootElement();
			for (Object block : root.getChildren()) {
				Element e = (Element) block;
				int x = Integer.parseInt(e.getAttributeValue("xPos"));
				int y = Integer.parseInt(e.getAttributeValue("yPos"));
				int type = Integer.parseInt(e.getAttributeValue("type"));
				world.grid1[(x / 32)][(y / 32)][1] = new Tile(x, y, type);

			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build("maps/map2layer0.xml");
			Element root = document.getRootElement();
			for (Object block : root.getChildren()) {
				Element e = (Element) block;
				int x = Integer.parseInt(e.getAttributeValue("xPos"));
				int y = Integer.parseInt(e.getAttributeValue("yPos"));
				int type = Integer.parseInt(e.getAttributeValue("type"));
				world.grid2[(x / 32)][(y / 32)][0] = new Tile(x, y, type);

			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build("maps/map2layer1.xml");
			Element root = document.getRootElement();
			for (Object block : root.getChildren()) {
				Element e = (Element) block;
				int x = Integer.parseInt(e.getAttributeValue("xPos"));
				int y = Integer.parseInt(e.getAttributeValue("yPos"));
				int type = Integer.parseInt(e.getAttributeValue("type"));
				world.grid2[(x / 32)][(y / 32)][1] = new Tile(x, y, type);

			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Maps loaded");

	}

	public void save(World world) {

		saveLayer0(world);
		System.out.println("saving Layer0...");
		saveLayer1(world);
		System.out.println("saving Layer1...");

	}

	public void load(World world) {

		loadLayer0(world);
		System.out.println("Loading Layer0...");
		loadLayer1(world);
		System.out.println("Loading Layer1...");

	}

	public void saveLayer0(World world) {

		this.world = world;

		Document document = new Document();
		Element root = new Element("blocks");
		document.setRootElement(root);
		for (int x = 0; x < 26 - 1; x++) {
			for (int y = 0; y < 20 - 1; y++) {
				Element block = new Element("block");
				block.setAttribute("xPos", String
						.valueOf((int) world.currentGrid[x][y][0].getxPos()));
				block.setAttribute("yPos", String
						.valueOf((int) world.currentGrid[x][y][0].getyPos()));
				block.setAttribute("type",
						String.valueOf((int) world.currentGrid[x][y][0].type));
				root.addContent(block);
			}
		}
		XMLOutputter output = new XMLOutputter();
		try {
			output.output(document, new FileOutputStream("maps/dumpL0.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void saveLayer1(World world) {

		this.world = world;

		Document document = new Document();
		Element root = new Element("blocks");
		document.setRootElement(root);
		for (int x = 0; x < 26 - 1; x++) {
			for (int y = 0; y < 20 - 1; y++) {
				Element block = new Element("block");
				if (world.currentGrid[x][y][1] != null) {
					block.setAttribute("xPos",
							String.valueOf((int) world.currentGrid[x][y][1]
									.getxPos()));
					block.setAttribute("yPos",
							String.valueOf((int) world.currentGrid[x][y][1]
									.getyPos()));
					block.setAttribute("type", String
							.valueOf((int) world.currentGrid[x][y][1].type));
					root.addContent(block);
				}
			}
		}
		XMLOutputter output = new XMLOutputter();
		try {
			output.output(document, new FileOutputStream("maps/dumpL1.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
