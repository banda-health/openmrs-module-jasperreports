package org.openmrs.module.jasperreport.db;

import java.util.List;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.jasperreport.JasperReport;

/**
 * JasperReport-related database functions
 * 
 * @author Simon Kelly
 * @version 1.0
 */
public interface JasperReportDAO {

	/**
	 * Creates a new JasperReport record
	 * 
	 * @param JasperReport
	 *            to be created
	 * @throws DAOException
	 */
	public void createJasperReport(JasperReport jasperReport)
			throws DAOException;

	/**
	 * Get JasperReport by internal identifier
	 * 
	 * @param JasperReportId
	 *            internal JasperReport identifier
	 * @return JasperReport with given internal identifier
	 * @throws DAOException
	 */
	public JasperReport getJasperReport(Integer reportId) throws DAOException;

	/**
	 * Update JasperReport
	 * 
	 * @param JasperReport
	 *            to be updated
	 * @throws DAOException
	 */
	public void updateJasperReport(JasperReport jasperReport)
			throws DAOException;

	public List<JasperReport> getJasperReports() throws DAOException;

	/**
	 * Delete jasperReport
	 * 
	 * @param jasperReport
	 *            to delete
	 */
	public void deleteJasperReport(JasperReport jasperReport);
}
