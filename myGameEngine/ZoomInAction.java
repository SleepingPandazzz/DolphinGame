package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class ZoomInAction extends AbstractInputAction {
	private MyGame game;

	public ZoomInAction(MyGame g) {
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		if (game.getRadius2() > 1.0f) {
			game.setRadius2(game.getRadius2() - 0.01f);
		}
	}

}
