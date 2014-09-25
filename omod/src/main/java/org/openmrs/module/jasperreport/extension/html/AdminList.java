package org.openmrs.module.jasperreport.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.jasperreport.util.JasperReportConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.Map;
import java.util.TreeMap;

public class AdminList extends AdministrationSectionExt  {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return JasperReportConstants.MODULE_ID + ".title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new TreeMap<String, String>();

		map.put("module/" + JasperReportConstants.MODULE_ID + "/jreport.list", JasperReportConstants.MODULE_ID + ".manage");
		
		return map;
	}

}