import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.ArrayList;

public class Test {

    public Test()
    {
    }

    public static void main(String[] args)
    {
        try {
            ParseurGrapheXML p = new ParseurGrapheXML("metare.xml");

            boolean PMR = true
            Graphe g = new Graphe(PMR);
            
            g = p.remplirGraphe(PMR);

            ArrayList<Integer> l = new ArrayList<Integer>(); // Ajout de checkpoint pour itineraire
            for(int i = 0 ; i < args.length ; i++)
            {
                l.add(Integer.parseInt(args[i]));
            }
            ArrayList<Noeud> chemin = g.itineraireMultiple(l); // Calcul itineraire le plus cours
                
            System.out.println("Distance a vol d'oiseau : " + g.noeuds.get(Integer.parseInt(args[0])).distance(g.noeuds.get(Integer.parseInt(args[args.length - 1]))) + "m");

            System.out.println("Chemin entre " + args[0] + " et " + args[args.length - 1] + " : "); // Affichage de l'itineraire
            for(int i = 0 ; i < chemin.size() ; i++)
            {
                System.out.println(chemin.get(i));
            }

        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
