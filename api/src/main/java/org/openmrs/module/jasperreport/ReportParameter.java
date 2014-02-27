/**
 * 
 */
package org.openmrs.module.jasperreport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.module.jasperreport.util.JasperReportConstants;

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
	private static Log log = LogFactory.getLog(ReportParameter.class);

	private Integer id;
	private String displayName;
	private String name;
	private Class<?> valueClass;
	private Class<?> mappedClass;
	private String default_value;
	private Date valueDate;
	private Boolean valueBoolean;
	private Location valueLocation;
	private Concept valueConcept;
	private boolean visible;

	public ReportParameter() {
	}

	public ReportParameter(Integer id) {
		this.id = id;
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

	public String getDefault_value() {
		return this.default_value;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public Integer getId() {
		return this.id;
	}

	public Class<?> getInterfaceClass() {
		if (this.mappedClass != null)
			return this.mappedClass;

		return this.valueClass;
	}

	public Class<?> getMappedClass() {
		return mappedClass;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Returns the object that the value of the parameter represents whose class
	 * is specified by ReportParameter.mappedClass.
	 * 
	 * @return
	 */
	public Object getMappedValue() {
		if (getInterfaceClass() == Location.class) {
			return valueLocation;
		} else if (getInterfaceClass() == Concept.class) {
			return valueConcept;
		} else if (getInterfaceClass() == Date.class) {
			return valueDate;
		} else if (getInterfaceClass() == Boolean.class) {
			return valueBoolean;
		} else if (getInterfaceClass() == Integer.class) {
			Integer num = null;
			try {
				num = Integer.parseInt(default_value);
			} catch (NumberFormatException e) {
				log.error("Failed to parse into Integer: '" + default_value
						+ "'");
			}
			return num;
		} else //String
			return default_value;
	}

	/**
	 * Returns the object that is the value of the parameter whose class is
	 * specified by ReportParameter.valueClass
	 * 
	 * @return
	 */
	public Object getValue() {
		Object valueObj = getMappedValue();
		if (valueObj == null) {
			return null;
		} else if (getInterfaceClass() == Location.class) {
			return ((Location) valueObj).getLocationId();
		} else if (getInterfaceClass() == Concept.class) {
			return ((Concept) valueObj).getConceptId();
		} else if (getInterfaceClass() == Boolean.class) {
			return ((Boolean) valueObj).booleanValue();
		} else
			// Date, String, or Integer
			return valueObj;
	}

	public Boolean getValueBoolean() {
		return valueBoolean;
	}

	public Class<?> getValueClass() {
		return this.valueClass;
	}

	public Concept getValueConcept() {
		return valueConcept;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public Location getValueLocation() {
		return valueLocation;
	}

	public boolean getVisible() {
		return this.visible;
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
	 * Initializes the parameter objects from the default_value stored in the
	 * database. Called whenever a JasperReport is loaded from the database.
	 */
	public void initFromLoad() {
		if (default_value == null || default_value.equals("")) {
			return;
		}

		try {
			if (getInterfaceClass() == Location.class) {
				valueLocation = (Location) JasperUtil.parse(Location.class,
						default_value);
			} else if (getInterfaceClass() == Concept.class) {
				valueConcept = (Concept) JasperUtil.parse(Concept.class,
						default_value);
			} else if (getInterfaceClass() == Date.class) {
				valueDate = (Date) JasperUtil.parse(Date.class, default_value);
			} else if (getInterfaceClass() == Boolean.class) {
				valueBoolean = (Boolean) JasperUtil.parse(Boolean.class,
						default_value);
			}
		} catch (NumberFormatException e) {
			log.error("Error parsing default_value to default_object: "
					+ default_value);
		} catch (ParseException e) {
			log.error("Error parsing default_value to default_object: "
					+ default_value);
		}
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMappedClass(Class<?> mappedClass) {
		this.mappedClass = mappedClass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValueBoolean(Boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
		if (valueBoolean != null)
			this.default_value = valueBoolean.toString();
		else
			this.default_value = "";
	}

	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}

	public void setValueConcept(Concept concept) {
		this.valueConcept = concept;
		if (valueConcept != null)
			this.default_value = concept.getConceptId().toString();
		else
			this.default_value = "";
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
		if (valueDate != null) {
			DateFormat dateFormat = new SimpleDateFormat(
					JasperReportConstants.DATE_FORMAT);
			this.default_value = dateFormat.format(valueDate);
		} else
			this.default_value = "";
	}

	public void setValueLocation(Location location) {
		this.valueLocation = location;
		if (valueLocation != null)
			this.default_value = location.getLocationId().toString();
		else
			this.default_value = "";
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
