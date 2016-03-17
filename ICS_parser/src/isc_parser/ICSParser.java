package isc_parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.CalendarComponent;

public class ICSParser {
	Calendar cal;
	
	public ICSParser(String s) throws FileNotFoundException, IOException, ParserException, ParseException {
		try {
			FileInputStream ics = new FileInputStream(s);
			CalendarBuilder builder = new CalendarBuilder();
			
			try {
				cal = builder.build(ics);
			} catch (IOException | ParserException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	EmploiDuTemps ICSToCalendrier() {
		EmploiDuTemps edt = new EmploiDuTemps();
		
		TimeZoneRegistry tzr = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone zone = tzr.getTimeZone("France/Paris");
		
		Iterator<CalendarComponent> calendrier = cal.getComponents().iterator();
		
		while(calendrier.hasNext()){
		    Component coursICS = (Component) calendrier.next();
		    Cours cours = new Cours();

		    Iterator<Property> infosCours = coursICS.getProperties().iterator();
		    
		    while(infosCours.hasNext()) {
		        Property info = (Property) infosCours.next();
		        
		        if(info.getName().equals("DTSTART")){
		        	try {
						DateTime debut = new DateTime(info.getValue());
						debut.setTimeZone(zone);
						
						long millis = debut.getTime();
						Date debutOk = new Date(millis);
						
						cours.setDebut(debutOk);
					} catch (ParseException e) {
						e.printStackTrace();
					}
		        }else if(info.getName().equals("DTEND")){
		        	try {
						DateTime fin = new DateTime(info.getValue());
						fin.setTimeZone(zone);
						
						long millis = fin.getTime();
						Date finOk = new Date(millis);
						
						cours.setFin(finOk);
					} catch (ParseException e) {
						e.printStackTrace();
					}
		        }else if(info.getName().equals("SUMMARY")){
		        	cours.setNom(info.getValue());
		        }else if(info.getName().equals("LOCATION")){
		        	cours.setSalle(info.getValue());
		        }
		    }
		    
		    edt.ajouter(cours);
		}
		
		return edt;
	}
}
