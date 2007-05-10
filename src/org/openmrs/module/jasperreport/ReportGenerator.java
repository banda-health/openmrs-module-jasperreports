/**
 * 
 */
package org.openmrs.module.jasperreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.pepfar.PepfarUtil;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class is responsible for generating reports.
 * 
 * @author Simon Kelly
 * @version 1.0
 * 
 */
public class ReportGenerator {
	private static Log log = LogFactory.getLog(ReportGenerator.class);

	static AdministrationService as = Context.getAdministrationService();
	/**
	 * @param report
	 * @param map
	 * @return
	 */
	public static File generate(JasperReport report, HashMap<String, Object> map)
			throws IOException {

		String reportDirPath = as.getGlobalProperty(
				"jasperReport.reportDirectory", "");

		// get report file and compile it if necessary
		String filename = report.getFileName();
		File reportFile = new File(reportDirPath + File.separator
				+ report.getReportId() + File.separator
				+ filename.replace("jrxml", "jasper"));
		String exportPath = reportDirPath
				+ File.separator
				+ report.getName().replaceAll("\\W", "")
				+ new SimpleDateFormat("dd-mm-yyyy-HH:mm", Context
						.getLocale()).format(new Date()) + ".pdf";
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(reportFile);
		} catch (FileNotFoundException e) {
			log.error("Could not find report file: "
					+ reportFile.getAbsolutePath(), e);
			throw e;
		}

		String url = Context.getRuntimeProperties().getProperty(
				"connection.url", null);
		Connection conn;
		try {
			conn = connect(url);
		} catch (SQLException e) {
			log.error("Error connecting to DB.", e);
			return null;
		}

		map.put("connection", conn);
		map.put("SUBREPORT_DIR", reportDirPath + File.separator
				+ report.getReportId() + File.separator);

		if (report.getFileName().equals(
				JasperReportConstants.PEPFAR_REPORT_NAME)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/mm/dd");
			try {
				map.put("startOfTime", df.parse("1901/01/01"));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			log.debug("Report parameter map: " + map);

			String reportDir = as.getGlobalProperty("jasperReport.reportDirectory",	"");
			File dataSource = PepfarUtil.doPepfarQuarterly(conn, map, reportDir);
			JasperPrint jasperPrint = null;
			try {
				// generate the report and write it to file
				JRCsvDataSource jcvs = new JRCsvDataSource(dataSource);
				jcvs.setUseFirstRowAsHeader(true);
				jasperPrint = JasperFillManager.fillReport(fileInputStream,
						map, jcvs);

				JasperExportManager.exportReportToPdfFile(jasperPrint,
						exportPath);
			} catch (JRException e) {
				log.error("Error generating report", e);
			}

			return new File(exportPath);
		}

		log.debug("Report parameter map: " + map);

		JasperPrint jasperPrint = null;
		try {
			// generate the report and write it to file
			jasperPrint = JasperFillManager.fillReport(fileInputStream, map,
					conn);
			JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
		} catch (JRException e) {
			log.error("Error generating report", e);
		}

		return new File(exportPath);
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection connect(String url) throws SQLException {
		// Step 1: Load the JDBC driver.
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.error("Could not find JDBC driver class.", e);
			throw (SQLException) e.fillInStackTrace();
		}

		// Step 2: Establish the connection to the database.
		return DriverManager.getConnection(url, OpenmrsConstants.DATABASE_NAME,
				Context.getRuntimeProperties().getProperty(
						"connection.password"));
	}

}
