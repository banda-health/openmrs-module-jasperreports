/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.jasperreport;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is responsible for generating reports.
 * 
 * @author Simon Kelly
 * @version 1.0
 * 
 */
public class ReportGenerator {
	private static Log log = LogFactory.getLog(ReportGenerator.class);

	public synchronized static File generate(JasperReport report,
			HashMap<String, Object> map, String format) throws IOException {
		return generate(report, map, true, false, format);
	}

	public synchronized static void generateHtmlAndWriteToResponse(JasperReport report,
			HashMap<String, Object> map, HttpServletResponse response) throws IOException {

		FileInputStream fileInputStream = getReportInputStream(report);
		
		String url = Context.getRuntimeProperties().getProperty(
				"connection.url", null);

		Connection conn;
		try {
			conn = connect(url);
		} catch (SQLException e) {
			log.error("Error connecting to DB.", e);
			return;
		}

		String reportDirPath = JasperUtil.getReportDirPath();
		map.put("connection", conn);
		map.put("SUBREPORT_DIR", reportDirPath + File.separator
				+ report.getReportId() + File.separator);

		log.debug("Report parameter map: " + map);

		JasperPrint jasperPrint = null;
		try {
			// generate the report and write it to file
			jasperPrint = JasperFillManager.fillReport(fileInputStream, map, conn);
			JRHtmlExporter exporter = new JRHtmlExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, response.getWriter());
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);			
			exporter.exportReport();
		} catch (JRException e) {
			log.error("Error generating report", e);
		} finally{
			try {
				if (!conn.isClosed()){
					conn.close();
				}
			} catch (SQLException e) {
				log.error("Exception closing report connection.", e);
			}
		}
	}

	/**
	 * @param report
	 * @param map
	 * @return
	 */
	public synchronized static File generate(JasperReport report,
			HashMap<String, Object> map, boolean appendDate, boolean pdfAutoPrint, String format) throws IOException {

		String reportDirPath = JasperUtil.getReportDirPath();

		String exportPath = reportDirPath
				+ File.separator
				+ JasperReportConstants.GENERATED_REPORT_DIR_NAME
				+ File.separator
				+ report.getName().replaceAll("\\W", "")
				+ (appendDate ? new SimpleDateFormat("dd-MM-yyyy-HH-mm", JasperUtil.getLocale()).format(new Date()) : "");

		FileInputStream fileInputStream = getReportInputStream(report);

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

		log.debug("Report parameter map: " + map);

		JasperPrint jasperPrint = null;
		try {
			// generate the report and write it to file
			jasperPrint = JasperFillManager.fillReport(fileInputStream, map,
					conn);

			if(StringUtils.equalsIgnoreCase(format, "pdf")){
				exportPath += ".pdf";
				exportPDFFormat(jasperPrint, exportPath, pdfAutoPrint);
			}
			else if(StringUtils.equalsIgnoreCase(format, "excel")){
				exportPath += ".xlsx";
				exportExcelFormat(jasperPrint, exportPath);
			}
			else{
				log.error("Unknown format " + format);
			}

		} catch (JRException e) {
			log.error("Error generating report", e);
		} finally{
			try {
				if (!conn.isClosed()){
					conn.close();
				}
			} catch (SQLException e) {
				log.error("Exception closing report connection.", e);
			}
		}

		return new File(exportPath);
	}

	/**
	 * Generates a PDF report
	 * @param jasperPrint
	 * @param exportPath
	 * @param pdfAutoPrint
	 * @throws JRException
     */
	private static void exportPDFFormat(JasperPrint jasperPrint, String exportPath, boolean pdfAutoPrint) throws JRException{
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportPath);
		if (pdfAutoPrint)
				exporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT, "this.print();");
			exporter.exportReport();

	}

	/**
	 * Generates an Excel report
	 * @param jasperPrint
	 * @param exportPath
	 * @throws JRException
     */
	private static void exportExcelFormat(JasperPrint jasperPrint, String exportPath) throws JRException{
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportPath);
		exporter.exportReport();
	}
	
	private static FileInputStream getReportInputStream(JasperReport report) throws FileNotFoundException {
		String reportDirPath = JasperUtil.getReportDirPath();
	
		// get report file and compile it if necessary
		String filename = report.getFileName();
		File reportFile = new File(reportDirPath + File.separator
				+ report.getReportId() + File.separator
				+ filename.replace("jrxml", "jasper"));
	
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(reportFile);
		} catch (FileNotFoundException e) {
			log.error("Could not find report file: "
					+ reportFile.getAbsolutePath(), e);
			throw e;
		}
		return fileInputStream;
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
		String username = Context.getRuntimeProperties().getProperty(
		"connection.username");
		String password = Context.getRuntimeProperties().getProperty(
		"connection.password");
		log.debug("connecting to DATABASE: " + OpenmrsConstants.DATABASE_NAME
				+ " USERNAME: " + username + " URL: " + url);
		return DriverManager.getConnection(url, username, password);
	}
}
