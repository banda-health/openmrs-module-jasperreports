<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Jasper Reports" otherwise="/login.htm"
	redirect="/module/jasperReport/jreportEdit.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<h2><spring:message code="jasperReport.generate.title" /></h2>

<spring:hasBindErrors name="jreport">
	<spring:message code="fix.error" />
	<div class="error"><c:forEach items="${errors.allErrors}" var="error">
		<spring:message code="${error.code}" text="${error.code}" />
		<br />
		<!-- ${error} -->
	</c:forEach></div>
</spring:hasBindErrors>

<br />

<h3><c:out value="${jreport.name}" /></h3>
<p><c:out value="${jreport.description}" escapeXml="false" /></p>

Please enter the following parameters:
<form method="post">
<table>
	<c:forEach var="parameter" items="${jreport.parameters}">
		<c:if test="${parameter.visible}">
		<tr>
			<td><c:out value="${parameter.displayName}" /></td>
			<td><input type="text" name="param_${parameter.id}" size="15" value="${parameter.default_value}"
					<c:if test="${parameter.valueClass == 'class java.util.Date'}">onClick="showCalendar(this);" (format: ${datePattern})</c:if> />
					<c:if test="${parameter.valueClass == 'class java.util.Date'}"> (format: ${datePattern})</c:if>
			</td>
		</tr>
		</c:if>
	</c:forEach>
</table>
<br />
<input type="submit" name="action" value="<spring:message code="jasperReport.generate"/>"></form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
