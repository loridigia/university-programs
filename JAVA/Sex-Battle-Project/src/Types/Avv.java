package Types;

import Exceptions.MaxIndexException;
import Exceptions.MinIndexException;
import Main.Controller;
import Main.Eco;
import Main.Umano;
import Tools.Stack;

import java.util.Random;

public class Avv extends Umano {

    
    public Avv() {
        super();
    }
	
    /*
	*  Il metodo run di Avv esegue le seguenti operazioni:
	*  1. Crea una stringa che indica se this ha genitori o meno
	*  2. Stampa la stringa in console
	*  3. Genera un figlio del suo stesso tipo
	*  4. Assegna this al campo "parent" del figlio
	*  5. invoca start sul figlio
	*
	* */
    
    @Override
    public void run() {
        int anno = this.age;
        while( vivo() && anno == this.age) {
            // VERIFICA THIS.GEN CON CLOCK COSì SE SONO ARRIVATO ALLA FINE DEL RUN, IL THREAD MUORE
            
            //CONTROLLO PAYOFF E CONTROLLO SE CI SONO FEMMINE SINGLE, ESCE SE RITORNA FALSE
            if( Controller.controllaPayoff(this) ) {
                Random x = new Random();
                int index = x.nextInt(Eco.FdaFare.get(0).size());
                this.partner = Eco.FdaFare.get(0).get(index);// prendo una Femmina single
                Eco.FdaFare.get(0).remove(this.partner); //Rimuovi dalla lista SINGLE my Girl.
                this.partner.partner = this;
                partner.run();  // ATTIVO LA MIA PARTNER ( ACCOPPIAMENTO CON TANTO DI NASCITA )
                // potrebbe morire partner QUI, cioè quando l'attivo
    
                //GESTIONE COSTI / GUADAGNI SE NON TI è MORTA LA PARTNER
                if(this.partner != null) {
                    if(partner.getClass().getName() == "Types.Prud") {
                        this.upPayoff(0);
                    } else {
                        this.upPayoff(Eco.par_A);
                    }
                    //AVVENTURIERO LASCIA LA RAGAZZA
                    this.partner = null;
                }
                //HO FATTO IL MIO DOVERE
                Eco.Mfatti.add(this);
                Eco.MdaFare.remove(this);
                
            }
            //INCREMENTO I SUOI ANNI DI 1
            this.age++;
        }
        if(!vivo()) Eco.MdaFare.remove(this);
		
        
    }


}
