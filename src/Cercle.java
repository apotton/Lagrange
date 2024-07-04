public class Cercle {
    /** Couleur du cercle */
    // public Color couleur;

    /** Position du centre du cercle */
    public Position centre;

    /** Position précédente du centre du cercle */
    public Position dernierePos;

    /** Rayon du cercle */
    public double rayon;

    /** Accélération selon X et selon Y */
    public double acceleration[] = { 0, AnimationMain.GRAVITE };

    /** Norme de la vitesse */
    public double vitesse = 0;

    /** Coordonnées du cercle dans la grille */
    public int coordonnees[] = { 0, 0 };

    /** Ordre d'ajout du cercle */
    public int index;

    /** Nombre de cercles ajoutés */
    public static int COUNT = 0;

    /**
     * Création d'un cercle étant données ses paramètres
     * 
     * @param rayon  Le rayon du cercle
     * @param centre La position de son centre
     * @param col    Sa couleur (pourra être ignorée selon le choix d'affichage)
     */
    public Cercle(int rayon, Position centre) {
        this.rayon = rayon;
        this.centre = centre;
        this.dernierePos = centre;

        this.index = COUNT;
        COUNT++;
    }

    /**
     * Mise à jour des coordonnées du cercle dans le quadrillage
     */
    public void majCoordonnees() {
        // On récupère les anciennes coordonnées sur le quadrillage
        int a_x = (int) this.coordonnees[0];
        int a_y = (int) this.coordonnees[1];

        // On crée des nouvelles coordonnées par des calculs savants
        int n_x = (int) this.centre.x / AnimationMain.RAYON_INFLUENCE / 2 + 1;
        int n_y = (int) this.centre.y / AnimationMain.RAYON_INFLUENCE / 2 + 1;

        // Si les coordonnées n'ont pas changé, on ne fait rien
        if ((a_x == n_x) && (a_y == n_y)) {
            return;
        }

        this.coordonnees[0] = n_x;
        this.coordonnees[1] = n_y;

        // On cherche l'indice du cercle à ses anciennes coordonnées
        int j = 0;
        while ((j < Scene.TAILLE) && (Scene.quadrillage[a_x][a_y][j] != this.index)) {
            j++;
        }
        if (j < Scene.TAILLE) {
            // Si on l'a trouvé, on le supprime
            Scene.quadrillage[a_x][a_y][j] = -1;
        }

        // On ajoute l'indice du cercle au bon endroit
        int h = 0;
        while ((h < Scene.TAILLE) && (Scene.quadrillage[n_x][n_y][h] != -1)) {
            h++;
        }
        if (h < Scene.TAILLE) {
            // Dès qu'il y a un espace libre, on y place
            Scene.quadrillage[n_x][n_y][h] = this.index;
        } else {
            // Si le tableau est plein pour l'ajout, il y a un problème
            System.out.println("Trop de cercles pour les positions x=" + n_x + ", y=" + n_y);
        }
    }

    public String toString() {
        return "Centre :" + centre.toString() + " rayon: " + rayon;
    }

    /**
     * Potentiel empirique inspiré de la loi de Coulomb et normalisé
     * 
     * @param distance La distance entre deux particules
     * @return La force qui s'exerce à chacune selon leur normale
     */
    private static double force(double distance) {
        return 100. / (Math.pow(distance / AnimationMain.RAYON_INFLUENCE, 2));
    }

    /**
     * Remplacement de la position de deux cercles qui se superposent
     * 
     * @param cercle1 Le premier cercle
     * @param cercle2 Le second cercle
     */
    static public void reglerCollision(Cercle cercle1, Cercle cercle2) {
        // Si les cercles sont le même, il n'y a pas de problème
        if (cercle1 == cercle2) {
            return;
        }

        double distance = Position.distance(cercle1.centre, cercle2.centre);

        // Cas de la collision élastique
        if (distance < 2 * AnimationMain.RAYON) {
            Position normale = new Position(Position.deplacement(cercle2.centre, cercle1.centre));
            normale.normaliser();

            // Calcul des proportions de recul pour la conservation de la quantité de
            // mouvement (masse = rayon)
            double delta = (distance - 2 * AnimationMain.RAYON) * 0.5;

            // Mise à jour de la position 1
            cercle1.centre.x -= (delta) * normale.x * 0.5;
            cercle1.centre.y -= (delta) * normale.y * 0.5;

            // Mise à jour de la position 2
            cercle2.centre.x += (delta) * normale.x * 0.5;
            cercle2.centre.y += (delta) * normale.y * 0.5;

            return;
        }

        // Cas de l'influence par le champ électrique
        if (distance < 2 * AnimationMain.RAYON_INFLUENCE) {
            // Calcul de la direction entre les deux cercles
            Position normale = Position.deplacement(cercle2.centre, cercle1.centre);

            // Application de l'intensité de la force
            normale.normer(force(distance));

            // Application du premier principe de la dynamique
            cercle1.acceleration[0] += normale.x;
            cercle1.acceleration[1] += normale.y;
            cercle2.acceleration[0] -= normale.x;
            cercle2.acceleration[1] -= normale.y;
        }
    }

    /**
     * Remet l'accélération d'un cercle à ses valeurs initiales
     */
    public void resetAcceleration() {
        this.acceleration[0] = 0;
        this.acceleration[1] = AnimationMain.GRAVITE;
    }

    /**
     * Calcule la norme de la vitesse en fonction de ses coordonnées
     */
    public void definirVitesse() {
        this.vitesse = Math.pow(this.centre.x - this.dernierePos.x, 2)
                + Math.pow(this.centre.y - this.dernierePos.y, 2);
    }
}
