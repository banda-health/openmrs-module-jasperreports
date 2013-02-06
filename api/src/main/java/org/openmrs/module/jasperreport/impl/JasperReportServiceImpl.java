/**
 * 
 */
package org.openmrs.module.jasperreport.impl;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.module.jasperreport.JasperReport;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.jasperreport.db.JasperReportDAO;

/**
 * @author Simon Kelly
 * @version 1.0
 */
public class JasperReportServiceImpl implements JasperReportService {

	private JasperReportDAO dao;
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.JasperReportService#setJasperReportDAO(org.openmrs.module.jasperreport.db.JasperReportDAO)
	 */
	public void setJasperReportDAO(JasperReportDAO dao) {
		this.dao = dao;
	}
	
	public JasperReportDAO getJasperReportDAO() {
		return this.dao;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.JasperReportService#createJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void createJasperReport(JasperReport jasperReport)
			throws APIException {
		getJasperReportDAO().createJasperReport(jasperReport);

	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.JasperReportService#getJasperReport(java.lang.Integer)
	 */
	public JasperReport getJasperReport(Integer reportId) throws APIException {
		return getJasperReportDAO().getJasperReport(reportId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.JasperReportService#getJasperReports()
	 */
	public List<JasperReport> getJasperReports() throws APIException {
		return getJasperReportDAO().getJasperReports();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.JasperReportService#updateJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void updateJasperReport(JasperReport jasperReport)
			throws APIException {
		getJasperReportDAO().updateJasperReport(jasperReport);

	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.JasperReportService#deleteJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void deleteJasperReport(JasperReport jasperReport) {
		getJasperReportDAO().deleteJasperReport(jasperReport);
		
	}


}
