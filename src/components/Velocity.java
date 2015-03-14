
package components;

import com.artemis.Component;
import org.jsfml.system.Vector2f;

/**
 *
 */
public class Velocity extends Component {

    private Vector2f mVelocity;

    public Velocity() {
        mVelocity = Vector2f.ZERO;
    }

    public Velocity(Vector2f vel) {
        mVelocity = vel;
    }

    public Vector2f getVelocity() {
        return mVelocity;
    }

    public void setVelocity(Vector2f velocity) {
        mVelocity = velocity;
    }

    public void setVelocity(float x, float y) {
        mVelocity = new Vector2f(x, y);
    }
}
