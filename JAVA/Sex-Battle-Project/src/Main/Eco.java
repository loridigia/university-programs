package Main;

import Tools.Stack;
import Tools.Tuple;
import Types.*;
import Types.Spre;

import javax.crypto.AEADBadTagException;
import javax.swing.table.TableColumn;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
* Classe Eco:
*
* Rappresenta l'ecosistema della simulazione
* È una classe fondamentale per il funzionamento della simulazione:
*
* La classe possiede:
* 1. 4 Stack per lo smistamento dei 4 Types
* 2. Una HashMap per mantenere il controllo delle generazioni
* 3. 3 parametri per la simulazione
* 4. Un booleano che rappresenta la stabilità del sistema
*
* */

public class Eco {
    
    //Arrays fatti( riprodotti e in wait ) daFare ( da accoppiare )
    public static ArrayList<Umano> Mfatti;
    public static ArrayList<Umano> MdaFare;
    
    public static ArrayList<ArrayList<Umano>> Ffatti;
    public static ArrayList<ArrayList<Umano>> FdaFare;
	
	// Clock dell'ecosistema statico
	public static int clock;
    

    // 4 Stack per ogni tipo [Types] di Umano
    public static ArrayList<Umano>	avv;
    public static ArrayList<Umano>	mod;
    public static ArrayList<Umano>	spre;
    public static ArrayList<Umano>	prud;
    
	
    // Parametri di stabilità del sistema
    public static int       par_A;
    public static int       par_B;
    public static int       par_C;


	// COSTRUTTORE
	/*
	* Il costruttore esegue le seguenti operazioni:
	* 1. Inizializza tutti i Field della classe
	* 2. Riempie avv e mod di relative istanze (Avv e Mod)
	* 3. Iterativamente invoca il metodo start() su ogni Umano
	*
	* */
	
    public Eco(int a, int b, int c) {
        
        //Arrays fatti( riprodotti ) daFare ( da accoppiare )
        Mfatti = new ArrayList<>();
        MdaFare = new ArrayList<>();
        
        // LE FEMMINE SONO ULTERIORMENTE DIVISE IN 1) SINGLE 2) ACCOPPIATE
        Ffatti = new ArrayList<>();
        FdaFare = new ArrayList<>();
        Ffatti.add(new ArrayList<Umano>());// F single
        Ffatti.add(new ArrayList<Umano>());// F accoppiate
        FdaFare.add(new ArrayList<Umano>());// F single
        FdaFare.add(new ArrayList<Umano>());// F accoppiate

        
        // Inizializzazione valori della simulazione
        par_A   = a;
        par_B   = b;
        par_C   = c;
        
        
        // Stack di smistamento
        avv  = new ArrayList<>();
        mod  = new ArrayList<>();
        spre = new ArrayList<>();
        prud = new ArrayList<>();
        
        
        // Thread Iniziali
        for (int i=0; i<5; i++) {
            avv.add(new Avv());
            mod.add(new Mod());
            spre.add(new Spre());
            prud.add(new Prud());
        }
        
        // Thread iniziali nella lista dafare
        for(Umano x :avv)  MdaFare.add(x);
        for(Umano x :mod)  MdaFare.add(x);
        for(Umano x :prud)  FdaFare.get(0).add(x);
        for(Umano x :spre)  FdaFare.get(0).add(x);
    }


    // Ritorna il numero di Avv presenti nel proprio Stack
    public static float getAvv() {
        if (avv.size()!=0) {
            float total = avv.size() + mod.size() + spre.size() + prud.size();
            return (avv.size() * 100) / total;
        }
        return 0;
    }

    // Ritorna il numero di Mod presenti nel proprio Stack
    public static float getMod() {
        if (avv.size()!=0) {
            float total = avv.size() + mod.size() + spre.size() + prud.size();
            return (mod.size() * 100) / total;
        }
        return 0;
    }

    // Ritorna il numero di Spre presenti nel proprio Stack
    public static float getSpre() {
        if (avv.size()!=0) {
            float total = avv.size() + mod.size() + spre.size() + prud.size();
            return (spre.size() * 100) / total;
        }
        return 0;
    }

    // Ritorna il numero di Prud presenti nel proprio Stack
    public static float getPrud() {
        if (avv.size()!=0) {
            float total = avv.size() + mod.size() + spre.size() + prud.size();
            return (prud.size() * 100) / total;
        }
        return 0;
    }
    
}

