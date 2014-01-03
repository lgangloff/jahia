<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<c:set var="start" value="${currentNode.properties['start'].date.time}" />
<c:set var="end" value="${currentNode.properties['end'].date.time}" />
<c:set var="resourceRef" value="${currentNode.properties['resourceRef'].node}" />
<c:set var="ownerRef" value="${currentNode.properties['ownerRef'].node}" />
<c:set var="title" value="${resourceRef.properties['jcr:title'].string}" />
<c:set var="color" value="${resourceRef.properties['color'].string}" />
<c:set var="status" value="${currentNode.properties['status'].string}" />

<fmt:formatDate var="startJs" value="${start}" pattern="yyyy-MM-dd HH:mm"/>
<fmt:formatDate var="endJs" value="${end}" pattern="yyyy-MM-dd HH:mm"/>

<fmt:message var="title" key="booking.json.title">
	<fmt:param value="${title}" />
	<fmt:param value="${ownerRef.name}" />
</fmt:message>
	
<c:if test="${status == '01_created'}">
	<c:set var="color" value="#848484" />
	
	<fmt:message var="title" key="booking.json.title.tovalidate">
		<fmt:param value="${title}" />
	</fmt:message>
</c:if>


<json:object escapeXml="true" prettyPrint="true">

    <json:property name="title" value="${title}" />
    <json:property name="start" value="${startJs}" />
    <json:property name="end" value="${endJs}" />
    <json:property name="allDay" value="${false}" />
    <json:property name="color" value="${color}" />

</json:object>
