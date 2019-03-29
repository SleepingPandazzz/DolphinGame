package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveLeftAction extends AbstractInputAction {
	private Node avN;
	private MyGame game;

	public MoveLeftAction(Node n, MyGame g) {
		avN = n;
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		avN.moveRight(0.05f);
		if (game.checkCollision(avN)) {
			game.incrementScore2();
		}
	}
}
