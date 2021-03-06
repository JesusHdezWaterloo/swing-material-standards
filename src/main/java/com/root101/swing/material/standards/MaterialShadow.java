/*
 * Copyright 2021 Root101 (jhernandezb96@gmail.com, +53-5-426-8660).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Or read it directly from LICENCE.txt file at the root of this project.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.root101.swing.material.standards;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import org.jdesktop.core.animation.timing.KeyFrames;

/**
 * A renderer for Material shadows. Shadows are a sign of elevation, and help
 * distinguishing elements inside a Material-based GUI.
 *
 * @author Root101 (jhernandezb96@gmail.com, +53-5-426-8660)
 * @author JesusHdezWaterloo@Github
 */
public class MaterialShadow implements Serializable {

    /**
     * The default offset between the border of the shadow and the top of a
     * Material component. Original value: 10, optimal value: 5.
     */
    public static final int OFFSET_TOP = 5;
    /**
     * The default offset between the border of the shadow and the left of a
     * Material component. Original value: 20, optimal value: 10.
     */
    public static final int OFFSET_LEFT = 10;
    /**
     * The default offset between the border of the shadow and the bottom of a
     * Material component. Original value: 20, optimal value: 10.
     */
    public static final int OFFSET_BOTTOM = 10;
    /**
     * The default offset between the border of the shadow and the right of a
     * Material component. Original value: 20, optimal value: 10.
     */
    public static final int OFFSET_RIGHT = 10;

    /**
     * No elevation for the component.
     */
    public static final double ELEVATION_NONE = 0;

    /**
     * Low elevation for the component, ideal shadow for most cases.
     */
    public static final double ELEVATION_DEFAULT = 1;

    /**
     * Hight shadow, too big for use, only in specifics cases.
     */
    public static final double ELEVATION_HIGHTEST = 2;

    /**
     * Hight shadow, too big for use, only in specifics cases.
     */
    public static final double ELEVATION_TOP = 5;

    //Solo con dos frames y la interpolacion automatica se hacen el resto de las sombras con una calidad excelente
    private static final KeyFrames<Float> opacityKeyFrame = new KeyFrames.Builder<>(0f)
            .addFrame(0.34f, 1 / 5.0)
            .addFrame(0.37f, 2 / 5.0)
            .build();

    private static final KeyFrames<Float> radiusKeyFrame = new KeyFrames.Builder<>(0f)
            .addFrame(6f, 1 / 5.0)
            .addFrame(18f, 2 / 5.0)
            .build();

    private static final KeyFrames<Float> offsetKeyFrame = new KeyFrames.Builder<>(0f)
            .addFrame(1f, 1 / 5.0)
            .addFrame(3f, 2 / 5.0)
            .build();

    /**
     * Default constructor for a {@code MaterialShadow}. It is recommended to
     * keep a single instance for each component that requires it. The
     * components bundled in this library already handle this by themselves.
     */
    public MaterialShadow() {
    }

    /**
     * Renders this {@link MaterialShadow} into a {@link BufferedImage} and
     * returns it. A copy of the latest render is kept in case a shadow of the
     * same dimensions and elevation is needed in order to decrease CPU usage
     * when the component is idle.
     *
     * @param width the witdh of the square component casting a shadow, or
     * diameter if it is circular.
     * @param height the height of the square component casting a shadow.
     * @param radius the radius of the borders of a square component casting a
     * shadow.
     * @param level the depth of the shadow [0~5]
     * @param type the type of projected shadow, either square or circular
     * @return A {@link BufferedImage} with the contents of the shadow.
     * @see Type#SQUARE
     * @see Type#CIRCULAR
     */
    public BufferedImage render(int width, int height, int radius, double level, Type type) {
        switch (type) {
            case SQUARE:
                return MaterialShadow.renderShadow(width, height, level, radius);
            case CIRCULAR:
                return MaterialShadow.renderCircularShadow(width, height, level);
            case ROUND:
                return MaterialShadow.renderRoundShadow(width, height, level);
            default:
                return MaterialShadow.renderShadow(width, height, level, radius);
        }
    }

    /**
     * Creates a {@link BufferedImage} containing a shadow projected from a
     * square component of the given width and height.
     *
     * @param width the component's width, inpixels
     * @param height the component's height, inpixels
     * @param level the elevation level [0~5]
     * @return A {@link BufferedImage} with the contents of the shadow for a
     * circular component of the given radius.
     */
    public static BufferedImage renderShadow(int width, int height, int level) {
        return renderShadow(width, height, level, 5);
    }

