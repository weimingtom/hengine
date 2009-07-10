package com.hvilela.client.util;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.renderer.Renderer;
import com.jme.scene.BillboardNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.*;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * @author Henrique
 */
public class TextLabel2D {

	private String text;
	
	private float blurIntensity = 0.1f;
	
	private int kernelSize = 5;
	
	private ConvolveOp blur;
	
	private Color foreground = new Color(1f, 1f, 1f);
	
	private Color background = new Color(0f, 0f, 0f);
	
	private float fontResolution = 40f;
	
	private int shadowOffsetX = 2;
	
	private int shadowOffsetY = 2;
	
	private Font font;

	public TextLabel2D(String text) {
		this.text = text;
		updateKernel();
		setFont(Font.decode("Sans PLAIN 40"));
	}
	
	private void updateKernel() {
		float[] kernel = new float[kernelSize * kernelSize];
		Arrays.fill(kernel, blurIntensity);
		blur = new ConvolveOp(new Kernel(kernelSize, kernelSize, kernel));
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setShadowOffsetX(int offsetPixelX) {
		shadowOffsetX = offsetPixelX;
	}

	public void setShadowOffsetY(int offsetPixelY) {
		shadowOffsetY = offsetPixelY;
	}

	public void setBlurSize(int kernelSize) {
		this.kernelSize = kernelSize;
		updateKernel();
	}

	public void setBlurStrength(float strength) {
		this.blurIntensity = strength;
		updateKernel();
	}

	public void setFontResolution(float fontResolution) {
		this.fontResolution = fontResolution;
	}

	private BufferedImage getImage(Vector2f scaleFactors) {
		BufferedImage tmp0 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) tmp0.getGraphics();
		Font drawFont = font.deriveFont(fontResolution);
		g2d.setFont(drawFont);
		Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);

		int actualX = (int) b.getWidth() + kernelSize + 1 + shadowOffsetX;
		int actualY = (int) b.getHeight() + kernelSize + 1 + shadowOffsetY;

		int desiredX = FastMath.nearestPowerOfTwo(actualX);
		int desiredY = FastMath.nearestPowerOfTwo(actualY);

		if (scaleFactors != null) {
			scaleFactors.x = (float) actualX / desiredX;
			scaleFactors.y = (float) actualY / desiredY;
		}

		tmp0 = new BufferedImage(desiredX, desiredY, BufferedImage.TYPE_INT_ARGB);

		g2d = (Graphics2D) tmp0.getGraphics();
		g2d.setFont(drawFont);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int textX = kernelSize / 2;
		int textY = g2d.getFontMetrics().getMaxAscent() - kernelSize / 2;

		g2d.setColor(background);
		g2d.drawString(text, textX + shadowOffsetX, textY + shadowOffsetY);

		BufferedImage ret = blur.filter(tmp0, null);

		g2d = (Graphics2D) ret.getGraphics();
		g2d.setFont(drawFont);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setColor(foreground);
		g2d.drawString(text, textX, textY);

		return ret;
	}

	public BillboardNode getBillboard(float height) {
		BillboardNode bb = new BillboardNode("bb");
		Quad q = getQuad(height);
		bb.attachChild(q);
		return bb;
	}

	private Quad getQuad(float height) {
		Vector2f scales = new Vector2f();
		BufferedImage img = getImage(scales);
		float w = img.getWidth() * scales.x;
		float h = img.getHeight() * scales.y;
		float factor = height / h;
		Quad ret = new Quad("textLabel2d", w * factor, h * factor);
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		Texture tex = TextureManager.loadTexture(img, Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);

		FloatBuffer texCo = ret.getTextureBuffer(0, 0);
		FloatBuffer newTC = BufferUtils.createFloatBuffer(texCo.limit());
		texCo.rewind();
		for (int i = 0; i < texCo.limit(); i += 2) {
			float u = texCo.get();
			float v = texCo.get();
			newTC.put(u * scales.x);
			newTC.put(v * scales.y);
		}
		ret.setTextureBuffer(0, newTC);
		ret.updateGeometricState(0, true);

		ts.setTexture(tex);
		ts.setEnabled(true);
		ret.setRenderState(ts);

		ret.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);

		AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		as.setEnabled(true);
		ret.setRenderState(as);

		ret.setLightCombineMode(LightState.OFF);
		ret.updateRenderState();
		return ret;
	}
	
	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public void setBackground(Color background) {
		this.background = background;
	}
}
