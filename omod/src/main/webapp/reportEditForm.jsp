<%--@elvariable id="pom" type="org.databene.benerator.gui.MavenDependency"--%>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Jasper Reports"
	otherwise="/login.htm" redirect="/module/${pom.parent.artifactId}/jreportEdit.form" />

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h2>
	<c:if test="${jreport.reportId > 0}">
		<spring:message code="jasperreport.edit.title" />
	</c:if>
	<c:if test="${jreport.reportId == null}">
		<spring:message code="jasperreport.add.title" />
	</c:if>
</h2>

<spring:hasBindErrors name="jreport">
	<spring:message code="fix.error" />
	<div class="error"><c:forEach items="${errors.allErrors}"
		var="error">
		<spring:message code="${error.code}" text="${error.code}" />
		<br />
		<!-- ${error} -->
	</c:forEach></div>
</spring:hasBindErrors>

<br />

<form method="post" enctype="multipart/form-data">
<table width="100%">
	<tr>
		<td><spring:message code="general.name" /></td>
		<td><spring:bind path="jreport.name">
			<input type="text" name="${status.expression}"
				value="${status.value}" size="35" />
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
		<td><spring:message code="${pom.parent.artifactId}.filename" /></td>
		<td><spring:bind path="jreport.fileName">
			<input type="text" name="${status.expression}"
				value="${status.value}" size="35" />
			<c:if test="${status.errorMessage != ''}">
				<c:if test="${status.errorMessage != ''}">
					<span class="error">${status.errorMessage}</span>
				</c:if>
			</c:if>
		</spring:bind></td>
	</tr>
	<tr>
		<td><spring:message code="${pom.parent.artifactId}.archive" /></td>
		<td><c:if test="${archiveExists}">
			<a
				href="${pageContext.request.contextPath}/moduleServlet/${pom.parent.artifactId}/jreportDownload?reportId=${jreport.reportId}">${jreport.reportId}.zip</a>&nbsp; &nbsp;
			<spring:message code="${pom.parent.artifactId}.archive.upload.new" />
		</c:if> <c:if test="${!archiveExists}">
			<spring:message code="${pom.parent.artifactId}.archive.upload" />
		</c:if> <input type="file" name="report_archive" size="30" /></td>
	</tr>
	<tr>
		<td><spring:message code="${pom.parent.artifactId}.published" /></td>
		<td><spring:bind path="jreport.published">
			<input type="hidden" name="_${status.expression}">
			<input type="checkbox" name="${status.expression}"
				id="${status.expression}"
				<c:if test="${status.value == true}">checked</c:if> />
		</spring:bind></td>
	</tr>
</table>
<br />
<c:if test="${jreport.reportId != null && archiveExists}">
	<b class="boxHeader"><spring:message
		code="${pom.parent.artifactId}.edit.parameters" /></b>
	<table id="parameters" class="box">
		<tr align="left">
			<th><spring:message code="${pom.parent.artifactId}.displayName" /></th>
			<th><spring:message code="${pom.parent.artifactId}.default_value" /></th>
			<th><spring:message code="general.name" /></th>
			<th><spring:message code="${pom.parent.artifactId}.class" /></th>
			<th><spring:message code="${pom.parent.artifactId}.class.mapped" /></th>
			<th><spring:message code="${pom.parent.artifactId}.visible" /></th>
		</tr>
		<tbody id="parametersTbody">
			<c:forEach var="parameter" items="${jreport.parameters}"
				varStatus="varStatus">
				<tr
					class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
					<spring:nestedPath path="jreport.parameters[${varStatus.index}]">
						<td><spring:bind path="displayName">
							<input type="text" name="${status.expression}"
								value="${status.value}" size="25" />
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</spring:bind></td>
						<td><c:choose>
							<c:when
								test="${parameter.interfaceClass == 'class java.lang.Boolean'}">
								<spring:bind path="valueBoolean">
									<select name="${status.expression}" id="valueBooleanSelect">
										<option value=""
											<c:if test="${empty status.value}">selected="selected"</c:if>></option>
										<option value="true"
											<c:if test="${status.value == true}">selected="selected"</c:if>><spring:message
											code="general.true" /></option>
										<option value="false"
											<c:if test="${status.value == false}">selected="selected"</c:if>><spring:message
											code="general.false" /></option>
									</select>
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind>
							</c:when>
							<c:when
								test="${parameter.interfaceClass == 'class org.openmrs.Concept'}">
								<spring:bind path="valueConcept">
									<openmrs:fieldGen type="org.openmrs.Concept"
										formFieldName="${status.expression}" val="${status.value}"
										parameters="isNullable=true" />
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind>
							</c:when>
							<c:when
								test="${parameter.interfaceClass == 'class java.util.Date'}">
								<spring:bind path="valueDate">
									<input type="text" name="${status.expression}" size="10"
										value="${status.value}" onClick="showCalendar(this)"
										id="${status.expression}" />
									<span class="datePatternHint">(${datePattern})</span>
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind>
							</c:when>
							<c:when
								test="${parameter.interfaceClass == 'class org.openmrs.Location'}">
								<spring:bind path="valueLocation">
									<select name="${status.expression}">
										<option value=""
											<c:if test="${status.value == null}">selected="selected"</c:if>></option>
										<openmrs:forEachRecord name="location">
											<option value="${record.locationId}"
												<c:if test="${status.value == record.locationId}">selected="selected"</c:if>>${record.name}</option>
										</openmrs:forEachRecord>
									</select>
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind>
							</c:when>
							<c:otherwise>
								<spring:bind path="default_value">
									<input type="text" name="${status.expression}"
										value="${status.value}" size="10" />
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind>
							</c:otherwise>
						</c:choose></td>
						<td><c:out value="${parameter.name}" /></td>
						<td><c:out value="${parameter.valueClass.name}" /></td>
						<td><c:if
							test="${parameter.valueClass == 'class java.lang.Integer'}">
							<spring:bind path="mappedClass">
								<select name="${status.expression}">
									<c:forEach var="clazz" items="${classes}">
										<option value="${clazz.name}"
											<c:if test="${parameter.mappedClass == clazz}">selected="selected"</c:if>>${clazz.name}</option>
									</c:forEach>
								</select>
								<c:if test="${status.errorMessage != ''}">
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</c:if>
							</spring:bind>
						</c:if></td>
						<td><spring:bind path="visible">
							<input type="hidden" name="_${status.expression}">
							<input type="checkbox" name="${status.expression}"
								id="${status.expression}"
								<c:if test="${status.value == true}">checked</c:if> />
						</spring:bind></td>
					</spring:nestedPath>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if> <br />
<input type="submit" name="action"
	value="<spring:message code="${pom.parent.artifactId}.save"/>"> <c:if
	test="${jreport.reportId != null}">
	<openmrs:hasPrivilege privilege="Manage Jasper Reports">
			 &nbsp; &nbsp; &nbsp;
			<input type="submit" name="action"
			value="<spring:message code="${pom.parent.artifactId}.delete"/>"
			onclick="return confirm('Are you sure you want to delete this entire report and all sub-reports?')" />
	</openmrs:hasPrivilege>
</c:if></form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
