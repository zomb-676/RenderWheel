package gl;

import lombok.Getter;
import org.apache.http.util.Asserts;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.Closeable;

/**
 * the base class of all gl object
 */
public abstract class GLObject implements Closeable {

	public static final int VALID_ID_BEGIN = 0;
	public static final int INITIAL_ID = -1;
	public static final int DESTROYED_ID = -2;

	/**
	 * the id/pname/index of this object<br>
	 * can't be got before it is initialized
	 */
	private int id = INITIAL_ID;
	@Getter
	private final String name;

	protected GLObject(String name) {
		this.name = name;
	}

	public final boolean isValid() {
		return id >= VALID_ID_BEGIN;
	}

	@OverridingMethodsMustInvokeSuper
	protected abstract void release();

	protected abstract int generateId();

	@Override
	public void close() {
		if (isValid()) {
			this.release();
			this.id = DESTROYED_ID;
		}
	}

	/**
	 * should only be overwritten by default stuffs, which always return id = 0
	 *
	 * @return the getter for the gl object's id
	 * @throws IllegalStateException while id is invalid
	 */
	public int getId() {
		Asserts.check(this.isValid(), "can't get an invalid id of gl object, name:" + name);
		return this.id;
	}

	protected final void initId() {
		Asserts.check(!this.isValid(), "can't init id multi times, name:" + name);
		this.id = generateId();
		Asserts.check(this.isValid(), "gl object should be valid after id generated, name:" + name);
	}

	public final boolean isDestoryed() {
		return this.id == DESTROYED_ID;
	}
}
