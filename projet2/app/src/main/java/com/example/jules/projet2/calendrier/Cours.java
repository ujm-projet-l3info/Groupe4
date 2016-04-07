package com.example.jules.projet2.calendrier;

import java.util.Date;

public class Cours {
	private Date debut;
	private Date fin;
	private String nom;
	private String salle;
	
	public Cours() {
		this.debut = new Date(0);
		this.fin = new Date(0);
		this.nom = "";
		this.salle = "";
	}
	
	public Cours(Date d, Date f, String n, String s) {
		this.debut = d;
		this.fin = f;
		this.nom = n;
		this.salle = s;
	}
	
	public String toString() {
		String s;
		
		s = "   Début : " + debut + "\n" +
			"   Fin   : " + fin + "\n" +
			"   Cours : " + nom + "\n" +
			"   Salle : " + salle;
		
		return s;
	}

	public Date getDebut() {
		return debut;
	}

	public void setDebut(Date debut) {
		this.debut = debut;
	}

	public Date getFin() {
		return fin;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getSalle() {
		return salle;
	}

	public void setSalle(String salle) {
		this.salle = salle;
	}
}
