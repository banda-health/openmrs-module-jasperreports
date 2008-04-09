/**
 * 
 */
package org.openmrs.module.jasperreport;

import java.util.Set;

/**
 * @author Simon
 * 
 */
public class JasperReport implements java.io.Serializable {

	private static final long serialVersionUID = 2063860525096931568L;

	private Integer reportId;
	private String name;
	private String description;
	private String fileName;
	private boolean published;
	private Set<ReportParameter> parameters;
	
	/** default constructor */
	public JasperReport() {
	}

	/** constructor with id */
	public JasperReport(Integer reportId) {
		this.reportId = reportId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JasperReport) {
			JasperReport t = (JasperReport)obj;
			if (this.getReportId() != null && t.getReportId() != null)
				return (this.getReportId().equals(t.getReportId()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (this.getReportId() == null) return super.hashCode();
		return this.getReportId().hashCode();
	}
	
	/** Property accessors **/
	public Set<ReportParameter> getParameters() {
		return this.parameters;
	}

	public void setParameters(Set<ReportParameter> parameters) {
		this.parameters = parameters;
	}

	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Integer getReportId() {
		return this.reportId;
	}
	
	public void setReportId(Integer report_id) {
		this.reportId = report_id;
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void intiParamsFromLoad() {
		for (ReportParameter param : parameters) {
			param.initFromLoad();
		}
	}
}
