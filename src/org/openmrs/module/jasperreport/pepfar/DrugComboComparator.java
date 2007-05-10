/**
 * 
 */
package org.openmrs.module.jasperreport.pepfar;

import java.util.Comparator;


/**
 * Used to comapre two DrugCominations
 * 
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * 
 * @author Simon
 *
 */
public class DrugComboComparator implements Comparator<DrugCombination> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(DrugCombination o1, DrugCombination o2) {
		return o1.getTotalCount() - o2.getTotalCount();
	}

}
