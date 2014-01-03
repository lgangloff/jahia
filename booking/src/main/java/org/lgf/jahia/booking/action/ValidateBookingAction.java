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

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

/**
 * Action Jahia permettant d'ajouter un reservation
 * 
 * @author Loic Gangloff (loic.gangloff@gmail.com]
 *
 */
public class ValidateBookingAction extends Action {

	private static final Logger LOG = Logger
			.getLogger(ValidateBookingAction.class);

	private JCRTemplate jcrTemplate;

    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }
    
	@Override
	public ActionResult doExecute(final HttpServletRequest req,
			RenderContext renderContext, Resource resource,
			final JCRSessionWrapper session,
			final Map<String, List<String>> parameters, URLResolver urlResolver)
			throws Exception {

		final JCRNodeWrapper currentNode = resource.getNode();

		//On verifie que le noeud est un 'jnt:booking'
		if (currentNode.isNodeType("jnt:booking")) {
			
			
			final String status = currentNode.getPropertyAsString("status");
			final JCRNodeWrapper resourceNode = (JCRNodeWrapper) currentNode.getProperty("resourceRef").getNode();
			final boolean hasPermissionToValidate = resourceNode.hasPermission("bookingValidation");
			
			
			//On verifie que la résa est en statut "créée" et que l'utilisateur courant
			//a le droit de valider la resource réservée.
			if (StringUtils.equals(status, "01_created") && hasPermissionToValidate){

				return jcrTemplate.doExecuteWithSystemSession(null,session.getWorkspace().getName(),session.getLocale(),new JCRCallback<ActionResult>() {
		            public ActionResult doInJCR(JCRSessionWrapper session) throws RepositoryException {
		            	
		            	JCRNodeWrapper node = session.getNodeByIdentifier(currentNode.getIdentifier());
		            	node.setProperty("status", "02_validated");
						session.save();
						
						return new ActionResult(HttpServletResponse.SC_OK, req.getParameter("jcrRedirectTo"));
		            }
		        });
				
			}
			else{
				LOG.warn("La réservation "+currentNode.getPropertyAsString("jcr:title")+
						" ne peut pas être validée (statut="+status+", haspersmission="+hasPermissionToValidate+")");

				return new ActionResult(HttpServletResponse.SC_FORBIDDEN, req.getParameter("jcrRedirectTo"));
			}
		}
		else{
			LOG.warn("Impossible d'appeler l'action sur un noeud de type "+currentNode.getNodeTypes());
			return new ActionResult(HttpServletResponse.SC_FORBIDDEN, req.getParameter("jcrRedirectTo"));
		}
		
	}

}
