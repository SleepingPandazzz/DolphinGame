package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveLeftRightAction extends AbstractInputAction {
	private Node avN;
	private MyGame game;

	public MoveLeftRightAction(Node node, MyGame g) {
		avN = node;
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		// move left
		if (e.getValue() > 0.7f) {
			avN.moveLeft(0.05f);
		}

		// move right
		if (e.getValue() < -0.7f) {
			avN.moveRight(0.05f);
		}

		if(game.checkCollision(avN)) {
			game.incrementScore1();
		}
		
	}

}
