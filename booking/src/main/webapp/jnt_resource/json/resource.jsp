<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<c:set var="title" value="${currentNode.properties['jcr:title'].string}" />
<c:set var="type" value="${currentNode.properties['type'].node.properties['jcr:title'].string}" />

<json:object escapeXml="true" prettyPrint="true">
    <json:property name="id" value="${currentNode.UUID}" />
    <json:property name="title" value="${title}" />
    <json:property name="type" value="${type}" />
</json:object>
