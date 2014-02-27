package org.openmrs.module.jasperreport.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.*;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.openmrs.web.WebConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
	@Override
	@SuppressWarnings("unchecked")
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object obj, BindException errors)
			throws Exception {

		HttpSession httpSession = request.getSession();
		String view = getFormView();
		String errorsString = "";

		if (Context.isAuthenticated()) {
			List<GeneratedReport> generatedReports = (List<GeneratedReport>) obj;
			MessageSourceAccessor msa = getMessageSourceAccessor();
			String action = request.getParameter("action");

			if (action == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						JasperReportConstants.MODULE_ID + ".nothingSelected");
			} else if (action.equals(JasperUtil.getModuleMessage(msa, "delete.selected"))) {
				String[] genReports = request.getParameterValues("genReport");
				if (genReports != null) {
					for (int i = 0; i < genReports.length; i++) {
						log.debug("param value " + genReports[i]);
						try {
							GeneratedReport genReport = generatedReports
									.get(Integer.valueOf(genReports[i]));
							JasperUtil.deleteGeneratedReport(genReport
									.getReportFileName());
						} catch (Exception e) {
							log.error("Error while deleting generated report.",
									e);
							errorsString = errorsString + e.getMessage()
									+ "<br />";
						}
					}
				}else
					errorsString = "No reports to delete.";
			}
		}

		if (errorsString.length() > 0)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					errorsString);

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
	@Override
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

	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request,
			Object obj, Errors errors) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		// default empty Object
		List<JasperReport> reportList = new Vector<JasperReport>();
		List<GeneratedReport> generatingReports;

		generatingReports = JasperUtil.getGeneratingReports();

		// only fill the Object is the user has authenticated properly
		if (Context.isAuthenticated()) {
			JasperReportService rs = (JasperReportService) Context
					.getService(JasperReportService.class);
			// FormService rs = new TestFormService();
			reportList = rs.getJasperReports();
		}

		map.put("reportList", reportList);
		map.put("generatingReports", generatingReports);

		if (generatingReports.size() > 0)
			map.put("refresh", true);
		else
			map.put("refresh", false);

		return map;
	}

}