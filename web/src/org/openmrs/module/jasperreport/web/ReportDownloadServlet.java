package org.openmrs.module.jasperreport.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

/**
 * Provides a servlet through which an XSN is downloaded. This class differs
 * from org.openmrs.module.formEntry.FormDownloadServlet in that this class
 * /will not/ modify the template or schema files inside of the xsn. This class
 * simply writes the named schema to the response
 * 
 * @author Ben Wolfe
 * @version 1.0
 */
public class ReportDownloadServlet extends HttpServlet {

	public static final long serialVersionUID = 123424L;

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		AdministrationService as = Context.getAdministrationService();
		String reportDirPath = as.getGlobalProperty(
				"jasperReport.reportDirectory", "");

		String reportId = request.getParameter("reportId");
		String path = reportDirPath + java.io.File.separator + reportId
		+ ".zip";
		
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + reportId + ".zip");

		File file = new File(path);

		try {
			FileInputStream formStream = new FileInputStream(file);
			OpenmrsUtil.copyFile(formStream, response.getOutputStream());
		} catch (FileNotFoundException e) {
			log.error("The request for '" + file.getAbsolutePath()
					+ "' cannot be found.", e);
			response.sendError(404);
		}
	}
}
