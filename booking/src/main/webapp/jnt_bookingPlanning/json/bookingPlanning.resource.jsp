<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="b" uri="http://www.lgf.org/tags/jahia/booking" %>

<c:set target="${renderContext}" property="contentType" value="application/json;charset=UTF-8"/>

<c:set var="start" value="${b:parse(param['start'])}"/>
<c:set var="end" value="${b:parseAndAddMinutes(param['start'], param['duration'])}"/>

<json:array>
	<c:forEach items="${b:getResourceFree(currentNode, param['type'], start, end) }" var="resource" varStatus="status">
		<template:module node="${resource }" templateType="json" editable="false"/>
	</c:forEach>
</json:array>
