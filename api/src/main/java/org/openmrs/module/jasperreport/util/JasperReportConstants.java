/**
 *
 */
package org.openmrs.module.jasperreport.util;

import java.util.ArrayList;
import java.util.List;

public class JasperReportConstants {
	public static final String MODULE_ID = "jasperreport";

	public static final String GENERATED_REPORT_DIR_NAME = "generated";

	public static final List<Class<?>> parameterClasses = new ArrayList<Class<?>>();

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	static {
		parameterClasses.add(java.lang.String.class);
		parameterClasses.add(java.lang.Integer.class);
		parameterClasses.add(java.util.Date.class);
	}
}