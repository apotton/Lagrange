import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

public class Souris {
    /** Abscisse de la souris */
    static public int x;

    /** Ordonnée de la souris */
    static public int y;

    /** Rayon de la sphère autour de la souris */
    static public int rayon = 50;

    /** Action de la sphère */
    static public boolean visible = false;

    /** Potentiel attractif ou répulsif */
    static int direction = 1;

    /** Gestion des clics dans la fenêtre */
    public static MouseListener clic() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Souris.visible = !Souris.visible;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Click gauche
                    Souris.direction = 1;
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // Click droit
                    Souris.direction = -1;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

        };
    }

    /** Gestion du déplacement de la souris */
    public static MouseMotionListener mouvement() {
        return new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                Souris.x = e.getX();
                Souris.y = e.getY() - AnimationMain.Y_OFFSET;
            }

            public void mouseDragged(MouseEvent e) {
            }
        };
    }
}
