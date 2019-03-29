package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveCAction extends AbstractInputAction {
	private MyGame game;

	public MoveCAction(MyGame g) {
		game = g;
	}

	@Override
	public void performAction(float time, Event e) {
		// move camera up for dolphin1
		if (e.getValue() == 0.25) {
			if (game.getCameraElevationAngle1() < 60) {
				game.setCameraElevationAngle1(game.getCameraElevationAngle1() + 5.0f);
			}
		}

		// move camera down for dolphin1
		if (e.getValue() == 0.75) {
			if (game.getCameraElevationAngle1() > -30) {
				game.setCameraElevationAngle1(game.getCameraElevationAngle1() - 5.0f);
			}
		}

		// move camera left for dolphin1
		if (e.getValue() == 1.0) {
//			if (game.getCameraAzimuthAngle1() > -30) {
				game.setCameraAzimuthAngle1(game.getCameraAzimuthAngle1() - 5.0f);
//			}
		}

		// move camera right for dolphin1
		if (e.getValue() == 0.5) {
//			if (game.getCameraAzimuthAngle1() < 30) {
				game.setCameraAzimuthAngle1(game.getCameraAzimuthAngle1() + 5.0f);
//			}
		}

	}
}
