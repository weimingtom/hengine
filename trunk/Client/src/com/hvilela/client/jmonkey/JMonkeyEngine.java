package com.hvilela.client.jmonkey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hvilela.client.ClientEngine;
import com.hvilela.client.hud.HUD;
import com.hvilela.client.input.Handler;
import com.hvilela.common.Position;
import com.jme.app.BaseGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.AseToJme;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.Md2ToJme;
import com.jmex.model.converters.Md3ToJme;
import com.jmex.model.converters.MilkToJme;
import com.jmex.model.converters.ObjToJme;
import com.jmex.model.converters.X3dToJme;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.AbstractHeightMap;
import com.jmex.terrain.util.HillHeightMap;

/**
 * @author Henrique
 */
public class JMonkeyEngine extends BaseGame implements ClientEngine {

	private static final Logger logger = Logger.getLogger(JMonkeyEngine.class.getName());

	private Node hud;

	private Skybox skybox;

	private Node scene;

	private TerrainBlock terrain;

	private GameAgent player;

	private Map<String, GameAgent> agents;

	private BasicPassManager passManager;

	private Camera camera;

	private ChaseCamera chaser;

	protected InputHandler input;

	protected Timer timer;

	private int width;

	private int height;

	private int depth;

	private int freq;

	private boolean fullscreen;

	private Map<String, Node> cache;

	private Random seed;

	private String id;

	private Position initialData;

	public static void main(String[] args) {
		JMonkeyEngine app = new JMonkeyEngine();
		app.setInitialData("player", new Position(100, 100, 1, 0));
		app.start();
	}

	public JMonkeyEngine() {
		//setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		
		Logger.getLogger("").setLevel(Level.WARNING);

		agents = new HashMap<String, GameAgent>();
		cache = new HashMap<String, Node>();
		seed = new Random(0);
	}
	
	@Override
	public void setInitialData(String id, Position position) {
		this.id = id;
		this.initialData = position;
	}
	
	protected void initSystem() {
		width = properties.getWidth();
		height = properties.getHeight();
		depth = properties.getDepth();
		freq = properties.getFreq();
		fullscreen = properties.getFullscreen();

		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(width, height, depth, freq, fullscreen);

			camera = display.getRenderer().createCamera(width, height);
		} catch (JmeException exception) {
			logger.log(Level.SEVERE, "Could not create displaySystem", exception);
			System.exit(1);
		}

		camera.setFrustumPerspective(45.0f, (float) width / (float) height, 1, 5000);
		camera.update();

		timer = Timer.getTimer();

		display.getRenderer().setCamera(camera);

