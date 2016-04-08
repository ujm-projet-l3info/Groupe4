package parseur;

import com.example.jules.projet2.MapsActivity;
import com.example.jules.projet2.R;

import graphe.*;

/**
 * Created by arthur on 07/04/16.
 */

import android.content.Context;
import android.view.View;

import java.io.*;
import java.net.URL;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class ParseurGrapheXML {

    File fXmlFile;
    Document doc ;
    DocumentBuilder dBuilder;

    public ParseurGrapheXML(File file_name) throws ParserConfigurationException, SAXException, IOException
    {

        fXmlFile = file_name;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        dBuilder = dbFactory.newDocumentBuilder();
    }

    public Graphe remplirGraphe()
    {
        /* Cette methode parse le fichier XML
         * ouvert et rempli le graphe associe
         */

        Graphe g = new Graphe();
        try {
            Noeud n = null;

            doc = dBuilder.parse(fXmlFile);                 // Parsing du fichier XML


            NodeList listeLat = doc.getElementsByTagName("latitude");
            NodeList listeLon = doc.getElementsByTagName("longitude");
            NodeList listeBat = doc.getElementsByTagName("batiment");
            NodeList listePOI = doc.getElementsByTagName("liste_POI");
            NodeList listeVoisins = doc.getElementsByTagName("liste_voisins");
            NodeList listeVoisinsPMR = doc.getElementsByTagName("liste_voisins_PMR");

            for(int i = 0 ; i < listeLat.getLength() ; i++) // Parcours des noeud du graphe
            {
                float latitude = Float.parseFloat(listeLat.item(i).getTextContent());
                float longitude = Float.parseFloat(listeLon.item(i).getTextContent());
                char batiment = '-';
                if(listeBat.item(i).getTextContent() != "")
                    batiment = listeBat.item(i).getTextContent().charAt(0);

                n = new Noeud(latitude , longitude , batiment);

                NodeList POIs = listePOI.item(i).getChildNodes();    // Ajout salles concernees par le noeud
                for(int j = 0 ; j < POIs.getLength() ; j++)
                    if(POIs.item(j).getNodeType() == Node.ELEMENT_NODE)
                        n.ajouterPOI(POIs.item(j).getTextContent());

                NodeList voisins = listeVoisins.item(i).getChildNodes();  // Ajout des voisins du noeud
                for(int j = 0 ; j < voisins.getLength() ; j++)
                    if(voisins.item(j).getNodeType() == Node.ELEMENT_NODE)
                        n.ajouterVoisin(Integer.parseInt(voisins.item(j).getTextContent()));

                NodeList voisinsPMR = listeVoisinsPMR.item(i).getChildNodes(); // Ajout des voisins PMR du noeud
                for(int j = 0 ; j < voisinsPMR.getLength() ; j++)
                    if(voisinsPMR.item(j).getNodeType() == Node.ELEMENT_NODE)
                        n.ajouterVoisinPMR(Integer.parseInt(voisinsPMR.item(j).getTextContent()));

                g.ajouterNoeud(n); // Ajout du noeud au graphe
            }


        }
        catch (SAXException | IOException e)
        {
            e.printStackTrace();
        }

        return g;
    }
}
