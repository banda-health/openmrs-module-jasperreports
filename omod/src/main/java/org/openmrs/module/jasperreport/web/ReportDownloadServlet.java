package org.openmrs.module.jasperreport.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.jasperreport.JasperUtil;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.openmrs.util.OpenmrsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportDownloadServlet extends HttpServlet {

	public static final long serialVersionUID = 123424L;

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String reportDirPath = JasperUtil.getReportDirPath();

		String reportId = request.getParameter("reportId");
		String reportName = request.getParameter("reportName");
		String path =  "";
		
		log.debug(request.getParameterMap());
		
		if (reportId != null && reportId.length() > 0 ) {
			path = reportDirPath + File.separator + reportId + ".zip";
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + reportId + ".zip");
		}
		else if (reportName != null && reportName.length() > 0 ){
			path = reportDirPath + File.separator + JasperReportConstants.GENERATED_REPORT_DIR_NAME + File.separator + reportName;
			response.setHeader("Content-Type", "application/pdf");
			response.setHeader("Content-Disposition", "attachment;filename=" + reportName);
		}
		
		if (path.length() <= 0)
			response.sendError(404, "The requested report could not be found: " + reportName);

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
