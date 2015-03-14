
package components;

import com.artemis.Component;

/**
 *
 */
public class AILuming extends Component {

    public enum LumState {

        INIT,
        MOVING,
        FALLING,
        DEAD
    }

    private LumState mState;
    private int mFallingCounter;

    public AILuming() {
        mState = LumState.INIT;
    }

    public LumState getState() {
        return mState;
    }

    public void setState(LumState mState) {
        this.mState = mState;
    }

    public void rstFallingCounter() {
        mFallingCounter = 0;
    }

    public void incrFallingCounter() {
        mFallingCounter++;
    }

    public int getFallingCounter() {
        return mFallingCounter;
    }

}
