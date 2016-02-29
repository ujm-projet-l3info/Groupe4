import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class Test {

    public Test()
    {
    }

    public static void main(String[] args)
    {
        try {
            ParseurGrapheXML p = new ParseurGrapheXML("metare.xml");
            Graphe g = new Graphe();
            
            g = p.remplirGraphe();
            g.afficherGraphe();
        }
        catch (SAXException | ParserConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
