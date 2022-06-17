package spectrace;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TraceConverter {
	private final String input_file;
	private final List<String[]> rows = new ArrayList<>();

	public TraceConverter(String input_file) {
		this.input_file = input_file;
	}

	public void convert() {
		try{
			readCSV();
			writeTxt();
		} catch(IOException e){
			e.printStackTrace();
			return;
		}

	}

	private void writeTxt() {
		String txt = "";
		for(String[] row : rows){
			return;
		}
	}

	private void readCSV() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(input_file);
		InputStreamReader reader = new InputStreamReader(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String row = null;
		while ((row = bufferedReader.readLine()) != null) {
			String[] dataLine = row.split(",");
			rows.add(dataLine);
		}
	}

}
