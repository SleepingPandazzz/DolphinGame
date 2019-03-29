package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveCUpAction extends AbstractInputAction {
	private MyGame game;

	public MoveCUpAction(MyGame g) {
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		if (game.getCameraElevationAngle2() < 60) {
			game.setCameraElevationAngle2(game.getCameraElevationAngle2() + 5.0f);
		}
	}
}
