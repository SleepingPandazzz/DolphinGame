package a2;

import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.*;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3Pcontroller {
	private Camera camera; // the camera being controlled
	private SceneNode cameraN; // the node the camera is attached to
	private SceneNode target; // the target the camera looks at
	private float cameraAzimuth; // rotation of camera around Y axis
	private float cameraElevation; // elevation of camera above target
	private float radias; // distance between camera and target
	private Vector3 targetPos; // target's position in the world
	private Vector3 worldUpVec;

	public Camera3Pcontroller(Camera cam, SceneNode camN, SceneNode targ, String controllerName, InputManager im) {
		camera = cam;
		cameraN = camN;
		target = targ;
		cameraAzimuth = 0.0f; // start from BEHIND and ABOVE the target
		cameraElevation = 20.0f; // elevation is in degrees
		radias = 2.0f;
		worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setUpInput(im, controllerName);
	}

	public void setCameraElevationAngle(float newElevation) {
		this.cameraElevation = newElevation;
	}

	public void setRadis(float r) {
		this.radias = r;
	}
	
	public void setCameraAzimuth(float newAzimuth) {
		this.cameraAzimuth=newAzimuth;
	}

	public float getCameraElevationAngle() {
		return this.cameraElevation;
	}

	public float getRadias() {
		return this.radias;
	}
	
	public float getCameraAzimuth() {
		return this.cameraAzimuth;
	}

	public void updateCameraPosition() {
		double theta = Math.toRadians(cameraAzimuth); // rot around target
		double phi = Math.toRadians(this.getCameraElevationAngle()); // altitude angle
		double x = radias * Math.cos(phi) * Math.sin(theta);
		double y = radias * Math.sin(phi);
		double z = radias * Math.cos(phi) * Math.cos(theta);
		cameraN.setLocalPosition(Vector3f.createFrom((float) x, (float) y, (float) z).add(target.getWorldPosition()));
		// System.out.println("target local position:
		// "+target.getWorldPosition().toString());
		cameraN.lookAt(target, worldUpVec); // points the node towards the specified location
		// System.out.println("camera N.local position:
		// "+cameraN.getLocalPosition().toString());
	}

	private void setUpInput(InputManager im, String cn) {
		Action orbitAAction = new OrbitAroundAction();
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RX, orbitAAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}

	private class OrbitAroundAction extends AbstractInputAction {
		// moves the camera around the target (change camera azimuth)
		@Override
		public void performAction(float time, Event evt) {
			float rotAmount;
			if (evt.getValue() < -0.2) {
				rotAmount = -0.2f;
			} else {
				if (evt.getValue() > 0.2) {
					rotAmount = 0.2f;
				} else {
					rotAmount = 0.0f;
				}
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
	}
}
