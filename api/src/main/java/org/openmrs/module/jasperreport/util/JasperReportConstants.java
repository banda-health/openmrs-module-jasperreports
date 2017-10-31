/**
 *
 */
package org.openmrs.module.jasperreport.util;

import java.util.ArrayList;
import java.util.List;

public class JasperReportConstants {
	public static final String MODULE_ID = "jasperreport";
	protected static final String MODULE_BASE = "/module/";
	public static final String MODULE_ROOT = MODULE_BASE + MODULE_ID + "/";
	public static final String REPORT_DOWNLOAD_URL = "/moduleServlet/jasperreport/jreportDownload";

	public static final String REPORT_ERROR_ROOT = MODULE_ROOT + "reportError";
	public static final String REPORT_ERROR_PAGE =  REPORT_ERROR_ROOT + ".form";

	public static final String REPORT_LIST_PAGE =  "/module/jasperreport/jreport.list";

	public static final String GENERATED_REPORT_DIR_NAME = "generated";

	public static final List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
	
	public static final List<String> REPORT_FORMATS = new ArrayList<String>();

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	static {
		parameterClasses.add(java.lang.String.class);
		parameterClasses.add(java.lang.Integer.class);
		parameterClasses.add(java.util.Date.class);
		
		REPORT_FORMATS.add("PDF");
		REPORT_FORMATS.add("Excel");
	}
}
