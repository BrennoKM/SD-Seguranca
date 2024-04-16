package Modelos;

public class Permissao {
	private String ip;
	private int porta;
	private boolean permitir;
	
	public Permissao(String ip, int porta, boolean permitir) {
		this.ip = ip;
		this.porta = porta;
		this.permitir = permitir;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPorta() {
		return porta;
	}
	public boolean getPermicao() {
		return permitir;
	}
}
