package org.openmrs.module.jasperreport;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleException;

public class JasperReportActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting JasperReport module");

		AdministrationService as = Context.getAdministrationService();

		// set up requirements
		String reportDirPath = as.getGlobalProperty("jasperReport.reportDirectory",
				"");
		if ("".equals(reportDirPath)) {
			throw new ModuleException(
					"Global property 'jasperReport.reportDirectory' must be defined");
		}

		File reportDir = new File(reportDirPath);
		if (!reportDir.exists())
				reportDir.mkdir();
		
		if (!reportDir.isDirectory())
			throw new ModuleException("Could not create report directory : " + reportDirPath);
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down JasperReport module");
	}

}