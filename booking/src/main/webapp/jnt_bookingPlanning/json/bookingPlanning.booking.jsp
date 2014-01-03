<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<c:set target="${renderContext}" property="contentType" value="application/json;charset=UTF-8"/>

<jcr:sql var="bookingsValidated" 
	sql="select * from [jnt:booking] as b where ISDESCENDANTNODE(b,'${currentNode.path}') order by b.[start]"/>
					
<json:array>
	<c:forEach items="${bookingsValidated.nodes}" var="booking" varStatus="status">
		<template:module node="${booking }" templateType="json" editable="false"/>
	</c:forEach>
</json:array>
