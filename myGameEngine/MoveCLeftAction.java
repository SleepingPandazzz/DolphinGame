package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveCLeftAction extends AbstractInputAction {
	private MyGame game;

	public MoveCLeftAction(MyGame g) {
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
//		if (game.getCameraAzimuthAngle2() > -30) {
			game.setCameraAzimuthAngle2(game.getCameraAzimuthAngle2() - 5.0f);
//		}
	}
}