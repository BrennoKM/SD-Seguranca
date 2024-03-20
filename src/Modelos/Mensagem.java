package Modelos;

import java.io.Serializable;

public class Mensagem<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	T mensagem;
	String hash;

	public Mensagem(T mensagem, String hash) {
		this.mensagem = mensagem;
		this.hash = hash;
	}

	public T getMensagem() {
		return mensagem;
	}

	public void setMensagem(T mensagem) {
		this.mensagem = mensagem;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
