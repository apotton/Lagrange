public class Scene {
    public Cercle objets[] = new Cercle[AnimationMain.NOMBRE_PARTICULES];

    // Dimension du grillage
    public static int DIM;
    // Nombre d'éléments max par carreau
    public static int TAILLE;

    public static int quadrillage[][][];
    // Nombre de cercles ajoutés à la scène
    public static double VITESSE_MAX = 0;
    public static double VITESSE_MIN = Double.MAX_VALUE;

    /**
     * Crée une scène contenant un nombre déterminé de particules
     */
    public Scene() {
        // Création du quadrillage de la scène
        double aire_carre = Math.pow(2 * AnimationMain.RAYON_INFLUENCE, 2);
        double aire_cercle = Math.PI * Math.pow(AnimationMain.RAYON, 2);

        // Quantité maximale empirique du nombre de cercles max par case
        TAILLE = (int) (aire_carre / aire_cercle) + 2;

        // Dimension du tableau
        DIM = (AnimationMain.WINDOW_SIZE / AnimationMain.RAYON_INFLUENCE) / 2 + 3;
        quadrillage = new int[DIM][DIM][TAILLE];

        // Initialisation du quadrillage à -1
        for (int x = 0; x < DIM; x++) {
            for (int y = 0; y < DIM; y++) {
                for (int i = 0; i < TAILLE; i++) {
                    quadrillage[x][y][i] = -1;
                }
            }
        }

        // Ajout des cercles sous forme de lignes
        int cerclesAjoutes = 0;
        int x_depart = (AnimationMain.WINDOW_SIZE
                - AnimationMain.COLONNES * AnimationMain.ESPACEMENT * AnimationMain.RAYON) / 2;
        int y_depart = (AnimationMain.WINDOW_SIZE * 7) / 8;

        while (cerclesAjoutes < AnimationMain.NOMBRE_PARTICULES) {
            int x = x_depart
                    + ((cerclesAjoutes % AnimationMain.COLONNES) * AnimationMain.ESPACEMENT * AnimationMain.RAYON);
            int y = y_depart
                    - ((cerclesAjoutes / AnimationMain.COLONNES) * AnimationMain.ESPACEMENT * AnimationMain.RAYON);
            objets[cerclesAjoutes] = new Cercle(AnimationMain.RAYON, new Position(x, y));

            cerclesAjoutes++;
        }
    }

    /**
     * Mise à jour de la position des cercles dans le quadrillage
     */
    private void majCercles() {
        for (Cercle cercle : this.objets) {
            cercle.majCoordonnees();
        }
    }

    /**
     * Mise à jour de la position de chaque particule au vu de leur accélération
     * 
     * @param dt Le laps de temps
     */
    private void acceleration(double dt) {
        double max = 0;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < AnimationMain.NOMBRE_PARTICULES; i++) {
            Cercle circle = this.objets[i];
            // Différence entre la position actuelle et la position précédente
            Position deplacement = Position.deplacement(circle.dernierePos, circle.centre);

            // La position actuelle devient la précédente, et on crée un nouvel objet pour
            // effacer les effets de bord
            circle.dernierePos = new Position(circle.centre);

            // Mise à jour des positions selon la méthode d'intégration de Verlet
            circle.centre.x += deplacement.x + circle.acceleration[0] * dt * dt;
            circle.centre.y += deplacement.y + circle.acceleration[1] * dt * dt;

            // Une fois que le déplacement a été fait, on remet l'accélération à 0
            circle.resetAcceleration();

            // On calcule la norme de la vitesse
            circle.definirVitesse();

            // On définit les vitesse extrémales pour l'affichage
            if (circle.vitesse > max) {
                max = circle.vitesse;
            }
            if (circle.vitesse < min) {
                min = circle.vitesse;
            }
        }

        // Mise à jour de la vitesse maximale, pour l'affichage en couleur
        VITESSE_MAX = max;
        VITESSE_MIN = min;
    }

    /**
     * Gestion basique pour maintenir les particules dans le contenant
     */
    public void contrainte() {
        for (int i = 0; i < AnimationMain.NOMBRE_PARTICULES; i++) {
            Cercle circle = objets[i];

            // Calcul de la distance entre le cercle et le centre de la scène
            if ((circle.centre.x - circle.rayon < 0)
                    | (circle.centre.x + circle.rayon > AnimationMain.WINDOW_SIZE)
                    | (circle.centre.y - circle.rayon < 0)
                    | (circle.centre.y + circle.rayon > AnimationMain.WINDOW_SIZE)) {

                // Si il faut régler quelque chose, on stocke l'ancienne position
                circle.dernierePos = new Position(circle.centre);

                if (circle.centre.x - circle.rayon < 0) {
                    // Dépassement par la gauche
                    circle.centre.x = circle.rayon;
                } else if (circle.centre.x + circle.rayon > AnimationMain.WINDOW_SIZE) {
                    // Dépassement par la droite
                    circle.centre.x = AnimationMain.WINDOW_SIZE - circle.rayon;
                }

                if (circle.centre.y - circle.rayon < 0) {
                    // Dépassement par le bas
                    circle.centre.y = circle.rayon;
                } else if (circle.centre.y + circle.rayon > AnimationMain.WINDOW_SIZE) {
                    // Dépassement par le haut
                    circle.centre.y = AnimationMain.WINDOW_SIZE - circle.rayon;
                }
            }
        }
    }

    private void accelerationSouris(Cercle cercle) {
        // Calcul de la distance entre la souris et le cercle
        double dist = Math.sqrt(Math.pow(cercle.centre.x - Souris.x, 2)
                + Math.pow(cercle.centre.y - Souris.y, 2));

        // Si le cercle est en dehors de de l'influence de la souris, on ne fait rien
        if (dist >= AnimationMain.RAYON + Souris.rayon) {
            return;
        }

        // Calcul de la direction entre la souris et le cercle et norme de la force
        Position normale = Position.deplacement(new Position(Souris.x, Souris.y), cercle.centre);
        normale.normer(50000);

        // Action sur l'accélération du cercle
        cercle.acceleration[0] += Souris.direction * normale.x;
        cercle.acceleration[1] += Souris.direction * normale.y;
    }

    /**
     * Implémentation de la logique de collision, qui utilise le quadrillage pour
     * faire un algorithme en O(n)
     */
    private void collision() {
        for (Cercle cercle : objets) {
            // Gestion du mouvement de la souris
            if (Souris.visible) {
                accelerationSouris(cercle);
            }

            // On récupère les coordonnées du cercle sur le quadrillage
            int x = cercle.coordonnees[0];
            int y = cercle.coordonnees[1];

            // On cherche des potentielles collisions dans les cases d'à côté
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    // Itération sur les cases du tableau
                    for (int i = 0; i < TAILLE; i++) {
                        int temp = quadrillage[x + dx][y + dy][i];
                        if (temp != -1) {
                            // Si il y a un cercle dans cette case, il y a un choc possible
                            Cercle.reglerCollision(cercle, objets[temp]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Mise à jour de la scène pour un temps dt
     */
    public void update(double dt) {
        // Etape 1: conversion des accélérations calculées en déplacement
        acceleration(dt);

        // Etape 2: recalage des cercles dans le domaine spatial
        contrainte();

        // Etape 3: mise à jour des cercles dans le quadrillage
        majCercles();

        // Etape 4:
        collision();
    }
}
