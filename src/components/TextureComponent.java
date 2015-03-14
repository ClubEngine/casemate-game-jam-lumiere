
package components;

import org.jsfml.graphics.ConstTexture;

/**
 *
 */
public class TextureComponent extends AbstractTextureComponent {

    private final ConstTexture texture;

    public TextureComponent(ConstTexture texture) {
        this.texture = texture;
    }

    @Override
    public ConstTexture getTexture() {
        return texture;
    }

}
