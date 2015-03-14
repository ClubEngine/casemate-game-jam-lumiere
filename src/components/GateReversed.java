
package components;

import com.artemis.Component;

/**
 *
 */
public class GateReversed extends Component {
    private int date;

    public GateReversed() {
        this.date = 0;
    }

    public int isAcquit() {
        return date;
    }

    public void incrDate() {
        date++;
    }

    public boolean acquit() {
        return date++ == 0;
    }

}