    /**
     * Creates a {@link BufferedImage} containing a shadow projected from a
     * square component of the given width and height.
     *
     * @param width the component's width, inpixels
     * @param height the component's height, inpixels
     * @param level the elevation level [0~5]
     * @param borderRadius an applicable radius to the border of the shadow
     * @return A {@link BufferedImage} with the contents of the shadow for a
     * circular component of the given radius.
     */
    public static BufferedImage renderShadow(int width, int height, double level, int borderRadius) {
        if (level < ELEVATION_NONE || level > ELEVATION_TOP) {
            throw new IllegalArgumentException("Shadow level must be between " + ELEVATION_NONE + " and " + ELEVATION_TOP + " (inclusive)");
        }

        BufferedImage shadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        if (width > 0 && height > 0 && level != ELEVATION_NONE) {
            shadow = makeShadow(shadow, opacityKeyFrame.getInterpolatedValueAt(level / 5), radiusKeyFrame.getInterpolatedValueAt(level / 5),
                    0, offsetKeyFrame.getInterpolatedValueAt(level / 5), borderRadius);
        }
        return shadow;
    }

    /**
     * Creates a {@link BufferedImage} containing a shadow projected from a
     * circular component of the given radius.
     *
     *
     * @param width in pixels of circle diameter
     * @param height in pixels of circle diameter
     * @param level the elevation level [0~5]
     * @return A {@link BufferedImage} with the contents of the shadow for a
     * circular component of the given radius.
     */
    public static BufferedImage renderCircularShadow(int width, int height, double level) {
        if (level < ELEVATION_NONE || level > ELEVATION_TOP) {
            throw new IllegalArgumentException("Shadow level must be between " + ELEVATION_NONE + " and " + ELEVATION_TOP + " (inclusive)");
        }

        BufferedImage shadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        shadow = makeCircularShadow(shadow, opacityKeyFrame.getInterpolatedValueAt(level / 5), radiusKeyFrame.getInterpolatedValueAt(level / 5),
                0, offsetKeyFrame.getInterpolatedValueAt(level / 5));
        return shadow;
    }

    public static BufferedImage renderRoundShadow(int width, int height, double level) {
        if (level < ELEVATION_NONE || level > ELEVATION_TOP) {
            throw new IllegalArgumentException("Shadow level must be between " + ELEVATION_NONE + " and " + ELEVATION_TOP + " (inclusive)");
        }

        BufferedImage shadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        shadow = makeRoundShadow(shadow, opacityKeyFrame.getInterpolatedValueAt(level / 5), radiusKeyFrame.getInterpolatedValueAt(level / 5),
                0, offsetKeyFrame.getInterpolatedValueAt(level / 5));
        return shadow;
    }

    private static BufferedImage makeShadow(BufferedImage shadow, float opacity, float radius, float leftOffset, float topOffset, int borderRadius) {
        Graphics2D g2 = shadow.createGraphics();
        g2.setColor(new Color(0, 0, 0, opacity));
        g2.fill(new RoundRectangle2D.Float(OFFSET_LEFT + leftOffset, OFFSET_TOP + topOffset,
                shadow.getWidth() - OFFSET_LEFT - OFFSET_RIGHT,
                shadow.getHeight() - OFFSET_TOP - OFFSET_BOTTOM,
                borderRadius * 2, borderRadius * 2));
        g2.dispose();
        return FastGaussianBlur.blur(shadow, (int) radius);
    }

    private static BufferedImage makeCircularShadow(BufferedImage shadow, float opacity, float radius, float leftOffset, float topOffset) {
        Graphics2D g2 = shadow.createGraphics();
        g2.setColor(new Color(0, 0, 0, opacity));

        float w = shadow.getWidth() - OFFSET_LEFT - OFFSET_RIGHT;
        float h = shadow.getHeight() - OFFSET_TOP - OFFSET_BOTTOM;
        float x = OFFSET_LEFT + leftOffset;
        float y = OFFSET_TOP + topOffset;

        w = h = Math.min(w, h);
        x = (shadow.getWidth() - w) / 2;
        y = (shadow.getHeight() - h) / 2;

        g2.fill(new Ellipse2D.Float(x, y,
                w, h));
        g2.dispose();

        return FastGaussianBlur.blur(shadow, (int) radius, true);
    }

    private static BufferedImage makeRoundShadow(BufferedImage shadow, float opacity, float radius, float leftOffset, float topOffset) {
        Graphics2D g2 = shadow.createGraphics();
        g2.setColor(new Color(0, 0, 0, opacity));

        float w = shadow.getWidth() - OFFSET_LEFT - OFFSET_RIGHT;
        float h = shadow.getHeight() - OFFSET_TOP - OFFSET_BOTTOM;
        float x = OFFSET_LEFT + leftOffset;
        float y = OFFSET_TOP + topOffset;

        g2.fill(new Ellipse2D.Float(x, y,
                w, h));
        g2.dispose();

        return FastGaussianBlur.blur(shadow, (int) radius, true);
    }

    /**
     * The types of shadow available for rendering.
     */
    public static enum Type {

        /**
         * A square, classic shadow. For panels, windows and paper components in
         * general.
         */
        SQUARE,
        /**
         * Sombra completamente circular centrada.
         */
        CIRCULAR,
        /**
         * A circular, rounded shadow. Mainly for specific components like FABs.
         */
        ROUND
    }

}
