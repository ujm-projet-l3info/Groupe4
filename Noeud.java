import java.util.ArrayList;

public class Noeud {
    private double latitude;
    private double longitude;
    private char batiment;
    public ArrayList<String> POIs;
    public ArrayList<Integer> voisins;
    public ArrayList<Integer> voisinsPMR;

    public Noeud(double lat , double lon)
    {
        this.latitude = lat;
        this.longitude = lon;
        this.POIs = new ArrayList<String>();
        this.voisins = new ArrayList<Integer>();
        this.voisinsPMR = new ArrayList<Integer>();
    }
    
    public Noeud(double lat , double lon , char bat)
    {
        this(lat , lon);
        this.batiment = bat;
    }

    public void ajouterPOI(String s)
    {
        /* Ajoute le voisin i au
           Noeud courant
        */

        this.POIs.add(s);
    }

    public void ajouterVoisin(int i)
    {
        /* Ajoute le voisin i au
           Noeud courant
        */

        this.voisins.add(i);
    }

    public void ajouterVoisinPMR(int i)
    {
        /* Ajoute le voisin PMR i au
           Noeud courant
        */

        this.voisinsPMR.add(i);
    }

    public String toString()
    {
        /* Affiche le noeud courant
           dans la console
        */

        String s = "";

        s += "Lat : " + this.latitude + "\n";
        s += "Lon : " + this.longitude + "\n";
        s += "Bat : " + this.batiment + "\n";

        for(int i = 0 ; i < POIs.size() ; i++)
        {
            s += "" + POIs.get(i) + "\n";
        }
        
        return s;
    }
    
    double distance(Noeud n)
    {
        /* Renvoit la distance entre
           deux noeuds
        */

        double R = 6371000; // en metres
        double a = this.getLat() * Math.PI / 180;
        double b = n.getLat() * Math.PI / 180;
        double c = this.getLon() * Math.PI / 180;
        double d = n.getLon() * Math.PI / 180;
        
        return R * Math.acos((Math.sin(a) * Math.sin(b)) +
                              (Math.cos(a) * Math.cos(b) * Math.cos(c - d)));
    }

    /* Accesseurs / Mutateurs */

    public double getLat()
    {
        /* Renvoit la latitude
           du noeud courant
        */

        return this.latitude;
    }

    public double getLon()
    {
        /* Renvoit la latitude
           du noeud courant
        */

        return this.longitude;
    }
    /* Redefinition de equals */

    public boolean equals(Noeud n)
    {
        /* Renvoit vrai si n est egal
           au noeud courant
        */

        return ((this.getLat() == n.getLat()) && (this.getLon() == n.getLon()));
    }
}

