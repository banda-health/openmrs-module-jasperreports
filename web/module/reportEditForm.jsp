<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Jasper Reports" otherwise="/login.htm"
	redirect="/module/jasperReport/jreportEdit.form" />

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<script type="text/javascript">
	function addParameter() {
		var tbody = document.getElementById('parametersTbody');
		var row = document.getElementById('parameterRow');
		var newrow = row.cloneNode(true);
		newrow.style.display = "";
		newrow.id = tbody.childNodes.length;
		tbody.appendChild(newrow);
	}
	
	function removeRow(btn) {
		var parent = btn.parentNode;
		while (parent.tagName.toLowerCase() != "tr")
			parent = parent.parentNode;
		
		parent.style.display = "none";
	}
	
	function removeHiddenRows() {
		var rows = document.getElementsByTagName("TR");
		var i = 0;
		while (i < rows.length) {
			if (rows[i].style.display == "none") {
				rows[i].parentNode.removeChild(rows[i]);
			}
			else {
				i = i + 1;
			}
		}
	}	
</script>

<style>
	th {
		text-align: left;
	}
	
	.sButton{
		font-size: .7em;
		border: 1px solid lightgrey;
		background-color: whitesmoke;
		cursor: pointer;
		width: 100px;
		margin: 1px 1px 5px 1px;
	}
	
	table tr td {
		padding: 0 1em 4px 0;	
	}
</style>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="jasperReport.edit.title" /></h2>

<spring:hasBindErrors name="jreport">
	<spring:message code="fix.error" />
	<div class="error"><c:forEach items="${errors.allErrors}" var="error">
		<spring:message code="${error.code}" text="${error.code}" />
		<br />
		<!-- ${error} -->
	</c:forEach></div>
</spring:hasBindErrors>

<br />

<form method="post" enctype="multipart/form-data" onSubmit="removeHiddenRows()">
<table>
	<tr>
		<td><spring:message code="general.name" /></td>
		<td><spring:bind path="jreport.name">
			<input type="text" name="${status.expression}" value="${status.value}" size="35" />
			<c:if test="${status.errorMessage != ''}">
				<c:if test="${status.errorMessage != ''}">
					<span class="error">${status.errorMessage}</span>
				</c:if>
			</c:if>
		</spring:bind></td>
	</tr>
	<tr>
		<td><spring:message code="general.description" /></td>
		<td><spring:bind path="jreport.description">
			<textarea name="${status.expression}" rows="3" cols="65" type="_moz">${status.value}</textarea>
			<c:if test="${status.errorMessage != ''}">
				<c:if test="${status.errorMessage != ''}">
					<span class="error">${status.errorMessage}</span>
				</c:if>
			</c:if>
		</spring:bind></td>
	</tr>
	<tr>
		<td><spring:message code="jasperReport.filename" /></td>
		<td><spring:bind path="jreport.fileName">
			<input type="text" name="${status.expression}" value="${status.value}" size="35" />
			<c:if test="${status.errorMessage != ''}">
				<c:if test="${status.errorMessage != ''}">
					<span class="error">${status.errorMessage}</span>
				</c:if>
			</c:if>
		</spring:bind></td>
	</tr>
	<tr>
		<td><spring:message code="jasperReport.archive" /></td>
		<td><c:if test="${archiveExists}">
			<a href="${pageContext.request.contextPath}/moduleServlet/jasperReport/jreportDownload?reportId=${jreport.reportId}">${jreport.reportId}.zip</a>&nbsp; &nbsp;
			<input type="submit" name="action" value="<spring:message code="jasperReport.reload.parameters"/>">
			<spring:message code="jasperReport.archive.upload.new" />
		</c:if> <c:if test="${!archiveExists}">
			<spring:message code="jasperReport.archive.upload" />
		</c:if> <input type="file" name="report_archive" size="30" /></td>
	</tr>
	<tr>
		<td><spring:message code="jasperReport.published" /></td>
		<td><spring:bind path="jreport.published">
			<input type="hidden" name="_${status.expression}">
			<input type="checkbox" name="${status.expression}" id="${status.expression}"
				<c:if test="${status.value == true}">checked</c:if> />
		</spring:bind></td>
	</tr>
	<c:if test="${jreport.reportId != null && archiveExists}">
		<tr>
			<td colspan=2>
			<h3><spring:message code="jasperReport.edit.parameters" /></h3>
			</td>
		</tr>
		<table id="parameters">
			<tr>
				<th><spring:message code="jasperReport.displayName" /></th>
				<th><spring:message code="jasperReport.default_value" /></th>
				<th><spring:message code="general.name" /></th>
				<th><spring:message code="jasperReport.class" /></th>
				<th><spring:message code="jasperReport.visible" /></th>
				<th></th>
			</tr>
			<tbody id="parametersTbody">
				<c:forEach var="parameter" items="${jreport.parameters}" varStatus="varStatus">
					<tr>
						<spring:nestedPath path="jreport.parameters[${varStatus.index}]">
							<td><spring:bind path="displayName">
								<input type="text" name="${status.expression}" value="${status.value}" size="25" />
								<c:if test="${status.errorMessage != ''}">
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</c:if>
							</spring:bind></td>
							<td><spring:bind path="default_value">
								<input type="text" name="${status.expression}" value="${status.value}" size="20"
									<c:if test="${parameter.valueClass == 'class java.util.Date'}">onClick="showCalendar(this);"</c:if> />
								<c:if test="${status.errorMessage != ''}">
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</c:if>
							</spring:bind></td>
							<td><c:out value="${parameter.name}" /></td>
							<td><c:out value="${parameter.valueClass.name}" /></td>
							<td><spring:bind path="visible">
								<input type="hidden" name="_${status.expression}">
								<input type="checkbox" name="${status.expression}" id="${status.expression}"
									<c:if test="${status.value == true}">checked</c:if> />
							</spring:bind></td>
							<td valign="middle" align="center"><c:if test="${parameter.dynamic}">
								<input type="checkbox" name="remove" id="remove" />
								<spring:message code="general.remove" />
							</c:if></td>
						</spring:nestedPath>
					</tr>
				</c:forEach>
				<tr id="parameterRow">
					<td><input type="text" name="displayName" size="25" /></td>
					<td><input type="text" name="default_value" size="20" /></td>
					<td><input type="text" name="pname" size="20" /></td>
					<td><select name="valueClass">
						<c:forEach var="clazz" items="${classes}">
							<option value="${clazz.name}">${clazz.name}</option>
						</c:forEach>
					</select></td>
					<td></td>
					<td valign="middle" align="center"><input type="button" name="closeButton" onClick="return removeRow(this);"
						class="closeButton" value='<spring:message code="general.remove"/>' /></td>
				</tr>
			</tbody>
		</table>
		<input type="button" class="sButton" onclick="addParameter()" width="100px"
			value="<spring:message code="jasperReport.addParameter" />" hidefocus />
	</c:if>
</table>
<br />
<input type="submit" name="action" value="<spring:message code="jasperReport.save"/>"> <c:if
	test="${jreport.reportId != null}">
	<openmrs:hasPrivilege privilege="Manage Jasper Reports">
			 &nbsp; &nbsp; &nbsp;
			<input type="submit" name="action" value="<spring:message code="jasperReport.delete"/>"
			onclick="return confirm('Are you sure you want to delete this entire report and all sub-reports?')" />
	</openmrs:hasPrivilege>
</c:if></form>

<script type="text/javascript">
	document.getElementById("parameterRow").style.display = "none";
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>
