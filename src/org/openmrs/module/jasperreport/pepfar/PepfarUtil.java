package org.openmrs.module.jasperreport.pepfar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.jasperreport.JasperReportConstants;
import org.openmrs.module.jasperreport.ReportGenerator;

public class PepfarUtil {
	
	private static Log log = LogFactory.getLog(PepfarUtil.class);

	public static File doPepfarQuarterly(Connection conn,
			HashMap<String, Object> map, String reportDir) throws IOException {
		// Create end-date object from date supplied in properties file
		// Date startDate = (Date) map.get("startQuarter");
		Date endDate = (Date) map.get("endQuarter");
		log.debug("end date " + endDate);
		List<DrugCombination> drugCombos = new ArrayList<DrugCombination>();

		try {
			Date ageCalcDate = (Date) map.get("ageCalcDate");
			
			// find all adult combos
			int minAge = (Integer) map.get("adultMinAge");
			int maxAge = (Integer) map.get("adultMaxAge");
			List<Integer> ids = getEncounters(conn, endDate, minAge, maxAge, ageCalcDate);
			drugCombos = addDrugCombos(conn, drugCombos, ids, Category.ADULT);

			// find all child combos
			minAge = (Integer) map.get("childMinAge");
			maxAge = (Integer) map.get("childMaxAge");
			ids = getEncounters(conn, endDate, minAge, maxAge, ageCalcDate);
			drugCombos = addDrugCombos(conn, drugCombos, ids, Category.PEDE);
		} catch (SQLException e) {
			log.error("Error compiling statistics from database", e);
		}

		// get totals
		int adultTotal = 0;
		int childTotal = 0;
		for (DrugCombination combination : drugCombos) {
			adultTotal += combination.getAdultCount();
			childTotal += combination.getPedeCount();
		}
		map.put("adultTotal", adultTotal);
		map.put("childTotal", childTotal);

		// Sort in order of total number of patients on a combo
		Collections.sort(drugCombos, Collections
				.reverseOrder(new DrugComboComparator()));

		String csvFileName = reportDir + File.separator
				+ JasperReportConstants.CSV_FILENAME;
		File csvFile = new File(csvFileName);
		try {
			// generate temp CSV file to be passed to the report
			FileWriter out = new FileWriter(csvFile);
			out.write("ComboName,AdultsOnCombo,PedesOnCombo\n");
			for (DrugCombination drugCombo : drugCombos) {
				out.write(drugCombo.toString() + ","
						+ drugCombo.getAdultCount() + ","
						+ drugCombo.getPedeCount() + "\n");
			}
			out.close();
		} catch (IOException e) {
			log.error("Could not write to file.", e);
		}

		return csvFile;
	}

	/**
	 * Generates a list of encounters which have drug combination observations.
	 * 
	 * @param stmt
	 * @param to
	 * @param minAge
	 * @param maxAge
	 * @return
	 * @throws SQLException
	 */
	public static List<Integer> getEncounters(Connection conn, Date endDate,
			int minAge, int maxAge,Date ageCalcDate) throws SQLException, IOException {
		String sql = "";
		String sqlPath = JasperReportConstants.SQL_FILE;
		Class<ReportGenerator> c = ReportGenerator.class;

		URL url = c.getResource(sqlPath);

		if (url == null) {
			String err = "Could not open report SQL file: " + sqlPath;
			log.error(err);
			throw new FileNotFoundException(err);
		}

		String extForm = url.toExternalForm();
		int i = extForm.indexOf(File.separator);
		extForm = extForm.substring(i);
		log.debug("Loading SQL statement from " + extForm);
		
//		trim out "jar:file:"
//		extForm = extForm.replaceFirst("jar:file:", "").replaceAll("%20", " ");
//
//		int i = extForm.indexOf("!");
//		String jarPath = extForm.substring(0, i);
//		String filePath = extForm.substring(i + 2); // skip over both the '!'
//		// and the '/'
//
//		log.debug("jarPath: " + jarPath);
//		log.debug("filePath: " + filePath);
//
//		File jar = new File(jarPath);
//		if (!jar.exists())
//			throw new IOException("Cannot find jar at: " + jar);
//
//		JarFile jarFile = new JarFile(jar);
//		JarEntry entry = jarFile.getJarEntry(filePath);
		
		try {
			sql = readStream(new FileInputStream(extForm));
		} catch (IOException e) {
			log.error("Could not read SQL from file", e);
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy/mm/dd");
		sql = sql.replaceAll("<end-date>", "'" + df.format(endDate) + "'");
		sql = sql.replaceAll("<min-age>", Integer.toString(minAge));
		sql = sql.replaceAll("<max-age>", Integer.toString(maxAge));
		sql = sql.replaceAll("<age-calc-date>", "'" + df.format(ageCalcDate) + "'");

		PreparedStatement stmt = conn.prepareStatement(sql);

		ResultSet rs = stmt.executeQuery();
		List<Integer> ids = new ArrayList<Integer>();
		while (rs.next()) {
			ids.add(rs.getInt("encounter_id"));
		}
		return ids;
	}
	

	/**
	 * @param stmt
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	private static List<DrugCombination> addDrugCombos(Connection conn,
			List<DrugCombination> regimens, List<Integer> ids, Category cat)
			throws SQLException {
		Statement stmt = conn.createStatement();
		for (Integer integer : ids) {
			DrugCombination reg = new DrugCombination();
			String query = "select o.value_coded, cn.name from obs o, concept_name cn"
					+ " where encounter_id="
					+ integer.intValue()
					+ " and o.concept_id="
					+ JasperReportConstants.DTHF_ARV_DRUG_LIST
					+ " and o.value_coded = cn.concept_id;";
			ResultSet rs = stmt.executeQuery(query);
			String name = "";
			while (rs.next()) {
				Drug d = new Drug();
				d.setId(rs.getInt(1));
				d.setName(rs.getString(2));
				reg.addDrug(d);
				name += d.getName() + " : ";
			}
			reg.setName(name.substring(0, name.length() - 3));
			switch (cat) {
				case ADULT :
					reg.incrimentAdultCount();
					break;
				case PEDE :
					reg.incrimentPedeCount();
					break;
				default :
					break;
			}

			if (!regimens.contains(reg)) {
				regimens.add(reg);
			} else {
				switch (cat) {
					case ADULT :
						regimens.get(regimens.indexOf(reg))
								.incrimentAdultCount();
						break;
					case PEDE :
						regimens.get(regimens.indexOf(reg))
								.incrimentPedeCount();
						break;
					default :
						break;
				}

			}

		}
		return regimens;
	}

	
	/**
	 * Returs a string read form an inputstream
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static String readStream(InputStream input) throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		StringBuffer fileData = new StringBuffer(1000);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	private enum Category {
		ADULT, PEDE, UNKNOWN;
	}
}
