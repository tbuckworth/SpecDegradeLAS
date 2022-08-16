package jitinteractor;

import tau.smlab.syntech.games.GameMemory;
import tau.smlab.syntech.games.controller.enumerate.ConcreteControllerConstruction;
import tau.smlab.syntech.games.controller.enumerate.EnumStrategyImpl;
import tau.smlab.syntech.games.controller.enumerate.printers.SimpleTextPrinter;
import java.io.PrintStream;

public class BDDTester {
	
	public static void main(String args[]) throws Exception {
		
		ConcreteControllerConstruction ccc = new ConcreteControllerConstruction(new GameMemory(), new GameModel());
		
		
		
//		String folder = "out/";
//		
//		BDD init = Env.loadBDD(folder + File.separator + "controller.init.bdd");
//		BDD trans = Env.loadBDD(folder + File.separator + "controller.trans.bdd");
//		
//		System.out.println("init string:");
//		System.out.println(init.toString());
		
//		SimpleTextPrinter print = new SimpleTextPrinter();
//		
//		EnumStrategyImpl enumStratImpl = new EnumStrategyImpl(true);
//		
//		PrintStream out = new PrintStream("ConcreteOutPrint.txt");
//		
//		System.out.println("about to print");
//		
//		System.out.println(
//				enumStratImpl.numOfStates()
//		);
//		
//		print.printController(out, enumStratImpl);
//				
//		
//		System.out.println("finished print");
		
		
	}
	
}
