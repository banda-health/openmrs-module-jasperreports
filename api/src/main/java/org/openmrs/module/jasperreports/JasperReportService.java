/**
 * 
 */
package org.openmrs.module.jasperreports;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.jasperreports.db.JasperReportDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * JasperReport related services
 * 
 * @author Simon Kelly
 * @version 1.0
 */
@Transactional
public interface JasperReportService {

	public void setJasperReportDAO(JasperReportDAO dao);

	/**
	 * Saves (creates) a new JasperReport
	 * 
	 * @param saying
	 *            to be created
	 * @throws APIException
	 */
	@Authorized({"Manage Jasper Reports"})
	public void createJasperReport(JasperReport jasperReport)
			throws APIException;

	/**
	 * Get jasperReport by internal identifier
	 * 
	 * @param reportId
	 *            internal saying identifier
	 * @return jasperReport with given internal identifier
	 * @throws APIException
	 */
	@Authorized({"Manage Jasper Reports"})
	@Transactional(readOnly = true)
	public JasperReport getJasperReport(Integer reportId) throws APIException;

	/**
	 * Save jasperReport
	 * 
	 * @param jasperReport
	 *            to be updated
	 * @throws APIException
	 */
	@Authorized({"Manage Jasper Reports"})
	public void updateJasperReport(JasperReport jasperReport)
			throws APIException;

	/**
	 * Get JasperReports
	 * 
	 * @return JasperReport list
	 * @throws APIException
	 */
	@Authorized({"Manage Jasper Reports"})
	public List<JasperReport> getJasperReports() throws APIException;

	/**
	 * Delete jasperReport
	 * 
	 * @param jasperReport
	 *            to delete
	 */
	@Authorized({"Manage Jasper Reports"})
	public void deleteJasperReport(JasperReport jasperReport);
}
