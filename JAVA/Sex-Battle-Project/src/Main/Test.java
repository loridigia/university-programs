package Main;


import Types.Avv;
import Types.Mod;
import Types.Prud;

public class Test {
	public static void main(String[] args) {
		
		Eco eco = new Eco(15, 20, 3);
		long start = System.currentTimeMillis();
		
		Controller.avvia(eco);
		
		long end = System.currentTimeMillis();
		System.out.println((end-start)/1000); //PER VEDERE LA VELOCITà DI ESECUZIONE
		

		
	}
}
