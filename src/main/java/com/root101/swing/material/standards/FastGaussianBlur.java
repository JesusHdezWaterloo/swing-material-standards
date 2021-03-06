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

import com.twelvemonkeys.image.ConvolveWithEdgeOp;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * Implementarion of a FastGaussinBlur to blur an specific Buffered image. It's
 * used to make shadow for the material-base components.<\br>
 * Use {@link ConvolveWithEdgeOp} to the better performance blur on the edges.
 *
 * @author Book Filthy Rich Clients QA76.73.C153H33 2007 at page. 431
 * @author Root101 (jhernandezb96@gmail.com, +53-5-426-8660)
 * @author JesusHdezWaterloo@Github
 */
public class FastGaussianBlur {

    /**
     * En realidad no hay que hacerle el blur a toda la imagen, ya que en su
     * mayor parte va a estar cubierta por un componente. Por lo tanto, se
     * separan las partes que se van a ver y a esas es a las que se les hace el
     * blur.<\br>
     * La manera de hacer el blur se elige dinamicamente en dependencia del
     * tamanno, por unas prubas de estres realizadas al algoritmo se probo que
     * el tamanno optimo se aproxima a los 150x150 px
     *
     * @param image
     * @param radius
     * @return
     */
    public static BufferedImage blur(BufferedImage image, double radius) {
        return blur(image, radius, false);
    }

    public static BufferedImage blur(BufferedImage image, double radius, boolean all) {
        final int w = image.getWidth();
        final int h = image.getHeight();
        ConvolveWithEdgeOp filterTrue = getGaussianBlurFilter((float) radius, true);
        ConvolveWithEdgeOp filterFalse = getGaussianBlurFilter((float) radius, false);

        if (all) {//si hay que pintarla toda ya, si no, uso varios algoritmos... se puede unir con el if de abajo pero asi se ve bien la diferencia
            image = filterTrue.filter(image, null);
            return filterFalse.filter(image, null);
        }

        if (w < 150 || h < 150) {//para sombras pequennas usar el clasico con la imagen completa
            image = filterTrue.filter(image, null);
            return filterFalse.filter(image, null);
        } else {//para imagenes grandes picarla por pedazos y despues unirla
            BufferedImage top = image.getSubimage(0, 0, w, 2 * MaterialShadow.OFFSET_TOP);
            top = filterTrue.filter(top, null);
            top = filterFalse.filter(top, null);

            BufferedImage down = image.getSubimage(0, h - 2 * MaterialShadow.OFFSET_BOTTOM, w, 2 * MaterialShadow.OFFSET_BOTTOM);
            down = filterTrue.filter(down, null);
            down = filterFalse.filter(down, null);

            BufferedImage left = image.getSubimage(0, 2 * MaterialShadow.OFFSET_TOP, 2 * MaterialShadow.OFFSET_LEFT, h - 2 * MaterialShadow.OFFSET_TOP - 2 * MaterialShadow.OFFSET_BOTTOM);
            left = filterTrue.filter(left, null);
            left = filterFalse.filter(left, null);

            BufferedImage right = image.getSubimage(w - 2 * MaterialShadow.OFFSET_RIGHT, 2 * MaterialShadow.OFFSET_TOP, 2 * MaterialShadow.OFFSET_RIGHT, h - 2 * MaterialShadow.OFFSET_TOP - 2 * MaterialShadow.OFFSET_BOTTOM);
            right = filterTrue.filter(right, null);
            right = filterFalse.filter(right, null);

            BufferedImage result = new BufferedImage(w, h, image.getType());
            Graphics2D g2 = result.createGraphics();
            g2.drawImage(top, null, 0, 0);
            g2.drawImage(down, null, 0, h - 2 * MaterialShadow.OFFSET_BOTTOM);
            g2.drawImage(left, null, 0, 2 * MaterialShadow.OFFSET_TOP);
            g2.drawImage(right, null, w - 2 * MaterialShadow.OFFSET_RIGHT, 2 * MaterialShadow.OFFSET_TOP);

            return result;
        }
    }

    private static ConvolveWithEdgeOp getGaussianBlurFilter(float radius, boolean horizontal) {
        radius++;
        int radiusInt = (int) Math.ceil(radius);
        int size = radiusInt * 2 + 1;
        float[] data = new float[size];
        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        for (int i = -radiusInt; i <= radiusInt; i++) {
            float distance = i * i;
            int index = i + radiusInt;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare)
                    / sigmaRoot;
            total += data[index];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }
        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveWithEdgeOp(kernel, ConvolveWithEdgeOp.EDGE_REFLECT, null);
    }

}
