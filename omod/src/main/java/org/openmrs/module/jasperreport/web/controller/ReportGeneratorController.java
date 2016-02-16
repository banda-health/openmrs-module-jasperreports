/**
 * 
 */
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
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Simon
 * 
 */
public class ReportGeneratorController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	SimpleDateFormat dateFormat;

	/**
	 * 
	 * Allows for other Objects to be used as values in input tags. Normally,
	 * only strings and lists are expected
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
	 *      org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		dateFormat = JasperUtil.getDateFormat();
		NumberFormat nf = NumberFormat.getInstance(JasperUtil.getLocale());
		
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true, 10));
        binder.registerCustomEditor(Concept.class, new ConceptEditor());
        binder.registerCustomEditor(Location.class, new LocationEditor());
        binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor(false));
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
					|| request.getParameter("action").equals(JasperUtil.getModuleMessage(msa, "generate"))) {
				Set<ReportParameter> params = report.getParameters();
				for (ReportParameter reportParameter : params) {
					if (reportParameter.getMappedValue() == null)
						errors.reject("All parameters must be entered: " + reportParameter.getName());
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
			MessageSourceAccessor msa = getMessageSourceAccessor();
			String action = request.getParameter("action");
			JasperReport report = (JasperReport) obj;

			if (action == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						JasperReportConstants.MODULE_ID + ".not.generated");
			} else if (action.equals(JasperUtil.getModuleMessage(msa, "generate"))) {
				log.debug("Parameters: " + request.getParameterMap());

				HashMap<String, Object> map = new HashMap<String, Object>();
				for (ReportParameter param : report.getParameters()) {
					map.put(param.getName(), param.getValue());
				}

				String threadName = "report_"
						+ report.getName().replaceAll("\\W", "")
						+ new SimpleDateFormat("dd-MM-yyyy-HH:mm", Context.getLocale()).format(new Date());
				String format = request.getParameter("format");
				new Thread(new Generator(report, map, format), threadName).start();
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

		return jreport;
	}

	/**
	 * 
	 * Called prior to form display. Allows for data to be put in the request to
	 * be used in the view
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest arg0)
			throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("datePattern", dateFormat.toLocalizedPattern().toLowerCase());
		map.put("reportFormats", JasperReportConstants.REPORT_FORMATS);

		return map;
	}

	private class Generator implements Runnable {

		private HashMap<String, Object> map;
		private JasperReport report;
		private String format;

		public Generator(JasperReport report, HashMap<String, Object> map, String format) {
			super();
			this.map = map;
			this.report = report;
			this.format = format;
		}

		public void run() {
			if (report == null || map == null)
				return;

			try {
				ReportGenerator.generate(report, map, format);
			} catch (IOException e) {
				log.error("Failed to generate report: " + report.getName(), e);
			}
		}

	}
}
