
package components;

import com.artemis.Component;

/**
 *
 */
public class AILuming extends Component {
    private boolean mColorHasChanged;

    public enum LumState {

        INIT,
        MOVING,
        FALLING,
        DEAD,
        COLLECTED
    }

    private LumState mState;
    private int mFallingCounter;
    private int mMaskColor;

    public AILuming(int maskColor) {
        mState = LumState.INIT;
        mMaskColor = maskColor;
    }

    public int getMaskColor() {
        return mMaskColor;
    }

    public void setMaskColor(int color) {
        mMaskColor = color;
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

    public void colorChanged() {
        mColorHasChanged = true;
    }

    public boolean colorHasChanged() {
        boolean old = mColorHasChanged;
        mColorHasChanged = false;
        return old;
    }

}