		KeyBindingManager.getKeyBindingManager().set("exit", KeyInput.KEY_ESCAPE);
	}

	protected void initGame() {
		MouseInput.get().setCursorVisible(true);

		display.setTitle("DarkMMO - Sample");

		scene = new Node("root");

		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		scene.setRenderState(buf);
		
		try {
			buildHUD();
			buildTerrain();
			buildSkyBox();
			buildLighting();
			buildEnvironment();
			buildPlayer();
			buildChaseCamera();
			buildPassManager();
			buildInput();
		} catch (Exception exception) {
			logger.log(Level.SEVERE, "Could not create world", exception);
			System.exit(1);
		}

		scene.updateRenderState();
	}

	protected void buildHUD() throws MalformedURLException {
		hud = new HUD();
	}

	private void buildTerrain() throws MalformedURLException {
		AbstractHeightMap heightMap = new HillHeightMap(100, 2000, 5.0f, 20.0f, (byte) 2, 0);
		heightMap.setHeightScale(0.001f);
		Vector3f terrainScale = new Vector3f(10, 0.5f, 10);

		terrain = new GrassTerrain(heightMap, terrainScale);

		scene.attachChild(terrain);
	}

	private void buildSkyBox() throws MalformedURLException {
		skybox = new BlueCloudySky();
	}

	private void buildLighting() {
		DirectionalLight light = new DirectionalLight();
		light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, .5f));
		light.setDirection(new Vector3f(1, -1, 0));
		light.setShadowCaster(true);
		light.setEnabled(true);

		LightState lightState = display.getRenderer().createLightState();
		lightState.setEnabled(true);
		lightState.setGlobalAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
		lightState.attach(light);

		scene.setRenderState(lightState);
	}

	private void buildEnvironment() throws MalformedURLException {
		for (int i = 0; i < 200; i++) {
			float x = (float) seed.nextDouble() * 1000;
			float y = (float) seed.nextDouble() * 1000;
			float rotation = (float) seed.nextDouble() * 360;

			GameObject object = createGameObject("models/tree1.ms3d", new Position(x, y, 0, rotation));
			object.model.setLocalScale(0.5f);
			scene.attachChild(object);
		}
	}

	private void buildPlayer() {
		player = createAgent(id, initialData);
	}

	private void buildChaseCamera() {
		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "20");
		props.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "0");
		//props.put(ThirdPersonMouseLook.PROP_MAXASCENT, "" + 45 * FastMath.DEG_TO_RAD);
		props.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(5, 0, 30 * FastMath.DEG_TO_RAD));
		props.put(ChaseCamera.PROP_DAMPINGK, "4");
		props.put(ChaseCamera.PROP_SPRINGK, "5");

		camera.setLocation(new Vector3f(player.getX(), player.getZ(), player.getY()));
		chaser = new ChaseCamera(camera, player, props);
		chaser.getMouseLook().setLookMouseButton(0);
		chaser.setMaxDistance(20);
		chaser.setMinDistance(8);
	}

	private void buildPassManager() {
		passManager = new BasicPassManager();

		/*
		WaterRenderPass waterEffectRenderPass = new WaterRenderPass(camera, 4, true, true);
		waterEffectRenderPass.setClipBias(0.5f);
		waterEffectRenderPass.setWaterMaxAmplitude(5.0f);
		waterEffectRenderPass.setWaterPlane(new Plane(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f));
		ProjectedGrid projectedGrid = new ProjectedGrid("ProjectedGrid", camera, 100, 70, 0.01f, new WaterHeightGenerator());
		waterEffectRenderPass.setWaterEffectOnSpatial(projectedGrid);
		scene.attachChild(projectedGrid);
		waterEffectRenderPass.setReflectedScene(skybox);
		waterEffectRenderPass.setSkybox(skybox);
		passManager.add(waterEffectRenderPass);
		*/

		RenderPass renderPass = new RenderPass();
		renderPass.add(hud);
		renderPass.add(skybox);
		renderPass.add(scene);
		passManager.add(renderPass);
	}

	private void buildInput() {
		input = new Handler(player, properties.getRenderer());
	}

	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		passManager.renderPasses(display.getRenderer());
	}

	protected void update(float time) {
		timer.update();

		time = timer.getTimePerFrame();

		skybox.setLocalTranslation(camera.getLocation());

		if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit")) {
			finished = true;
		}

		if (camera.getLocation().y < (terrain.getHeight(camera.getLocation()))) {
			camera.getLocation().y = terrain.getHeight(camera.getLocation());
			camera.update();
		}

		player.update(time);
		followTerrain(player);
		
		for (GameAgent agent : agents.values()) {
			agent.update(time);
			followTerrain(agent);
		}
		
		input.update(time);
		chaser.update(time);

		skybox.updateGeometricState(time, true);
		scene.updateGeometricState(time, true);
	}

	private void followTerrain(GameObject object) {
		float characterMinHeight = terrain.getHeight(object.getLocalTranslation());
		if (!Float.isInfinite(characterMinHeight) && !Float.isNaN(characterMinHeight)) {
			object.getLocalTranslation().y = characterMinHeight;
		}
	}

	@Override
	public void setAgentMovement(String id, Position position, float speed) {
		if (scene != null) {
			if (!agents.containsKey(id)) {
				createAgent(id, position);
			} else {
				GameAgent agent = agents.get(id);
				agent.setPosition(position);
				agent.setSpeed(speed);
				
				followTerrain(agent);
			}
		}
	}

	private GameAgent createAgent(String id, Position position) {
		Node model = null;

		try {
			model = loadModel("models/man3.ms3d");
			model.setLocalScale(0.3f);

			model.setModelBound(new BoundingBox());
			model.updateModelBound();
		} catch (Exception exception) {
			logger.throwing(this.getClass().toString(), "buildPlayer()", exception);
		}

		GameAgent agent = new GameAgent(id, model);
		agent.setPosition(position);

		scene.attachChild(agent);
		agents.put(id, agent);

		return agent;
	}

	private GameObject createGameObject(String modelName, Position position) {
		Node model = null;

		if (cache.containsKey(modelName)) {
			model = cache.get(modelName);
		} else {
			model = loadModel(modelName);
			cache.put(modelName, model);
		}

		Spatial spatial = new SharedNode("object", model);
		scene.attachChild(spatial);

		GameObject object = new GameObject("object", spatial);
		object.setPosition(position);

		followTerrain(object);

		return object;
	}

	protected void reinit() {
		display.recreateWindow(width, height, depth, freq, fullscreen);
	}

	protected void quit() {
		super.quit();
		System.exit(0);
	}

	protected void cleanup() {
	}

	private Node loadModel(String fileName, String... textures) {
		Node model = null;

		try {
			String format = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

			File binary = new File(fileName.substring(0, fileName.lastIndexOf(".") + 1) + "jme");
			if (!binary.exists()) {
				FormatConverter formatConverter = null;

				if (format.equalsIgnoreCase("ase")) {
					formatConverter = new AseToJme();
				} else if (format.equalsIgnoreCase("3ds")) {
					formatConverter = new MaxToJme();
				} else if (format.equalsIgnoreCase("md2")) {
					formatConverter = new Md2ToJme();
				} else if (format.equalsIgnoreCase("md3")) {
					formatConverter = new Md3ToJme();
				} else if (format.equalsIgnoreCase("ms3d")) {
					formatConverter = new MilkToJme();
				} else if (format.equalsIgnoreCase("obj")) {
					formatConverter = new ObjToJme();
				} else if (format.equalsIgnoreCase("x3d")) {
					formatConverter = new X3dToJme();
				}

				File file = new File(fileName);

				try {
					ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(file.toURI().toURL()));
				} catch (URISyntaxException exception) {
					logger.log(Level.WARNING, "unable to setup texture directories.", exception);
				}

				formatConverter.setProperty("texurl", file.getParentFile().toURI().toURL());
				formatConverter.setProperty("mtllib", file.toURI().toURL());

				ByteArrayOutputStream output = new ByteArrayOutputStream();

				formatConverter.convert(file.toURI().toURL().openStream(), output);
				BinaryImporter importer = BinaryImporter.getInstance();
				model = (Node) importer.load(new ByteArrayInputStream(output.toByteArray()));

				for (String texture : textures) {
					TextureState textureState = display.getRenderer().createTextureState();
					textureState.setEnabled(true);
					textureState.setTexture(TextureManager.loadTexture(new File(texture).toURI().toURL(), Texture.MM_LINEAR, Texture.FM_LINEAR));
					model.setRenderState(textureState);
				}

				// BinaryExporter.getInstance().save((Savable) model, binary);
			} else {
				model = (Node) BinaryImporter.getInstance().load(binary.toURI().toURL());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return model;
	}

	public GameAgent getPlayer() {
		return player;
	}
}