/**
 * 
 */
package org.openmrs.module.jasperreport;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon
 *
 */
public class ReportDeployerTest {

	private JasperReport report = new JasperReport();
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Set<ReportParameter> newParams = new HashSet<ReportParameter>();
		report.setName("name");
		report.setReportId(1);
		ReportParameter p = new ReportParameter();
		p.setId(1);
		p.setName("name1");
		p.setDisplayName("displayName1");
		p.setValueClass(java.lang.String.class);
		newParams.add(p);
		
		p = new ReportParameter();
		p.setId(2);
		p.setName("name2");
		p.setDisplayName("displayName2");
		p.setValueClass(java.lang.String.class);
		newParams.add(p);
		
		p = new ReportParameter();
		p.setId(3);
		p.setName("name3");
		p.setDisplayName("displayName3");
		p.setValueClass(java.lang.String.class);
		newParams.add(p);
		
		report.setParameters(newParams);
		
	}

	/**
	 * Test method for {@link org.openmrs.module.jasperreport.ReportDeployer#refreshParameters(org.openmrs.module.jasperreport.JasperReport)}.
	 */
	@Test
	public void testMergeParameters() {
		Set<ReportParameter> newParams = new HashSet<ReportParameter>();
		ReportParameter p = new ReportParameter();
		p.setId(1);
		p.setName("name1");
		p.setValueClass(java.lang.String.class);
		newParams.add(p);
		
		p = new ReportParameter();
		p.setName("name2");
		p.setValueClass(java.lang.String.class);
		newParams.add(p);
		
		p = new ReportParameter();
		p.setName("name4");
		p.setDisplayName("displayName4");
		p.setValueClass(java.lang.String.class);
		newParams.add(p);
		
		ReportDeployer.mergerParameters(report, newParams);
		
		assertTrue(report.getParameters().contains(p));
		for(ReportParameter param : report.getParameters()) {
			System.out.println(param.getName() + " : " + param.getDisplayName());
			assertFalse(param.getDisplayName().equals(""));
		}
	}

}
