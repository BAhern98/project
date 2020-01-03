package project;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import collision.AABB;
import entity.Entity;
import entity.Player;
import entity.Transform;
import player.Animations;
import world.Tile;
import world.TileRenderer;

public class World {
	private List<Entity> entities;
	private byte[] tiles;
	private int width;
	private int height;
	private int width2;
	private int height2;
	private int scale;
	private Matrix4f world;
	private final int view = 24;
	private AABB[] bounding_boxes;
//rock = 1 rgb

	public World(String rock) {
		try {
			BufferedImage tile_sheet = ImageIO.read(new File("./levels/" + rock + "/tiles.png"));
			BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + rock + "/entities.png"));


			width = tile_sheet.getWidth();
			height = tile_sheet.getHeight();
			width2 = entity_sheet.getWidth();
			height2 = entity_sheet.getHeight();
			scale = 16;
			
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
//
			int[] colorTileSheet = tile_sheet.getRGB(0, 0, width, height, null, 0, width);// returns all pixels within
//																							// image,Returns an array of
//																							// integer pixels in the
//																							// default RGB color model
//																							// (TYPE_INT_ARGB) and
//																							// default sRGB color space,
//																							// from a portion of the
//																							// image data.
			int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width2, height2, null, 0, width2);

			tiles = new byte[width * height];// inalise tiles
			bounding_boxes = new AABB[width * height];// initalise boundig boxes
			entities = new ArrayList<Entity>();// initialise list to new array list

			Transform transform;// transform for entity


			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {

					int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;// gets pixel
					int entity_index = (colorEntitySheet[x + y + width] >> 16) & 0xFF;// red channel
					int entity_alpha = (colorEntitySheet[x + y + width] >> 24) & 0xFF;// alpha channel

					Tile t;
					try {
						t = Tile.tiles[red];

					} catch (ArrayIndexOutOfBoundsException e) {
						t = null;
						// TODO: handle exception
					}

					if (t != null)
						setTile(t, x, y);

					if (entity_alpha > 0) {
						transform = new Transform();
						transform.pos.x = x;
						transform.pos.y = -y;// set entity to right position in the world
						switch (entity_index) {
						case 1:
							Player player = new Player(transform);
							entities.add(player);
							break;
						default:
							break;
						}
					}

				}
			}

			// TODO
			entities.add(new Player(new Transform()));// add entity player

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public World() {
		width = 200;
		height = 200;
		scale = 16;

		tiles = new byte[width * height];
		bounding_boxes = new AABB[width * height];

		world = new Matrix4f().setTranslation(new Vector3f(0));
		world.scale(scale);
	}

	public Matrix4f getWorldMatrix() {
		return world;
	}

	public void render(TileRenderer render, Shader shader, Camera cam, Window window) {

		int posX = ((int) cam.getPosition().x + (window.getWidth() / 2)) / (scale * 2);
		int posY = ((int) cam.getPosition().y - (window.getHeight() / 2)) / (scale * 2);

		for (int i = 0; i < view; i++) {
			for (int j = 0; j < view; j++) {
				Tile t = getTile(i - posX, j + posY);
				if (t != null)
					render.renderTile(t, i - posX, -j - posY, shader, world, cam);

			}
		}

		for (Entity entity : entities) {// itaarate through all the entities in list of entities
			entity.render(shader, cam, this);// render each entity
		}

	}

	public void update(float delta, Window window, Camera camera) {
		for (Entity entity : entities) {// itaarate through all the entities in list of entities
			entity.update(delta, window, camera, this);// update each entity
		}

		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).collideWithTiles(this);// get entity at i and collide with tiles this

			for (int j = i + 1; j < entities.size(); j++) {// where we colide with other entities
				entities.get(i).collideWithEntity(entities.get(j));

			}
			entities.get(i).collideWithTiles(this);// where we coliede with tiles again, if you colide with an enttiy
													// and it pushes you into a tile it checks it
		}
	}

	public void correctCamera(Camera camera, Window window) {
		Vector3f pos = camera.getPosition();
		int w = -width * scale * 2;// get exict width or world
		int h = height * scale * 2;

		if (pos.x > -(window.getWidth() / 2) + scale)// stops camera leaving left map
			pos.x = -(window.getWidth() / 2) + scale;

		if (pos.x < w + (window.getWidth() / 2) + scale)// stops camera leaving right map
			pos.x = w + (window.getWidth() / 2) + scale;

		if (pos.y < (window.getHeight() / 2) - scale)// stops camera leaving top map
			pos.y = (window.getHeight() / 2) - scale;

		if (pos.y > h - (window.getHeight() / 2) - scale)// stops camera leaving bottom map
			pos.y = h - (window.getHeight() / 2) - scale;
	}

	public void setTile(Tile tile, int x, int y) {
		tiles[x + y * width] = tile.getId();
		if (tile.isSolid()) {
			bounding_boxes[x + y * width] = new AABB(new Vector2f(x * 2, -y * 2), new Vector2f(1, 1));
		} else {
			bounding_boxes[x + y * width] = null;
		}
	}

	public Tile getTile(int x, int y) {
		try {
			return Tile.tiles[tiles[x + y * width]];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;// TODO: handle exception
		}
	}

	public AABB getTileBoundingBox(int x, int y) {
		try {
			return bounding_boxes[x + y * width];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;// TODO: handle exception
		}
	}

	public int getScale() {
		return scale;
	}

}
