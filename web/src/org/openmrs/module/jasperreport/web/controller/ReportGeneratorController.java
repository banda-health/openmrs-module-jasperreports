/**
 * 
 */
package org.openmrs.module.jasperreport.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.module.jasperreport.JasperReport;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.jasperreport.JasperUtil;
import org.openmrs.module.jasperreport.ReportGenerator;
import org.openmrs.module.jasperreport.ReportParameter;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

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
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		dateFormat = new SimpleDateFormat(OpenmrsConstants
				.OPENMRS_LOCALE_DATE_PATTERNS().get(
						Context.getLocale().toString().toLowerCase()), Context
				.getLocale());

		NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class,
				new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
				dateFormat, true, 10));
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

		if (Context.isAuthenticated()) {
			MessageSourceAccessor msa = getMessageSourceAccessor();

			if (request.getParameter("action") == null
					|| request.getParameter("action").equals(
							msa.getMessage("jasperReport.generate"))) {
				for (ReportParameter param : report.getParameters()) {
					log.debug("Processing: " + param.getDisplayName());
					if (param.getVisible()) {
						String paramId = "param_" + param.getId();
						String passedParam = request.getParameter(paramId);

						log.debug("Parsing parameter: " + passedParam);
						
						int i = param.getValueClass().getName().lastIndexOf('.');
						String className = param.getValueClass().getName().substring(i+1);
						String [] args =  new String[] {passedParam, className, param.getDisplayName()};
						String msg = msa.getMessage("jasperReport.error.parameter.value", args);
						
						try {
							JasperUtil.parse(param, passedParam);
						} catch (ParseException e) {
							errors.reject(msg);
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

		if (Context.isAuthenticated()) {
			MessageSourceAccessor msa = getMessageSourceAccessor();
			String action = request.getParameter("action");
			JasperReport report = (JasperReport) obj;

			if (action == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						"jasperReport.not.generated");
			} else if (action.equals(msa.getMessage("jasperReport.generate"))) {
				log.debug("Parameters: " + request.getParameterMap());

				HashMap<String, Object> map = new HashMap<String, Object>();
				for (ReportParameter param : report.getParameters()) {
					String passedParam = request.getParameter("param_"
							+ param.getId());
					if (param.getVisible()) {
						if (passedParam == null)
							throw new ModuleException(
									"All parameters must be entered.");
						else
							map.put(param.getName(), JasperUtil.parse(param,
									passedParam));
					} else if (param.getDefault_value() != null
							&& !param.getDefault_value().equals(""))
						map.put(param.getName(), JasperUtil.parse(param, param
								.getDefault_value()));
				}

				File reportPdf = ReportGenerator.generate(report, map);

				/**
				* The following Pragma and Cache-Control lines are necessary
				* as the overcome an issue that IE has in some server configurations
				* when the no-cach header is sent. The two lines override these
				* headers, allowing IE to proceed. When not turned on, the error IE
				* provides is:
				* Internet Explorer cannot download <item URL here instead of item file name> from <domain>.
				*
				* Internet Explorer was not able to open this Internet site. The requested site is either
				* unavailable or cannot be found. Please try again later.
				* 
				* Sean P. O. MacCath-Moran
				* www.emanaton.com
				*/
				response.setHeader("Pragma", "public");
				response.setHeader("Cache-Control", "max-age=0");
								
				response.setContentType("application/pdf");
				response.setHeader("content-disposition",
						"attachment; filename=" + reportPdf.getName() + ";");

				try {
					FileInputStream reportStream = new FileInputStream(
							reportPdf);
					OpenmrsUtil.copyFile(reportStream, response
							.getOutputStream());
				} catch (FileNotFoundException e) {
					log.error("The request for '" + reportPdf.getAbsolutePath()
							+ "' cannot be found.", e);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
							"jasperReport.not.generated");
					return new ModelAndView(new RedirectView(getFormView()));
				}
			}
		}
		return null;
		// return new ModelAndView(new RedirectView(view));
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
	protected Map<String, Object> referenceData(HttpServletRequest arg0) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("datePattern", dateFormat.toLocalizedPattern().toLowerCase());

		return map;
	}

}
