package org.openmrs.module.jasperreport;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleException;
import org.openmrs.module.jasperreport.util.JasperReportConstants;

public class JasperReportActivator extends BaseModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting JasperReport module");

		// // set up requirements
		String reportDirPath = JasperUtil.getReportDirPath();

		File reportDir = new File(reportDirPath);
		if (!reportDir.exists())
			reportDir.mkdir();

		if (!reportDir.isDirectory())
			throw new ModuleException("Could not create report directory : "
					+ reportDirPath);

		/*
		 * Use OpenMRS application data directory
		 * http://dev.openmrs.org/ticket/2000
		 */
		Context.openSession();
		AdministrationService as = Context.getAdministrationService();
		GlobalProperty reportDirProp = as
				.getGlobalPropertyObject(JasperReportConstants.MODULE_ID + ".reportDirectory");

		if (reportDirProp != null
				&& !reportDirProp.getPropertyValue().isEmpty()) {
			log.info("Copying contents of '" + reportDirProp.getPropertyValue()
					+ "' to '" + reportDirPath + "'");
			copyDataFromOldReportDirectory(reportDirProp.getPropertyValue(),
					reportDirPath);
			log.warn("Old jasperReportsDirectory can be removed manually: '"
					+ reportDirProp.getPropertyValue() + "'");
			log.info("Removing global property '" + JasperReportConstants.MODULE_ID + ".reportDirectory'");
			Context.addProxyPrivilege("Purge Global Properties");
			as.purgeGlobalProperty(reportDirProp);
			Context.removeProxyPrivilege("Purge Global Properties");
		}
		Context.closeSession();

	}

	private static void copyDataFromOldReportDirectory(String oldPath,
			String newPath) {
		File oldDir = new File(oldPath);
		if (oldDir.exists() && oldDir.isDirectory()) {
			moveDirectoryContents(oldDir, newPath);
		}

	}

	private static void moveDirectoryContents(File oldDir, String newPath) {
		File newDir = new File(newPath);
		newDir.mkdir();

		File[] listFiles = oldDir.listFiles();
		for (File file : listFiles) {
			if (file.isDirectory()) {
				moveDirectoryContents(file, newPath + File.separator
						+ file.getName());
			} else {
				String name = file.getName();
				file.renameTo(new File(newPath + File.separator + name));
			}
		}

	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down JasperReport module");
	}

}