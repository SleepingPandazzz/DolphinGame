package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class LeftRightRotationAction extends AbstractInputAction {
	private MyGame game;

	public LeftRightRotationAction(MyGame g) {
		game = g;
	}

	@Override
	public void performAction(float arg0, Event e) {
		SceneNode dNode = game.getEngine().getSceneManager().getSceneNode("MyDolphinNode");
		if (e.getValue() < -0.7) {
			Angle rotAmt = Degreef.createFrom(5.0f);
			dNode.yaw(rotAmt);
			game.setCameraAzimuthAngle1(game.getCameraAzimuthAngle1() + 5.0f);
		}

		if (e.getValue() > 0.7) {
			Angle rotAmt = Degreef.createFrom(-5.0f);
			dNode.yaw(rotAmt);
			game.setCameraAzimuthAngle1(game.getCameraAzimuthAngle1() - 5.0f);
		}
	}

}
