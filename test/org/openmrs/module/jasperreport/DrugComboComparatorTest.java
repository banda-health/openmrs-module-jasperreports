/**
 * 
 */
package org.openmrs.module.jasperreport;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.jasperreport.pepfar.DrugCombination;
import org.openmrs.module.jasperreport.pepfar.DrugComboComparator;

/**
 * @author Simon
 * 
 */
public class DrugComboComparatorTest {

	private final DrugComboComparator comp = new DrugComboComparator();

	@Test
	public void testCompareEqual() {
		DrugCombination combo1 = new DrugCombination();
		combo1.setAdultCount(10);
		combo1.setPedeCount(5);

		DrugCombination combo2 = new DrugCombination();
		combo2.setAdultCount(3);
		combo2.setPedeCount(12);

		assertTrue(comp.compare(combo1, combo2) == 0);
	}
	
	@Test
	public void testCompareTransitive() {
		DrugCombination x = new DrugCombination();
		x.setAdultCount(10);
		x.setPedeCount(5);

		DrugCombination y = new DrugCombination();
		y.setAdultCount(5);
		y.setPedeCount(8);

		DrugCombination z = new DrugCombination();
		z.setAdultCount(0);
		z.setPedeCount(6);

		assertTrue(comp.compare(x,y) > 0);
		assertTrue(comp.compare(y,z) > 0);
		assertTrue(comp.compare(x,z) > 0);
	}

	@Test
	public void testCompareTransitive2() {
		DrugCombination x = new DrugCombination();
		x.setAdultCount(10);
		x.setPedeCount(5);

		DrugCombination y = new DrugCombination();
		y.setAdultCount(5);
		y.setPedeCount(10);

		DrugCombination z = new DrugCombination();
		z.setAdultCount(0);
		z.setPedeCount(6);

		assertTrue(comp.compare(x,y) == 0);
		assertTrue(sgn(comp.compare(x,z)) == sgn(comp.compare(y,z)));
	}

	/**
	 * @param i
	 * @return
	 */
	private int sgn(int i) {
		if (i > 0)
			return 1;
		else if (i < 0)
			return -1;
		else
			return i;
	}
}
