package writeCTD;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import tau.smlab.syntech.ctd.CTDExecutor;
import tau.smlab.syntech.ctd.CTDExecutorContinuing;
import tau.smlab.syntech.ctd.CTDUILogic;

public class CTDWriter {
	
	public static void main(String[] args) throws Exception {
		// This example computes and executes a test suite for MonkeyWithBanana.
		// Make sure before running that you have already generated a symbolic controller and a .properties file. 
		
		// This will generate the test suite. You can remove this if you already generated one.
		CTDUILogic.compute(
				"CTD/TrafficE2.spectra", /* The spec file */
				"CTD/TrafficE2.spectra.ctd.properties", /* The properties file for configuring the requested coverage */
				"CTD/out/", /* The "out" path containing the symbolic controller.
						The test suite will also be written here in a folder named ctd.out */
				false); /* Should the results be in a new time-stamped folder?*/  
		
		// This will execute the test suite using the restarting or continuing test executor.
		//executor = new CTDExecutorRestarting(
		CTDExecutor executor = new CTDExecutorContinuing(
				"CTD/out/", /* Folder containing symbolic controller */
				"CTD/out/ctd.out/TrafficE2.spectra_tree.bin",
				/* .spectra_tree.bin test suite file */
				"CTD/out/ctd.out/", /* Folder containing the test suite BDDs */
				false); /* Should we turn on auto reordering? */
		
		Set<String> input_list = executor.getEnvVars().keySet();
		Set<String> output_list = executor.getSysVars().keySet();
		Map<Integer, Map<String, String>> trace = new HashMap<>();

		while (executor.hasNext()) {
			
			executor.updateState();
			// Print all controller inputs and outputs
			for(String input : input_list) {
				System.out.println(input + ": " + executor.getCurrValue(input));
			}
			for(String output : output_list) {
				System.out.println(output + ": " + executor.getCurrValue(output));
			}
		}
	}
}
