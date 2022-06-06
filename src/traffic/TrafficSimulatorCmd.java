package traffic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;

public class TrafficSimulatorCmd {

	public static void main(String args[]) throws Exception {

		Map<String, String> inputs = new HashMap<>();
		Map<Integer,Map<String, String>> trace = new HashMap<>();
        Scanner in = new Scanner(System.in);
        
		// Instantiate a new controller executor
		ControllerExecutor executor = new ControllerExecutor(new BasicJitController(), "out/");

		List<String> input_list = Arrays.asList("carA", "carB", "emergency");
		
		boolean iniState = true;
		int state = 0;
		while (true) {
			inputs.clear();
			
			addInputs(inputs, in, input_list);
			
			// execute controller
			if (iniState) {
				executor.initState(inputs);
				iniState = false;
			} else {
				updateExecutor(executor, inputs, in, input_list, trace, state);
			}

			// read outputs
			boolean greenA = Boolean.parseBoolean(executor.getCurrValue("greenA"));
			boolean greenB = Boolean.parseBoolean(executor.getCurrValue("greenB"));
			
			if (greenA) {
				System.out.println("-- carA is allowed to go");
			} else if (greenB) {
				System.out.println("-- carB is allowed to go");
			} else {
				System.out.println("-- all cars must stop");
			}
			
			Map<String, String> output = executor.getCurrOutputs();
			for(String key : inputs.keySet()) {
				//System.out.println("Output: " + key);
				output.put(key, inputs.get(key));	
			}	
			
			//save trace
			trace.put(state, output);
			state++;
		}
	}

	private static void addInputs(Map<String, String> inputs, Scanner in, List<String> input_list) {
		for(String input : input_list) {
			boolean bool_input = getInput(in, input);
			inputs.put(input, Boolean.toString(bool_input));
		}
	}

	private static void updateExecutor(ControllerExecutor executor, Map<String, String> inputs, Scanner in, List<String> input_list, Map<Integer, Map<String, String>> trace, Integer state) {
		try {
			executor.updateState(inputs);
		} catch(Exception e) {
			trace.put(state, inputs);
			writeTrace(trace, "trace_output.csv");
			
			System.out.println(e.getMessage());
			System.out.println("-- Please repeat inputs.");
			
			addInputs(inputs, in, input_list);
			updateExecutor(executor, inputs, in, input_list, trace, state);
		}
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

//	private static void addInputs(Map<String, String> inputs, boolean carA, boolean carB) {
//		inputs.put("carA", Boolean.toString(carA));
//		inputs.put("carB", Boolean.toString(carB));
//		System.out.println(inputs);
//	}
	
	private static boolean getInput(Scanner in, String name) {
		String line;
		System.out.println("Is " + name + " present? (Y/n)");
		line = in.nextLine();
		boolean variable = !"n".equals(line);
		return variable;
	}

}
