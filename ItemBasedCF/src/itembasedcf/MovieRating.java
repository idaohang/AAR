package itembasedcf;

/**
 * Class to represent a single movie rating (ie. a row of a movie ratings table)
 * 
 * @author Jordan
 */
public class MovieRating {
    private final int userId, movieId;
    private final double ratingValue;
    
    /**
     * Create a MovieRating and set it's private fields
     * 
     * @param userId id of the user who made this rating
     * @param movieId the movie being rated
     * @param ratingValue the rating of the movie out of 5
     */
    public MovieRating(int userId, int movieId, double ratingValue) {
        this.userId = userId;
        this.movieId = movieId;
        this.ratingValue = ratingValue;
    }
    
    /**
     * Getter for user id
     * @return the user id for this movie rating
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Getter for movie id
     * @return the movie id for this movie rating
     */
    public int getMovieId() {
        return movieId;
    }
    
    /**
     * Getter for rating value
     * @return  the rating value out of 5 for this movie
     */
    public double getRatingValue() {
        return ratingValue;
    }
}
