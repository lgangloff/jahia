<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>

<c:set var="title" value="${currentNode.properties['jcr:title'].string}" />
<c:set var="type" value="${currentNode.properties['type'].node}" />
<c:set var="color" value="${currentNode.properties['color'].string}" />

<span style="color: ${color}">
	(${type.properties['jcr:title'].string}) ${title}
</span>

