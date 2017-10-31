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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public abstract class ReportsControllerBase {
	@RequestMapping(method = RequestMethod.GET)
	public String render(@RequestParam(value = "reportId", required = true) int reportId, WebRequest request,
	                     HttpServletResponse response) throws IOException {
		if (Context.getAuthenticatedUser() == null) {
			return "redirect:/login.htm";
		} else {
			return parse(reportId, request, response);
		}
	}

	public abstract String parse(int reportId, WebRequest request, HttpServletResponse response) throws IOException;

	/**
	 * Renders PDF Reports. The method signature has been maintained to ensure backward compatibility
	 * @param reportId
	 * @param parameters
	 * @param reportName
	 * @param response
	 * @return
     * @throws IOException
     */
	public String renderReport(int reportId, HashMap<String, Object> parameters, String reportName,
							   HttpServletResponse response) throws IOException {
		return renderReport(reportId, parameters, reportName, response, "pdf");
	}

	/**
	 * Renders reports given a specific format (pdf/excel).
	 * @param reportId
	 * @param parameters
	 * @param reportName
	 * @param response
	 * @param format
	 * @return
     * @throws IOException
     */
	public String renderReport(int reportId, HashMap<String, Object> parameters, String reportName,
	                           HttpServletResponse response, String format) throws IOException {
		JasperReportService jasperService = Context.getService(JasperReportService.class);
		JasperReport report = jasperService.getJasperReport(reportId);
		String message = null;
		if (report == null) {
			message = "Could not find report. The Report could not be found in the system. Please upload the reports and "
					+ "try again";
			return "redirect:" + JasperReportConstants.REPORT_ERROR_PAGE + "?reportId=" + reportId + "&message=" + message;
		}

		if (!StringUtils.isEmpty(reportName)) {
			report.setName(reportName);
		}

		try {
			ReportGenerator.generate(report, parameters, false, true, format);
		} catch (IOException e) {
			message = "Error Generating the report. The Report files are not present. Please Upload the report"
					+ " files and try again. The following error occurred " + e.getMessage();
			return "redirect:" + JasperReportConstants.REPORT_ERROR_PAGE + "?reportId=" + reportId + "&message=" + message;
		}

		String reportFile = "redirect:" + JasperReportConstants.REPORT_DOWNLOAD_URL + "?reportName=" + report.getName().replaceAll("\\W", "");
		if(StringUtils.equalsIgnoreCase(format, "pdf")){
			reportFile += ".pdf";
		}
		else if(StringUtils.equalsIgnoreCase(format, "excel")){
			reportFile += ".xlsx";
		}
		else{
			// unknown format
			return "";
		}
		return reportFile;
	}
}
