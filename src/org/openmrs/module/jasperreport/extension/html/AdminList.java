package org.openmrs.module.jasperreport.extension.html;

import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

public class AdminList extends AdministrationSectionExt  {

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "jasperReport.title";
	}
	
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new TreeMap<String, String>();
		
		map.put("module/jasperReport/jreport.list", "jasperReport.manage");
		
		return map;
	}

}