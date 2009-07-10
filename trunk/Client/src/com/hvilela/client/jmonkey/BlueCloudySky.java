package com.hvilela.client.jmonkey;

import java.io.File;
import java.net.MalformedURLException;

import com.jme.image.Texture;
import com.jme.scene.Skybox;
import com.jme.util.TextureManager;

/**
 * @author Henrique
 *
 */
public class BlueCloudySky extends Skybox {

	private static final long serialVersionUID = 1361405895009355774L;

	public BlueCloudySky() throws MalformedURLException {
		super("skybox", 10, 10, 10);

		setTexture(Skybox.NORTH, TextureManager.loadTexture(new File("skyboxes/sky_cloudy_2100_fr.jpg").toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
		setTexture(Skybox.EAST, TextureManager.loadTexture(new File("skyboxes/sky_cloudy_2100_rt.jpg").toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
		setTexture(Skybox.SOUTH, TextureManager.loadTexture(new File("skyboxes/sky_cloudy_2100_bk.jpg").toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
		setTexture(Skybox.WEST, TextureManager.loadTexture(new File("skyboxes/sky_cloudy_2100_lf.jpg").toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
		setTexture(Skybox.UP, TextureManager.loadTexture(new File("skyboxes/sky_cloudy_2100_up.jpg").toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
		
		preloadTextures();
		
		updateRenderState();
	}
}
