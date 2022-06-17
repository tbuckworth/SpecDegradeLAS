package spectrace;

import static org.junit.Assert.*;
import java.io.File;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;


public class TraceConverterTest {

//	@Rule
//	public JUnitRuleMockery context = new JUnitRuleMockery();
	String input_file = "trace_output.csv";

	TraceConverter traceConverter = new TraceConverter(input_file);

	@Test
	public void readsCSV() {
//		context.checking(new Expectations(){{
//			exactly(1).of(input_file)
//		}});
		traceConverter.convert();

	}
	
	@Test
	public void convertsToASP() {
		
	}

}
