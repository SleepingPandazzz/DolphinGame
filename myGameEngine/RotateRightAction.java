package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateRightAction extends AbstractInputAction {
	private MyGame game;
	private Node n;

	public RotateRightAction(Node node, MyGame g) {
		n = node;
		game = g;
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		Angle rotAmt = Degreef.createFrom(-5.0f);
		n.yaw(rotAmt);
		game.setCameraAzimuthAngle2(game.getCameraAzimuthAngle2() - 5.0f);
	}
}
