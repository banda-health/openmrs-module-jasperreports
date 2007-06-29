<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Jasper Reports"
	otherwise="/login.htm" redirect="/module/@MODULE_ID@/jreport.list" />

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
<c:if test="${refresh == true}">
	<script type="text/javascript"> 
	<!--
	// CREDITS: 
	// Automatic Page Refresher by Peter Gehrig and Urs Dudli www.24fun.com 
	// Permission given to use the script provided that this notice remains as is. 
	// Additional scripts can be found at http://www.hypergurl.com
	// Configure refresh interval (in seconds) 
	var refreshinterval=30
	// Shall the coundown be displayed inside your status bar? Say "yes" or "no" below:
	var displaycountdown="yes" 
	// Do not edit the code below
	var starttime
	var nowtime
	var reloadseconds=0
	var secondssinceloaded=0 
	function starttime() { 
		starttime=new Date()
		starttime=starttime.getTime()
		countdown() 
	} 
	function countdown() { 
		nowtime= new Date() 
		nowtime=nowtime.getTime() 
		secondssinceloaded=(nowtime-starttime)/1000 
		reloadseconds=Math.round(refreshinterval-secondssinceloaded) 
		if (refreshinterval>=secondssinceloaded){
			var timer=setTimeout("countdown()",1000)
			if (displaycountdown=="yes"){
				window.status="Page refreshing in "+reloadseconds+ " seconds"
			}
		} 
		else { 
			clearTimeout(timer)
			window.location.reload(true)
		}
	} 
	
		window.onload=starttime
	
	// -->
	</script>
</c:if>

<h2><spring:message code="@MODULE_ID@.manage" /></h2>

<a href="jreportEdit.form"><spring:message code="@MODULE_ID@.add" /></a>

<br />
<br />

<b class="boxHeader"><spring:message code="@MODULE_ID@.list.title" /></b>
<form method="post" class="box">
<table cellpadding="2" cellspacing="0">
	<tr>
		<th></th>
		<th><spring:message code="general.name" /></th>
		<th><spring:message code="@MODULE_ID@.published" /></th>

	</tr>
	<c:forEach var="report" items="${reportList}">
		<tr>
			<td valign="top" style="white-space: nowrap"><a
				href="jreportGenerate.form?reportId=${report.reportId}"><spring:message
				code="@MODULE_ID@.generate" /></a> | <a
				href="jreportEdit.form?reportId=${report.reportId}"><spring:message
				code="@MODULE_ID@.edit" /></a></td>
			<td valign="top" style="white-space: nowrap">${report.name}</td>
			<td valign="top"><c:if test="${report.published == true}">
				<spring:message code="general.yes" />
			</c:if></td>
		</tr>
	</c:forEach>
</table>
</form>
<br />

<table width="100%">
	<tr>
		<td valign="top"><b class="boxHeader"> <spring:message
			code="@MODULE_ID@.generated.list.title" /> </b>
		<form method="post" class="box" name="genReports">
		<table cellpadding="2" cellspacing="0">
			<c:forEach var="genReport" items="${generatedReports}"
				varStatus="varStatus">
				<tr>
					<c:if test="${genReport.generating == false}">
						<td><input type="checkbox" name="genReport"
							id="genReport_${varStatus.index}" value="${varStatus.index}"
							<c:if test="${genReport.delete == true}">checked</c:if> /></td>
						<td><a
							href="${pageContext.request.contextPath}/moduleServlet/@MODULE_ID@/jreportDownload?reportName=${genReport.reportFileName}"><c:out
							value="${genReport.reportFileName}" /></a></td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
		<br />
		<input type="button"
			onclick="SetAllCheckBoxes('genReports', 'genReport', true);"
			value="Select All"> <input type="button"
			onclick="SetAllCheckBoxes('genReports', 'genReport', false);"
			value="Select None"> <openmrs:hasPrivilege
			privilege="Manage Jasper Reports">
			 &nbsp; &nbsp; &nbsp;
			<input type="submit" name="action"
				value="<spring:message code="@MODULE_ID@.delete.selected"/>"
				onclick="return confirm('Are you sure you want to delete the selected reports?')" />
		</openmrs:hasPrivilege></form>
		</td>
		<c:if test="${refresh == true}">
			<td width="50%" valign="top"><b class="boxHeader"> <spring:message
				code="@MODULE_ID@.generating.list.title" /> </b>
			<div class="box" id="genReportListing">
			<table cellpadding="2" cellspacing="0">
				<c:forEach var="genReport" items="${generatingReports}"
					varStatus="varStatus">
					<c:if test="${genReport.generating == true}">
						<tr>
							<td>"<c:out value="${genReport.reportFileName}" />" report
							is being generated.</td>
						</tr>
					</c:if>
				</c:forEach>
			</table>
			</div>
			</td>
		</c:if>
	</tr>
</table>
<%@ include file="/WEB-INF/template/footer.jsp"%>
