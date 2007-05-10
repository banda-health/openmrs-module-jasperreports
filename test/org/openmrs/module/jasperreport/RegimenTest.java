/**
 * 
 */
package org.openmrs.module.jasperreport;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.jasperreport.pepfar.Drug;
import org.openmrs.module.jasperreport.pepfar.DrugCombination;

/**
 * @author Simon
 *
 */
public class RegimenTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.celllife.report.DrugCombination#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Drug a = new Drug();
		a.setId(1);
		a.setName("drug 1");
		
		Drug b = new Drug();
		b.setId(2);
		b.setName("drug 2");
		
		Drug c = new Drug();
		c.setId(3);
		c.setName("drug 4");
		
		DrugCombination reg1 = new DrugCombination();
		reg1.addDrug(a);
		reg1.addDrug(b);
		reg1.addDrug(c);
		
		DrugCombination reg2 = new DrugCombination();
		reg2.addDrug(b);
		reg2.addDrug(a);
		reg2.addDrug(c);
		
		assertTrue(reg1.equals(reg2));
	}
	
	/**
	 * Test method for {@link org.celllife.report.DrugCombination#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject2() {
		Drug a = new Drug();
		a.setId(1);
		a.setName("drug 1");
		
		Drug b = new Drug();
		b.setId(2);
		b.setName("drug 2");
		
		Drug a1 = new Drug();
		a1.setId(1);
		a1.setName("drug 1");
		
		Drug b1 = new Drug();
		b1.setId(2);
		b1.setName("drug 2");
		
		DrugCombination reg1 = new DrugCombination();
		reg1.addDrug(a);
		reg1.addDrug(b);
			
		DrugCombination reg2 = new DrugCombination();
		reg2.addDrug(b1);
		reg2.addDrug(a1);
				
		assertTrue(reg1.equals(reg2));
	}

}
