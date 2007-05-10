<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Jasper Reports" otherwise="/login.htm" redirect="/module/jasperReport/jreport.list" />
	
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="jasperReport.manage" /></h2>	

<a href="jreportEdit.form"><spring:message code="jasperReport.add" /></a>

<br />
<br />

<b class="boxHeader">
	<spring:message code="jasperReport.list.title" />
</b>
<form method="post" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th> </th>
			<th> <spring:message code="general.name" /> </th>
			<th> <spring:message code="jasperReport.published" /> </th>

		</tr>
		<c:forEach var="report" items="${reportList}">
			<tr>
				<td valign="top" style="white-space: nowrap">
				<a href="jreportGenerate.form?reportId=${report.reportId}"><spring:message code="jasperReport.generate"/></a> | 
					<a href="jreportEdit.form?reportId=${report.reportId}"><spring:message code="jasperReport.edit"/></a>
				</td>
				<td valign="top" style="white-space: nowrap">${report.name}</td>
				<td valign="top"><c:if test="${report.published == true}"><spring:message code="general.yes"/></c:if></td>
			</tr>
		</c:forEach>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>