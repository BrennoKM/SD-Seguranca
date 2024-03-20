package Cifra;

import java.io.Serializable;

public class ChavesModulo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String chavePub, chavePri, modulo;

	public ChavesModulo(String chavePub, String modulo) {
		this.chavePub = chavePub;
		this.modulo = modulo;
	}
	
	public ChavesModulo(String chavePub, String chavePri, String modulo) {
		this.chavePub = chavePub;
		this.chavePri = chavePri;
		this.modulo = modulo;
	}

	public String getChavePub() {
		return chavePub;
	}

	public String getModulo() {
		return modulo;
	}

	public String toString() {
		return "ChavesModulo [\n\t\tchavePub=" + chavePub + ", \n\t\tchavePri=" + chavePri + ", \n\t\tmodulo=" + modulo + "]";
	}
	
	

	public String getChavePri() {
		return chavePri;
	}

	public void setChavePri(String chavePri) {
		this.chavePri = chavePri;
	}
}
