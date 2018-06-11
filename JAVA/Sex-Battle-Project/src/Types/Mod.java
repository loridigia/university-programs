package Types;

import Exceptions.MaxIndexException;
import Exceptions.MinIndexException;
import Main.Controller;
import Main.Eco;
import Main.Umano;
import Tools.Stack;

import java.util.Random;

public class Mod extends Umano {
    
    
    public Mod() {
        super();
    }


	/*
	*  Il metodo run di Mod esegue le seguenti operazioni:
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
        while(vivo() && anno == this.age ) {
    
            //CONTROLLO PAYOFF E CONTROLLO SE CI SONO FEMMINE SINGLE, ESCE SE RITORNA FALSE
            if(Controller.controllaPayoff(this)) {
                if(this.partner == null) {
                    // CERCA PARTNER
                    Random x = new Random();
                    int index = x.nextInt(Eco.FdaFare.get(0).size());
                    this.partner = Eco.FdaFare.get(0).get(index);//PRENDO FEMMINA SINGLE
                    this.partner.partner = this;
                    Eco.FdaFare.get(0).remove(this.partner); //Rimuovi dalla lista SINGLE my Girl.
                } else Eco.FdaFare.get(1).remove(this.partner);
                partner.run(); // ATTIVO LA MIA PARTNER ( ACCOPPIAMENTO CON RIPRODUZIONE )
    
                //GESTIONE COSTI / GUADAGNI
                if(this.partner != null) {
                    if(partner.getClass().getName() == "Types.Prud") {
                        this.upPayoff(Eco.par_A - (Eco.par_B / 2) - Eco.par_C);
                    } else {
                        this.upPayoff(Eco.par_A - (Eco.par_B / 2));
                    }
                    
                }
    
                //HO FATTO IL MIO DOVERE ( sposto in array "MFATTI" )
                Eco.Mfatti.add(this);
                Eco.MdaFare.remove(this);
                
            }
            //INCREMENTO I SUOI ANNI DI 1
            this.age++;
        }
        if(!vivo()){
            Eco.MdaFare.remove(this);
            Eco.mod.remove(this);
            if(this.partner != null) {
                Eco.FdaFare.get(0).add(this.partner); //Metti la moglie tra le single
                Eco.FdaFare.get(1).remove(this.partner); //Rimuovila dalle sposate
                this.partner.partner = null;
            }
            
        }
        
    }
}

