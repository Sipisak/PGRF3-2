package transforms;

import java.util.Locale;

import static transforms.GluUtils.gluLookAt;

public class GLCamera {

	private double azimuth, radius, zenith;

	private boolean first_person, valid; // true -> 1st person, 0 -> 3rd person

	private Vec3D eye, eye_vector, up, pos, centre;

	private void compute_matrix() {
		eye_vector = new Vec3D(
				Math.sin(azimuth) * Math.cos(zenith),
				Math.sin(zenith),
				-(Math.cos(azimuth) * Math.cos(zenith))
		);
		up = new Vec3D(
				Math.sin(azimuth) * Math.cos(zenith + Math.PI / 2),
				Math.sin(zenith + Math.PI / 2),
				-(Math.cos(azimuth) * Math.cos(zenith + Math.PI / 2))
		);
		if (first_person) {
			eye = new Vec3D(pos);
			centre = eye.add(eye_vector.mul(radius));
		} else {
			eye = pos.add(eye_vector.mul(-1 * radius));
			centre = new Vec3D(pos);
		}
		valid = true;
	}

	public GLCamera() {
		azimuth = zenith = 0.0f;
		radius = 1.0f;
		pos = new Vec3D(0.0f, 0.0f, 0.0f);
		first_person = true;
		valid = false;
	}

	public GLCamera(GLCamera camera) {
		azimuth = camera.getAzimuth();
		zenith = camera.getZenith();
		radius = camera.getRadius();
		pos = new Vec3D(camera.getPosition());
		first_person = camera.getFirstPerson();
		valid = false;
	}

	public void addAzimuth(double ang) {
		azimuth += ang;
		valid = false;
	}

	public void addRadius(double dist) {
		if (radius + dist < 0.1f)
			return;
		radius += dist;
		valid = false;
	}

	public void mulRadius(double scale) {
		if (radius * scale < 0.1f)
			return;
		radius *= scale;
		valid = false;
	}

	public void addZenith(double ang) {
		if (Math.abs(zenith + ang) <= Math.PI / 2) {
			zenith += ang;
			valid = false;
		}
	}

	public void setAzimuth(double ang) {
		azimuth = ang;
		valid = false;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setRadius(double dist) {
		radius = dist;
		valid = false;
	}

	public void setZenith(double ang) {
		zenith = ang;
		valid = false;
	}

	public double getZenith() {
		return zenith;
	}

	public double getRadius() {
		return radius;
	}

	public void backward(double speed) {
		forward((-1) * speed);
	}

	public void forward(double speed) {
		pos = pos.add(new Vec3D(
				Math.sin(azimuth) * Math.cos(zenith),
				Math.sin(zenith),
				-Math.cos(azimuth) * Math.cos(zenith))
				.mul(speed));
		valid = false;
	}

	public void left(double speed) {
		right((-1) * speed);
	}

	public void right(double speed) {
		pos = pos.add(new Vec3D(
				 -Math.sin(azimuth - Math.PI / 2),
				0.0f,
				 +Math.cos(azimuth - Math.PI / 2))
				.mul(speed));
		valid = false;
	}

	public void down(double speed) {
		pos = pos.withY(pos.getY() - speed);
		valid = false;
	}

	public void up(double speed) {
		pos = pos.withY(pos.getY() + speed);
		valid = false;
	}

	public void move(Vec3D dir) {
		pos = pos.add(dir);
		valid = false;
	}

	public void setPosition(Vec3D apos) {
		pos = new Vec3D(apos);
		valid = false;
	}

	public boolean getFirstPerson() {
		return first_person;
	}

	public void setFirstPerson(boolean fp) {
		first_person = fp;
		valid = false;
	}

	public Vec3D getEye() {
		if (!valid)
			compute_matrix();
		return eye;
	}

	public Vec3D getEyeVector() {
		if (!valid)
			compute_matrix();
		return eye_vector;
	}

	public Vec3D getPosition() {
		if (!valid)
			compute_matrix();
		return pos;
	}

	public void setMatrix() {
		if (!valid)
			compute_matrix();
		gluLookAt(
				eye.getX(), eye.getY(), eye.getZ(),
				centre.getX(), centre.getY(), centre.getZ(),
				up.getX(), up.getY(), up.getZ()
		);
	}

	public String toString(final String format) {
		return String.format(Locale.US,
				"Camera()\n" +
						"	.withFirstPerson("+ getFirstPerson() + ")\n" +
						"	.withPosition(new Vec3D"+ getPosition().toString(format) + ")\n" +
						"	.withAzimuth("+ format + ")\n" +
						"	.withZenith("+ format + ")\n" +
						"	.withRadius("+ format + ")",
				getAzimuth(), getZenith(), getRadius()
		);
	}

	public String toString() {
		return toString("%4.2f");
	}
}