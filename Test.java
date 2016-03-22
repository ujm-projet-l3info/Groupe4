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
            boolean PMR = true;
            Graphe g = new Graphe(PMR);
            double lat = 45.4186702;
            double lon = 4.4257971;
            g = p.remplirGraphe(PMR);
            g.afficherGraphe();
            
            System.out.println("\nPosition : (" + lat + ";" + lon + ") Recolle  a : ");
            System.out.println(g.getNoeud(g.recollerGraphe(lat , lon)));
        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
