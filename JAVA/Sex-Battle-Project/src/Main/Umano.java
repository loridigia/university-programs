package Main;

/*
*  Classe Umano:
*  Estende Thread e verrà estesa dai 4 Types che avranno un metodo run implementato diversamente
*
* */

import java.util.ArrayList;

public abstract class Umano extends Thread {
 
	
	// Parametri di ogni umano
    public int			payoff;
	// Umano Partner
	public Umano		partner;
    // ID (Numero di Thread generati)
    public static int	ID = 0;
    // Numero della generazione a cui appartiene
    public int			gen;
    // age è l'eta
	public int age;
    //
    public ArrayList<Umano> figli;

    
    
	// COSTRUTTORE:
    public Umano() {
    
		// Gen = nascita
        gen = Eco.clock;
        // Inizializzo il payoff
        payoff = 1;
        // Incrementa il contatore delle istanze di Umano di 1
        upID();
		//Inizializzo figli a 0
		figli = new ArrayList<>();
		//eta = 0
		age = 0;

    }

	
    // Incrementa in modo sincronizzato l'id ad ogni creazione del thread
    public synchronized void upID() { ID += 1; }

    // Incrementa il payoff di this a seconda di n
    public void upPayoff(int n) { this.payoff += n; }
    
    public int getPayoff(){
    	return this.payoff;
	}
    
    // Vivo o morto True allora vivo, false allora morto
	public Boolean vivo() {
		if(this.age == 5) {
			if(this.partner == null) return false;
			else {
				this.partner.partner = null;
				this.partner = null;
				return false;
			}
		} else return true;
	}
	
    

}
