package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveUpDownAction extends AbstractInputAction {
	private Node avN;
	private MyGame game;

	public MoveUpDownAction(Node n, MyGame g) {
		avN = n;
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		// move forward
		if (e.getValue() < -0.7f) {
			avN.moveForward(0.05f);
		}

		// move backward
		if (e.getValue() > 0.7f) {
			avN.moveBackward(0.05f);
		}

		if(game.checkCollision(avN)) {
			game.incrementScore1();
		}
		
	}
}
