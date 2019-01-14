package cafe.deadbeef._2e1hnk.CSLGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		int qsoCount = 0;

		if (args.length != 2) {
			throw new Exception("Incorrect number of arguments");
		}

		File adifDir = new File(args[0]);
		if (!adifDir.exists() || !adifDir.isDirectory()) {
			throw new Exception("ADIF directory incorrect");
		}

		File cslFile = new File(args[1]);
		Map<String, ContactDetails> csl = new HashMap<String, ContactDetails>();

		if (cslFile.exists()) {
			System.out.println("CSL file already exists, it will be appended to!");

			// Populate csl from existing file
			BufferedReader cslFileRecords = new BufferedReader(new FileReader(cslFile));
			String record = null;
			while (record == cslFileRecords.readLine()) {
				if (record.startsWith("#")) {
					continue;
				}
				ContactDetails contact = new ContactDetails(record);
				csl.put(contact.getCallsign(), contact);
			}
			cslFileRecords.close();
		}

		AdiReader adifReader = new AdiReader();
		adifReader.setQuirksMode(true);

		File[] directoryListing = adifDir.listFiles(new FilenameFilter() {
			public boolean accept(File adifDir, String name) {
				return name.endsWith(".adi");
			}
		});
		if (directoryListing != null) {
			for (File adifFile : directoryListing) {
				BufferedReader buffInput = new BufferedReader(new FileReader(adifFile));
				try {
					Optional<Adif3> adif = adifReader.read(buffInput);
					System.out.println("From file: " + adifFile.getName());
					for (Adif3Record record : adif.get().getRecords()) {
						qsoCount++;
						try {
							if (csl.containsKey(record.getCall())) {
								// Increment the count
								csl.get(record.getCall()).incrementCount();
								System.out.println("Updated " + csl.get(record.getCall()).toString());
							} else {
								if (!record.getGridsquare().equals("null")) {
									ContactDetails details = new ContactDetails(record.getCall(),
											record.getGridsquare(), "", 1);
									csl.put(details.getCallsign(), details);
									System.out.println(
											"Added " + record.getCall() + "," + record.getGridsquare() + ",,1");
								} else {
									System.out.println("Skipping " + record.getCall() + " (no grid square)");
								}
							}
						} catch (NumberFormatException e) {
							System.out.println("Error found, continuing...");
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					System.out.println("Error found, continuing...");
					e.printStackTrace();
				}
			}
			
			// Write out CSL file
			FileWriter fileWriter = new FileWriter(cslFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			for (String callsign : csl.keySet()) {
				ContactDetails details = csl.get(callsign);
				printWriter.printf("%s,%s,%s,%d\n", details.getCallsign(), details.getLocator(), details.getExchange(),
						details.getCount());
			}

			printWriter.close();

			System.out.println(
					"Processed " + qsoCount + " contacts and generated CSL file with " + csl.size() + " entries.");

		} else {
			throw new Exception("Problem processing files in ADIF directory.");
		}

	}
}
