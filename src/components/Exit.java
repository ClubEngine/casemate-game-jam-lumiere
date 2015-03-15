
package components;

import com.artemis.Component;
import com.artemis.Entity;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class Exit extends Component {

    private final int mColor;
    private final Set<Entity> lumingsCollected;

    public Exit(int color) {
        lumingsCollected = new HashSet<>();
        mColor = color;
    }   

    public int getNumberLumingsCollected() {
        return lumingsCollected.size();
    }

    public void addCollectedLuming(Entity lum) {
        lumingsCollected.add(lum);
    }

    public int getMaskColor() {
        return mColor;
    }
}
