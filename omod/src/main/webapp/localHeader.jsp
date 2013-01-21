<ul id="menu">
	<li class="first"><a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short" /></a>
	</li>
	<openmrs:hasPrivilege privilege="Manage Jasper Reports">
		<li <c:if test="<%= request.getRequestURI().contains(\"jasperreports/view\") %>">class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/jasperreports/jreport.list" class="retired">
				<spring:message	code="jasperreports.manage" />
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>
