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
package org.lgf.jahia.esni.rules;

import javax.jcr.RepositoryException;

import org.drools.spi.KnowledgeHelper;
import org.jahia.services.content.rules.AbstractNodeFact;
import org.jahia.services.content.rules.ChangedPropertyFact;
import org.lgf.jahia.esni.service.ElasticSearchService;

/**
 * 
 * Drools consequence permettant de lancer l'indexation elasticsearch à la publication d'un noeud
 * 
 * @author Loic Gangloff (loic.gangloff@gmail.com]
 *
 */
public class ElasticSearch {

	private ElasticSearchService elasticSearchService;

	/**
	 * Lance l'indexation elasticsearch des enfants de <code>parentNodeToIndex</code> sur l'index <code>indexName</code>
	 * 
	 * @param parentNodeToIndex
	 * @param indexName
	 * @param drools
	 * @throws RepositoryException
	 */
	public void indexChildren(AbstractNodeFact parentNodeToIndex, ChangedPropertyFact indexName, KnowledgeHelper drools) throws RepositoryException{
		elasticSearchService.indexChildren(parentNodeToIndex.getNode(), indexName.getStringValue());
	}

	public void setElasticSearchService(ElasticSearchService elasticSearchService) {
		this.elasticSearchService = elasticSearchService;
	}
	
}
