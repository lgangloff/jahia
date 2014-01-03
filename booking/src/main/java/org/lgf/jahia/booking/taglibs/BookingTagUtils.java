/*

Copyright (c) 2013 Loic Gangloff (loic.gangloff@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.*/
package org.lgf.jahia.booking.taglibs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.taglibs.jcr.node.JCRTagUtils;

/**
 * 
 * Bibliotheque de tags utilises dans le module de reservation
 * 
 * @author Loic gangloff (loic.gangloff@gmail.com)
 *
 */
public class BookingTagUtils {


	private static final Logger LOG = Logger.getLogger(BookingTagUtils.class);

	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd H:m:s");

	private static final DateFormat DATE_FORMAT_JCR = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
	
	static{
		DATE_FORMAT_JCR.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

    public static Date parse(String inputDateStr) throws ParseException {
        return DATE_FORMAT.parse(inputDateStr);
    }

    public static Date parseAndAddMinutes(String inputDateStr, Integer minutes) throws ParseException {
        Calendar cal = Calendar.getInstance();
    	cal.setTime(DATE_FORMAT.parse(inputDateStr));
    	cal.add(Calendar.MINUTE, minutes);
        
        return cal.getTime();
    }
    
    public static List<Node> getResourceFree(JCRNodeWrapper currentNode, String type, Date start, Date end) throws InvalidQueryException, RepositoryException{

    	
    	String allResourcesQuery = 
    		"select * from [jnt:resource] as r " +
    		"WHERE ISDESCENDANTNODE(r,'"+currentNode.getPath()+"') ";

    	if (StringUtils.isNotBlank(type)){
    		allResourcesQuery += "AND [type]='"+type+"'";
    	}
    	
    	String startStr = DATE_FORMAT_JCR.format(start);
    	String endStr = DATE_FORMAT_JCR.format(end);
    	
    	
    	String allBookingDuringPeriod = 
    		"SELECT * from [jnt:booking] as b " +
    		"WHERE ISDESCENDANTNODE(b,'"+currentNode.getPath()+"') "+
    		"AND ( "+
    		"	 ([end]   > '"+startStr+"' 	AND [start]   < '"+endStr+"') "+
    		") ";

    	LOG.debug("getResourceFree(type="+type+", start="+start+", end="+end+"): "+allBookingDuringPeriod);
    	//On cherche les ressources deja reserve
    	Set<Node> resourceAlreadybooked = new HashSet<Node>();    	
    	QueryResult qr2 = currentNode.getSession().getWorkspace().getQueryManager().createQuery(
    			allBookingDuringPeriod,Query.JCR_SQL2).execute();
    	for (NodeIterator iterator = qr2.getNodes(); iterator.hasNext(); ) {
    		Node bookingNode = iterator.nextNode();
    		resourceAlreadybooked.add(bookingNode.getProperty("resourceRef").getNode());
    	}
    	    
    	
    	//On cherche toutes les ressources, auxquelles on enleve celle deja reserve
    	List<Node> resourceFreeNodes = new ArrayList<Node>();
    	QueryResult qr = currentNode.getSession().getWorkspace().getQueryManager().createQuery(
    			allResourcesQuery,Query.JCR_SQL2).execute();
    	
    	for (NodeIterator iterator = qr.getNodes(); iterator.hasNext(); ) {
    		Node resourceNode = iterator.nextNode();
    		if (!resourceAlreadybooked.contains(resourceNode)){
    			resourceFreeNodes.add(resourceNode);
    		}
    	}
    	return resourceFreeNodes;
    }
    
    public static List<Node> getBookingToBeValidated(JCRNodeWrapper bookingPlanningNode) throws InvalidQueryException, RepositoryException{

    	List<JCRNodeWrapper> resourceNodesAllowedForValidation = JCRTagUtils.findAllowedNodesForPermission("bookingValidation", bookingPlanningNode, "jnt:resource");
    	
    	List<Node> bookingNodes = new ArrayList<Node>();
    	
    	if (resourceNodesAllowedForValidation != null && resourceNodesAllowedForValidation.size() > 0){

        	String bookingQuery = 
        		"select * from [jnt:booking] as b " +
        		"WHERE ISDESCENDANTNODE(b,'"+bookingPlanningNode.getPath()+"') " +
        		"AND b.[status] = '01_created' "+
        		"AND ( ";
        	boolean first = true;
        	for (JCRNodeWrapper resourceNode : resourceNodesAllowedForValidation) {
				if (!first)
					bookingQuery += " OR ";
        		bookingQuery += "b.[resourceRef] = '" + resourceNode.getIdentifier()+"' ";
        		first = false;				
			}        	
        	bookingQuery += " ) ";
        	bookingQuery += "order by b.[start] ";

        	LOG.debug("getBookingToBeValidated: "+bookingQuery);
 	
        	QueryResult qr = bookingPlanningNode.getSession().getWorkspace().getQueryManager().createQuery(
        			bookingQuery,Query.JCR_SQL2).execute();
        	for (NodeIterator iterator = qr.getNodes(); iterator.hasNext(); ) {
        		Node bookingNode = iterator.nextNode();
        		bookingNodes.add(bookingNode);
        	}
    		
    	}    	
    	return bookingNodes;
    }
    
    public static String now(){
    	return DATE_FORMAT_JCR.format(new Date());
    }
}
