package gui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private final int THUMB_SIZE = 2; // Grosor de la barra
    private final Color THUMB_COLOR = new Color(37, 99, 235); //  Azul  
    private final Color TRACK_COLOR = new Color(243, 244, 246); // Fondo gris claro

    @Override
    protected void configureScrollBarColors() {
        thumbColor = THUMB_COLOR;
        trackColor = TRACK_COLOR;
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(TRACK_COLOR);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(THUMB_COLOR);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 0, 0);
        g2.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
}