package Modelos;

import Cifra.ChavesModulo;

public class TokenInfo {
	private ChavesModulo remenChavesRSA, destinChavesRSA;
	private String chaveHmac, chaveAES;

	public TokenInfo(ChavesModulo chavesRSA, String chaveHmac, String chaveAES) {
		this.remenChavesRSA = chavesRSA;
		this.chaveHmac = chaveHmac;
		this.chaveAES = chaveAES;
	}

	public TokenInfo(ChavesModulo chavesRSA) {
		this.remenChavesRSA = chavesRSA;
	}

	public TokenInfo() {
	}

	public ChavesModulo getRemenChavesRSA() {
		return remenChavesRSA;
	}

	public void setRemenChavesRSA(ChavesModulo chavesRSA) {
		this.remenChavesRSA = chavesRSA;
	}

	public ChavesModulo getDestinChavesRSA() {
		return destinChavesRSA;
	}

	public void setDestinChavesRSA(ChavesModulo destinChavesRSA) {
		this.destinChavesRSA = destinChavesRSA;
	}

	public String getChaveHmac() {
		return chaveHmac;
	}

	public void setChaveHmac(String chaveHmac) {
		this.chaveHmac = chaveHmac;
	}

	public String getChaveAES() {
		return chaveAES;
	}

	public void setChaveAES(String chaveAES) {
		this.chaveAES = chaveAES;
	}

	@Override
	public String toString() {
		return "TokenInfo [remenChavesRSA=\n\t" + remenChavesRSA + ", \n\tdestinChavesRSA=" + destinChavesRSA + ", \n\tchaveHmac="
				+ chaveHmac + ", \n\tchaveAES=" + chaveAES + "]";
	}



}
