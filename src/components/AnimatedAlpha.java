
package components;

import com.artemis.Component;

/**
 *
 */
public class AnimatedAlpha extends Component {

    private int alpha;
    private final int delta;

    public AnimatedAlpha(int alpha, int delta) {
        this.alpha = alpha;
        this.delta = delta;
    }

    public void addAlpha(float dtime) {
        alpha += delta * dtime;
    }

    public int getAlpha() {
        return alpha;
    }

}
