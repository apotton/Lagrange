import java.awt.Color;

public class RGB {
    /**
     * Fonction qui associe chaque vitesse à une couleur, bleu pour les particules
     * lentes et rouge pour les rapides
     * 
     * @param vitesse La vitesse de la particule
     * @return La couleur correspondante
     */
    public static Color coulVitesse(double vitesse) {
        // Recalage de la vitesse et mise à l'échelle pour bien distinguer les couleurs
        double valeurNormalisee = (vitesse - Scene.VITESSE_MIN) / (Scene.VITESSE_MAX - Scene.VITESSE_MIN);
        valeurNormalisee = Math.pow(valeurNormalisee, 0.3);

        return new Color((float) valeurNormalisee, 0f, (float) (1 - valeurNormalisee));
    }
}
