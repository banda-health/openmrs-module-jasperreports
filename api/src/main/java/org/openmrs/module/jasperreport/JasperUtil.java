/**
 * 
 */
package org.openmrs.module.jasperreport;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Simon
 * 
 */
public class JasperUtil {

	private static final String JASPER_REPORTS = "jasperReports";
	private static Log log = LogFactory.getLog(JasperUtil.class);

	public static String getModuleMessage(MessageSourceAccessor messageSource, String codeFragment) {
		if (!org.apache.commons.lang3.StringUtils.startsWith(codeFragment, ".")) {
			codeFragment = "." + codeFragment;
		}

		return messageSource.getMessage(JasperReportConstants.MODULE_ID + codeFragment);
	}

	/**
	 * This method finds all the report files that have not been compiled and
	 * compiles them.
	 */
	public static void buildNew() throws IOException {
		String reportDirPath = getReportDirPath();

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

		String reportDirPath = getReportDirPath();

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
			ModuleException me = new ModuleException(
					"Could not compile report: \n" + e.getMessage());
			me.initCause(e);
			throw me;
		}
	}

	/**
	 * Deletes the contents of a directroy
	 * 
	 * @param reportDir
	 */
	public static boolean deleteDir(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDir(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}

	public static void deleteGeneratedReport(String fileName)
			throws IOException {
		String reportDirPath = getReportDirPath();

		File report = new File(reportDirPath + File.separator
				+ JasperReportConstants.GENERATED_REPORT_DIR_NAME
				+ File.separator + fileName);
		if (!report.exists())
			throw new IOException(report.getAbsolutePath() + " does not exist.");

		report.delete();
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
		String reportDirPath = getReportDirPath();

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
		String reportDirPath = getReportDirPath();

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
		return parse(param.getInterfaceClass(), passedParam);
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
			throws ParseException, NumberFormatException {
		DateFormat dateFormat = new SimpleDateFormat(
				JasperReportConstants.DATE_FORMAT);

		log.debug("Parsing: " + clazz.getName() + " value: " + passedParam);

		if (clazz == java.lang.String.class) {
			return passedParam;
		} else if (passedParam == null || passedParam.length() == 0) {
			return null;
		} else if (clazz == java.util.Date.class) {
			return dateFormat.parse(passedParam);
		} else if (clazz == java.lang.Boolean.class) {
			return passedParam.equalsIgnoreCase("true");
		} else if (clazz == Integer.class) {
			return Integer.valueOf(passedParam);
		} else if (clazz == Concept.class) {
			return getConcept(Integer.valueOf(passedParam));
		} else if (clazz == Location.class) {
			return getLocation(Integer.valueOf(passedParam));
		} else
			throw new ParseException("unknown parameter class: "
					+ clazz.getName(), -1);
	}

	/**
	 * Searches the report directory for already generated reports
	 * and returns a list of these.
	 * 
	 * Also returns reports that are currently being generated (threads).
	 * 
	 * @return
	 * @throws IOException
	 */
	public static List<GeneratedReport> getGeneratedReports()
			throws IOException {
		String reportDirPath = getReportDirPath();

		List<GeneratedReport> reports = new Vector<GeneratedReport>();

		File jasperDirectory = new File(reportDirPath);
		File reportDir = new File(reportDirPath + File.separator
				+ JasperReportConstants.GENERATED_REPORT_DIR_NAME);

		if (!jasperDirectory.isDirectory() || !reportDir.isDirectory()) {
			if (!jasperDirectory.isDirectory()) {
				log.warn(jasperDirectory.getPath()
						+ "does not exist or is not a directory.");
				log.warn(jasperDirectory.getPath() + ": creating new directory.");
				jasperDirectory.mkdir();
			}
			if (!reportDir.isDirectory()) {
				log.warn(reportDir.getPath()
						+ "does not exist or is not a directory.");
				log.warn(reportDir.getPath() + ": creating new directory.");
				reportDir.mkdir();
			}
		}

		File[] files = reportDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".pdf") || name.endsWith(".xlsx"));
			}
		});
		
		if (files != null) {
			for (int i = files.length - 1; i >= 0; i--) {
				reports.add(new GeneratedReport(files[i].getName(), false, false));
			}
		}

		return reports;
	}

	/**
	 * Gets a list of threads that are busy generating reports.
	 * 
	 * @param reports
	 * @return
	 */
	public static List<GeneratedReport> getGeneratingReports() {
		List<GeneratedReport> reports = new Vector<GeneratedReport>();

		List<Thread> threads = findThreadsByName(null, 0, "report_");
		for (Thread thread : threads) {
			reports.add(new GeneratedReport(thread.getName().substring(7),
					false, true));
		}

		return reports;
	}

	// This method recursively visits all thread groups under `group'.
	public static List<Thread> findThreadsByName(ThreadGroup group, int level,
			String name) {

		if (group == null) {
			group = Thread.currentThread().getThreadGroup().getParent();
			while (group.getParent() != null) {
				group = group.getParent();
			}
		}

		List<Thread> threadList = new Vector<Thread>();

		// Get threads in `group'
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);

		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			if (thread.getName().startsWith(name)) {
				threadList.add(thread);
			}
		}

		// Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);

		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++) {
			threadList.addAll(findThreadsByName(groups[i], level + 1, name));
		}

		return threadList;

	}

	public static String getReportDirPath() {
		/*
		 * Use OpenMRS application data directory
		 * http://dev.openmrs.org/ticket/2000
		 */
		// Context.openSession();
		// AdministrationService as = Context.getAdministrationService();
		// String reportDirPath = as.getGlobalProperty(
		// "@MODULE_ID@.reportDirectory", "");
		// Context.closeSession();

		String dataDir = OpenmrsUtil.getApplicationDataDirectory();
		if (!StringUtils.endsWithIgnoreCase(dataDir, File.separator)) {
			dataDir += File.separator;
		}

		return dataDir + JASPER_REPORTS;
	}

	public static Concept getConcept(Integer id) {
		//Context.openSession();
		ConceptService as = Context.getConceptService();
		Concept concept = as.getConcept(id);
		//Context.closeSession();
		return concept;
	}

	public static Location getLocation(Integer id) {
		//Context.openSession();
		LocationService as = Context.getLocationService();
		Location location = as.getLocation(id);
		//Context.closeSession();
		return location;
	}

	public static Locale getLocale() {
		//Context.openSession();
		Locale locale = Context.getLocale();
		//Context.closeSession();
		return locale;
	}

	public static SimpleDateFormat getDateFormat() {
		//Context.openSession();
		SimpleDateFormat format = Context.getDateFormat();
		//Context.closeSession();
		return format;
	}
}
