import java.util.ArrayList;

public class Graphe {

    public ArrayList<Noeud> noeuds;
    public boolean isPMR;
    
    public Graphe(boolean PMR)
    {
        this.noeuds = new ArrayList<Noeud>();
        this.isPMR = PMR;
    }

    public void ajouterNoeud(Noeud n)
    {
        /* Ajoute le noeud n au graphe
           courant
        */

        this.noeuds.add(n);
    }

    public Noeud getNoeud(int i)
    {
        /* Renvoit le noeud numero
           i
        */

        return noeuds.get(i);
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
}

