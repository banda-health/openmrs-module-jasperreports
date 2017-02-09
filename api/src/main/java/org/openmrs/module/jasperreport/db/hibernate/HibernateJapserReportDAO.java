/**
 * 
 */
package org.openmrs.module.jasperreport.db.hibernate;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
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
		getCurrentSession().saveOrUpdate(jasperReport);		
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#getJasperReport(java.lang.Integer)
	 */
	public JasperReport getJasperReport(Integer reportId) throws DAOException {
		JasperReport jasperReport = (JasperReport) getCurrentSession().get(JasperReport.class, reportId);
		jasperReport.intiParamsFromLoad();
		return jasperReport;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#getJasperReports()
	 */
	public List<JasperReport> getJasperReports() throws DAOException {
		List<JasperReport> reports = getCurrentSession().createCriteria(JasperReport.class).list();
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
			getCurrentSession().saveOrUpdate(jasperReport);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.jasperreport.db.JasperReportDAO#deleteJasperReport(org.openmrs.module.jasperreport.JasperReport)
	 */
	public void deleteJasperReport(JasperReport jasperReport) {
		getCurrentSession().delete(jasperReport);
		
	}

	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 * 
	 * @return the current hibernate session.
	 */
	private org.hibernate.Session getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.hibernate.Session)method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
}
