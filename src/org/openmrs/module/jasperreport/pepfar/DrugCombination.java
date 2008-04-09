/**
 * 
 */
package org.openmrs.module.jasperreport.pepfar;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Simon
 * 
 */
public class DrugCombination {

	private String name;

	private List<Drug> drugs;

	private int adultCount;

	private int pedeCount;

	/**
	 * 
	 */
	public DrugCombination() {
		super();
		drugs = new ArrayList<Drug>();
		adultCount = 0;
		pedeCount = 0;
	}

	/**
	 * @return the drugs
	 */
	public List<Drug> getDrugs() {
		return this.drugs;
	}

	/**
	 * @param drugs
	 *            the drugs to set
	 */
	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Adds a drug to the list of drugs
	 * 
	 * @param d
	 */
	public void addDrug(Drug d) {
		drugs.add(d);
	}

	/**
	 * Removes a drug from the list of drugs
	 * 
	 * @param d
	 */
	public void removeDrug(Drug d) {
		drugs.remove(d);
	}

	/*
	 * (non-Javadoc)
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
		final DrugCombination other = (DrugCombination) obj;
		if (this.drugs == null) {
			if (other.drugs != null)
				return false;
		} else {
			for (Drug d : this.drugs) {
				if (!other.getDrugs().contains(d))
					return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public void incrimentAdultCount() {
		adultCount++;
	}

	/**
	 * 
	 */
	public void incrimentPedeCount() {
		pedeCount++;
	}

	public int getAdultCount() {
		return this.adultCount;
	}

	public void setAdultCount(int adultCount) {
		this.adultCount = adultCount;
	}

	public int getPedeCount() {
		return this.pedeCount;
	}

	public void setPedeCount(int pedeCount) {
		this.pedeCount = pedeCount;
	}

	@Override
	public String toString() {
		String nameOut = "";
		for (Drug d : drugs) {
			nameOut += d.getName() + " - ";
		}
		return nameOut.substring(0, nameOut.length() - 3);

	}

	/**
	 * @return
	 */
	public int getTotalCount() {
		return adultCount + pedeCount;
	}
}
