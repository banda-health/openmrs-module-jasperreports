package org.openmrs.module.jasperreport.extension.html;

import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

public class AdminList extends AdministrationSectionExt  {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return "@MODULE_ID@.title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new TreeMap<String, String>();
		
		map.put("module/@MODULE_ID@/jreport.list", "@MODULE_ID@.manage");
		
		return map;
	}

}