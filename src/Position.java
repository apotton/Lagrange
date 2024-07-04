/**
 * Simplistic class representing Cartesian 2D positions.
 */
public class Position {

    public double x;
    public double y;

    /**
     * Crée une position
     * @param x0 L'absisce
     * @param y0 L'ordonnée
     */
    public Position(double x0, double y0) {
        this.x = x0;
        this.y = y0;
    }

    /**
     * Duplique une position
     * @param p0 La position de départ
     */
    public Position(Position p0) {
        this.x = p0.x;
        this.y = p0.y;
    }

    /**
     * 
     * @return La norme du vecteur
     */
    public double distanceToOrigin() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Calcul de la distance entre deux positions
     * @param p1 La première position
     * @param p2 La seconde position
     * @return |p2-p1|²
     */
    static public double distance(Position p1, Position p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Calcul de la différence entre deux vecteurs
     * 
     * @param ancienne Le premier vecteur
     * @param actuelle Le second vecteur
     * @return actuelle-ancienne
     */
    public static Position deplacement(Position ancienne, Position actuelle) {
        return new Position(actuelle.x - ancienne.x, actuelle.y - ancienne.y);
    }

    /**
     * Calcul du produit scalaire euclidien entre deux vecteurs
     * 
     * @param p1 Le premier vecteur
     * @param p2 Le second vecteur
     * @return <p1|p2>
     */
    public static double produitScalaire(Position p1, Position p2) {
        return p1.x * p2.x + p1.y + p2.y;
    }

    /**
     * Normalise un vecteur
     */
    public void normaliser() {
        double norme = this.distanceToOrigin();
        this.x /= norme;
        this.y /= norme;
    }

    /**
     * Norme le vecteur par la norme choisie
     * @param norme La norme voulue
     */
    public void normer(double norme) {
        this.normaliser();
        this.x *= Math.sqrt(norme);
        this.y *= Math.sqrt(norme);
    }

}
