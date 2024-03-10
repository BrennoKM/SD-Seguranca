package Cifra;

import java.io.Serializable;

public class ChavePubModulo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String chavePub, modulo;

	public ChavePubModulo(String chavePub, String modulo) {
		this.chavePub = chavePub;
		this.modulo = modulo;
	}

	public String getChavePub() {
		return chavePub;
	}

	public String getModulo() {
		return modulo;
	}

	public String toString() {
		return "ChavePubModulo [chavePub=" + chavePub + ", modulo=" + modulo + "]";
	}
}
