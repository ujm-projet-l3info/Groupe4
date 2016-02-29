public class Noeud {
    private float latitude;
    private float longitude;
    private char batiment;
    private ArrayList<int> salles;
    private ArrayList<int> voisins;
    
    public Noeud(lat , long , bat)
    {
        this.latitude = lat;
        this.longitude = long;
        this.batiment = bat;
        this.salles = new ArrayList<int>;
        this.voisins = new ArrayList<int>;
    }

    void ajouterSalle(int i)
    {
        /* Ajoute le voisin i au
           Noeud courant
        */

        this.salles.add(i);
    }

    void ajouterVoisin(int i)
    {
        /* Ajoute le voisin i au
           Noeud courant
        */

        this.voisins.add(i);
    }
    
}

