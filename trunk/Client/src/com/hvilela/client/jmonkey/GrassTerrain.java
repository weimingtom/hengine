package com.hvilela.client.jmonkey;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.AbstractHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * @author Henrique
 *
 */
public class GrassTerrain extends TerrainBlock {

	private static final long serialVersionUID = 8904419205521800084L;

	public GrassTerrain(AbstractHeightMap heightMap, Vector3f stepScale) throws MalformedURLException {
		super("grass", heightMap.getSize(), stepScale, heightMap.getHeightMap(), new Vector3f(0, 0, 0), false);

		ProceduralTextureGenerator generator = new ProceduralTextureGenerator(heightMap);
		generator.addTexture(new ImageIcon(new File("terrain/grassb.png").toURI().toURL()), -128, 0, 128);
		generator.addTexture(new ImageIcon(new File("terrain/dirt.jpg").toURI().toURL()), 0, 128, 255);
		generator.addTexture(new ImageIcon(new File("terrain/highest.jpg").toURI().toURL()), 128, 255, 384);
		generator.createTexture(32);

		TextureState texture = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		Texture grass = TextureManager.loadTexture(generator.getImageIcon().getImage(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);
		texture.setTexture(grass, 0);

		Texture detail = TextureManager.loadTexture(new File("terrain/Detail.jpg").toURI().toURL(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);

		texture.setTexture(detail, 1);
		detail.setWrap(Texture.WM_WRAP_S_WRAP_T);

		grass.setApply(Texture.AM_COMBINE);
		grass.setCombineFuncRGB(Texture.ACF_MODULATE);
		grass.setCombineSrc0RGB(Texture.ACS_TEXTURE);
		grass.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
		grass.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
		grass.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
		grass.setCombineScaleRGB(1.0f);

		detail.setApply(Texture.AM_COMBINE);
		detail.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
		detail.setCombineSrc0RGB(Texture.ACS_TEXTURE);
		detail.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
		detail.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
		detail.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
		detail.setCombineScaleRGB(1.0f);

		setRenderState(texture);
		setDetailTexture(1, 16);

		setModelBound(new BoundingBox());
		updateModelBound();

	}
}
