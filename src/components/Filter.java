
package components;

import com.artemis.Component;

/**
 *
 */
public class Filter extends Component {

    private final int mColor;

    public Filter(int color) {
        mColor = color;
    }

    public int getMaskColor() {
        return mColor;
    }

}
