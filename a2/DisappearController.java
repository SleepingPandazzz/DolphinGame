package a2;

import ray.rage.scene.Node;
import ray.rage.scene.controllers.AbstractController;
import ray.rage.scene.controllers.RotationController;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class DisappearController extends AbstractController {	
	private float scaleRate = 0.003f; // growth per second
	private float cycleTime = 200.0f; // default cycle time
	private float totalTime = 0.0f;
	private float direction = 1.0f;

	@Override
	protected void updateImpl(float elapsedTimeMillis) {
		totalTime += elapsedTimeMillis;
		float scaleAmt = 1.0f + direction * scaleRate;

		if (totalTime > cycleTime) {
			direction = -direction;
			totalTime = 0.0f;
		}

		for (Node n : super.controlledNodesList) {
			Vector3 curPos = n.getLocalPosition();
			curPos = Vector3f.createFrom(curPos.x() * scaleAmt, curPos.y(), curPos.z());
			n.setLocalPosition(curPos);
		}
	}
}