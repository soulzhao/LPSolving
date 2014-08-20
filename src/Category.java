import java.util.Map;


/**
 * TODO Put here a description of what this class does.
 *
 * @author xizhzhao.
 *         Created 2014-8-20.
 */
public class Category {
	float target;
	float range;
	
	public Category(float target, float range) {
		super();
		this.target = target;
		this.range = range;
	}

	public float getTarget() {
		return this.target;
	}

	public void setTarget(float target) {
		this.target = target;
	}

	public float getRange() {
		return this.range;
	}

	public void setRange(float range) {
		this.range = range;
	}
}
