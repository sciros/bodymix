package net.sciros.bodymix.style;

import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class GradientGenerator {
    public LayerDrawable generateDualGradient (Resources resources, int height, final int[] colors, final float[] positions) {
        Drawable[] layers = new Drawable[1];
        
        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize (int width, int height) {
                LinearGradient lg = new LinearGradient(
                        0, 0,
                        0, height,
                        colors,
                        null,
                        Shader.TileMode.CLAMP);
                return lg;
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        p.setCornerRadii(new float[] { 5, 5, 5, 5, 0, 0, 0, 0 });
        layers[0] = (Drawable) p;

        LayerDrawable composite = new LayerDrawable(layers);
        return composite;
    }
    
}
