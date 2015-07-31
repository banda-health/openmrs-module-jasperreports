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
package org.openmrs.module.jasperreport.web.controller;

import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@RequestMapping(value = JasperReportConstants.REPORT_ERROR_ROOT)
public class ReportErrorController {
	@RequestMapping(method = RequestMethod.GET)
	public void ReportError(ModelMap model, Integer reportId, String message) throws IOException {
		model.addAttribute("reportId", reportId);
		model.addAttribute("message", message);
	}
}


