package a2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import myGameEngine.*;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.*;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rage.util.BufferUtil;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;

public class MyGame extends VariableFrameRateGame {
	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, counterStr, dispStr1, dispStr2;
	String score1Str, score2Str;
	int elapsTimeSec = 0;
	int score1 = 0, score2 = 0;
	boolean ifVisited1 = false, ifVisited2 = false, ifVisited3 = false;

	private Camera3Pcontroller orbitController1, orbitController2;

	private InputManager im;
	private Action moveCAct;
	private Action moveForwardAct, moveBackwardAct, moveLeftAct, moveRightAct;
	private Action moveCUpAct, moveCDownAct, moveCLeftAct, moveCRightAct;
	private Action moveLeftRightAct, moveUpDownAct;
	private Action zoomInAct, zoomOutAct, zoomAct;
	private Action rotateLeftAct1, rotateRightAct1, rotateLeftAct2, rotateRightAct2;
	private Action leftRightRotationAct;

	public MyGame() {
		super();
	}

	public static void main(String[] args) {
		Game game = new MyGame();
		try {
			game.startup();
			game.run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			game.shutdown();
			game.exit();
		}
	}

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}

	@Override
	protected void setupWindowViewports(RenderWindow rw) {
		rw.addKeyListener(this);

		Viewport topViewport = rw.getViewport(0);
		topViewport.setDimensions(0.51f, 0.01f, 0.99f, 0.49f); // B,L,W,H
		topViewport.setClearColor(new Color(0.0f, 0.0f, 0.0f));

		Viewport botViewport = rw.createViewport(0.01f, 0.01f, 0.99f, 0.49f);
		botViewport.setClearColor(new Color(0.5f, 0.5f, 0.5f));
	}

	@Override
	protected void setupCameras(SceneManager sm, RenderWindow rw) {
		SceneNode rootNode = sm.getRootSceneNode();
		Camera camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
		rw.getViewport(0).setCamera(camera);
		SceneNode cameraNode = rootNode.createChildSceneNode("MainCameraNode");
		cameraNode.attachObject(camera);
		camera.setMode('n');
		camera.getFrustum().setFarClipDistance(1000.0f);

		Camera camera2 = sm.createCamera("MainCamera2", Projection.PERSPECTIVE);
		rw.getViewport(1).setCamera(camera2);
		SceneNode cameraN2 = rootNode.createChildSceneNode("MainCamera2Node");
		cameraN2.attachObject(camera2);
		camera2.setMode('n');
		camera2.getFrustum().setFarClipDistance(1000.0f);
	}

	@Override
	protected void setupScene(Engine eng, SceneManager sm) throws IOException {
		im = new GenericInputManager();

		SceneNode worldObjectsN = sm.getRootSceneNode().createChildSceneNode("WorldObjectsN");

		// make manual objects
		SceneNode manualObjectN = worldObjectsN.createChildSceneNode("ManualObjectNode");

		// make triangle
		ManualObject triangle1 = makeTriangle1(eng, sm);
		SceneNode triangle1N = manualObjectN.createChildSceneNode("Triangle1Node");
		triangle1N.scale(100.0f, 0.05f, 100.0f);
		triangle1N.moveDown(2.0f);
		triangle1N.attachObject(triangle1);

		ManualObject triangle2 = makeTriangle2(eng, sm);
		SceneNode triangle2N = manualObjectN.createChildSceneNode("Triangle2Node");
		triangle2N.yaw(Degreef.createFrom(180.0f));
		triangle2N.scale(100.0f, 0.05f, 100.0f);
		triangle2N.moveDown(2.0f);
		triangle2N.attachObject(triangle2);

		SceneNode axisN = manualObjectN.createChildSceneNode("AxisNode");
		// x axis
		ManualObject pyrX = this.makeAxeX(eng, sm);
		SceneNode pyrNX = axisN.createChildSceneNode("AxeXNode");
		pyrNX.attachObject(pyrX);
		// y axis
		ManualObject pyrY = this.makeAxeY(eng, sm);
		SceneNode pyrNY = axisN.createChildSceneNode("AxeYNode");
		pyrNY.attachObject(pyrY);

		// z axis
		ManualObject pyrZ = this.makeAxeZ(eng, sm);
		SceneNode pyrNZ = axisN.createChildSceneNode("AxeZNode");
		pyrNZ.attachObject(pyrZ);

		SceneNode playerN = worldObjectsN.createChildSceneNode("PlayerNode");
		// dolphin avatar for player in the top window
		Entity dolphinE = sm.createEntity("MyDolphin", "dolphinHighPoly.obj");
		dolphinE.setPrimitive(Primitive.TRIANGLES);

		SceneNode dolphinN = playerN.createChildSceneNode("MyDolphinNode");
		dolphinN.attachObject(dolphinE);

		// dolphin avatar for player in the bottom window
		Entity dolphinE2 = sm.createEntity("MyDolphin2", "dolphinHighPoly.obj");
		dolphinE2.setPrimitive(Primitive.TRIANGLES);

		SceneNode dolphinN2 = playerN.createChildSceneNode("MyDolphinNode2");
		dolphinN2.attachObject(dolphinE2);

		setupOrbitCamera(eng, sm);
		setupInputs(sm);
		dolphinN.yaw(Degreef.createFrom(180.0f));
		dolphinN2.yaw(Degreef.createFrom(180.0f));

		// Planet model
		SceneNode planetN = worldObjectsN.createChildSceneNode("PlanetNode");

		// Planet 1 model
		Entity planet1E = sm.createEntity("MyPlanet1", "earth.obj");
		planet1E.setPrimitive(Primitive.TRIANGLES);
		SceneNode planet1N = planetN.createChildSceneNode(planet1E.getName() + "Node");
		planet1N.attachObject(planet1E);
		// planet1N.setLocalPosition(-2.0f, 2.0f, -1.0f);
		planet1N.setLocalPosition(20 + new Random().nextFloat() * (10 - 5), 0.0f,
				10 + new Random().nextFloat() * (30 - 20));
		planet1N.setLocalScale(3.0f, 3.0f, 3.0f);

		// Planet 1's rotation
		RotationController rc1 = new RotationController(Vector3f.createUnitVectorY(), .02f);
		rc1.addNode(planet1N);
		sm.addController(rc1);

		// Planet 2
		Entity planet2E = sm.createEntity("MyPlanet2", "earth.obj");
		planet2E.setPrimitive(Primitive.TRIANGLES);

		TextureManager tm = eng.getTextureManager();
		Texture blueTexture = tm.getAssetByPath("moon.jpeg");
		RenderSystem rs = sm.getRenderSystem();
		TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
		state.setTexture(blueTexture);
		planet2E.setRenderState(state);

		SceneNode planet2N = planetN.createChildSceneNode(planet2E.getName() + "Node");
		planet2N.setLocalPosition(5 + new Random().nextFloat() * (20 - 5), 0.0f,
				-20 - new Random().nextFloat() * (25 - 20));

		planet2N.scale(3.0f, 3.0f, 3.0f);
		planet2N.attachObject(planet2E);

		// Planet 2's rotation
		RotationController rc2 = new RotationController(Vector3f.createUnitVectorY(), 0.01f);
		rc2.addNode(planet2N);
		sm.addController(rc2);

		// Planet 3
		Entity planet3E = sm.createEntity("MyPlanet3", "earth.obj");
		planet3E.setPrimitive(Primitive.TRIANGLES);

		TextureManager tm3 = eng.getTextureManager();
		Texture redTexture = tm3.getAssetByPath("red.jpeg");
		RenderSystem rs3 = sm.getRenderSystem();
		TextureState state3 = (TextureState) rs3.createRenderState(RenderState.Type.TEXTURE);
		state3.setTexture(redTexture);
		planet3E.setRenderState(state3);

		SceneNode planet3N = planetN.createChildSceneNode(planet3E.getName() + "Node");
		planet3N.setLocalPosition(-5 - new Random().nextFloat() * (20 - 5), 0.0f,
				20 - new Random().nextFloat() * (30 - 20));
		planet3N.setLocalScale(3.0f, 3.0f, 3.0f);
		planet3N.attachObject(planet3E);

		// Planet 3's rotation
		RotationController rc3 = new RotationController(Vector3f.createUnitVectorY(), 0.05f);
		rc3.addNode(planet3N);
		sm.addController(rc3);

		// Planet 3
		Entity planet33E = sm.createEntity("MyPlanet33", "earth.obj");
		planet33E.setPrimitive(Primitive.TRIANGLES);

		TextureManager tm33 = eng.getTextureManager();
		Texture redTexture3 = tm33.getAssetByPath("red.jpeg");
		RenderSystem rs33 = sm.getRenderSystem();
		TextureState state33 = (TextureState) rs33.createRenderState(RenderState.Type.TEXTURE);
		state33.setTexture(redTexture3);
		planet33E.setRenderState(state33);

		SceneNode planet33N = planet3N.createChildSceneNode(planet3E.getName() + "3Node");
		planet33N.setLocalPosition(-5 - new Random().nextFloat() * (20 - 5), 0.0f,
				20 - new Random().nextFloat() * (30 - 20));
		planet33N.setLocalScale(0.1f, 0.1f, 0.1f);
		planet33N.attachObject(planet33E);

		// light
		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));

		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
		plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
		plight.setRange(5f);

		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
		plightNode.attachObject(plight);

		StretchController sc = new StretchController(); // user-defined node controller
		sc.addNode(dolphinN);
		sc.addNode(dolphinN2);
		sm.addController(sc);
	}

	protected ManualObject makeTriangle1(Engine eng, SceneManager sm) throws IOException {
		ManualObject triangle = sm.createManualObject("Triangle1");
		ManualObjectSection triangleSec = triangle.createManualSection("Triangle1Section");
		triangle.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

		float[] vertices = new float[] { 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // front top
				1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, // front bottom
				0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, // right top
				0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, // right bottom
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // left top
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, // left bottom
				0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // top
				0.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f // bottom
		};

		float[] texcoords = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // front top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // front bottom
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // right top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // right bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // left top
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // left bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // top
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f }; // bottom

		float[] normals = new float[] { 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, // front top
				0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, // front bottom
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, // right top
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, // right bottom
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left top
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left bottom
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // top
				0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f // bottom

		};

		int[] indices = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
				23 };

		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);

		triangleSec.setVertexBuffer(vertBuf);
		triangleSec.setTextureCoordsBuffer(texBuf);
		triangleSec.setNormalsBuffer(normBuf);
		triangleSec.setIndexBuffer(indexBuf);

		Texture tex = eng.getTextureManager().getAssetByPath("ground.jpeg");
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

		triangle.setDataSource(DataSource.INDEX_BUFFER);
		triangle.setRenderState(texState);
		triangle.setRenderState(faceState);

		return triangle;
	}

	protected ManualObject makeTriangle2(Engine eng, SceneManager sm) throws IOException {
		ManualObject triangle = sm.createManualObject("Triangle2");
		ManualObjectSection triangleSec = triangle.createManualSection("Triangle2Section");
		triangle.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

		float[] vertices = new float[] { 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // front top
				1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, // front bottom
				0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, // right top
				0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, // right bottom
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // left top
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, // left bottom
				0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // top
				0.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f // bottom
		};

		float[] texcoords = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // front top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // front bottom
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // right top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, // right bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // left top
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // left bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // top
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f }; // bottom

		float[] normals = new float[] { 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, // front top
				0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, // front bottom
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, // right top
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, // right bottom
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left top
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left bottom
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // top
				0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f // bottom

		};

		int[] indices = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
				23 };

		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);

		triangleSec.setVertexBuffer(vertBuf);
		triangleSec.setTextureCoordsBuffer(texBuf);
		triangleSec.setNormalsBuffer(normBuf);
		triangleSec.setIndexBuffer(indexBuf);

		Texture tex = eng.getTextureManager().getAssetByPath("ground.jpeg");
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

		triangle.setDataSource(DataSource.INDEX_BUFFER);
		triangle.setRenderState(texState);
		triangle.setRenderState(faceState);

		return triangle;
	}

	protected void setupOrbitCamera(Engine eng, SceneManager sm) {
		SceneNode dolphinN = sm.getSceneNode("MyDolphinNode");
		SceneNode cameraN = sm.getSceneNode("MainCameraNode");
		Camera camera = sm.getCamera("MainCamera");
		String gpName = im.getFirstGamepadName();
		orbitController1 = new Camera3Pcontroller(camera, cameraN, dolphinN, gpName, im);

		SceneNode dolphinN2 = sm.getSceneNode("MyDolphinNode2");
		SceneNode cameraN2 = sm.getSceneNode("MainCamera2Node");
		Camera camera2 = sm.getCamera("MainCamera2");
		String msName = im.getMouseName();
		orbitController2 = new Camera3Pcontroller(camera2, cameraN2, dolphinN2, msName, im);

	}

	protected void setupInputs(SceneManager sm) {
		String kbName = im.getKeyboardName();
		String gpName = im.getFirstGamepadName();
		String msName = im.getMouseName();
		System.out.println(msName);
		SceneNode dolphinN = this.getEngine().getSceneManager().getSceneNode("MyDolphinNode");
		SceneNode dolphinN2 = this.getEngine().getSceneManager().getSceneNode("MyDolphinNode2");

		// move action for dolphin1
		moveCAct = new MoveCAction(this);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.POV, moveCAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveLeftRightAct = new MoveLeftRightAction(dolphinN, this);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, moveLeftRightAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveUpDownAct = new MoveUpDownAction(dolphinN, this);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, moveUpDownAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		leftRightRotationAct = new LeftRightRotationAction(this);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, leftRightRotationAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		zoomAct = new ZoomAction(this);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Z, zoomAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// move action for dolphin2
		moveForwardAct = new MoveForwardAction(dolphinN2, this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveBackwardAct = new MoveBackwardAction(dolphinN2, this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveLeftAct = new MoveLeftAction(dolphinN2, this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveLeftAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveRightAct = new MoveRightAction(dolphinN2, this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveRightAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// adjust the camera elevation angle for dolphin2
		moveCUpAct = new MoveCUpAction(this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.UP, moveCUpAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveCDownAct = new MoveCDownAction(this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.DOWN, moveCDownAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveCLeftAct = new MoveCLeftAction(this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.LEFT, moveCLeftAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		moveCRightAct = new MoveCRightAction(this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.RIGHT, moveCRightAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// zoom in and out camera for dolphin2
		zoomInAct = new ZoomInAction(this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.I, zoomInAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		zoomOutAct = new ZoomOutAction(this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.O, zoomOutAct,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// rotate left and right for dolphin2
		rotateLeftAct2 = new RotateLeftAction(dolphinN2, this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.N, rotateLeftAct2,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		rotateRightAct2 = new RotateRightAction(dolphinN2, this);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.M, rotateRightAct2,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}

	// gamepad dolphin
	public void setCameraElevationAngle1(float newAngle) {
		this.orbitController1.setCameraElevationAngle(newAngle);
	}

	public float getCameraElevationAngle1() {
		return this.orbitController1.getCameraElevationAngle();
	}

	public void setRadius1(float r) {
		this.orbitController1.setRadis(r);
	}

	public float getRadius1() {
		return this.orbitController1.getRadias();
	}

	public void setCameraAzimuthAngle1(float newAngle) {
		this.orbitController1.setCameraAzimuth(newAngle);
	}

	public float getCameraAzimuthAngle1() {
		return this.orbitController1.getCameraAzimuth();
	}

	public void incrementScore1() {
		score1 += 5;
	}

	// keyboard dolphin
	public void setCameraElevationAngle2(float newAngle) {
		this.orbitController2.setCameraElevationAngle(newAngle);
	}

	public float getCameraElevationAngle2() {
		return this.orbitController2.getCameraElevationAngle();
	}

	public void setRadius2(float r) {
		this.orbitController2.setRadis(r);
	}

	public float getRadius2() {
		return this.orbitController2.getRadias();
	}

	public void setCameraAzimuthAngle2(float newAngle) {
		this.orbitController2.setCameraAzimuth(newAngle);
	}

	public float getCameraAzimuthAngle2() {
		return this.orbitController2.getCameraAzimuth();
	}

	public void incrementScore2() {
		score2 += 5;
	}

	@Override
	protected void update(Engine engine) {
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime / 1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		score1Str = Integer.toString(score1);
		score2Str = Integer.toString(score2);
		dispStr1 = "Time = " + elapsTimeStr + "   Score = " + score1Str;
		dispStr2 = "Time = " + elapsTimeStr + "   Score = " + score2Str;
		rs.setHUD(dispStr2, 30, 15);
		rs.setHUD2(dispStr1, 30, engine.getRenderSystem().getCanvas().getHeight() / 2 + 10);

		// tell the input manager to process the inputs
		im.update(elapsTime);
		orbitController1.updateCameraPosition();
		orbitController2.updateCameraPosition();
	}

	protected ManualObject makeAxeX(Engine eng, SceneManager sm) throws IOException {
		ManualObject axe = sm.createManualObject("AxesX");
		ManualObjectSection axeSec = axe.createManualSection("AxesSectionX");
		axe.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		axe.setPrimitive(Primitive.LINES);

		float[] vertices = new float[] { 0.0f, 0.0f, 0.0f, 15.0f, 0.0f, 0.0f };

		int[] indices = new int[] { 0, 1 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		axeSec.setVertexBuffer(vertBuf);
		axeSec.setIndexBuffer(indexBuf);

		axe.setDataSource(DataSource.INDEX_BUFFER);

		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");

		mat.setEmissive(Color.BLUE);
		Texture tex = eng.getTextureManager().getAssetByPath("bright-red.jpeg");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

		tstate.setTexture(tex);

		axeSec.setRenderState(tstate);

		axeSec.setMaterial(mat);

		return axe;
	}

	protected ManualObject makeAxeY(Engine eng, SceneManager sm) throws IOException {
		ManualObject axe = sm.createManualObject("AxesY");
		ManualObjectSection axeSec = axe.createManualSection("AxesSectionY");
		axe.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		axe.setPrimitive(Primitive.LINES);

		float[] vertices = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 15.0f, 0.0f };

		int[] indices = new int[] { 0, 1 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		axeSec.setVertexBuffer(vertBuf);
		axeSec.setIndexBuffer(indexBuf);

		axe.setDataSource(DataSource.INDEX_BUFFER);

		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");

		mat.setEmissive(Color.BLUE);
		Texture tex = eng.getTextureManager().getAssetByPath("bright-blue.jpeg");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

		tstate.setTexture(tex);

		axeSec.setRenderState(tstate);

		axeSec.setMaterial(mat);

		return axe;
	}

	protected ManualObject makeAxeZ(Engine eng, SceneManager sm) throws IOException {
		ManualObject axe = sm.createManualObject("AxesZ");
		ManualObjectSection axeSec = axe.createManualSection("AxesSectionZ");
		axe.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		axe.setPrimitive(Primitive.LINES);

		float[] vertices = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 15.0f };

		int[] indices = new int[] { 0, 1 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		axeSec.setVertexBuffer(vertBuf);
		axeSec.setIndexBuffer(indexBuf);

		axe.setDataSource(DataSource.INDEX_BUFFER);

		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");

		mat.setEmissive(Color.BLUE);
		Texture tex = eng.getTextureManager().getAssetByPath("bright-green.jpeg");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

		tstate.setTexture(tex);

		axeSec.setRenderState(tstate);

		axeSec.setMaterial(mat);

		return axe;
	}

	public boolean checkCollision(Node avN) {
		boolean ifCol = false;
		SceneNode p1 = this.getEngine().getSceneManager().getSceneNode("MyPlanet1Node");
		SceneNode p2 = this.getEngine().getSceneManager().getSceneNode("MyPlanet2Node");
		SceneNode p3 = this.getEngine().getSceneManager().getSceneNode("MyPlanet3Node");

		if (!ifVisited1) {
			if (this.ifNodesCol(avN, p1)) {
				System.out.println("score ++");
				ifCol = true;
				ifVisited1 = true;
				if (avN.getName() == "MyDolphinNode2") {
					RotateController rc = new RotateController();
					rc.addNode(p1);
					this.getEngine().getSceneManager().addController(rc);
				} else {
					DisappearController dc = new DisappearController();
					dc.addNode(p1);
					this.getEngine().getSceneManager().addController(dc);
				}
			}
		}

		if (!ifVisited2) {
			if (this.ifNodesCol(avN, p2)) {
				System.out.println("score ++");
				ifCol = true;
				ifVisited2 = true;
				if (avN.getName() == "MyDolphinNode2") {
					RotateController rc = new RotateController();
					rc.addNode(p2);
					this.getEngine().getSceneManager().addController(rc);
				} else {
					DisappearController dc = new DisappearController();
					dc.addNode(p2);
					this.getEngine().getSceneManager().addController(dc);
				}
			}
		}

		if (!ifVisited3) {
			if (this.ifNodesCol(avN, p3)) {
				System.out.println("score ++");
				ifCol = true;
				ifVisited3 = true;
				if (avN.getName() == "MyDolphinNode2") {
					RotateController rc = new RotateController();
					rc.addNode(p3);
					this.getEngine().getSceneManager().addController(rc);
				} else {
					DisappearController dc = new DisappearController();
					dc.addNode(p3);
					this.getEngine().getSceneManager().addController(dc);
				}
			}
		}

		return ifCol;
	}

	protected boolean ifNodesCol(Node node1, Node node2) {
		if ((Math.abs(node1.getLocalPosition().x() - node2.getLocalPosition().x()) < 7.0f)
				&& (Math.abs(node1.getLocalPosition().y() - node2.getLocalPosition().y()) < 7.0f)
				&& (Math.abs(node1.getLocalPosition().z() - node2.getLocalPosition().z()) < 7.0f)) {
			return true;
		} else {
			return false;
		}
	}
}
