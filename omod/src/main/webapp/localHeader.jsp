<%--@elvariable id="pom" type="org.databene.benerator.gui.MavenDependency"--%>

<ul id="menu">
	<li class="first"><a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short" /></a>
	</li>
	<openmrs:hasPrivilege privilege="Manage Jasper Reports">
		<li <c:if test="<%= request.getRequestURI().contains(\"jasperreport/report\") %>">class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/${pom.parent.artifactId}/jreport.list">
				<spring:message code="jasperreport.manage" />
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>
