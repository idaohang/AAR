package com.qut.eprints;

import java.util.ArrayList;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 * GlassFish webservice called by presentation tier
 *
 * @author Andrew Hood et al (2012)
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au> (2013)
 * @version	2.0
 */
@WebService(serviceName = "RecommendWS")
@Stateless()
public class RecommendWS {

    /**
     * Get list of categories or subcategories
     *
     * @param categoryId parent category, or 0 to view top level categories
     * @return list of subcategories
     */
    @WebMethod(operationName = "GetCategories")
    public ArrayList<String[]> GetCategories(@WebParam(name = "categoryId") int categoryId) {
        ConnectMSSQL dbConnection = new ConnectMSSQL();
        return dbConnection.getCategoriesByParentID(categoryId);
    }

    /**
     * Get search results for text input box
     *
     * @param term search term from input box
     * @param topN number of results to return
     * @return search results
     */
    @WebMethod(operationName = "GetArticlesBySearchTerm")
    public ArrayList<String[]> GetArticlesBySearchTerm(@WebParam(name = "term") String term, @WebParam(name = "topN") int topN) {
        ConnectMSSQL dbConnection = new ConnectMSSQL();
        return dbConnection.getArticlesBySearchTerm(term, topN);
    }

    /**
     * Get articles from a category and its child categories
     *
     * @param category category ID as stored in database
     * @param topN number of results to return
     * @return category's title followed by list of articles
     */
    @WebMethod(operationName = "GetArticlesByCategory")
    public ArrayList<String[]> GetArticlesByCategory(@WebParam(name = "category") int category, @WebParam(name = "topN") int topN) {
        ConnectMSSQL dbConnection = new ConnectMSSQL();
        return dbConnection.getArticlesByCategory(category, topN);
    }

    /**
     * Get article metadata (title, abstract etc)
     *
     * @param articleId article ID
     * @return article metadata
     */
    @WebMethod(operationName = "GetArticleDetails")
    public String[] GetArticleDetails(@WebParam(name = "articleId") int articleId) {
        ConnectMSSQL dbConnection = new ConnectMSSQL();
        return dbConnection.getArticleDetails(articleId);
    }

    /**
     * Get recommended articles similar to a given article
     *
     * @param articleId article ID to make recommendations on
     * @param topN number of results to return
     * @return articles
     */
    @WebMethod(operationName = "RecommendByArticle")
    public ArrayList<String[]> RecommendByArticle(@WebParam(name = "articleId") int articleId, @WebParam(name = "topN") int topN) {
        ConnectMSSQL dbConnection = new ConnectMSSQL();
        return dbConnection.recommendByArticle(articleId, topN);
    }
}
