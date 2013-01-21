/**
 * 
 */
package org.openmrs.module.jasperreports;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon
 * 
 */
public class JasperReportConstants {
	public static final String MODULE_ID = "jasperreports";

	public static final String GENERATED_REPORT_DIR_NAME = "generated";
	
	public static final List<Class<?>> parameterClasses = new ArrayList<Class<?>>();

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	static {
		parameterClasses.add(java.lang.String.class);
		parameterClasses.add(java.lang.Integer.class);
		parameterClasses.add(java.util.Date.class);
	}	
}