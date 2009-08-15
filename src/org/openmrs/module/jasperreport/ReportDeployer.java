/**
 * 
 */
package org.openmrs.module.jasperreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

/**
 * @author Simon
 * 
 */
public class ReportDeployer {

	private static Log log = LogFactory.getLog(ReportDeployer.class);

	/**
	 * Public access method for uploading a report archive to a temporary
	 * directory and then extracting it to a named subdirectory in the reports
	 * directory. The top level report is also parsed and the required
	 * parameters extracted.
	 * 
	 * @param inputStream
	 *            inputStream from which archive may be read
	 * @param JasperReport
	 *            the JasperReport with which the given archive is to be
	 *            associated
	 */
	public static JasperReport uploadReport(InputStream inputStream,
			JasperReport report) throws IOException {

		String reportDirPath = JasperUtil.getReportDirPath();

		File reportDir = new File(reportDirPath);

		// create file on file system to hold the uploaded file
		String reportId = report.getReportId().toString();
		File reportArchive = JasperUtil.getReportArchive(reportId);
		boolean reload = false;
		if (reportArchive.exists()) {
			File dest = new File(reportDirPath
					+ File.separator
					+ reportId
					+ "-backup-"
					+ new SimpleDateFormat("dd-MMM-yyyy-HHmmss", JasperUtil
							.getLocale()).format(new Date()) + ".zip");
			reportArchive.renameTo(dest);
			reload = true;
		}

		reportArchive = new File(reportDir, report.getReportId() + ".zip");

		// copy the uploaded file over to the temp file system file
		OpenmrsUtil.copyFile(inputStream, new FileOutputStream(reportArchive));

		File topLevelReport = extractReportArchive(reportArchive, report,
				reload);

		if (reload) {
			Set<ReportParameter> newParams = getParametersFromFile(topLevelReport);
			mergerParameters(report, newParams);
		} else
			report.setParameters(getParametersFromFile(topLevelReport));

		JasperUtil.compileReportFiles(report);

		return report;
	}

	/**
	 * @param report
	 * @param newParams
	 */
	public static void mergerParameters(JasperReport report,
			Set<ReportParameter> newParams) {
		// copy parameters (to avoid concurrent modification exception)
		Set<ReportParameter> oldParams = new HashSet<ReportParameter>();
		oldParams.addAll(report.getParameters());

		// add new parameters
		for (ReportParameter newp : newParams) {
			if (!report.getParameters().contains(newp))
				report.getParameters().add(newp);
		}

		// remove old parameters
		for (ReportParameter oldp : oldParams) {
			if (!newParams.contains(oldp))
				report.getParameters().remove(oldp);
		}
	}

	/**
	 * @param absolutePath
	 * @return
	 */
	private static File extractReportArchive(File archive, JasperReport report,
			boolean reload) throws IOException {

		if (!archive.exists())
			throw new IOException("Could not find file: "
					+ archive.getAbsolutePath());

		File reportDir = new File(archive.getParent() + java.io.File.separator
				+ report.getReportId());
		log.debug("Report dir: " + reportDir.getAbsolutePath());

		if (reload) {
			JasperUtil.deleteDir(reportDir);
		} else {
			if (reportDir.exists()) {
				throw new IOException("Can not extract archive to "
						+ reportDir.getAbsolutePath()
						+ " , directory already exists.");
			}
		}
		reportDir.mkdir();

		JasperUtil.extractArchive(archive, reportDir);
		return new File(reportDir.getAbsoluteFile() + java.io.File.separator
				+ report.getFileName());
	}

	/**
	 * 
	 * Given a jrxml file this method compiles a list of all the parameters.
	 * 
	 * @param file
	 * @return
	 * @throws JRException
	 */
	@SuppressWarnings("unchecked")
	public static Set<ReportParameter> getParametersFromFile(File file)
			throws IOException {
		JasperDesign jasperDesign;
		try {
			jasperDesign = JRXmlLoader.load(file);
		} catch (JRException e) {
			IOException e1 = new IOException("Error loading JRXML: "
					+ file.getAbsolutePath() + "\n" + e.getMessage());
			;
			e1.initCause(e);
			throw e1;
		}
		List<JRParameter> list = jasperDesign.getParametersList();

		Set<ReportParameter> params = new HashSet<ReportParameter>();
		ReportParameter param;
		JRParameter jparam;
		for (Object object : list) {
			jparam = (JRParameter) object;
			if (!jparam.isSystemDefined()
					&& !jparam.getName().equals("connection")
					&& !jparam.getName().equals("SUBREPORT_DIR")) {
				param = new ReportParameter();
				param.setName(jparam.getName());
				param.setValueClass(jparam.getValueClass());
				param.setVisible(true);
				params.add(param);
			}

		}

		log.debug("Getting parameters from file " + file.getAbsolutePath());

		return params;

	}

	/**
	 * @param inputStream
	 * @return
	 */
	public static JasperReport uploadReport(InputStream inputStream)
			throws IOException {
		uploadReport(inputStream, null);
		return null;
	}

	/**
	 * Refreshes the report parameters from the reports top level file.
	 * 
	 * @param report
	 * @throws IOException
	 *             if the file can not be found
	 */
	public static void refreshParameters(JasperReport report)
			throws IOException {
		String reportDirPath = JasperUtil.getReportDirPath();

		File reportFile = new File(reportDirPath + File.separator
				+ report.getReportId() + File.separator + report.getFileName());
		if (!reportFile.exists())
			throw new IOException(reportFile.getAbsolutePath() + " not found.");

		log.info("Refreshing parameters for report " + report.getReportId()
				+ " from file: " + reportFile.getAbsolutePath());
		Set<ReportParameter> newParams = getParametersFromFile(reportFile);
		mergerParameters(report, newParams);

	}

	/**
	 * @param report
	 */
	public static void deleteReport(JasperReport report) throws IOException {
		String reportDirPath = JasperUtil.getReportDirPath();

		File toDelete = new File(reportDirPath + java.io.File.separator
				+ report.getReportId() + ".zip");
		toDelete.delete();

		toDelete = new File(reportDirPath + java.io.File.separator
				+ report.getReportId());
		JasperUtil.deleteDir(toDelete);
	}
}
