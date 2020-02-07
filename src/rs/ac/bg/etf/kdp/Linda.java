package rs.ac.bg.etf.kdp;

import java.io.*;
import java.rmi.RemoteException;

public interface Linda extends Serializable {
	/**
	 * Ubacuje torku u prostor torki. Nije dozvoljeno da bilo slati bilo koji
	 * string koji je null
	 */
	public void out(String[] tuple);

	/**
	 * Dohvatanje torke iz prostora torki. Ovo je blokirajuca operacija. Ukoliko
	 * je neko od polja unutar ovog niza postavljeno na vrednost null onda to
	 * polje takodje treba popuniti. Ukoliko ima vise torki uzima se bilo koja.
	 * Nakon ove operacije torka se vise ne nalazi u prostoru torki.
	 */
	public void in(String[] tuple);

	/**
	 * Dohvatanje torke iz prostora torki. Ovo je neblokirajuca operacija.
	 * Ukoliko je neko od polja unutar ovog niza postavljeno na vrednost null
	 * onda to polje takodje treba popuniti. Ukoliko torka postoji onda se kao
	 * rezultat vraca vrednost true, u suprotnom se vraca vrednost false.
	 * Ukoliko ima vise torki uzima se bilo koja. Nakon ove operacije torka se
	 * vise ne nalazi u prostoru torki.
	 */
	public boolean inp(String[] tuple);

	/**
	 * Cita torku iz prostora torki. Ovo je blokirajuca operacija. Ukoliko je
	 * neko od polja unutar ovog niza postavljeno na vrednost null onda to polje
	 * takodje treba popuniti. Ukoliko ima vise torki uzima se bilo koja. Nakon
	 * ove metode torka se i dalje nalazi u prostoru torki.
	 */
	public void rd(String[] tuple);

	/**
	 * Cita torku iz prostora torki. Ovo je neblokirajuca operacija. Ukoliko je
	 * neko od polja unutar ovog niza postavljeno na vrednost null onda to polje
	 * takodje treba popuniti. Ukoliko torka postoji onda se kao rezultat vraca
	 * vrednost true, u suprotnom se vraca vrednost false. Ukoliko ima vise
	 * torki uzima se bilo koja. Nakon ove operacije torka se i dalje nalazi u
	 * prostoru torki.
	 */
	public boolean rdp(String[] tuple);

	/** Pokretanje nove niti na datom racunaru. */
	public void eval(String name, Runnable thread);

	/**
	 * Pokretanje nove niti na datom racunaru. Pokrece se izvrsavanje metode
	 * zadate parametrom methodName koja prima argimente arguments. Izvrsavanje
	 * se pokrece na instanci klase zadate nazivom klase className i argumentima
	 * konstruktora initargs.
	 */
	public void eval(String className, Object[] construct, String methodName,
			Object[] arguments);
}
