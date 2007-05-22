/**
 * 
 */
package org.openmrs.module.jasperreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
//	@Test
//	public void testPrepForSaveString() {
//		ReportParameter one = new ReportParameter();
//		one.setValueClass(String.class);
//		one.setDefault_value("string object");
//		one.prepForSave();
//		assertTrue(one.getDefault_value().equals("string object"));
//	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
//	@Test
//	public void testPrepForSaveDate() {
//		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//		ReportParameter one = new ReportParameter();
//		one.setValueClass(Date.class);
//		Date d = new Date();
//		one.setDefault_object(d);
//		one.prepForSave();
//		assertTrue(one.getDefault_value().equals(dateFormat.format(d)));
//	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
//	@Test
//	public void testPrepForSaveBoolean() {
//		ReportParameter one = new ReportParameter();
//		one.setValueClass(Boolean.class);
//		one.setDefault_object(new Boolean(true));
//		one.prepForSave();
//		assertTrue(one.getDefault_value().equals("true"));
//	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
//	@Test
//	public void testPrepForSaveInteger() {
//		ReportParameter one = new ReportParameter();
//		one.setValueClass(Integer.class);
//		one.setDefault_object(new Integer(1534));
//		one.prepForSave();
//		assertTrue(one.getDefault_value().equals("1534"));
//	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
//	@Test
//	public void testPrepForSaveLocation() {
//		ReportParameter one = new ReportParameter();
//		one.setValueClass(Location.class);
//		one.setDefault_object(new Location(132));
//		one.prepForSave();
//		assertTrue(one.getDefault_value().equals("132"));
//	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
//	@Test
//	public void testPrepForSaveConcept() {
//		ReportParameter one = new ReportParameter();
//		one.setValueClass(Concept.class);
//		one.setDefault_object(new Concept(123));
//		one.prepForSave();
//		assertTrue(one.getDefault_value().equals("123"));
//	}
	
	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportParameter#prepForSave()}.
	 */
	@Test
	public void testPrepForSaveConcept() {
		Integer num = null;
		try {
			num = Integer.parseInt(null);
		} catch (NumberFormatException e) {

		}
System.out.println(num);
	}
}
