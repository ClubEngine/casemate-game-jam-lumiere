
package components;

import com.artemis.Component;

/**
 *
 */
public class Gate extends Component {

    private final int mColor;

    public Gate(int color) {
        this.mColor = color;
    }

    public int getMaskColor() {
        return mColor;
    }

}
