package com.jhw.swing.material.standards.example;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.swing.ImageIcon;
import javax.swing.UnsupportedLookAndFeelException;
import com.jhw.swing.material.standards.MaterialImages;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Jesus Hernandez Barrios (jhernandezb96@gmail.com)
 */
public class MATERIAL_IMAGES_EXAMPLE extends javax.swing.JFrame {

    Field[] fields = MaterialImages.class.getDeclaredFields();

    public MATERIAL_IMAGES_EXAMPLE() {
        initComponents();

        this.panelBack.setLayout(new GridLayout(0, 10));

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())
                    && BufferedImage.class.isAssignableFrom(field.getType())) {
                try {
                    ImageIcon im = new ImageIcon((BufferedImage) field.get(null));
                    String name = field.getName();

                    JLabel mp = new JLabel();
                    mp.setIcon(im);
                    mp.setToolTipText(name);
                    this.panelBack.add(mp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("done");
        this.validate();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);//maximize the window

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//
    private void initComponents() {

        panelBack = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(panelBack);

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnsupportedLookAndFeelException {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MATERIAL_IMAGES_EXAMPLE().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//:variables
    private JPanel panelBack;
    // End of variables declaration                   

}