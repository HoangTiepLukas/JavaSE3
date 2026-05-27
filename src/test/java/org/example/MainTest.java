package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void saveGeneratedFilesToOutputTest() throws Exception {
        Path outputDir = Path.of("output_test");
        Files.createDirectories(outputDir);

        List<Game> games = List.of(
                new Game("Alpha Sim", 2020, "Dev One", "Pub One", "Simulator, Strategy"),
                new Game("Beta Racer", 2018, "Dev Two", "Pub One", "Racing"),
                new Game("Gamma Sim", 2019, "Dev Three", "Pub Two", "Simulator, Adventure")
        );

        invokeExport("exportGenres", games, outputDir.resolve("game_genres.csv"));
        invokeExport("exportSimulatorGames", games, outputDir.resolve("simulator_games.csv"));
        invokeExport("exportPublishers", games, outputDir.resolve("game_publishers.csv"));

        System.out.println("Test files saved to: " + outputDir.toAbsolutePath());

        assertTrue(Files.exists(outputDir.resolve("game_genres.csv")));
        assertTrue(Files.exists(outputDir.resolve("simulator_games.csv")));
        assertTrue(Files.exists(outputDir.resolve("game_publishers.csv")));

    }

    private static void invokeExport(String methodName, List<Game> games, Path output) throws Exception {
        Method method = Main.class.getDeclaredMethod(methodName, List.class, Path.class);
        method.setAccessible(true);
        method.invoke(null, games, output);
    }
}