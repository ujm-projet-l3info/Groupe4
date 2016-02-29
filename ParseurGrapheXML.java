import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class ParseurGrapheXML {

    File fXmlFile;
    Document doc ;
    DocumentBuilder dBuilder;
    
    public ParseurGrapheXML(String file_name) throws ParserConfigurationException, SAXException, IOException
    {
        fXmlFile = new File(file_name);
		
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
        dbFactory.setValidating(false);
		
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
            NodeList listeSalles = doc.getElementsByTagName("liste_salles");
            NodeList listeVoisins = doc.getElementsByTagName("liste_voisins");
    		
            for(int i = 0 ; i < listeLat.getLength() ; i++) // Parcours des noeud du graphe
            {
                float latitude = Float.parseFloat(listeLat.item(i).getTextContent());
                float longitude = Float.parseFloat(listeLon.item(i).getTextContent());
                char batiment = '-';
                if(listeBat.item(i).getTextContent() != "")
                    batiment = listeBat.item(i).getTextContent().charAt(0);
    			
                n = new Noeud(latitude , longitude , batiment);
    			
                NodeList salles = listeSalles.item(i).getChildNodes();    // Ajout salles concernees par le noeud
                for(int j = 0 ; j < salles.getLength() ; j++)
                    if(salles.item(j).getNodeType() == Node.ELEMENT_NODE)
                        n.ajouterSalle(Integer.parseInt(salles.item(j).getTextContent()));
    			
                NodeList voisins = listeVoisins.item(i).getChildNodes();  // Ajout des voisins du noeud
                for(int j = 0 ; j < voisins.getLength() ; j++)
                    if(voisins.item(j).getNodeType() == Node.ELEMENT_NODE)
                        n.ajouterVoisin(Integer.parseInt(voisins.item(j).getTextContent()));
    			
                g.ajouterNoeud(n); // Ajout du noeud au graphe
            }
    		
     
    	}
    	catch (SAXException | IOException e)
        {
            e.printStackTrace();
        }
        
        return g;
    }
    
    
    public static void main(String args[]) // Exemple d'utilisation (temporaire)
    {
        try {
            ParseurGrapheXML p = new ParseurGrapheXML("metare.xml");
            p.remplirGraphe();
        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }

}
