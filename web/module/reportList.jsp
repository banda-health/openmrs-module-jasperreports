<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Jasper Reports"
	otherwise="/login.htm" redirect="/module/jasperReport/jreport.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript">
<!--
function SetAllCheckBoxes(FormName, FieldName, CheckValue)
{
	if(!document.forms[FormName])
		return;
	var objCheckBoxes = document.forms[FormName].elements[FieldName];
	if(!objCheckBoxes)
		return;
	var countCheckBoxes = objCheckBoxes.length;
	if(!countCheckBoxes)
		objCheckBoxes.checked = CheckValue;
	else
		// set the check value for all check boxes
		for(var i = 0; i < countCheckBoxes; i++)
			objCheckBoxes[i].checked = CheckValue;
}
// -->
</script>

<h2><spring:message code="jasperReport.manage" /></h2>

<a href="jreportEdit.form"><spring:message code="jasperReport.add" /></a>

<br />
<br />

<b class="boxHeader"> <spring:message code="jasperReport.list.title" />
</b>
<form method="post" class="box">
<table cellpadding="2" cellspacing="0">
	<tr>
		<th></th>
		<th><spring:message code="general.name" /></th>
		<th><spring:message code="jasperReport.published" /></th>

	</tr>
	<c:forEach var="report" items="${reportList}">
		<tr>
			<td valign="top" style="white-space: nowrap"><a
				href="jreportGenerate.form?reportId=${report.reportId}"><spring:message
				code="jasperReport.generate" /></a> | <a
				href="jreportEdit.form?reportId=${report.reportId}"><spring:message
				code="jasperReport.edit" /></a></td>
			<td valign="top" style="white-space: nowrap">${report.name}</td>
			<td valign="top"><c:if test="${report.published == true}">
				<spring:message code="general.yes" />
			</c:if></td>
		</tr>
	</c:forEach>
</table>
</form>
<br />

<b class="boxHeader"> <spring:message
	code="jasperReport.generated.list.title" /> </b>
<form method="post" class="box" name="genReports">
<table cellpadding="2" cellspacing="0">
	<tr>
		<th></th>
		<th><spring:message code="general.name" /></th>

	</tr>
	<c:forEach var="genReport" items="${generatedReports}"
		varStatus="varStatus">
		<tr>
			<td><input type="checkbox" name="genReport"
				id="genReport_${varStatus.index}" value="${varStatus.index}"
				<c:if test="${genReport.delete == true}">checked</c:if> /></td>
			<td><a
				href="${pageContext.request.contextPath}/moduleServlet/jasperReport/jreportDownload?reportName=${genReport.reportFileName}"><c:out
				value="${genReport.reportFileName}" /></a></td>
		</tr>
	</c:forEach>
</table>

<input type="button"
	onclick="SetAllCheckBoxes('genReports', 'genReport', true);"
	value="Select All"> <input type="button"
	onclick="SetAllCheckBoxes('genReports', 'genReport', false);"
	value="Select None"> <openmrs:hasPrivilege
	privilege="Manage Jasper Reports">
			 &nbsp; &nbsp; &nbsp;
			<input type="submit" name="action"
		value="<spring:message code="jasperReport.delete.selected"/>"
		onclick="return confirm('Are you sure you want to delete the selected reports?')" />
</openmrs:hasPrivilege></form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
