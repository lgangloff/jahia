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
package org.lgf.jahia.booking.action;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.lgf.jahia.booking.taglibs.BookingTagUtils;

/**
 * Action Jahia permettant d'ajouter un reservation
 * 
 * @author Loic Gangloff (loic.gangloff@gmail.com]
 *
 */
public class AddBookingAction extends Action {

	private static final Logger LOG = Logger
			.getLogger(AddBookingAction.class);

	private JCRTemplate jcrTemplate;

    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }
    
	@Override
	public ActionResult doExecute(final HttpServletRequest req,
			final RenderContext renderContext, Resource resource,
			final JCRSessionWrapper session,
			final Map<String, List<String>> parameters, URLResolver urlResolver)
			throws Exception {

		

		final JCRNodeWrapper currentNode = resource.getNode();

		//On verifie que le noeud est un 'jnt:bookingPlanning'
		if (currentNode.isNodeType("jnt:bookingPlanning")) {
			//query parameter
			final String resourceUUID = req.getParameter("resource");
			final String startStr = req.getParameter("start");
			final Integer durationInMinutes = Integer.valueOf(req.getParameter("duration"));
			
			//Parameter parsed
			final Calendar start = Calendar.getInstance();
			start.setTime(BookingTagUtils.parse(startStr));

			final Calendar end = Calendar.getInstance();
			end.setTime(BookingTagUtils.parseAndAddMinutes(startStr, durationInMinutes));
			
			final JCRNodeWrapper resourceNode = session.getNodeByUUID(resourceUUID);
			
			List<Node> resourceFree = BookingTagUtils.getResourceFree(
					currentNode, resourceNode.getPropertyAsString("type"), 
					start.getTime(), end.getTime());
			
			//On verifie que la resource est encore dispo au moment de la résa
			if (resourceFree.contains(resourceNode)){

				//La reservation doit etre valide ?
				final String status = resourceNode.getProperty("needValidation").getBoolean() ? "01_created" : "02_validated";
				
				return jcrTemplate.doExecuteWithSystemSession(null,session.getWorkspace().getName(),session.getLocale(),new JCRCallback<ActionResult>() {
		            public ActionResult doInJCR(JCRSessionWrapper session) throws RepositoryException {
		            	
		            	JCRNodeWrapper node = session.getNodeByIdentifier(currentNode.getIdentifier());

						JCRNodeWrapper newBookingNode = node.addNode(
								JCRContentUtils.findAvailableNodeName(node, "booking"),
								"jnt:booking");
						newBookingNode.setProperty("start", start);
						newBookingNode.setProperty("end", end);
						newBookingNode.setProperty("status", status);
						newBookingNode.setProperty("resourceRef", resourceNode.getIdentifier());
						newBookingNode.setProperty("ownerRef", renderContext.getUser().getUserProperty("jcr:uuid").getValue());
					
						session.save();
												
						return new ActionResult(HttpServletResponse.SC_OK, req.getParameter("jcrRedirectTo"));
		            }
		        });
			}
			else{
				LOG.warn("La resource "+resourceNode.getPropertyAsString("jcr:title")+
						" n'est pas disponible entre le "+start.getTime() + " et le " + end.getTime());

				return new ActionResult(HttpServletResponse.SC_BAD_REQUEST, null);
			}
		}
		else{
			LOG.warn("Impossible d'appeler l'action sur un noeud de type "+currentNode.getNodeTypes());
			return new ActionResult(HttpServletResponse.SC_FORBIDDEN, null);
		}
		
	}

}
