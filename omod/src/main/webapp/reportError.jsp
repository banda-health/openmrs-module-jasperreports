<%@ page import="org.openmrs.module.jasperreport.util.JasperReportConstants" %>
<%@ page import="org.openmrs.module.jasperreport.util.JasperReportPrivilegeConstants" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require allPrivileges="<%=JasperReportPrivilegeConstants.VIEW_JASPER_REPORTS%>"
                 otherwise="/login.htm"
                 redirect="<%= JasperReportConstants.REPORT_ERROR_PAGE %>"/>
<%@ include file="/WEB-INF/template/header.jsp"%>
<h2>Report Generation Error</h2>

<p>The report with the Id ${reportId} cannot be generated</p>

<p>Please make sure that the report is added in the system. Click
	<a href="${pageContext.request.contextPath}<%= JasperReportConstants.REPORT_LIST_PAGE %>">
		Here
	</a> to add the report.</p>

<p>For Documentation on how to add the report . Click <a
		href="https://wiki.openmrs.org/display/docs/Jasper+Report+Module" target="_blank">Here</a>.</p>
<%@ include file="/WEB-INF/template/footer.jsp" %>
