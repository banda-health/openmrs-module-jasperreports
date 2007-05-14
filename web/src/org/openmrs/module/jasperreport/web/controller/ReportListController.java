package org.openmrs.module.jasperreport.web.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.GeneratedReport;
import org.openmrs.module.jasperreport.JasperReport;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.jasperreport.JasperUtil;
import org.openmrs.web.WebConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ReportListController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * 
	 * The onSubmit function receives the form/command object that was modified
	 * by the input form and saves it to the db
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@SuppressWarnings("unchecked")
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object obj, BindException errors)
			throws Exception {

		HttpSession httpSession = request.getSession();
		String view = getFormView();
		String errorsString = "";
		String successString = "";

		if (Context.isAuthenticated()) {
			List<GeneratedReport> generatedReports = (List<GeneratedReport>) obj;
			MessageSourceAccessor msa = getMessageSourceAccessor();
			String action = request.getParameter("action");

			if (action == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						"jasperReport.nothingSelected");
			} else if (action.equals(msa
					.getMessage("jasperReport.delete.selected"))) {
				String[] genReports = request.getParameterValues("genReport");
				if (genReports != null) {
					for (int i = 0; i < genReports.length; i++) {
						log.debug("param value " + genReports[i]);
						try {
							GeneratedReport genReport = generatedReports
									.get(Integer.valueOf(genReports[i]));
							JasperUtil.deleteGeneratedReport(genReport
									.getReportFileName());
							successString = successString
									+ genReport.getReportFileName()
									+ " was deleted. <br />";
						} catch (Exception e) {
							log.error("Error while deleting generated report.",
									e);
							errorsString = errorsString + e.getMessage()
									+ "<br />";
						}
					}
				}else
					successString = "No reports to delete.";
			}
		}

		if (errorsString.length() > 0)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					errorsString);
		if (successString.length() > 0)
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
					successString);

		view = getSuccessView();
		return new ModelAndView(new RedirectView(view));
	}

	/**
	 * 
	 * This is called prior to displaying a form for the first time. It tells
	 * Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {

		List<GeneratedReport> generatedReports;
		try {
			generatedReports = JasperUtil.getGeneratedReports();
		} catch (IOException e) {
			log.error("Error getting list of generated reports.", e);
			generatedReports = new Vector<GeneratedReport>();
		}

		log.debug("number of generated reports: " + generatedReports.size());

		return generatedReports;
	}

	protected Map<String, Object> referenceData(HttpServletRequest request,
			Object obj, Errors errors) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		// default empty Object
		List<JasperReport> reportList = new Vector<JasperReport>();

		// only fill the Object is the user has authenticated properly
		if (Context.isAuthenticated()) {
			JasperReportService rs = (JasperReportService) Context
					.getService(JasperReportService.class);
			// FormService rs = new TestFormService();
			reportList = rs.getJasperReports();
		}

		map.put("reportList", reportList);

		return map;
	}

}