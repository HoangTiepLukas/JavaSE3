package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    private static final Path OUTPUT_DIR = Path.of("outputs");

    static void main(String[] args) {
        Path input = Path.of("games.csv");

        try {
            Files.createDirectories(OUTPUT_DIR);

            // Load all games from CSV
            List<Game> games = loadGames(input);

            // Generate required output files
            Path genresOutput = OUTPUT_DIR.resolve("game_genres.csv");
            Path simulatorOutput = OUTPUT_DIR.resolve("simulator_games.csv");
            Path publishersOutput = OUTPUT_DIR.resolve("game_publishers.csv");

            exportGenres(games, genresOutput);
            exportSimulatorGames(games, simulatorOutput);
            exportPublishers(games, publishersOutput);

            System.out.println("All files generated successfully.");
            System.out.println("Saved to: " + OUTPUT_DIR.toAbsolutePath());
            System.out.println("- " + genresOutput.toAbsolutePath());
            System.out.println("- " + simulatorOutput.toAbsolutePath());
            System.out.println("- " + publishersOutput.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Loads games.csv into a List<Game>.
     * Handles quoted fields like "Puzzle, Strategy, Adventure".
     */
    private static List<Game> loadGames(Path csvPath) throws IOException {
        if (!Files.exists(csvPath)) {
            throw new IOException("File does not exist");
        }

        List<Game> games = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line = reader.readLine(); // skip header

            if (line == null) {
                return games; // empty file
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = parseCsvLine(line);

                String title = parts[0];
                int released;
                try {
                    released = Integer.parseInt(parts[1]);
                } catch (NumberFormatException _) {
                    released = 0;
                }
                String developer = parts[2];
                String publisher = parts[3];
                String genres = parts[4];

                games.add(new Game(title, released, developer, publisher, genres));
            }
        }

        return games;
    }

    /**
     * Custom CSV parser that handles quotes and commas inside quotes.
     */
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes; // toggle state
            } else if (c == ',' && !insideQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    /**
     * Creates game_genres.txt containing unique, sorted genres.
     */
    private static void exportGenres(List<Game> games, Path output) throws IOException {
        Set<String> genres = new TreeSet<>(); // TreeSet = sorted + unique

        for (Game g : games) {
            String[] parts = g.getGenres().split(",");
            for (String genre : parts) {
                genres.add(genre.trim());
            }
        }

        Files.writeString(output, String.join(",", genres));
    }

    /**
     * Creates simulator_games.csv containing only simulator games sorted by year.
     */
    private static void exportSimulatorGames(List<Game> games, Path output) throws IOException {
        List<Game> simulators = games.stream()
                .filter(g -> g.getGenres().toLowerCase().contains("simulator"))
                .sorted(Comparator.comparingInt(Game::getReleased))
                .toList();

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("title, year");
            writer.newLine();

            for (Game g : simulators) {
                writer.write(g.getTitle() + ", " + g.getReleased());
                writer.newLine();
            }
        }
    }

    /**
     * Creates game_publishers.csv containing publisher + number of games.
     */
    private static void exportPublishers(List<Game> games, Path output) throws IOException {

        Map<String, Integer> counts = new HashMap<>();

        for (Game g : games) {
            counts.merge(g.getPublisher(), 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> sorted =
                counts.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .toList();

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("publisher, counts");
            writer.newLine();

            for (Map.Entry<String, Integer> entry : sorted) {
                writer.write(entry.getKey() + ", " + entry.getValue());
                writer.newLine();
            }
        }
    }
}
