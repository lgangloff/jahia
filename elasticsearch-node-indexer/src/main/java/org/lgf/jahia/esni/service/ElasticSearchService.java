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
package org.lgf.jahia.esni.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PutMethod;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * 
 * Service elasticsearch en charge de l'indexation des noeuds.
 * Il s'appuie sur l'API REST d'ElasticSearch pour indexer les noeuds.
 * Les noeuds jahia sont transformés en JSON:
 * 	Toutes les propriétés sont extraites, ainsi que les enfants 
 * 	du noeud courant et de ses enfants jusqu'à un profondeur de 3
 * 
 * @author Loic Gangloff (loic.gangloff@gmail.com]
 *
 */
public class ElasticSearchService {

	private static Logger logger = org.slf4j.LoggerFactory
			.getLogger(ElasticSearchService.class);

	/**
	 * Hostname d'elasticsearch
	 */
	private String hostname;
	
	/**
	 * Port elasticsearch
	 */
	private int port;
	
	private static final int MAX_DEPTH = 3;
	
	private static ElasticSearchService instance = null;

	public ElasticSearchService() {

	}
	
	/**
	 * 
	 * Index un noeud <code>node</code> de type <code>type</code> (type elasticsearch)
	 * à l'index <code>indexName</code>
	 * 
	 * @param node
	 * @param indexName
	 * @param type
	 * @throws RepositoryException
	 */
	public void indexNode(JCRNodeWrapper node, String indexName, String type) throws RepositoryException {

		String baseUrl = "http://" + hostname + ":" + port + "/" + indexName + "/" + type + "/";
		HttpClient client = new HttpClient();

		String url = baseUrl + node.getIdentifier();
		logger.info("PUT " + url);

		try {

			PutMethod put = new PutMethod(url);
			put.setRequestBody(serializeNodeToJSON(node, 0).toString());

			client.executeMethod(put);

		} catch (IOException e) {
			logger.error("Error on PUT " + url, e);
		} catch (JSONException e) {
			logger.error("Error when serialize node to json", e);
		}


	}
	
	/**
	 * Index tous les enfants de <code>parent</code> qui sont du type jnt:content.<br>
	 * Le type elasticsearch correspond au primary type du noeud JCR
	 * 
	 * @param parent
	 * @param indexName
	 * @throws RepositoryException
	 */
	public void indexChildren(JCRNodeWrapper parent, String indexName)
			throws RepositoryException {
		
		for (JCRNodeWrapper node : JCRContentUtils.getChildrenOfType(parent, "jnt:content")) {
			indexNode(node, indexName, JCRContentUtils.replaceColon(node.getPrimaryNodeTypeName()));
	    }
	}
	

	private static JSONObject serializeNodeToJSON(JCRNodeWrapper node, int depth)
			throws RepositoryException, IOException, JSONException {

		//indexing property
		final PropertyIterator stringMap = node.getProperties();
		Map<String, Object> map = new HashMap<String, Object>();
		while (stringMap.hasNext()) {
			JCRPropertyWrapper propertyWrapper = (JCRPropertyWrapper) stringMap.next();

			final String name = JCRContentUtils.replaceColon(propertyWrapper.getName());

			map.put(name, serializePropertyToJSON(propertyWrapper));
		}
		
		if (depth <= MAX_DEPTH){
			//indexing children nodes
			final NodeIterator nodeIt = node.getNodes();
			while (nodeIt.hasNext()) {
				JCRNodeWrapper nodeWrapper = (JCRNodeWrapper) nodeIt.next();
				if (!nodeWrapper.isNodeType("jnt:acl")){
					final String name = JCRContentUtils.replaceColon(nodeWrapper.getName());
		
					map.put(name, serializeNodeToJSON(nodeWrapper, depth+1));
				}
			}
		}

		JSONObject nodeJSON = new JSONObject(map);
		return nodeJSON;
	}

	private static Object serializePropertyToJSON(
			JCRPropertyWrapper propertyWrapper) throws ValueFormatException, IllegalStateException, RepositoryException {
		
		if (propertyWrapper.isMultiple()) {
			List<Object> values = new ArrayList<Object>();
			for (Value value : propertyWrapper.getValues()) {
				values.add(JCRContentUtils.getValue(value));
			}
			return values;
		}
		else{
			final int type = propertyWrapper.getType();

			String value = null;
			if (type == PropertyType.WEAKREFERENCE
					|| type == PropertyType.REFERENCE) {
				value = ((JCRNodeWrapper)propertyWrapper.getNode()).getUrl();
			} 
			else {
				value = propertyWrapper.getValue().getString();
			}
			return value;
		}
	}

	public static ElasticSearchService getInstance() {
		if (instance == null) {
			synchronized (ElasticSearchService.class) {
				instance = new ElasticSearchService();
			}
		}
		return instance;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
