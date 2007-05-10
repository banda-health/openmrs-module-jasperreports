/**
 * 
 */
package org.openmrs.module.jasperreport;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.jasperreport.pepfar.Drug;

/**
 * @author Simon
 *
 */
public class DrugTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.celllife.report.Drug#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		int id = ((Double)Math.random()).intValue();
		String name = "some random ASDna1234";
		
		Drug a = new Drug();
		a.setId(id);
		a.setName(name);
		
		Drug b = new Drug();
		b.setId(id);
		b.setName(name);
		
		assertTrue(a.equals(b));
	}
	
	@Test
	public void testNotEqualsObject() {
		int id = ((Double)Math.random()).intValue();
		String name = "some random ASDna1234";
		
		Drug a = new Drug();
		a.setId(id);
		a.setName(name);
		
		Drug b = new Drug();
		b.setId(id);
		b.setName(name.toLowerCase());
		
		assertFalse(a.equals(b));
	}

}
