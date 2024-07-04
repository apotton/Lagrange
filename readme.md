# Simulation lagrangienne de fluides ✨💫

## Introduction

Ce projet présente une simulation de mécanique des fluides, fondée sur des interactions individuelles entre particules. Ces interactions ont lieu si les cercles sont suffisamment près les uns des autres, c'est-à-dire la distance `RAYON_INFLUENCE`. Chacune cercle impose aux autres un potentiel répulsif. La simulation lagrangienne rend difficile l'incompressibilité du fluide, et elle est donc plus adaptée à l'étude d'un gaz détendu. Lorsque l'on lance la simulation avec une force de gravité, on observe la création de trois phases: la phase solide en bas, avec des particules qui se touchent, la phase liquide au dessus avec quelques liaisons, puis la phase gazeuse avec des particules isolées.

Il peut être également intéressant de changer le paramètre `REPEINDRE`. Si sa valeur est `false`, l'affichage ne se remettra pas à zéro entre deux images, et donc les positions de chaque cercle resteront à l'écran. Avec aucune gravité, le résultat est particulièrement beau, notamment si on impose un vortex avec la souris. La couleur de chaque cercle est directement liée à sa vitesse: rouge pour les plus rapides et bleu pour les moins rapides. Deux statistiques s'affichent en haut à gauche de l'écran, les fps et la vitesse maximale.

A ce propos, un clic gauche de la souris crée une zone répulsive circulaire ayant pour centre la position du pointeur. Les cercles ne rentreront pas dans cette zone, qui peut être utilisée pour leur donner une vitesse dans une direction particulière. En revanche, un clic droit crée un potentiel attractif, qui maintient quelques cercles dans une zone, malgré leur répulsion. En bougeant la souris un peu vite, leur inertie les jette en dehors cette zone.

## Fonctionnement du code

A chaque étape de la simulation, la fonction `update(dt)` est appelée. Celle-ci agit en quatre étapes.

### Accélération

La méthode applique les accélérations calculées à l'étape précédente pour chaque cercle et les convertit en déplacement. La vitesse n'est pas nécessaire pour cela, grâce à une méthode nommée "intégration de Verlet". En ayant simplement accès aux positions actuelle et précédente, on trouve une approximationn précise à l'ordre 4 (quatre!) de la position suivante à l'aide de la formule `x(t+dt) = 2x(t) - x(t-dt) + a(t)dt²`. Une fois le déplacement effectué, l'accélération est remise à son état initial (uniquement la force de gravité).

### Contrainte

Bien entendu, les cercles ne doivent pas sortir du cadre. Dans le cas où l'application de l'accélération en aura conduit à dépasser, la méthode `contrainte` recale de force le centre des cercles concernés dans la fenêtre. La position avant recalage est stockée, ce qui conserve la vitesse du cercle. En effet, plus un cercle va vite, plus il dépassera, donc plus le recalage sera important, entraînant une vitesse induite élevée.

### Mise à jour de la position des cercles dans le quadrillage

Une fois que tous les cercles sont arrivés à leur position définitive, on peut recalculer les forces issues des potentiels créés par chaque cercle. Pour faire cela, deux options s'offrent à nous. D'abord une double itération par deux boucles _for_ imbriquées, pour bien considérer chaque couple possible (O(n²)).

Sinon, on peut utiliser le fait que deux cercles ne se voient pas si ils sont éloignés d'une distance plus important que `RAYON_INFLUENCE`. Ainsi, si on construit un quadrillage virtuel ayant pour côté cette distance, un cercle dans une case ne sera influencé que par les neuf cases voisines. Il faut donc, pour chaque case, stocker les index des cercles présents à l'intérieur; et pour chaque cercle sa position dans le quadrillage. Si cela est fait, il suffit d'itérer chaque cercle (O(n)), et pour chaque cercle, de fouiller les cases environnantes (O(1)) afin de gérer les interactions de l'ensemble des cercles. L'économie de ressources devient remarquable pour des grandes valeurs de n, par exemple pour n=2000.
