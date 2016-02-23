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
		
        dbFactory.setValidating(true);
		
        dBuilder = dbFactory.newDocumentBuilder();
    }

    public Graphe remplirGraphe()
    {
    	 /* Cette methode parse le fichier XML
    	  * ouvert et rempli le graphe associe
    	  */
    	
    	try {
    		Graphe g = new Graphe();
    		Noeud n = null;
    		
    		doc = dBuilder.parse(fXmlFile);                 // Parsing du fichier XML
    		
    		
    		NodeList listeLat = doc.getElementsByTagName("latitude");
    		NodeList listeLon = doc.getElementsByTagName("longitude");
    		NodeList listeBat = doc.getElementsByTagName("batiment");
    		NodeList listeSalles = doc.getElementsByTagName("liste_salles");
    		NodeList listeVoisins = doc.getElementsByTagName("liste_voisins");
    		
    		for(int i = 0 ; i < listeLat.getLength() ; i++) // Parcours des noeud du graphe
    		{
    			float latitude = (float) listeLat.item(i).getTextContent();
    			float longitude = (float) listeLon.item(i).getTextContent();
    			char batiment = (char) listeBat.item(i).getTextContent();
    			
    			n = new Noeud(latitude , longitude , batiment);
    			
    			NodeList salles = listeSalles.item(i).getChildNodes();    // Ajout salles concernees par le noeud
    			for(int j = 0 ; j < salles.getLength() ; j++)
    				if(salles.item(j).getNodeType() == Node.ELEMENT_NODE)
    					n.ajouterSalle(salles.item(j).getTextContent());
    			
    			NodeList voisins = listeVoisins.item(i).getChildNodes();  // Ajout des voisins du noeud
    			for(int j = 0 ; j < voisins.getLength() ; j++)
    				if(voisins.item(j).getNodeType() == Node.ELEMENT_NODE)
    					n.ajouterVoisin(voisins.item(j).getTextContent());
    			
    			g.ajouterNoeud(n); // Ajout du noeud au graphe
    		}
    		
    		return g;
    	}
    	catch (SAXException | IOException e)
        {
            e.printStackTrace();
        }
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
