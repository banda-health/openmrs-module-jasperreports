/**
 * 
 */
package org.openmrs.module.jasperreport;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon
 *
 */
public class ReportParameterTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsNoId() {
		ReportParameter one = new ReportParameter();
		one.setName("one");
		one.setValueClass(java.util.Date.class);
		
		ReportParameter two = new ReportParameter();
		two.setName("one");
		two.setValueClass(java.util.Date.class);

		assertEquals(one, two);
	}

	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsOneId() {
		ReportParameter one = new ReportParameter();
		one.setName("one");
		one.setValueClass(java.util.Date.class);
		one.setId(3);
		
		ReportParameter two = new ReportParameter();
		two.setName("one");
		two.setValueClass(java.util.Date.class);

		assertEquals(one, two);
	}

	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsBothId() {
		ReportParameter one = new ReportParameter();
		one.setName("one");
		one.setValueClass(java.util.Date.class);
		one.setId(6);
		
		ReportParameter two = new ReportParameter();
		two.setName("one");
		two.setValueClass(java.util.Date.class);
		two.setId(6);

		assertEquals(one, two);
	}

	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals1() {
		ReportParameter one = new ReportParameter();
		one.setName("two");
		one.setValueClass(java.util.Date.class);
		
		ReportParameter two = new ReportParameter();
		two.setName("one");
		two.setValueClass(java.util.Date.class);

		assertFalse(one.equals(two));
	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals2() {
		ReportParameter one = new ReportParameter();
		one.setName("one");
		one.setValueClass(java.util.Date.class);
		
		ReportParameter two = new ReportParameter();
		two.setName("one");
		two.setValueClass(java.lang.String.class);

		assertFalse(one.equals(two));
	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals3() {
		ReportParameter one = new ReportParameter();
		one.setName("one");
		one.setValueClass(java.util.Date.class);
		one.setId(1);
		
		ReportParameter two = new ReportParameter();
		two.setName("one");
		two.setValueClass(java.util.Date.class);
		two.setId(5);

		assertFalse(one.equals(two));
	}
	
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals4() {
		ReportParameter one = new ReportParameter();
		one.setName("one");
			
		ReportParameter two = new ReportParameter();
		two.setName("one");
		
		assertTrue(one.equals(two));
	}
}
