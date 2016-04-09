package graphe;

/**
 * Created by arthur on 07/04/16.
 */

import java.util.ArrayList;

public class Chemin {
    private float distance;
    public ArrayList<Integer> noeuds;

    public Chemin(int d , ArrayList<Integer> n)
    {
        this.distance = d;
        this.noeuds = n;
        this.nettoyerChemin();
    }

    public float getDistance()
    {
        /* Renvoit la longueur
           d'un chemin exprimee
           en metres
        */

        return this.distance;
    }

    public void nettoyerChemin()
    {
        /* Supprime les etapes
           successives identiques
           dans l'itineraire
           Hypothese : au plus des doublons
        */

        ArrayList<Integer> n = new ArrayList<Integer>();

        for(int i = 0 ; i < noeuds.size() ; i++)
        {
            n.add(noeuds.get(i));
            if(i != noeuds.size() - 1)
                if(noeuds.get(i).equals(noeuds.get(i+1)))
                    i++;
        }

        this.noeuds = n;
    }
}
