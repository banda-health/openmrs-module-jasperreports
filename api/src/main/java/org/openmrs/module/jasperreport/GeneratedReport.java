package org.openmrs.module.jasperreport;

public class GeneratedReport implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2830404385972905730L;

	private String reportFileName;
	private boolean delete;
	private boolean generating;

	public GeneratedReport(String reportFileName, boolean delete,
			boolean isThread) {
		super();
		this.reportFileName = reportFileName;
		this.delete = delete;
		this.generating = isThread;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public boolean isDelete() {
		return delete;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean getGenerating() {
		return generating;
	}

	public void setGenerating(boolean generating) {
		this.generating = generating;
	}
}
