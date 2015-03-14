
package systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import components.AbstractTextureComponent;
import components.AbstractTextureRect;
import components.AnimatedAlpha;
import components.Transformation;
import graphics.GraphicEngine;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Transform;

/**
 *
 */
public class RenderSpriteSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Transformation> transm;
    @Mapper
    ComponentMapper<AbstractTextureComponent> rsm;
    @Mapper
    ComponentMapper<AbstractTextureRect> trm;
    @Mapper
    ComponentMapper<AnimatedAlpha> aam;

    private final GraphicEngine mGraphicEngine;
    private final Sprite mTmpSprite;

    @SuppressWarnings("unchecked")
    public RenderSpriteSystem(GraphicEngine graphicEngine) {
        super(Aspect.getAspectForAll(Transformation.class,
                AbstractTextureComponent.class,
                AbstractTextureRect.class
        ).one(AbstractTextureRect.class,
                AnimatedAlpha.class));

        mGraphicEngine = graphicEngine;
        mTmpSprite = new Sprite();
    }

    @Override
    protected void process(Entity entity) {
        Transform transform = transm.get(entity).getTransformable().getTransform();
        AbstractTextureComponent rs = rsm.get(entity);
        AbstractTextureRect tr = trm.get(entity);
        
        mTmpSprite.setTexture(rs.getTexture());
        mTmpSprite.setTextureRect(tr.getRect());

        if (aam.has(entity)) {
            mTmpSprite.setColor(new Color(255, 255, 255, aam.get(entity).getAlpha()));
        } else {
            mTmpSprite.setColor(Color.WHITE);
        }

        RenderStates renderStates = new RenderStates(transform);
        mGraphicEngine.getRenderTarget().draw(mTmpSprite, renderStates);
    }

}
