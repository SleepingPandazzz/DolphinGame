package myGameEngine;

import a2.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class ZoomAction extends AbstractInputAction {
	private MyGame game;
	
	public ZoomAction(MyGame g) {
		game=g;
	}

	@Override
	public void performAction(float time, Event e) {
		// zoom out
		if(e.getValue()<0.2) {
			if (game.getRadius1() < 3.0f) {
				game.setRadius1(game.getRadius1() + 0.01f);
			}
		}
		
		//zoom in
		if(e.getValue()>0.8) {
			if (game.getRadius1() > 1.0f) {
				game.setRadius1(game.getRadius1() - 0.01f);
			}
		}
		
	}
	
	
}
