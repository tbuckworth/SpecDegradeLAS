package jitinteractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;

public class JitControllerInteractorCmd {

	public static void main(String args[]) throws Exception {

		Map<String, String> inputs = new HashMap<>();
		Map<Integer, Map<String, String>> trace = new HashMap<>();
        Scanner in = new Scanner(System.in);
        
		// Instantiate a new controller executor
		ControllerExecutor executor = new ControllerExecutor(new BasicJitController(), "out/");

		Set<String> input_list = executor.getEnvVars().keySet();
		Set<String> output_list = executor.getSysVars().keySet();
		
		boolean iniState = true;
		int state = 0;
		while (true) {
			inputs.clear();
			
			// Get all inputs from command line
			addInputs(inputs, in, input_list);
			
			// execute controller
			iniState = updateExecutor(executor, inputs, in, input_list, trace, state, iniState);

			// Print all controller outputs
			for(String output : output_list) {
				System.out.println(output + ": " + executor.getCurrValue(output));
			}			
			
			// create trace for current state
			Map<String, String> state_trace = executor.getCurrOutputs();
			state_trace.putAll(executor.getCurrInputs());
			
			// Save trace
			trace.put(state, state_trace);
			state++;
		}
	}

	private static void addInputs(Map<String, String> inputs, Scanner in, Set<String> input_list) {
		for(String input : input_list) {
			boolean bool_input = getInput(in, input);
			inputs.put(input, Boolean.toString(bool_input));
		}
	}

	private static boolean updateExecutor(ControllerExecutor executor, Map<String, String> inputs, 
			Scanner in, Set<String> input_list, Map<Integer, Map<String, String>> trace, Integer state, boolean iniState) {	
		try {
			if(iniState) {
				executor.initState(inputs);
			} else {
				executor.updateState(inputs);
			}
		} catch(Exception e) {
			trace.put(state, inputs);
			//Save environment assumption violating trace to file:
			writeTrace(trace, "trace_output.csv");
			
			System.out.println(e.getMessage());
			System.out.println("-- Please repeat inputs.");
			
			addInputs(inputs, in, input_list);
			updateExecutor(executor, inputs, in, input_list, trace, state, iniState);
		}
		return false;
	}

	private static void writeTrace(Map<Integer, Map<String, String>> trace, String file_name) {
		String csv = "State,Variable,Value,\n";
		for(Integer key : trace.keySet()) {
			for(String subKey : trace.get(key).keySet()) {
				String value = trace.get(key).get(subKey);
				csv += "S" + key.toString() + "," + subKey + "," + value + ",\n";
			}
		}
		File outputFile = new File(file_name);
		try (PrintWriter pw = new PrintWriter(outputFile)) {
			pw.write(csv);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static boolean getInput(Scanner in, String name) {
		String line;
		System.out.println("Is " + name + " present? (Y/n)");
		line = in.nextLine();
		boolean variable = !"n".equals(line);
		return variable;
	}
}
