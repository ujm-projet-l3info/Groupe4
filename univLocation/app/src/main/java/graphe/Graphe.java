package graphe;

/**
 * Created by arthur on 07/04/16.
 */

import com.example.jules.univLocation.MapsActivity;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class Graphe {

    public ArrayList<Noeud> noeuds;

    public Graphe()
    {
        this.noeuds = new ArrayList<Noeud>();
    }

    public void ajouterNoeud(Noeud n)
    {
        /* Ajoute le noeud n au graphe
           courant
        */

        this.noeuds.add(n);
    }

    public void afficherGraphe()
    {
        /* Affiche le graphe
           dans la console
        */

        System.out.println("Nombre de noeuds : "+ noeuds.size());
        for(int i = 0 ; i < noeuds.size() ; i++)
            System.out.println(noeuds.get(i));
    }


    /* Methodes associees au calcul d'itineraire */

    public boolean isVide()
    {
        /* Renvoit vrai si tous les sommets
           du graphe courant sont marques
           comme inutiles
        */

        for(int i = 0 ; i < noeuds.size() ; i++)
        {
            if(noeuds.get(i).isUtile())
                return false;
        }

        return true;
    }

    public int trouveNoeud(Noeud n)
    {
        /* Renvoit l'indice de n
           dans le graphe courant
        */

        for(int i = 0 ; i < noeuds.size() ; i++)
        {
            if(noeuds.get(i).equals(n))
            {
                return i;
            }
        }

        return -1;
    }

    public int trouveMin()
    {
        /* Renvoit l'indice du sommet
           ayant la plus faible distance courante
        */

        double d = Float.MAX_VALUE;
        int k = -1;

        for(int i = 0 ; i < noeuds.size() ; i++)
        {
            if((noeuds.get(i).getDistance() < d) &&   // Si distance plus petite que actuelle
                    (noeuds.get(i).isUtile()) &&           // Et que le noeud n'as pas encore ete vu
                    (noeuds.get(i).getDistance() != -1))   // Et que d n'est pas infini
            {
                d = noeuds.get(i).getDistance();
                k = i;
            }
        }

        return k;
    }

    public Chemin itineraireMultiple(ArrayList<Integer> l , boolean isPMR)
    {
        /* Renvoit le chemin le plus court passant par tous les noeuds
           de la liste
        */

        int d = 0;

        ArrayList<Integer> chemin = new ArrayList<Integer>();

        for(int i = 0 ; i < l.size()-1 ; i++)
        {
            chemin.addAll(itineraire(noeuds.get(l.get(i)) ,
                    noeuds.get(l.get(i+1)) ,
                    isPMR));
            d += noeuds.get(l.get(i+1)).getDistance();
        }

        return new Chemin(d , chemin);
    }

    public ArrayList<Integer> itineraire(Noeud src , Noeud dest , boolean isPMR)
    {
        /* Renvoit le chemin le plus court entre
           les deux noeuds s'il existe ( on
           suppose que oui )
        */


        /* Initialisation */

        int voisin;
        double d;
        double p;

        int s1;


        /* Initialisation */

        for(int i = 0 ; i < noeuds.size() ; i++) // Parcours des noeuds du graphe
        {
            noeuds.get(i).setDistance(-1); // Distance infini du sommet src pour les autres
            noeuds.get(i).setUtile(true);
            if(noeuds.get(i).equals(src))
            {
                noeuds.get(i).setDistance(0); // Distance nulle du sommet src a lui-meme
            }
        }

        s1 = trouveMin(); // Selection du noeud le plus proche de src actuellement (init : src lui-meme)

        while((!isVide()) && (s1 != trouveNoeud(dest)) && (s1 != -1)) // Tant que l'on est pas sur dest ou qu'il reste des sommets a visiter
        {
            noeuds.get(s1).setUtile(false); // 'Suppression'/'Visite' de ce noeud

            if (isPMR) // Parcours des voisins adaptes
            {
                for (int j = 0; j < noeuds.get(s1).voisinsPMR.size(); j++) // Parcours des voisins de s1
                {
                    voisin = noeuds.get(s1).voisinsPMR.get(j); // Recuperation indice voisin n°j

                    if (noeuds.get(voisin).isUtile()) {
                        d = noeuds.get(voisin).getDistance(); // Distance actuelle pour atteindre voisin

                        p = noeuds.get(s1).distance(noeuds.get(voisin)) + // Poids entre s1 et voisins
                                noeuds.get(s1).getDistance();                 // + distance pour atteindre s1

                        if ((p <= d) || (d == -1) || (d + p == 0)) // Si il est mieux d'emprunter s1 pour atteindre voisin
                        {
                            noeuds.get(voisin).setPredecesseur(s1); // On passe par s1 pour atteindre voisin
                            noeuds.get(voisin).setDistance(p); // MaJ de la distance de voisin
                        }
                    }
                }
            } else {
                for (int j = 0; j < noeuds.get(s1).voisins.size(); j++) // Parcours des voisins de s1
                {
                    voisin = noeuds.get(s1).voisins.get(j); // Recuperation indice voisin n°j

                    if (noeuds.get(voisin).isUtile()) {
                        d = noeuds.get(voisin).getDistance(); // Distance actuelle pour atteindre voisin

                        p = noeuds.get(s1).distance(noeuds.get(voisin)) + // Poids entre s1 et voisins
                                noeuds.get(s1).getDistance();                 // + distance pour atteindre s1

                        if ((p <= d) || (d == -1) || (d + p == 0)) // Si il est mieux d'emprunter s1 pour atteindre voisin
                        {
                            noeuds.get(voisin).setPredecesseur(s1); // On passe par s1 pour atteindre voisin
                            noeuds.get(voisin).setDistance(p); // MaJ de la distance de voisin
                        }
                    }
                }
            }

            s1 = trouveMin(); // Selection du noeud le plus proche (iteration suivante)
        }


        /* Recuperer le chemin */

        ArrayList<Integer> chemin = new ArrayList<Integer>();
        int s;

        if(s1 == -1)
        {
            s = noeuds.indexOf(src);
            chemin.add(s);
            return chemin;
        }

        s = noeuds.indexOf(dest);

        while(s != noeuds.indexOf(src))
        {
            chemin.add(0 , s); // Ajout du sommet au chemin
            s = noeuds.get(s).getPredecesseur(); // Passage au predecesseur
        }
        chemin.add(0 , s); // Ajout sommet source

        return chemin;
    }

    public void afficherChemin(Chemin c)
    {
        /* Affiche l'itineraire c
           et les infos relatives
           a celui-ci
         */

        System.out.println("Distance totale : " + c.getDistance() + "m");

        for(int i = 0 ; i < c.noeuds.size() ; i++)
        {
            System.out.println("Etape n°" + i + " :");
            System.out.println(this.noeuds.get(c.noeuds.get(i)));
            System.out.println("\n");
        }
    }



    /* Methodes supplementaires */

    public int recollerGraphe(double lat , double lon)
    {
        /* Renvoit l'indice du sommet
           du graphe le plus proche
           de la position passee
           en parametre
        */

        Noeud pos = new Noeud(lat , lon);
        double d = pos.distance(noeuds.get(0));
        int k = 0;

        for(int i = 1 ; i < noeuds.size() ; i++) // Parcours des noeuds
        {
            if(pos.distance(noeuds.get(i)) < d) // Si noeud actuel plus proche - MaJ
            {
                k = i;
                d = pos.distance(noeuds.get(i));
            }
        }

        return k;
    }


    /* Recherche de POIs */

    public ArrayList<String> getPOIS()
    {
        /* Renvoit la liste des
           POIs disponibles
        */

        ArrayList<String> listePOI = new ArrayList<String>();
        HashSet hashList = new HashSet();

        for(int i = 0 ; i < noeuds.size() ; i++)
        {
            listePOI.addAll(noeuds.get(i).POIs);
        }

        hashList.addAll(listePOI);

        return new ArrayList<String>(hashList);
    }

    public ArrayList<Integer> cherchePOI(String poi) {
        /* Renvoit la liste des Noeuds correspondant
         au POI poi */

        ArrayList<Integer> l = new ArrayList<Integer>();

        if (poi.equals("") || poi.matches("( *)")) {
            return l;
        }

        if (poi.equals("Ma position")) {
            l.add(-2);
            return l;
        }

        for (int i = 0; i < noeuds.size(); i++) {
            for (int j = 0; j < noeuds.get(i).POIs.size(); j++) {
                String str = noeuds.get(i).POIs.get(j).toLowerCase();
                str = Normalizer.normalize(str, Normalizer.Form.NFD);
                str = str.replaceAll("[^\\p{ASCII}]", "");

                poi = poi.toLowerCase();
                poi = Normalizer.normalize(poi, Normalizer.Form.NFD);
                poi = poi.replaceAll("[^\\p{ASCII}]", "");

                if (str.equals(poi)) {
                    System.out.println(noeuds.get(i).getLat() + " " + noeuds.get(i).getLon() + " " + str);
                    l.add(i);
                    break;
                }
            }
        }

        if (l.isEmpty()) {
            for (int i = 0; i < noeuds.size(); i++) {
                for (int j = 0; j < noeuds.get(i).POIs.size(); j++) {
                    String str = noeuds.get(i).POIs.get(j).toLowerCase();
                    str = Normalizer.normalize(str, Normalizer.Form.NFD);
                    str = str.replaceAll("[^\\p{ASCII}]", "");
                    str = str.replaceAll("salle", "");

                    poi = poi.toLowerCase();
                    poi = Normalizer.normalize(poi, Normalizer.Form.NFD);
                    poi = poi.replaceAll("[^\\p{ASCII}]", "");
                    poi = poi.replaceAll("salle", "");

                    System.out.println("on compare " + poi + " et " + str);

                    if (!str.equals(poi)) {
                        if ((str.matches("(.*)" + poi + "(.*)")) ||
                                (poi.matches("(.*)" + str + "(.*)")))// Si POI correspond a REGEXP
                        {
                            l.add(i);
                            break;
                        }
                    }
                }
            }
        }

        Collections.sort(l, new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                int recolle = recollerGraphe(MapsActivity.latitude , MapsActivity.longitude);
                Noeud tmp = noeuds.get(recolle);

                /*ArrayList<Integer> l1 = new ArrayList<Integer>();
                ArrayList<Integer> l2 = new ArrayList<Integer>();

                l1.add(recolle);
                l2.add(recolle);

                l1.add(i1);
                l2.add(i2);

                Chemin c1 = MapsActivity.g.itineraireMultiple(l1, false);
                Chemin c2 = MapsActivity.g.itineraireMultiple(l2, false);

                return (int) (c1.getDistance() - c2.getDistance());*/

                return (int) (tmp.distance(noeuds.get(i1)) - tmp.distance(noeuds.get(i2)));
            }
        });

        return l;
    }

    public ArrayList<Integer> chercheToilettes()
    {
        /* Renvoit la liste des Noeuds correspondant
         au POI Toilettes */

        return cherchePOI("Toilettes");
    }

    public ArrayList<Integer> chercheDistributeurs()
    {
        /* Renvoit la liste des Noeuds correspondant
           au POI Distributeurs
        */

        return cherchePOI("Distributeurs");
    }

    public ArrayList<Integer> chercheRestauration()
    {
        /* Renvoit la liste des Noeuds correspondant
           au POI de restauration
        */

        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(29);
        l.add(91);
        l.add(95);

        return l;
    }
}
