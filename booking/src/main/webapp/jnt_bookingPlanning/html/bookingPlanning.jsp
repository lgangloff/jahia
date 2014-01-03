<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="b" uri="http://www.lgf.org/tags/jahia/booking" %>

<%--@elvariable id="selectorType" type="org.jahia.services.content.nodetypes.SelectorType"--%>

<c:set var="title" value="${currentNode.properties['jcr:title'].string}" />
<c:set var="baseResourceType" value="${currentNode.properties['baseResourceType'].node}" />

<c:url value="${url.base}${currentNode.path}.booking.json" var="bookingJsonFeedUrl"/>
<c:url value="${url.base}${currentNode.path}.resource.json" var="resourceJsonUrl"/>
<c:url value="${url.base}${currentNode.path}.addBooking.do" var="addBookingActionUrl"/>

<template:addResources type="css" resources="fullcalendar.css"/>
<template:addResources type="css" resources="booking.css"/>


 <c:choose>
	<c:when test="${renderContext.editMode}">
		<c:if test="${not empty title and not jcr:isNodeType(currentNode, 'jmix:skinnable')}">
			<h1>${title}</h1>
		</c:if>
		<div class="ressources">
			<h2><fmt:message key="resources.list" /></h2>
			<ul>
				<c:forEach items="${jcr:getChildrenOfType(currentNode, 'jnt:resource')}" var="resource">
					<li>
						<template:module node="${resource}" />
					</li>
				</c:forEach>
			</ul>
			<template:module path="*" nodeTypes="jnt:resource" />
		</div>
	</c:when>
	<c:otherwise>
	
		<template:addResources type="javascript" resources="jquery.min.js"/>
		<template:addResources type="javascript" resources="fullcalendar.js"/>
		<template:addResources type="javascript" resources="i18n/calendar-${renderContext.mainResourceLocale}.js"/>
	
		<div class="calendar-wrapper">
	
				<div class="calendar-header">
					<c:if test="${renderContext.loggedIn}">
						<input id="addEventButton" type="button" value="<fmt:message key="booking.add" />" /> 
					</c:if>
					<c:if test="${not empty title and not jcr:isNodeType(currentNode, 'jmix:skinnable')}">
						<h1>${title}</h1>
					</c:if>
				</div>
		
				<div id="calendar">
				</div>
				
				<div id="addEvtForm" title='<fmt:message key="booking.new"/>'>
					<div id="step-1" class="step">
						<div class="group">
							<label><fmt:message key="booking.new.resourceType"/></label>
							<select id="resourceType" name="resourceType">
								<option value=""><fmt:message key="booking.new.resourceType.any"/></option>
								<c:forEach var="resourceType" items="${jcr:getChildrenOfType(baseResourceType, 'jnt:category')}">
									<option value="${resourceType.UUID}">
										${resourceType.properties['jcr:title'].string}
									</option>
								</c:forEach>
							</select>
						</div>
						
						<div class="group">
							<label><fmt:message key="booking.new.startDate"/></label>
							<input type="text" id="start" readonly="readonly"/>						
							<ui:dateSelector fieldId="start" time="true">
							    {dateFormat: $.datepicker.ISO_8601, showButtonPanel: true, showOn:'focus'}
							</ui:dateSelector>
						</div>
						
						
						<div class="group">
							<label><fmt:message key="booking.new.duration"/></label>
							<input id="duration" type="text" name="duration" value="2"/>
							<select id="durationUnit" name="durationUnit">
								<option value="m"><fmt:message key="booking.duration.unit.min"/></option>
								<option value="h" selected="selected"><fmt:message key="booking.duration.unit.hour"/></option>
								<option value="d"><fmt:message key="booking.duration.unit.day"/></option>						
							</select>		
						</div>		
					</div>	
						
					<div id="step-2" class="step" style="display:none;">
						<div class="group">
							<label><fmt:message key="booking.new.resources.available"/></label>
							<select id="resource" name="resource"></select>
						</div>
					</div>		
					
					<div class="step-control">
						<input id="prev-step" type="button" value="<fmt:message key="booking.new.step.prev"/>" style="display:none;"/>
						<input id="next-step" type="button" value="<fmt:message key="booking.new.step.next"/>"/>
						<input id="validate-booking" type="button" value="<fmt:message key="booking.new.step.done"/>"" style="display:none;"/>
					</div>
				</div>
		</div>
		
		
		<template:addResources>
			<script>
			
				$(document).ready(function() {			
					$('#calendar').fullCalendar({
						header: {
							left: 'prev,next today',
							center: 'title',
							right: 'month,agendaWeek,agendaDay'
						},
						firstDay: 1,
						weekends: false,
						editable: false,
						allDaySlot: false,
						timeFormat: 'H:mm{ - H:mm}',
						defaultView: 'agendaWeek',
						firstHour: 8,
						events: '${bookingJsonFeedUrl}'
					});
					
				});
				$(document).ready(function() {		
					$( "#addEvtForm" ).dialog({
						autoOpen: false,
						height: 300,
						width: 350,
						modal: true
					});
					$('#addEventButton').click(function(){
						$("#addEvtForm" ).dialog( "open" );
					});
					$('#next-step').click(function(){

						if (getStart() == "" || getDurationInMinutes() == ""){	
							alert('<fmt:message key="booking.add.required.field.message"/>');							
						}
						else{
							
							$.get(
								'${resourceJsonUrl}', 
								{
									'type' : $("#resourceType option:selected").val(),
									'start' : getStart(),
									'duration' : getDurationInMinutes()
								}, 
								function(data){
									$("#resource").html('');
									if (data.length > 0){
										addOption("#resource", "", "Choisir");
										
										$.each( data, function( key, val ) {
											addOption("#resource", val.id, val.title);
										});
	
	
										$('#step-1').hide();
										$('#step-2').show();
											
										$('#prev-step').show();
										$('#next-step').hide();
										$("#validate-booking").show();
									}
									else{
										alert("<fmt:message key="booking.add.no.resource.available"/>");
									}
							});
						}
					});

					$('#prev-step').click(function(){
						
						$('#step-1').show();
						$('#step-2').hide();
						
						$('#prev-step').hide();
						$('#next-step').show();
						$("#validate-booking").hide();
					});
					
					

					$('#validate-booking').click(function(){
						if ($("#resource option:selected").val() != ""){							
							$.post(
									'${addBookingActionUrl}', 
									{
										'resource' : $("#resource option:selected").val(),
										'start' : getStart(),
										'duration' : getDurationInMinutes()
									}
							).done(function() {
								alert("<fmt:message key="booking.add.ok"/>");
								$("#addEvtForm" ).dialog( "close" );
					
							}).fail(function(jqXHR) {
								if (jqXHR.status == 401){
									alert("<fmt:message key="booking.add.nok.401"/>");
								}
								else{
									alert("<fmt:message key="booking.add.nok"/>");
								}
							}).always(function() {
								$('#prev-step').click();
								$('#calendar').fullCalendar( 'refetchEvents' );
							});
						}
						else{
							alert("<fmt:message key="booking.add.choose.resource"/>");
						}
					});
				});
				
				function getStart(){
					return $("#start").val();
				}
				function getDurationInMinutes(){
					var duration = $("#duration").val();
					var durationUnit = $("#durationUnit option:selected").val()
					
					switch (durationUnit) {
					    case "h": //heure
					    	duration = duration * 60; // en minute
					        break;
					    case "d": //jour
					    	duration = duration * 24 * 60; // en minute
					        break;
					}
		
				    return duration;
				}
				function addOption(selector, id, text){
					var o = new Option(text, id);
					/// jquerify the DOM object 'o' so we can use the html method
					$(o).html(text);
					$(selector).append(o);
				}
			</script>
		</template:addResources>
	
	</c:otherwise>
</c:choose>