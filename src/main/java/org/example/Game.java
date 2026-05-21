package org.example;

public class Game {
    private final String title;
    private final int released;
    private final String developer;
    private final String publisher;
    private final String genres;

    public Game(String title, int released, String developer, String publisher, String genres) {
        this.title = title;
        this.released = released;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public int getReleased() {
        return released;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getGenres() {
        return genres;
    }
}
