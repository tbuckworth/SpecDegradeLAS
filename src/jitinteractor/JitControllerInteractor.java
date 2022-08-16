package jitinteractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;


import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;

public class JitControllerInteractor {
//	Map<String, String> inputs = new HashMap<>();
//	Map<Integer, Map<String, String>> trace = new HashMap<>();
//	Set<String> input_list;
//	Set<String> output_list;
//	boolean iniState = true;
//	ControllerExecutor executor;
	static Random rd = new Random();
//	
//	public void JitControllerInteractor(String folder) throws Exception {
//		executor = new ControllerExecutor(new BasicJitController(), folder);
//		input_list = executor.getEnvVars().keySet();
//		output_list = executor.getSysVars().keySet();
//	}
//	
//	public void setInput(String name, boolean state) {
//		inputs.put(name, Boolean.toString(state));
//	}
	
//	public void executeController() {
//		iniState = updateExecutor(executor, inputs, in, input_list, trace, state, iniState);
//	}
	
	
	
	public static void main(String args[]) throws Exception {

		Map<String, String> inputs = new HashMap<>();
		Map<Integer, Map<String, String>> trace = new HashMap<>();
		
        // Instantiate a new controller executor
		ControllerExecutor real_executor = new ControllerExecutor(new BasicJitController(), "real_spec/out/");
		ControllerExecutor incomplete_executor = new ControllerExecutor(new BasicJitController(), "incomplete_spec/out/");
		// possible to adjust using CustomControllerExecutor2
		
		Set<String> input_list = real_executor.getEnvVars().keySet();
		Set<String> output_list = real_executor.getSysVars().keySet();
		
		boolean real_state_initial = true, inc_state_initial = true;
		int state = 0;
//		int count = 0;
		while (true) {
			inputs.clear();
			
			// Get all inputs from command line
			addInputs(inputs, input_list);
			
			// execute controller
			real_state_initial = updateExecutor(real_executor, inputs, input_list, trace, state, real_state_initial, true, 0);

			// Print all controller outputs
			for(String output : output_list) {
				System.out.println(output + ": " + real_executor.getCurrValue(output));
			}			
			
			// create trace for current state
			Map<String, String> state_trace = real_executor.getCurrOutputs();
			state_trace.putAll(real_executor.getCurrInputs());
			
			// Save trace
			trace.put(state, state_trace);
			state++;
			
			//send inputs to second controller
			inc_state_initial = updateExecutor(incomplete_executor, inputs, input_list, trace, state, inc_state_initial, false, 0);	
			if(inc_state_initial) {
				return;
			}
//			count++;
		}
	}

	private static void addInputs(Map<String, String> inputs, Set<String> input_list) {
//		boolean same_inputs = true;
		for(String input : input_list) {
			boolean bool_input = rd.nextBoolean();
//			if(inputs.get(input) != null){
//				if(bool_input != Boolean.parseBoolean(inputs.get(input))) {
//					same_inputs = false;
//				}
//			}
			inputs.put(input, Boolean.toString(bool_input));
			System.out.println(input + ":" + Boolean.toString(bool_input));
		}
//		if(same_inputs) {
//			addInputs(inputs, input_list);
//		}
	}

	private static boolean updateExecutor(ControllerExecutor executor, Map<String, String> inputs, 
			Set<String> input_list, Map<Integer, Map<String, String>> trace, Integer state, boolean iniState, boolean is_real, int count) {	
		try {
			if(iniState) {
				executor.initState(inputs);
			} else {
				executor.updateState(inputs);
			}
		} catch(Exception e) {
			//trace.put(state, inputs);
			//Save environment assumption violating trace to file:
//			writeTraceToCSV(trace, "trace_output.csv");
			//writeTraceViolation(trace,"trace_violation.txt");
			
			System.out.println(e.getMessage());
//			System.out.println("-- Please repeat inputs.");
			if(count > 20) {
				return false;
			}
			if(is_real) {
				inputs.clear();
				addInputs(inputs, input_list);
				updateExecutor(executor, inputs, input_list, trace, state, iniState, is_real, count + 1);
			} else {
//				trace.put(state, inputs);
				//Save environment assumption violating trace to file:
				writeTraceToCSV(trace, "trace_output.csv");
				//writeTraceViolation(trace,"trace_violation.txt");
				//run_procedure
				System.out.println("run procedure");
				return true;
			}
		}
		return false;
	}

	private static void writeTraceViolation(Map<Integer, Map<String, String>> trace, String file_name) {
		String txt = "Trace Type: Counterexample\n";
		for(Integer key : trace.keySet()){
			double state = (key + 11.0) / 10.0;
			txt += "  -> State: " + state + " <-\n";
			Map<String, String> variables = trace.get(key);
			for(String variable : variables.keySet()){
				String value = variables.get(variable);
				txt += "    " + variable + " = " + value.toUpperCase() + '\n';
			}
		}
		txt += "  -- Loop starts here\nEnd";
		writeString(file_name, txt);
	}

	private static void writeString(String file_name, String txt) {
		File outputFile = new File(file_name);
		try (PrintWriter pw = new PrintWriter(outputFile)) {
			pw.write(txt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void writeTraceToCSV(Map<Integer, Map<String, String>> trace, String file_name) {
		String csv = "State,Variable,Value,\n";
		for(Integer key : trace.keySet()) {
			for(String subKey : trace.get(key).keySet()) {
				String value = trace.get(key).get(subKey);
				csv += "S" + key.toString() + "," + subKey + "," + value + ",\n";
			}
		}
		writeString(file_name,csv);
	}

	private static boolean getInput(Scanner in, String name) {
		String line;
		System.out.println("Is " + name + " present? (Y/n)");
		line = in.nextLine();
		boolean variable = !"n".equals(line);
		return variable;
	}
}
