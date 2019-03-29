package myGameEngine;

import a2.MyGame;
import a2.RotateController;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveBackwardAction extends AbstractInputAction {
	private Node avN;
	private MyGame game;

	public MoveBackwardAction(Node n, MyGame g) {
		avN = n;
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		avN.moveBackward(0.05f);
		if(game.checkCollision(avN)) {
			game.incrementScore2();
		}
	}
}
