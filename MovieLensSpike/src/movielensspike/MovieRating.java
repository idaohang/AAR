package movielensspike;

/**
 * Class to represent a single movie rating (ie. a row of a movie ratings table)
 *
 * @author Jordan
 */
public class MovieRating {

    private final int userId, movieId;
    private final double ratingValue;

    /**
     * Create a MovieRating and set its private fields.
     *
     * @param userId ID of the user who made this rating
     * @param movieId The movie being rated
     * @param ratingValue The rating of the movie out of 5
     */
    public MovieRating(int userId, int movieId, double ratingValue) {
        this.userId = userId;
        this.movieId = movieId;
        this.ratingValue = ratingValue;
    }

    /**
     * Getter for user ID
     *
     * @return The user ID for this movie rating
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Getter for movie ID
     *
     * @return the movie ID for this movie rating
     */
    public int getMovieId() {
        return movieId;
    }

    /**
     * Getter for rating value
     *
     * @return The rating value out of 5 for this movie
     */
    public double getRatingValue() {
        return ratingValue;
    }
}
