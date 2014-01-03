<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="b" uri="http://www.lgf.org/tags/jahia/booking" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>

<c:set var="title" value="${currentNode.properties['jcr:title'].string}" />
<c:set var="max" value="${currentNode.properties['max'].long}" />
<c:set var="bookingPlanningNode" value="${currentNode.properties['bookingPlanningRef'].node}" />

<template:addResources type="css" resources="booking.css"/>

<template:addCacheDependency path="${bookingPlanningNode.path}/[^/]*/[^/]*" />

<c:set var="sql" value="select * from [jnt:booking] as b  WHERE ISDESCENDANTNODE(b,'${bookingPlanningNode.path}') AND b.[ownerRef] = '${renderContext.user.identifier}'  AND b.[start] >= '${b:now()}' order by b.[start] " />

<query:definition var="listQuery"  statement="${sql }" limit="${max }"/>

<c:set target="${moduleMap}" property="editable" value="false"/>
<c:set target="${moduleMap}" property="listQuery" value="${listQuery}"/>
<c:set target="${moduleMap}" property="subNodesView" value="my" />

<c:if test="${not empty title and not jcr:isNodeType(currentNode, 'jmix:skinnable')}">
	<h3>${title}</h3>
</c:if>