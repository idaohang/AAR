package com.qut.eprints;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Database handler class
 *
 * @author Andrew Hood et al (2012)
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au> (2013)
 * @version	2.0
 */
public class ConnectMSSQL {

    // Database connection settings
    private final String databaseServer = "SEF-EEC-066935";
    private final String databaseName = "capstone";
    private final String databaseUser = "qut";
    private final String databasePassword = "vres2012";

    private Connection connection = null;
    private final String connectionString = "jdbc:jtds:sqlserver://" + databaseServer + "/" + databaseName + ";instance=MSQLSERVER2012";

    /**
     * Constructor
     */
    public ConnectMSSQL() {
        this.connection = getConnection();
    }

    /**
     * Connect to database
     *
     * @return database connection
     */
    private Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(connectionString, databaseUser, databasePassword);
            } catch (Exception e) {
                System.out.println("Error Trace in getConnection(): " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Close connection to database
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    /**
     * Get list of categories or subcategories
     *
     * @param categoryId parent category, or 0 to view top level categories
     * @return list of subcategories
     */
    public ArrayList<String[]> getCategoriesByParentID(int categoryId) {
        ArrayList<String[]> categories = new ArrayList<String[]>();
        try {
            if (connection != null) {
                String query = "SELECT a.[CatID], a.[Category], COUNT(b.[CatID])\n"
                        + "FROM [capstone].[dbo].[tblCategoryID] a, [capstone].[dbo].[tblCategoryID] b\n"
                        + "WHERE a.[ParentID] " + ((categoryId > 0) ? "= " + categoryId : "IS NULL") + "\n"
                        + "AND b.[ParentID] = a.[CatID]\n"
                        + "GROUP BY a.[CatID], a.[Category], b.[ParentID]\n"
                        + "UNION\n"
                        + "SELECT [CatID], [Category], 0\n"
                        + "FROM [capstone].[dbo].[tblCategoryID]\n"
                        + "WHERE [ParentID] " + ((categoryId > 0) ? "= " + categoryId : "IS NULL") + "\n"
                        + "AND [CatID] NOT IN (\n"
                        + "	SELECT DISTINCT [ParentID]\n"
                        + "	FROM [capstone].[dbo].[tblCategoryID]\n"
                        + "	WHERE [ParentID] IS NOT NULL)\n"
                        + "AND [CatID] <> 687\n" //TODO ugly override, cat 687 and art 13623 cause problems
                        + "ORDER BY a.[Category];";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    String[] category = {rs.getString(1), rs.getString(2), rs.getString(3)};
                    categories.add(category);
                }
                rs.close();
            } else {
                System.out.print("No database connection");
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return categories;
    }

    /**
     * Get search results for text input box
     *
     * @param term search term from input box
     * @param topN number of results to return
     * @return search results
     */
    public ArrayList<String[]> getArticlesBySearchTerm(String term, int topN) {
        ArrayList<String[]> articles = new ArrayList<String[]>();

        String queryString = "SELECT [ePrintID], [Title], [Abstract]\n"
                + "FROM [capstone].[dbo].[tblArticleInfo]\n"
                + "WHERE [Abstract] LIKE ?\n"
                + "AND [ePrintID] <> 13623\n" //TODO ugly override, cat 687 and art 13623 cause problems
                + "ORDER BY [ePrintID]\n"
                + "OFFSET 0 ROWS\n"
                + "FETCH NEXT ? ROWS ONLY;";
        try {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, "%" + term + "%");
            statement.setInt(2, topN);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    new String(rs.getString(3).getBytes())
                };
                articles.add(row);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return articles;
    }

    /**
     * Get articles from a category and its child categories
     *
     * @param category category ID as stored in database
     * @param topN number of results to return
     * @return category's title followed by list of articles
     */
    public ArrayList<String[]> getArticlesByCategory(Integer category, Integer topN) {
        ArrayList<String[]> articles = new ArrayList<String[]>();
        try {

            // find all child categories (at all levels) of specified category
            ArrayList<Integer> categories = new ArrayList<Integer>();
            categories.add(category);
            for (int i = 0; i < categories.size(); i++) {
                String query = "SELECT [CatID]\n"
                        + "FROM [capstone].[dbo].[tblCategoryID]\n"
                        + "WHERE [ParentID] = " + categories.get(i) + ";";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    categories.add(rs.getInt(1));
                }
            }
            String categoriesString = String.valueOf(categories.get(0));
            for (int i = 1; i < categories.size(); i++) {
                categoriesString += ", " + String.valueOf(categories.get(i));
            }

            // return category's name first
            String query = "SELECT [Category]\n"
                    + "FROM [capstone].[dbo].[tblCategoryID]\n"
                    + "WHERE [CatID] = " + category + ";";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                String[] firstRow = {
                    rs.getString(1)
                };
                articles.add(firstRow);

                // get articles in category and child categories
                query = "SELECT DISTINCT b.[ePrintID], b.[Title], b.[Abstract]\n"
                        + "FROM [capstone].[dbo].[tblSplitSubject] a\n"
                        + "INNER JOIN [capstone].[dbo].[tblArticleInfo] b\n"
                        + "ON a.[ePrintID] = b.[ePrintID]\n"
                        + "WHERE a.[CatID] IN (" + categoriesString + ")\n"
                        + "ORDER BY [ePrintID]\n"
                        + "OFFSET 0 ROWS\n"
                        + "FETCH NEXT " + topN + " ROWS ONLY;";
                statement = connection.createStatement();
                rs = statement.executeQuery(query);
                while (rs.next()) {
                    String[] row = {
                        String.valueOf(rs.getInt(1)),
                        new String(rs.getString(2).getBytes()),
                        new String(rs.getString(3).getBytes())
                    };
                    articles.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return articles;
    }

    /**
     * Get article metadata (title, abstract etc)
     *
     * @param articleId article ID
     * @return article metadata
     */
    public String[] getArticleDetails(int articleId) {
        String[] information = new String[4];
        String query = "SELECT TOP 1 ePrintID, Title, Abstract, Subject\n"
                + "FROM OriginaltblArticleInfo\n" //TODO was tbl not Originaltbl
                + "WHERE ePrintID = " + articleId;

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                information[0] = rs.getString(1);
                information[1] = rs.getString(2);
                information[2] = rs.getString(3);
                information[3] = rs.getString(4);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return information;
    }

    /**
     * Get recommended articles similar to a given article
     *
     * @param articleId article ID to make recommendations on
     * @param topN number of results to return
     * @return articles
     */
    public ArrayList<String[]> recommendByArticle(int articleId, int topN) {
        ArrayList<String[]> recommendations = new ArrayList<String[]>();
        try {
            if (connection != null) {
                String query = "SELECT TOP " + topN + " [tblPatterns].[ePrintID], [Title], SUM([patternRating])\n"
                        + "FROM [capstone].[dbo].[tblPatterns]\n"
                        + "INNER JOIN [capstone].[dbo].[OriginaltblArticleInfo]\n"
                        + "ON [tblPatterns].[ePrintID] = [OriginaltblArticleInfo].[ePrintID]\n"
                        + "WHERE [patternID] IN\n"
                        + "(SELECT patternID FROM [capstone].[dbo].[tblPatterns]\n"
                        + "WHERE [ePrintID] = " + articleId + ")\n"
                        + "AND [tblPatterns].[ePrintID] <> " + articleId + "\n"
                        + "GROUP BY [tblPatterns].[ePrintID], [Title]\n"
                        + "ORDER BY SUM([patternRating]) DESC;";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    String[] recommendation = {
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                    };
                    recommendations.add(recommendation);
                }
                rs.close();
            } else {
                System.out.print("No database connection");
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return recommendations;
    }
}
