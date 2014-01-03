<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>

<c:set var="start" value="${currentNode.properties['start'].time}" />
<c:set var="end" value="${currentNode.properties['end'].time}" />
<c:set var="resourceRef" value="${currentNode.properties['resourceRef'].node}" />
<c:set var="resourceRefTitle" value="${resourceRef.properties['jcr:title'].string}" />
<c:set var="ownerRef" value="${currentNode.properties['ownerRef'].node}" />


<fmt:message key="booking.datetime.format" var="pattern"/>
<fmt:formatDate var="start" value="${start}" pattern="${pattern }"/>


<fmt:message var="title" key="booking.json.title">
	<fmt:param value="${resourceRefTitle}" />
	<fmt:param value="${ownerRef.name}" />
</fmt:message>

<c:url value="${url.base}${currentNode.path}.validate.do" var="validateBookingActionUrl"/>

<form method="POST" action="${validateBookingActionUrl }">
	${start} : ${title}
    <input type="hidden" name="jcrRedirectTo" value="<c:url value='${url.base}${renderContext.mainResource.node.path}'/>">
	<input type="submit" value="<fmt:message key="booking.validate"/>"/>
</form>
