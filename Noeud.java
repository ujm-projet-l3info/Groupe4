import java.util.ArrayList;

public class Noeud {
    private float latitude;
    private float longitude;
    private char batiment;
    private ArrayList<Integer> salles;
    private ArrayList<Integer> voisins;
    
    public Noeud(float lat , float lon , char bat)
    {
        this.latitude = lat;
        this.longitude = lon;
        this.batiment = bat;
        this.salles = new ArrayList<Integer>();
        this.voisins = new ArrayList<Integer>();
    }

    public void ajouterSalle(int i)
    {
        /* Ajoute le voisin i au
           Noeud courant
        */

        this.salles.add(i);
    }

    public void ajouterVoisin(int i)
    {
        /* Ajoute le voisin i au
           Noeud courant
        */

        this.voisins.add(i);
    }

    public String toString()
    {
        /* Affiche le noeud courant
           dans la console
        */

        String s = "";

        s += "Lat : " + this.latitude + "\n";
        s += "Lon : " + this.longitude + "\n";

        return s;
    }
}

