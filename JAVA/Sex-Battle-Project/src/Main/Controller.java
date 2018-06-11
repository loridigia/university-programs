package Main;

import sun.jvm.hotspot.oops.FieldType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
	
	// MAPPA ANNO -- 4 TYPES %
	public static HashMap<Integer, ArrayList<Float>> stableMap = new HashMap();
	
	public static void changeYear(){
		Eco.clock ++;
	}
	
	public static String getGenere(Umano umano){ // ritorna il genere
		
		if(umano.getClass().getName() == "Types.Avv" || umano.getClass().getName() == "Types.Mod"){
			return "M";
		}
		else return "F";
	}
	
	
	
	// [[[[[[[[[[[ REGOLA EVOLUTIVA ]]]]]]]]]]]]
	
	public static boolean controllaPayoff(Umano umano) {
		// RITORNA TRUE SE TUTTO VA BENE ( PAYOFF / f.DAFARE>0 ) altrimenti FALSE
		if (umano.getPayoff() <= 0 &&  Eco.clock - umano.gen > 0|| Eco.FdaFare.get(0).size() <1 && umano.partner == null ) {
			//Eco.FdaFare.get(0).size() == 0 solo
			// se questo metodo viene eseguito da un maschio
			if( getGenere(umano) == "M"){
				Eco.MdaFare.remove(umano);
				Eco.Mfatti.add(umano);
				return false;
			}
			else{
				if(umano.partner == null){ //SE è FEMMINA SINGLE ALLORA SPOSTALA IN SINGLE F-FATTI
				Eco.FdaFare.get(0).remove(umano);
				Eco.Ffatti.get(0).add(umano);
				return false;
				}
				else{   // SE FEMMINA SPOSATA ALLORA SPOSTALA IN SPOSATA F-FATTI
					Eco.FdaFare.get(1).remove(umano);
					Eco.Ffatti.get(1).add(umano);
					return false;
				}
			}
		}
		else return true;

	
	}
	
	// Ritorna la stabilità del sistema
	public static synchronized boolean isStable(Eco eco) {
		if(eco.clock != 0 && eco.clock % 10 == 0){ //Controllo so sono passati 10 anni
			ArrayList<Float> base = stableMap.get(eco.clock - 10); // BASE PER CONFRONTARLI
			for(int i = eco.clock-9; i != eco.clock; i++){
				if(base.get(0) - stableMap.get(i).get(0) > 5 || base.get(0) - stableMap.get(i)
						.get(0) < -5) return false; // CONTROLLO RANGE AVVENTURIERI ( +5 -5 )
				if(base.get(1) - stableMap.get(i).get(1) > 5 || base.get(1) - stableMap.get(i)
						.get(1) < -5) return false; //CONTROLLO RANGE MODERATI ( +5 -5 )
				if(base.get(2) - stableMap.get(i).get(2) > 5 || base.get(2) - stableMap.get(i)
						.get(2) < -5) return false;  // CONTROLLO RANGE PURDENTI ( +5 -5 )
				if(base.get(3) - stableMap.get(i).get(3) > 5 || base.get(3) - stableMap.get(i)
						.get(3) < -5) return false;
			}
			return true; //SE PASSANO I CONTROLLI TORNA TRUE
		}
		return false; //NON SONO PASSATI 10 ANNI OPPURE SIAMO ANNO 0
	}
	
	
	public static void avvia(Eco eco) {
		int TOT;
		while( !isStable(eco) ) {
			eco.MdaFare.addAll(eco.Mfatti); //SWAPPO ARRAYS PER COMINCIARE NEW YEAR
			eco.Mfatti.clear();
			eco.FdaFare.get(0).addAll(eco.Ffatti.get(0));
			eco.Ffatti.get(0).clear();
			eco.FdaFare.get(1).addAll(eco.Ffatti.get(1));
			eco.Ffatti.get(1).clear();
			
			while(eco.MdaFare.size() != 0) {    //AVVIA I TIPI MASCHI
				Umano x = eco.MdaFare.get(0);
				x.run(); }
				
				//SMISTAMENTO NEI RELATIVI GRUPPI DI APPARTENZA
			for(Umano x : eco.Mfatti) {
				if(x.getClass().getName() == "Types.Avv" && Eco.clock - x.gen == 0)
					eco.avv.add(x);
				else if(x.getClass().getName() == "Types.Mod" && Eco.clock - x.gen == 0){
					eco.mod.add(x);
				} }
			
				//SMISTAMENTO SINGLE
			for(Umano x : eco.Ffatti.get(0)) {
				if(x.getClass().getName() == "Types.Prud" && Eco.clock - x.gen == 0)
					eco.prud.add(x);
				else if(x.getClass().getName() == "Types.Spre" && Eco.clock - x.gen == 0){
					eco.spre.add(x);
				} }
			
				//SMISTAMENTO ACCOPPIATE
			for(Umano x : eco.Ffatti.get(1)) {
				if(x.getClass().getName() == "Types.Prud" && Eco.clock - x.gen == 0)
					eco.prud.add(x);
				else if(x.getClass().getName() == "Types.Spre" && Eco.clock - x.gen == 0){
					eco.spre.add(x);
				} }
				
			//TOTALE ESSERI VIVENTI
			TOT = eco.Mfatti.size() + eco.MdaFare.size() + eco.FdaFare.get(0).size() + eco.FdaFare.get(1).size() + eco.Ffatti.get(0).size() + eco.Ffatti.get(1).size();
			
			//STAMPA LE STATISTICHE DELL'ANNO PASSATO
			System.out.println("ANNO PASSATO: "+eco.clock+"	AVV: "+eco.getAvv()+" MOD: "+eco.getMod()+" PRUD: "+eco.getPrud()+" SPRE: "+eco.getSpre() + " TOT= "+ TOT +
							" TOTALE THREADS GENERATI " + Umano.ID);
			
			
			//AGGIUNGI KEY= ANNO --- VALUE: ARRAYLIST ( % AVV, % MOD, % PURD, % SPRE)
			stableMap.put(eco.clock, new ArrayList<>());
			stableMap.get(eco.clock).add(eco.getAvv());
			stableMap.get(eco.clock).add(eco.getMod());
			stableMap.get(eco.clock).add(eco.getPrud());
			stableMap.get(eco.clock).add(eco.getSpre());
			
			System.out.println("STABILE: " + isStable(eco) +"\n");
			eco.clock++;

		}
		System.out.println("STABILITà RAGGIUNTA!!!");
	}
}
