package jitinteractor;

import java.io.IOException;
import java.util.Map;

import net.sf.javabdd.BDD;
import tau.smlab.syntech.controller.Controller;
import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;
import tau.smlab.syntech.jtlv.Env;

public class CustomControllerExecutor extends ControllerExecutor {

	public CustomControllerExecutor(Controller controller, String folder) throws IOException {
		super(controller, folder);
	}
	
//	public static void main(String[] args) throws Exception {
//		CustomControllerExecutor executor = new CustomControllerExecutor(new BasicJitController(), "out/");
//
//	}
	
	
	public void updateStateCustom(Map<String, String> inputs) throws IllegalStateException, IllegalArgumentException {
		
		if (currentState == null) {
			throw new IllegalStateException("Controller has not yet been initialized");
		}
		
		BDD inputsBDD = getInputsBDDcustom(inputs);
		
		BDD currAndTrans = controller.transitions().id();
		currAndTrans.andWith(currentState.id());
		
		//Titus adding:
		currAndTrans.printSet();
		currAndTrans.toVarSet();
		currAndTrans.var();
		//if BDD is zero, how can we see why it is zero?
		
		if (currAndTrans.isZero()) {
			throw new IllegalStateException("The environment is in a deadlock state. There is no possible safe input for the environment");
		}
		currAndTrans.andWith(Env.prime(inputsBDD));
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
