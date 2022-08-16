package jitinteractor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDVarSet;
import tau.smlab.syntech.controller.Controller;
import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;
import tau.smlab.syntech.jtlv.Env;

public class CustomControllerExecutor extends ControllerExecutor {

	public CustomControllerExecutor(Controller controller, String folder) throws IOException {
		super(controller, folder);
	}
	
	public static void OLD_main(String[] args) throws Exception {
		Map<String, String> inputs = new HashMap<>();
        Scanner in = new Scanner(System.in);
		
		CustomControllerExecutor executor = new CustomControllerExecutor(new BasicJitController(), "out/");
		
		Set<String> input_list = executor.getEnvVars().keySet();
		//Set<String> output_list = executor.getSysVars().keySet();
		// Get all inputs from command line
		addInputs(inputs, in, input_list);
		
		// execute controller
		executor.initState(inputs);
		while(true) {
			addInputs(inputs, in, input_list);
			executor.updateStateCustom(inputs);
		}
	}
	private static void addInputs(Map<String, String> inputs, Scanner in, Set<String> input_list) {
		inputs.clear();
		for(String input : input_list) {
			boolean bool_input = getInput(in, input);
			inputs.put(input, Boolean.toString(bool_input));
		}
	}
	private static boolean getInput(Scanner in, String name) {
		String line;
		System.out.println("Is " + name + " present? (Y/n)");
		line = in.nextLine();
		boolean variable = !"n".equals(line);
		return variable;
	}
	
	
	public void updateStateCustom(Map<String, String> inputs) throws IllegalStateException, IllegalArgumentException {
		
		if (currentState == null) {
			throw new IllegalStateException("Controller has not yet been initialized");
		}
		
		BDD inputsBDD = getInputsBDDcustom(inputs);
		
		BDD currAndTrans = controller.transitions().id();
		currAndTrans.andWith(currentState.id());
		
		//Titus adding:
		System.out.println("bdd dot before:");
		currAndTrans.printDot();
		System.out.println("bdd set before:");
		currAndTrans.printSet();
		
//		BDDVarSet variableSet = currAndTrans.toVarSet();
//		System.out.println(variableSet.toString());
//		currAndTrans.var();
//		
		
		//if BDD is zero, how can we see why it is zero?
		
		if (currAndTrans.isZero()) {
			throw new IllegalStateException("The environment is in a deadlock state. There is no possible safe input for the environment");
		}
		currAndTrans.andWith(Env.prime(inputsBDD));
		
		System.out.println("bdd dot after:");
		currAndTrans.printDot();
		System.out.println("bdd set after:");
		currAndTrans.printSet();
		
		if (currAndTrans.isZero()) {
			throw new IllegalArgumentException("The inputs are a safety violation for the environment");
		}
		currAndTrans.free();
		
		BDD nextStates = controller.next(currentState, inputsBDD);

	    currentState.free();
		currentState = Env.randomSat(nextStates, Env.globalUnprimeVars());
		
		// TODO: satOne currently not working
//		currentState = nextStates.satOne(Env.globalUnprimeVars());
		
		nextStates.free();
		inputsBDD.free();
	}
	
	private BDD getInputsBDDcustom(Map<String, String> inputs) throws IllegalArgumentException {
		
		BDD inputsBDD = Env.TRUE();
		for (String varName : inputs.keySet()) {
			BDD valBdd = Env.getBDDValue(varName, inputs.get(varName));
			if (valBdd == null) {
				throw new IllegalArgumentException("Invalid variable name or value");
			}
			if (!envVars.containsKey(varName)) {
				throw new IllegalArgumentException("The variable name " + varName + " does not refer to an input variable");
			}
			
			inputsBDD.andWith(valBdd.id());
//			valBdd.free();
			//inputsBDD = inputsBDD.and(valBdd);
		}
		return inputsBDD;
	}

}
