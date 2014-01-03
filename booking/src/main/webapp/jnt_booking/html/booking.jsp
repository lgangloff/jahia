<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>

<c:set var="start" value="${currentNode.properties['start'].time}" />
<c:set var="end" value="${currentNode.properties['end'].time}" />
<c:set var="resourceRef" value="${currentNode.properties['resourceRef'].node}" />
<c:set var="resourceRefTitle" value="${resourceRef.properties['jcr:title'].string}" />
<c:set var="status" value="${currentNode.properties['status'].string}" />


<fmt:message key="booking.datetime.format" var="pattern"/>

<fmt:formatDate var="start" value="${start}" pattern="${pattern }"/>
<fmt:formatDate var="end" value="${end}" pattern="${pattern }"/>

<div class="booking">
	${resourceRefTitle}: ${start} -> ${end} (<fmt:message key="${status}.key"/>)
</div>
