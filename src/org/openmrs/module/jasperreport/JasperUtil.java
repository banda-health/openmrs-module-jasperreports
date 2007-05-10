/**
 * 
 */
package org.openmrs.module.jasperreport;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * @author Simon
 * 
 */
public class JasperUtil {

	private static Log log = LogFactory.getLog(JasperUtil.class);
	static AdministrationService as = Context.getAdministrationService();

	/**
	 * This method finds all the report files that have not been compiled and
	 * compiles them.
	 */
	public static void buildNew() throws IOException {
		as = Context.getAdministrationService();
		String reportDirPath = as.getGlobalProperty(
				"jasperReport.reportDirectory", "");

		File reportDir = new File(reportDirPath);

		if (!reportDir.isDirectory())
			throw new IOException(
					"Reports directory does not exist or is not a directroy");

		File[] jrxml = reportDir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName().toLowerCase();
				return name.endsWith("jrxml");
			}
		});

		for (File file : jrxml) {
			String name = file.getName();
			int i = name.indexOf(".");
			name = name.substring(0, i) + ".jasper";
			log.debug("Looking for file: " + name);
			if (!OpenmrsUtil.folderContains(reportDir, name))
				compileReport(file);
		}
	}

	/**
	 * Given a jrxml file this method will delete jasper files of the same name
	 * and then compile the jrxml file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void rebuildJRXML(File file) throws IOException {
		if (!file.getName().endsWith("jrxml"))
			throw new ModuleException("Only 'jrxml' files can be rebuild.");

		as = Context.getAdministrationService();
		String reportDirPath = as.getGlobalProperty(
				"jasperReport.reportDirectory", "");

		File reportDir = new File(reportDirPath);

		if (!reportDir.isDirectory())
			throw new IOException(
					"Reports directory does not exist or is not a directroy");

		String fileName = file.getCanonicalPath();
		fileName = fileName.replace("jrxml", "jasper");
		File jasper = new File(fileName);
		if (jasper.exists())
			jasper.delete();

		compileReport(file);

	}

	/**
	 * @throws JRException
	 */
	public static void compileReport(File report) {
		log.debug("Attempting to compile " + report.getAbsolutePath());
		if (!report.getName().endsWith("jrxml"))
			throw new ModuleException("Only 'jrxml' files can be compiled.");
		try {
			JasperDesign design = JRXmlLoader.load(report.getAbsolutePath());
			JasperCompileManager.compileReportToFile(design, report
					.getAbsolutePath().replace("jrxml", "jasper"));
		} catch (JRException e) {
			log.error("Error compiling JasperReport", e);
			ModuleException me = new ModuleException("Could not compile report: \n" + e.getMessage());
			me.initCause(e);
			throw me;
		}
	}

	/**
	 * Deletes the contents of a directroy
	 * 
	 * @param reportDir
	 */
	public static void deleteDirContents(File dir) throws IOException {
		if (!dir.isDirectory())
			throw new IOException(dir.getAbsolutePath() + " is not a directroy");

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	/**
	 * Extracts a JAR/ZIP to the given destination directory
	 * 
	 * @param archive
	 *            file to be extracted
	 * @param destinationDir
	 * @throws IOException
	 */
	public static void extractArchive(File archive, File destinationDir)
			throws IOException {
		JarFile jar = new JarFile(archive);
		java.util.Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry file = entries.nextElement();
			java.io.File f = new java.io.File(destinationDir
					+ java.io.File.separator + file.getName());
			if (file.isDirectory()) { // if its a directory, create it
				f.mkdir();
				continue;
			}
			java.io.InputStream is = jar.getInputStream(file); // get the input
			// stream
			java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			while (is.available() > 0) { // write contents of 'is' to 'fos'
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
	}

	/**
	 * Create a temporary directory with the given prefix and a random suffix
	 * 
	 * @param prefix
	 * @return New temp directory pointer
	 * @throws IOException
	 */
	public static File createTempDirectory(String prefix) throws IOException {
		String dirname = System.getProperty("java.io.tmpdir");
		if (dirname == null)
			throw new IOException("Cannot determine system temporary directory");

		File directory = new File(dirname);
		if (!directory.exists())
			throw new IOException("System temporary directory "
					+ directory.getName() + " does not exist.");
		if (!directory.isDirectory())
			throw new IOException("System temporary directory "
					+ directory.getName() + " is not really a directory.");

		File tempDir;
		do {
			String filename = prefix + System.currentTimeMillis();
			tempDir = new File(directory, filename);
		} while (tempDir.exists());

		if (!tempDir.mkdirs())
			throw new IOException("Could not create temporary directory '"
					+ tempDir.getAbsolutePath() + "'");
		if (log.isDebugEnabled())
			log.debug("Successfully created temporary directory: "
					+ tempDir.getAbsolutePath());

		tempDir.deleteOnExit();
		return tempDir;
	}

	/**
	 * @param reportId
	 * @return File report archive
	 */
	public static File getReportArchive(String reportId) {
		String reportDirPath = as.getGlobalProperty(
				"jasperReport.reportDirectory", "");

		File archive = new File(reportDirPath + java.io.File.separator
				+ reportId + ".zip");
		return archive;
	}

	/**
	 * Compiles all the JRXML files for a report.
	 * 
	 * @param report
	 *            whose files to compile
	 * @throws IOException
	 *             if report directory does not exist
	 */
	public static void compileReportFiles(JasperReport report)
			throws IOException {
		String reportDirPath = as.getGlobalProperty(
				"jasperReport.reportDirectory", "");

		File reportDir = new File(reportDirPath + File.separator
				+ report.getReportId());
		if (!reportDir.isDirectory())
			throw new IOException(reportDir.getAbsolutePath()
					+ " does not exist or is not a directroy.");

		File[] files = reportDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith("jrxml"))
				rebuildJRXML(file);
		}

	}

	/**
	 * Attempts to parse a String into an object whose class is specified by the
	 * passed ReportParameter
	 * 
	 * @param param
	 * @param passedParam
	 * @return
	 */
	public static Object parse(ReportParameter param, String passedParam)
			throws ParseException {
		return parse(param.getValueClass(), passedParam);
	}
	
	/**
	 * Attempts to parse a String into an object whose class is specified by the
	 * passed ReportParameter
	 * 
	 * @param param
	 * @param passedParam
	 * @return
	 */
	public static Object parse(Class<?> clazz, String passedParam)
			throws ParseException, NumberFormatException{
		SimpleDateFormat dateFormat = new SimpleDateFormat(OpenmrsConstants
				.OPENMRS_LOCALE_DATE_PATTERNS().get(
						Context.getLocale().toString().toLowerCase()), Context
				.getLocale());

		if (clazz == java.util.Date.class) {
			return dateFormat.parse(passedParam);
		} else if (clazz == java.lang.Integer.class)
			return Integer.valueOf(passedParam);
		else
			return passedParam;
	}
}
