
package components;

import com.artemis.Component;
import com.artemis.Entity;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class Exit extends Component {

    private int mNumberLemmingsCollected;
    private final int mNumberLemmingsRequested;

    private Set<Entity> lumingsCollected;

    public Exit(int numberLemmingsRequested) {
        this.mNumberLemmingsCollected = 0;
        this.mNumberLemmingsRequested = numberLemmingsRequested;
        lumingsCollected = new HashSet<>();
    }

    public void incrNumberLumingsCollected() {
        mNumberLemmingsCollected++;
    }

    public int getNumberLumingsCollected() {
        return mNumberLemmingsCollected;
    }

    public int getNumberLumingsRequested() {
        return mNumberLemmingsRequested;
    }

    /**
     *
     * @param lum
     * @return true if the luming was not already collected.
     */
    public boolean addCollectedLuming(Entity lum) {
        return lumingsCollected.add(lum);
    }


}