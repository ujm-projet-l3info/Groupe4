package isc_parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EmploiDuTemps {
	ArrayList<Cours> edt;
	
	public EmploiDuTemps() {
		edt = new ArrayList<Cours>();
	}
	
	public void ajouter(Cours c){
		edt.add(c);
	}
	
	public void supprimer(int i){
		edt.remove(i);
	}
	
	public void trierParDate() {
		Collections.sort(this.edt, new compareCours());
	}
	
	class compareCours implements Comparator<Cours> {
		public int compare(Cours a, Cours b){
			if(a.getDebut().getTime() > b.getDebut().getTime())
				return 1;
			
			return -1;
		}
	}
	
	public void afficherEDT() {
		for(int i = 0; i < edt.size(); i++)
			System.out.println("Cours " + i + "\n" + edt.get(i) + "\n");
	}
	
	public ArrayList<Cours> getEDT() {
		return edt;
	}
}
