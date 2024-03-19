package Cifra;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {

	private BigInteger chavePri;
	private BigInteger chavePub;
	private BigInteger modulo;

	public RSA() {
		
	}

	public void gerarChaves(int bits) {
		SecureRandom random = new SecureRandom();
		BigInteger p = BigInteger.probablePrime(bits, random);
		BigInteger q = BigInteger.probablePrime(bits, random);
		//System.out.println(p + "\n" + q);
		modulo = p.multiply(q);

		// phi(n) = (p – 1) * (q – 1).
		BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

		chavePub = BigInteger.probablePrime(32, random); // mdc(chavePub, phi(modulo)) = 1 
		chavePri = chavePub.modInverse(phi); //  chavePri*chavePub mod phi(modulo) = 1
	}
	public String criptografar(String mensagem) {
		BigInteger msg = new BigInteger(mensagem.getBytes());
		return msg.modPow(chavePub, modulo).toString();
	}

	public String descriptografar(String mensagem) {
		BigInteger msg = new BigInteger(mensagem);
		return new String(msg.modPow(chavePri, modulo).toByteArray());
	}
	
	public static String criptografar(String mensagem, String chavePub, String modulo) {
//		System.out.println("==================================================");
//		System.out.println("RSA crip recebeu mensagemCrip: " + mensagem);
//		System.out.println("RSA crip recebeu chavePub: " + chavePub);
//		System.out.println("RSA crip recebeu modulo: " + modulo);
//		System.out.println("==================================================");
		BigInteger msg = new BigInteger(mensagem.getBytes());
		BigInteger chaPub = new BigInteger(chavePub);
		BigInteger mod = new BigInteger(modulo);
//		System.out.println("==================================================");
//		System.out.println("RSA crip recebeu mensagemCrip: " + msg);
//		System.out.println("RSA crip recebeu chavePub: " + chavePub);
//		System.out.println("RSA crip recebeu modulo: " + mod);
//		System.out.println("==================================================");
		return msg.modPow(chaPub, mod).toString();
	}
	
	public static String descriptografar(String mensagem, String chavePri, String modulo) {
//		System.out.println("==================================================");
//		System.out.println("RSA descrip recebeu mensagemCrip: " + mensagem);
//		System.out.println("RSA descrip recebeu chavePri: " + chavePri);
//		System.out.println("RSA descrip recebeu modulo: " + modulo);
//		System.out.println("==================================================");
		BigInteger msg = new BigInteger(mensagem);
		BigInteger chaPri = new BigInteger(chavePri);
		BigInteger mod = new BigInteger(modulo);
//		System.out.println("==================================================");
//		System.out.println("RSA descrip recebeu mensagemCrip: " + msg);
//		System.out.println("RSA descrip recebeu chavePri: " + chaPri);
//		System.out.println("RSA descrip recebeu modulo: " + mod);
//		System.out.println("==================================================");
//		BigInteger msgDescriptografada = msg.modPow(chaPri, mod);
//		System.out.println("Mensagem bigI descriptografada: " + new String(msgDescriptografada.toByteArray()));
//		return msg.modPow(chaPri, mod).toString();
		return new String(msg.modPow(chaPri, mod).toByteArray());
//		return new String(msg.modPow(chavePri, modulo).toByteArray());
	}
	
	public String getChavePub() {
		return new String(this.chavePub.toString());
	}

	public String getModulo() {
		return new String(this.modulo.toString());
	}
	
	public String getChavePri() {
		return new String(this.chavePri.toString());
	}
	
	public static void main(String[] args) {
		RSA rsa = new RSA();
		rsa.gerarChaves(1024);
		String mensagemOriginal = "Testandoooooooooooo";

		String criptografado = rsa.criptografar(mensagemOriginal);
		String descriptografado = rsa.descriptografar(criptografado);

		System.out.println("Mensagem Original: " + mensagemOriginal);
		System.out.println("Mensagem Criptografada: " + criptografado);
		System.out.println("Mensagem Descriptografrada: " + descriptografado);
		
		System.out.println("\n\nTeste Manual");
		String chavePri = "6736014947838846565248189338158974733039152012444082704637675012206604204093964027346829908096361226485848122491600528590164910668827148043057594082146328848636529108643071661831844616126440392661222716340354310301916955523706090319545721167410833340343911852990067374001437938393787589542485970531740868478211815683865036358535394469709068508580987267426978845369090740470018261611711564287843607762389202356854165444352016276527878553126184466554276762082164421877190444252072831525615860912717555648703931679135822740539118213109926791157569798115139909315298145092088805081661963595377937381633271556371971048497";
		String modulo = "16400111881882550239492926096103898026569095231426846281812887557581700710443053735798692016008292804079167412279219252627001922524070317308041665181723157506244602429346199104743056713243128167539882352358934558074772699091950592216066124086061512171265285352158780202464233530296220271039672451546872029774261357047524486769800215662891501514232822976225013144158624222317215403642947778428150898242299785102686855741383722636401355282720654473312358608677857981740857865907459958609032210206879684556716021950687812038599117067637860414100502318830385285648897674194929132552769049194868299821633841886348988075159";
		String chavePub = "65537";
		String mensagem = "Testando RSAAAAA";
		
		
		String crip = RSA.criptografar(mensagem, chavePub, modulo);
		String decrip = RSA.descriptografar(crip, chavePri, modulo);
//		String crip = rsa.criptografar(mensagem);
//		String decrip = rsa.descriptografar(crip);
		System.out.println("Mensagem: " + mensagem);
		System.out.println("Crip: " + crip);
		System.out.println("Decrip: " + decrip);
	}
	
}