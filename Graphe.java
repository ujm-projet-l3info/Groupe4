import java.util.ArrayList;

public class Graphe {

    private ArrayList<Noeud> noeuds;

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
}

