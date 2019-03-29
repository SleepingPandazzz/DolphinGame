package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveForwardAction extends AbstractInputAction {
	private Node avN;
	private MyGame game;

	public MoveForwardAction(Node n, MyGame g) {
		avN = n;
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		avN.moveForward(0.05f);
		if (game.checkCollision(avN)) {
			game.incrementScore2();
		}
	}
}
