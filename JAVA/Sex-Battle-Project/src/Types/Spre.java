package Types;

import Main.Controller;
import Main.Eco;
import Main.Umano;

import java.util.Random;

public class Spre extends Umano {
    
    @Override
    public void run() {
        int anno = this.age;
        while(vivo() && anno == this.age ) {
    
            //CONTROLLO PAYOFF E CONTROLLO SE CI SONO FEMMINE SINGLE( solo maschi )
            if(Controller.controllaPayoff(this)) {
    
                //GESTIONE COSTI / GUADAGNI
                if(partner.getClass().getName() == "Types.Avv") {
                    this.upPayoff(Eco.par_A - Eco.par_B);
                } else {
                    this.upPayoff(Eco.par_A - (Eco.par_B / 2));
                }
    
                //RIPRODUZIONE
                Random x = new Random();
                if(x.nextBoolean()) {         // x == true allora MASCHIO ( genere padre )
                    if(this.partner.getClass().getName() == "Types.Avv") {
                        this.figli.add(new Avv());
                        this.partner.figli.add(this.figli.get(figli.size()-1));
                        Eco.Mfatti.add(this.figli.get(figli.size() - 1));
                    } else {
                        this.figli.add(new Mod());
                        this.partner.figli.add(this.figli.get(figli.size()-1));
                        Eco.Mfatti.add(this.figli.get(figli.size() - 1));
                    }
                } else {      // x!= true allora Femmina ( genere Madre )
                    this.figli.add(new Spre());
                    Eco.Ffatti.get(0).add(this.figli.get(figli.size() - 1)); // METTI IN FFATTI SINGLE NEWFIGLIA
                }
    
    
                //SE AVVENTURIERO LASCIALO E METTI THIS IN F FATTI
                if(partner.getClass().getName() == "Types.Avv") {
                    this.partner = null;
                    Eco.Ffatti.get(0).add(this);
                } else Eco.Ffatti.get(1).add(this);
    
            }
            //INCREMENTO I SUOI ANNI DI 1
            this.age++;
        }
        if(!vivo()){
            if(this.partner != null){
                this.partner.partner = null;
                Eco.FdaFare.get(1).remove(this);
            }
            else Eco.FdaFare.get(0).remove(this);
        }
    
    }
}
