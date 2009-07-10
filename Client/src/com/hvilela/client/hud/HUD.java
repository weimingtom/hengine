package com.hvilela.client.hud;

import java.io.File;
import java.net.MalformedURLException;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * @author Henrique
 *
 */
public class HUD extends Node {

	private static final long serialVersionUID = -3052408306651235569L;

	private int border = 20;

	public HUD() throws MalformedURLException {
		super("hud");

		DisplaySystem display = DisplaySystem.getDisplaySystem();

		//createMiniMap(display);
		//createPicture(display);
		//createLife(display);
		//createMenu(display);
	}

	private void createMiniMap(DisplaySystem display) throws MalformedURLException {
		float width = 100;
		float height = 100;

		Quad life = createArea("cc_map.gif", display, width, height);

		life.setLocalTranslation(new Vector3f(display.getWidth() - width / 2 - border, display.getHeight() - height / 2 - border, 0));
		life.updateRenderState();
		
		attachChild(life);
	}

	private void createPicture(DisplaySystem display) throws MalformedURLException {
		float width = 80;
		float height = 80;

		Quad life = createArea("picture.PNG", display, width, height);

		life.setLocalTranslation(new Vector3f(width / 2 + border, display.getHeight() - height / 2 - border, 0));
		life.updateRenderState();

		attachChild(life);
	}

	private void createLife(DisplaySystem display) throws MalformedURLException {
		float width = 150;
		float height = 15;

		Quad life = createArea("life.PNG", display, width, height);

		life.setLocalTranslation(new Vector3f(200, display.getHeight() - height / 2 - border, 0));
		life.updateRenderState();

		attachChild(life);
	}

	private void createMenu(DisplaySystem display) throws MalformedURLException {
		float width = 600;
		float height = 40;

		Quad life = createArea("actions.PNG", display, width, height);

		life.setLocalTranslation(new Vector3f(display.getWidth() / 2, border + height / 2, 0));
		life.updateRenderState();

		attachChild(life);
	}
	
	private Quad createArea(String filename, DisplaySystem display, float width, float height) throws MalformedURLException {
		Quad life = new Quad("hud", width, height);
		life.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		TextureState textureState = display.getRenderer().createTextureState();
		textureState.setEnabled(true);

		textureState.setTexture(TextureManager.loadTexture(new File(filename).toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
		life.setRenderState(textureState);

		return life;
	}
}