<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="b" uri="http://www.lgf.org/tags/jahia/booking" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>

<%--@elvariable id="selectorType" type="org.jahia.services.content.nodetypes.SelectorType"--%>

<c:set var="title" value="${currentNode.properties['jcr:title'].string}" />
<c:set var="bookingPlanningNode" value="${currentNode.properties['bookingPlanningRef'].node}" />

<template:addCacheDependency path="${bookingPlanningNode.path}/[^/]*/[^/]*" />

<template:addResources type="css" resources="booking.css"/>

<c:set target="${moduleMap}" property="currentList" value="${b:getBookingToBeValidated(bookingPlanningNode)}" />
<c:set target="${moduleMap}" property="end" value="${functions:length(moduleMap.currentList)}"/>
<c:set target="${moduleMap}" property="listTotalSize" value="${moduleMap.end}"/>
<c:set target="${moduleMap}" property="subNodesView" value="tovalidate" />

<c:if test="${not empty title and not jcr:isNodeType(currentNode, 'jmix:skinnable')}">
	<h3>${title}</h3>
</c:if>