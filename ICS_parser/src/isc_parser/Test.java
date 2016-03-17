package isc_parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import net.fortuna.ical4j.data.ParserException;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParserException, ParseException {
		ICSParser parser = new ICSParser("cal.ics");
		EmploiDuTemps edt = new EmploiDuTemps();
		
		edt = parser.ICSToCalendrier();
		
		edt.trierParDate();
		edt.afficherEDT();
	}

}
