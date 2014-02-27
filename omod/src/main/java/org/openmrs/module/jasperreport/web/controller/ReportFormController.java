package org.openmrs.module.jasperreport.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.*;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReportFormController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	SimpleDateFormat dateFormat;

	/**
	 * 
	 * Allows for Integers to be used as values in input tags. Normally, only
	 * strings and lists are expected
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
	 *      org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {

		dateFormat = JasperUtil.getDateFormat();

		binder.registerCustomEditor(java.util.Date.class, "parameters.valueDate", new CustomDateEditor(dateFormat, true, 10));
		binder.registerCustomEditor(Concept.class, "parameters.valueConcept", new ConceptEditor());
		binder.registerCustomEditor(Location.class, "parameters.valueLocation", new LocationEditor());
		binder.registerCustomEditor(Boolean.class, "parameters.valueBoolean", new CustomBooleanEditor(true)); // allow for an empty boolean value
		binder.registerCustomEditor(java.lang.Class.class,"parameters.mappedClass", new ClassEditor());
	}

	/**
	 * Called prior to onSubmit to allow error checking.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#processFormSubmission(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object obj, BindException errors)
			throws Exception {

		JasperReport report = (JasperReport) obj;

		if (Context.isAuthenticated()) {
			MessageSourceAccessor msa = getMessageSourceAccessor();

			if (request.getParameter("action") == null
				|| request.getParameter("action").equals(JasperUtil.getModuleMessage(msa, "save"))) {
				Set<ReportParameter> params = report.getParameters();
				if (params != null) {
					for (ReportParameter reportParameter : params) {
						if (!reportParameter.isVisible() &&
							(reportParameter.getDefault_value() == null ||
							 reportParameter.getDefault_value().equals("")
							)) {
							errors.reject("All parameters that are not visible must have default values: " +
									reportParameter.getName());
						}
					}
				}				
			}
		}

		return super.processFormSubmission(request, response, report, errors);
	}

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
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object obj, BindException errors)
			throws Exception {

		HttpSession httpSession = request.getSession();
		String view = getFormView();
		
		if (Context.isAuthenticated()) {
			JasperReportService rs = (JasperReportService) Context
					.getService(JasperReportService.class);
			JasperReport report = (JasperReport) obj;
			MessageSourceAccessor msa = getMessageSourceAccessor();
			String action = request.getParameter("action");

			if (action == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, JasperReportConstants.MODULE_ID + ".not.saved");
			} else if (action.equals(JasperUtil.getModuleMessage(msa, "save"))) {
				try {

					// save report in order to get reportId
					rs.updateJasperReport(report);

					// retrieve archive from request if it was uploaded
					if (request instanceof MultipartHttpServletRequest) {
						MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
						MultipartFile reportArchive = multipartRequest
								.getFile("report_archive");
						if (reportArchive != null && !reportArchive.isEmpty()) {
							ReportDeployer.uploadReport(reportArchive
									.getInputStream(), report);
						}
					}

					log.debug("reportId : " + report.getReportId());
					fillParameterName(report);

					// save report with parameter map
					rs.updateJasperReport(report);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, JasperReportConstants.MODULE_ID + ".saved");
				} catch (Exception e) {
					log.error("Error while saving report " + report.getReportId(), e);
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
							JasperUtil.getModuleMessage(msa, "not.saved") + " : " + e.getMessage());

					return showForm(request, response, errors);
				}
			} else if (action.equals(JasperUtil.getModuleMessage(msa, "delete"))) {
				try {
					ReportDeployer.deleteReport(report);
					rs.deleteJasperReport(report);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, JasperReportConstants.MODULE_ID +".deleted");
				} catch (Exception e) {
					log.error("Error while deleting report "
							+ report.getReportId(), e);
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
							JasperUtil.getModuleMessage(msa, "cannot.delete") + " : " + e.getMessage());

					return showForm(request, response, errors);
				}
			}
		}	
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

		JasperReport jreport = null;

		if (Context.isAuthenticated()) {
			JasperReportService rs = (JasperReportService) Context
					.getService(JasperReportService.class);
			String reportId = request.getParameter("reportId");
			if (reportId != null)
				jreport = rs.getJasperReport(Integer.valueOf(reportId));
		}

		if (jreport == null)
			jreport = new JasperReport();

		fillParameterName(jreport);
		return jreport;
	}

	/**
	 * @param jreport
	 */
	private void fillParameterName(JasperReport jreport) {
		if (jreport.getParameters() == null
				|| jreport.getParameters().isEmpty())
			return;

		for (ReportParameter param : jreport.getParameters()) {
			if (param.getDisplayName() == null
					|| param.getDisplayName().equals(""))
				param.setDisplayName(param.getName());
		}
	}

	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request,
			Object obj, Errors errors) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		String reportId = request.getParameter("reportId");

		Boolean archiveExists = false;
		if (JasperUtil.getReportArchive(reportId).exists())
			archiveExists = true;

		map.put("archiveExists", archiveExists);

		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(Integer.class);
		classes.add(Concept.class);
		classes.add(Location.class);

		map.put("datePattern", dateFormat.toLocalizedPattern().toLowerCase());
		map.put("classes", classes);
		return map;
	}
}
