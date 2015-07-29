package org.openmrs.module.jasperreport.web.controller;

import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller @RequestMapping(value = JasperReportConstants.REPORT_ERROR_ROOT) public class ReportErrorController {
	@RequestMapping(method = RequestMethod.GET)
		public void ReportError(ModelMap model, Integer reportId) throws IOException {
			model.addAttribute("reportId", reportId);
		}
}


