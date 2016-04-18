package graphe;

/**
 * Created by arthur on 07/04/16.
 */

import java.util.ArrayList;

public class Noeud {
    private int niveau;
    private float latitude;
    private float longitude;
    private char batiment;
    private double distance;
    private boolean utile;
    public ArrayList<String> POIs;
    public ArrayList<Integer> voisins;
    public ArrayList<Integer> voisinsPMR;
    public int predecesseur;

    public Noeud()
    {
        this.POIs = new ArrayList<String>();
        this.voisins = new ArrayList<Integer>();
        this.voisinsPMR = new ArrayList<Integer>();
    }

    public Noeud(float lat , float lon)
    {
        this.latitude = lat;
        this.longitude = lon;
        this.distance = -1;
        this.utile = true;
        this.POIs = new ArrayList<String>();
        this.voisins = new ArrayList<Integer>();
        this.voisinsPMR = new ArrayList<Integer>();
        this.predecesseur = -1;
    }

    public Noeud(float lat , float lon , char bat)
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
        /* Renvoit la distance metrique
           entre deux noeuds
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

    public float getNiveau()
    {
        /* Renvoit le niveau
           du noeud courant
        */

        return this.niveau;
    }

    public float getLat()
    {
        /* Renvoit la latitude
           du noeud courant
        */

        return this.latitude;
    }

    public float getLon()
    {
        /* Renvoit la latitude
           du noeud courant
        */

        return this.longitude;
    }

    public double getDistance()
    {
        /* Renvoit la distance
           du Noeud actuel par rapport
           au Noeud src de l'itinéraire actuel
        */

        return this.distance;
    }

    public boolean isUtile()
    {
        /* Renvoit vrai si le noeud
           actuel n'est pas encore
           visite dans le calcul d'itineraire
        */

        return this.utile;
    }

    public int getPredecesseur()
    {
        /* Renvoit le predecesseur
           actuel du Noeud courant
           dans le calcul d'itineraire
           courant
        */

        return this.predecesseur;
    }

    public void setNiveau(int n)
    {
        /* MaJ du niveau du
           Noeud actuel
        */

        this.niveau = n;
    }

    public void setLatitude(float lat)
    {
        /* MaJ de la latitude du
           Noeud actuel
        */

        this.latitude = lat;
    }

    public void setLongitude(float lon)
    {
        /* MaJ de la longitude du
           Noeud actuel
        */

        this.longitude = lon;
    }

    public void setBatiment(char b)
    {
        /* MaJ du batiment du
           Noeud actuel
        */

        this.batiment= b;
    }

    public void setDistance(double d)
    {
        /* MaJ de la distance du Noeud
           actuel au Noeud src
        */

        this.distance = d;
    }

    public void setUtile(boolean u)
    {
        /* MaJ de l'utilite
           du Noeud
        */

        this.utile = u;
    }

    public void setPredecesseur(int p)
    {
        /* MaJ du predecesseur dans le
           calcul d'itineraire en cours
        */

        this.predecesseur = p;
    }

    /* Redefinition de equals */

    public boolean equals(Noeud n)
    {
        /* Renvoit vrai si n est egal
           au noeud courant
        */

        return ((this.getLat() == n.getLat()) &&
                (this.getLon() == n.getLon()) &&
                (this.getNiveau() == n.getNiveau()));
    }
}

