
package components;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.jsfml.graphics.ConstTexture;

/**
 *
 */
public class MultipleTextures extends AbstractTextureComponent {

    /**
     * Special type to identify animations
     */
    public interface MultipleTexturesEnum {}

    private ConstTexture mCurrentTexture;
    private MultipleTexturesEnum mCurrentId;
    private final Map<MultipleTexturesEnum, ConstTexture> mTextures;

    public MultipleTextures() {
        mCurrentTexture = null;
        mCurrentId = null;
        mTextures = new HashMap<>();
    }

    public void add(MultipleTexturesEnum id, ConstTexture texture) {
        Validate.notNull(texture);
        mTextures.put(id, texture);
    }

    public void setTexture(MultipleTexturesEnum id) {
        if (id != mCurrentId) {
            if (mTextures.containsKey(id)) {
                mCurrentTexture = mTextures.get(id);
                mCurrentId = id;
            }
        }
    }

    @Override
    public ConstTexture getTexture() {
        Validate.notNull(mCurrentTexture);
        return mCurrentTexture;
    }


}
