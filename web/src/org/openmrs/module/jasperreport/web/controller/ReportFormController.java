package org.openmrs.module.jasperreport.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.JasperReport;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.jasperreport.JasperUtil;
import org.openmrs.module.jasperreport.ReportDeployer;
import org.openmrs.module.jasperreport.ReportParameter;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ReportFormController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	private ArrayList<ReportParameter> newParameters;

	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		binder.registerCustomEditor(java.lang.Integer.class,
				new CustomNumberEditor(java.lang.Integer.class, true));
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
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object obj, BindException errors)
			throws Exception {

		JasperReport report = (JasperReport) obj;
		String[] default_values = request.getParameterValues("default_value");
		String[] valueClasses = request.getParameterValues("valueClass");
		String[] names = request.getParameterValues("pname");

		if (Context.isAuthenticated()) {
			MessageSourceAccessor msa = getMessageSourceAccessor();

			if (request.getParameter("action") == null
					|| request.getParameter("action").equals(
							msa.getMessage("jasperReport.save"))) {

				// check newly added parameters
				if (names != null) {
					for (int i = 0; i < names.length; i++) {

						int j = valueClasses[i].lastIndexOf('.');
						String className = valueClasses[i].substring(j + 1);
						String[] args = new String[]{default_values[i],
								className, names[i]};
						String valueMsg = msa.getMessage(
								"jasperReport.error.parameter.value", args);
						String nameMsg = msa.getMessage(
								"jasperReport.error.parameter.name",
								new String[]{names[i]});

						if (names[i].equals(""))
							errors.reject(nameMsg);

						try {
							JasperUtil.parse(Class.forName(valueClasses[i]),
									default_values[i]);
						} catch (ParseException e) {
							errors.reject(valueMsg);
						}
					}
				}

				// check existing parameters
				if (report.getParameters() != null) {
					for (ReportParameter param : report.getParameters()) {
						int i = param.getValueClass().getName()
								.lastIndexOf('.');
						String className = param.getValueClass().getName()
								.substring(i + 1);
						String[] args = new String[]{param.getDefault_value(),
								className, param.getDisplayName()};
						String msg = msa.getMessage(
								"jasperReport.error.parameter.value", args);

						if (!param.getDefault_value().equals("")) {
							try {
								JasperUtil.parse(param.getValueClass(), param
										.getDefault_value());
							} catch (ParseException e) {
								errors.reject(msg);
							} catch (NumberFormatException e1) {
								errors.reject(msg);
							}
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

			String[] displayNames = request.getParameterValues("displayName");
			String[] default_values = request
					.getParameterValues("default_value");
			String[] names = request.getParameterValues("pname");
			String[] valueClasses = request.getParameterValues("valueClass");;
			String[] removes = request.getParameterValues("remove");

			if (displayNames != null) {
				if (log.isDebugEnabled()) {
					log.debug("displayNames: " + displayNames);
					for (String s : displayNames)
						log.debug(s);
					log.debug("default_values: " + default_values);
					for (String s : default_values)
						log.debug(s);
					log.debug("names: " + names);
					for (String s : names)
						log.debug(s);
					log.debug("valueClasses: " + valueClasses);
					for (String s : valueClasses)
						log.debug(s);
				}

				newParameters = new ArrayList<ReportParameter>();

				for (int i = 0; i < displayNames.length; i++) {
					ReportParameter parameter = new ReportParameter();
					parameter.setDisplayName(displayNames[i]);
					parameter.setDefault_value(default_values[i]);
					parameter.setName(names[i]);
					parameter.setValueClass(Class.forName(valueClasses[i]));
					parameter.setVisible(false);
					parameter.setDynamic(true);
					newParameters.add(parameter);
				}

				report.getParameters().addAll(newParameters);
			}

			if (removes != null) {
				for (int i = 0; i < removes.length; i++) {
					if (removes[i].equals("on")) {
						Object toRemove = report.getParameters().toArray()[i];
						report.getParameters().remove(toRemove);
					}
				}
			}

			if (action == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						"jasperReport.not.saved");
			} else if (action.equals(msa.getMessage("jasperReport.save"))) {
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
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
							"jasperReport.saved");
				} catch (Exception e) {
					log.error("Error while saving report "
							+ report.getReportId(), e);
					errors.reject(e.getMessage());
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
							"jasperReport.not.saved");
					return showForm(request, response, errors);
				}
			} else if (action.equals(msa.getMessage("jasperReport.delete"))) {
				try {
					ReportDeployer.deleteReport(report);
					rs.deleteJasperReport(report);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
							"jasperReport.deleted");
				} catch (Exception e) {
					log.error("Error while deleting report "
							+ report.getReportId(), e);
					errors.reject(e.getMessage());
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
							"jasperReport.cannot.delete");
					return showForm(request, response, errors);
				}
			} else if (action.equals(msa
					.getMessage("jasperReport.reload.parameters"))) {
				ReportDeployer.refreshParameters(report);
				rs.updateJasperReport(report);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
						"jasperReport.saved");
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
		if (jreport.getParameters() == null || jreport.getParameters().isEmpty())
			return;
		
		for (ReportParameter param : jreport.getParameters()) {
			if (param.getDisplayName() == null || param.getDisplayName().equals(""))
				param.setDisplayName(param.getName());
		}
	}

	protected Map<String, Object> referenceData(HttpServletRequest request, Object obj,
			Errors errors) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		String reportId = request.getParameter("reportId");

		Boolean archiveExists = false;
		if (JasperUtil.getReportArchive(reportId).exists())
			archiveExists = true;

		map.put("archiveExists", archiveExists);

		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(java.lang.String.class);
		classes.add(java.lang.Integer.class);
		classes.add(java.util.Date.class);
		// classes.add(java.lang.Boolean.class);

		map.put("classes", classes);
		return map;
	}
}
