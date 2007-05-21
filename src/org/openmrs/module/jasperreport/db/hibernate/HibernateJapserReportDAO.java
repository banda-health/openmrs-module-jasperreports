/**
 * 
 */
package org.openmrs.module.jasperreport.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.jasperreport.JasperReport;
import org.openmrs.module.jasperreport.db.JasperReportDAO;

/**
 * @author Simon
 *
 */
public class HibernateJapserReportDAO implements JasperReportDAO{

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	public HibernateJapserReportDAO() { }
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) { 
		this.sessionFactory = sessionFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#createJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void createJasperReport(JasperReport jasperReport) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(jasperReport);		
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#getJasperReport(java.lang.Integer)
	 */
	public JasperReport getJasperReport(Integer reportId) throws DAOException {
		JasperReport jasperReport = (JasperReport) sessionFactory.getCurrentSession().get(JasperReport.class, reportId);
		jasperReport.intiParamsFromLoad();
		return jasperReport;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#getJasperReports()
	 */
	public List<JasperReport> getJasperReports() throws DAOException {
		List<JasperReport> reports = sessionFactory.getCurrentSession().createCriteria(JasperReport.class).list();
		for (JasperReport jasperReport : reports) {
			jasperReport.intiParamsFromLoad();
		}
		return reports;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#updateJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void updateJasperReport(JasperReport jasperReport) throws DAOException {
		if (jasperReport.getReportId() == null) {
			log.debug("create report " + jasperReport.getName());
			createJasperReport(jasperReport);
		} else {
			sessionFactory.getCurrentSession().saveOrUpdate(jasperReport);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#deleteJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void deleteJasperReport(JasperReport jasperReport) {
		sessionFactory.getCurrentSession().delete(jasperReport);
		
	}

}
