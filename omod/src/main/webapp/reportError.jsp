<%@ page import="org.openmrs.module.jasperreport.util.JasperReportConstants" %>
<%--
  ~ The contents of this file are subject to the OpenMRS Public License
  ~ Version 2.0 (the "License"); you may not use this file except in
  ~ compliance with the License. You may obtain a copy of the License at
  ~ http://license.openmrs.org
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
  ~ the License for the specific language governing rights and
  ~ limitations under the License.
  ~
  ~ Copyright (C) OpenHMIS.  All Rights Reserved.
  --%>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<b class="boxHeader">Report Generation Error</b>

<div class="box">
	<table>
		<tr>
			<td><h2>Report ${reportId} Generation Error</h2></td>
		</tr>
		<tr>
			<td><p>${message}</p></td>
		</tr>
		<tr>
			<td><p>Please make sure that the report is added in the system. Click
				<a href="${pageContext.request.contextPath}<%= JasperReportConstants.REPORT_LIST_PAGE %>">
					Here
				</a> to add the report.</p></td>
		</tr>
		<tr>
			<td>
				<p>For Documentation on how to add the report . Click <a
						href="https://wiki.openmrs.org/display/docs/Jasper+Report+Module" target="_blank">Here</a>.</p>
			</td>
		</tr>
	</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>
