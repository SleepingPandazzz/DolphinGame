package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class ZoomOutAction extends AbstractInputAction {
	private MyGame game;

	public ZoomOutAction(MyGame g) {
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		if (game.getRadius2() < 3.0f) {
			game.setRadius2(game.getRadius2() + 0.01f);
		}
	}

}
