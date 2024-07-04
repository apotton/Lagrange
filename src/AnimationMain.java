import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimationMain extends JPanel implements ActionListener {
    /// Paramètres de la simulation
    static final int WINDOW_SIZE = 1000; // Taille de la fenêtre
    static final int Y_OFFSET = 37; // Offset en Y pour l'affichage
    static final int DUREE_IMAGES = 10; // Durée d'affichage de chaque image

    static final int SUBSTEPS = 5; // Nombre de calculs par image

    static final int NOMBRE_PARTICULES = 2000; // Nombre de particules

    static final int RAYON = 5; // Rayon des particules
    static final int RAYON_INFLUENCE = 20; // Rayon d'influence de force de particule
    static final int GRAVITE = 10; // Force de gravité

    static final int ESPACEMENT = 2; // Espacement des boules au départ
    static final int COLONNES = 50; // Nombre de colonnes au départ
    static final boolean REPEINDRE = true; // Afficher la trajectoire de chaque cercle

    // Éléments utiles pour le programme
    public Timer timer;
    public Scene scene = new Scene();
    public Souris mouse = new Souris();
    static int compteur;
    public long temps = System.nanoTime();
    static public double fps = 0;

    void paint_scene(Graphics g) {
        // Commenter les lignes suivantes pour avoir des résultats intéressants
        // Couleur de l'arrière-plan
        if (REPEINDRE) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
        }

        // Peindre chaque cercle
        for (Cercle cercle : scene.objets) {
            // Intérieur de chaque cercle
            g.setColor(RGB.coulVitesse(cercle.vitesse));
            g.fillOval((int) (cercle.centre.x - cercle.rayon), (int) (cercle.centre.y - cercle.rayon),
                    (int) cercle.rayon * 2, (int) cercle.rayon * 2);

            // Bordure de chaque cercle
            g.setColor(Color.BLACK);
            g.drawOval((int) (cercle.centre.x - cercle.rayon), (int) (cercle.centre.y - cercle.rayon),
                    (int) cercle.rayon * 2, (int) cercle.rayon * 2);
        }

        // Affichage des stats
        g.fillRect(0, 0, 50, 25);
        g.setColor(Color.WHITE);
        g.drawString(Double.toString(Double.valueOf(String.valueOf(((int) (fps * 1000)))) / 1000), 0, 10);
        g.drawString(Integer.toString((int) (Scene.VITESSE_MAX * 100)), 0, 20);
    }

    /** Initialisation du minuteur */
    public AnimationMain() {
        timer = new Timer(DUREE_IMAGES, this);
        timer.start();
    }

    public void paint(Graphics g) {
        if (compteur % 10 == 1) {
            temps = System.nanoTime();
        }

        // Render de la scène
        paint_scene(g);

        // Réalisation de plusieurs updates
        for (int i = 0; i < SUBSTEPS; i++) {
            scene.update((double) 1 / (DUREE_IMAGES * SUBSTEPS));
        }

        // Calcul des informations de fps
        compteur++;
        if (compteur % 10 == 0) {
            temps = System.nanoTime() - temps;
            fps = 10 / ((double) temps / Math.pow(10, 9));
        }

    }

    public static void main(String[] args) throws InterruptedException {
        // Initialisation de la fenêtre
        JFrame frame = new JFrame("Simulation");

        // Ajout de la logique de l'animation
        frame.add(new AnimationMain());

        // Ajout de la détection des clics
        frame.addMouseListener(Souris.clic());

        // Ajout de la détection de mouvement
        frame.addMouseMotionListener(Souris.mouvement());

        // Arrêt du programme à la fermeture de la fenêtre
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Taille de la fenêtre
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE + Y_OFFSET);

        // Position de la fenêtre
        frame.setLocationRelativeTo(null);

        // Visibilité de la fenêtre
        frame.setVisible(true);

        // Couleur de fond
        frame.setBackground(Color.BLACK);

        // Fluidité de l'affichage
        frame.getToolkit().sync();
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}