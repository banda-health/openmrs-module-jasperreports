/**
 * 
 */
package org.openmrs.module.jasperreport;

/**
 * 
 * @author Simon Kelly
 * @version 1.0
 * 
 */
public class ReportParameter implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 682089678474901371L;

	private Integer id;
	private String displayName;
	private String name;
	private Class<?> valueClass;
	private String default_value;
	private boolean visible;
	private boolean dynamic;

	public ReportParameter() {
	}

	public ReportParameter(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getValueClass() {
		return this.valueClass;
	}

	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = PRIME * result
				+ ((this.valueClass == null) ? 0 : this.valueClass.hashCode());
		return result;
	}

	/**
	 * This equals method compares the parameters name and valueClass exactly
	 * but only compares id's if BOTH parameters have and id (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ReportParameter other = (ReportParameter) obj;
		if (this.id != null && other.id != null) {
			if (!this.id.equals(other.id))
				return false;
		}
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.valueClass == null) {
			if (other.valueClass != null)
				return false;
		} else if (!this.valueClass.equals(other.valueClass))
			return false;
		return true;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDefault_value() {
		return this.default_value;
	}

	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public boolean getVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDynamic() {
		return this.dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

}
