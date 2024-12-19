package fuzz;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FuzzEngine {
    private final Random random;
    private final String output;
    private final List<String> errorCases = new ArrayList<>();
    private String logMessage;
    static String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:',.<>?/`~";

    public FuzzEngine(String output) {
        this.random = new Random();
        this.output = output;
    }

    /**
     * Generates a random string of up to the specified maximum length.
     * The string can contain letters, digits, and special characters.
     *
     * @param maxLen the maximum length of the generated string
     * @return a random string
     */
    public String randString(int maxLen) {
        if (maxLen <= 0) {
            throw new IllegalArgumentException("Maximum length must be greater than 0.");
        }

        int length = random.nextInt(maxLen) + 1; // Random length between 1 and maxLen
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charPool.length());
            sb.append(charPool.charAt(randomIndex));
        }

        return sb.toString();
    }

    /**
     * Generates a random integer within the specified range [min, max].
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return a random integer within the range
     */
    public int randInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum value cannot be greater than maximum value.");
        }

        return random.nextInt((max - min) + 1) + min; // Random integer between min and max (inclusive)
    }

    public LocalDateTime randLocalDateTime() {
        return randLocalDateTime(LocalDateTime.now().minusYears(10), LocalDateTime.now().plusYears(10));
    }

    public LocalDateTime randLocalDateTime(LocalDateTime start, LocalDateTime end) {
        long startEpoch = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpoch = end.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch + 1);
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);
    }

    public LocalDate randLocalDate() {
        return randLocalDate(LocalDate.now().minusYears(10), LocalDate.now().plusYears(10));
    }

    public LocalDate randLocalDate(LocalDate start, LocalDate end) {
        long startEpochDay = start.toEpochDay(); // 将 LocalDate 转换为 Epoch Day
        long endEpochDay = end.toEpochDay();
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomEpochDay); // 将随机的 Epoch Day 转换回 LocalDate
    }

    public void fuzz(Runnable test, int executeTime) {
        int passCount = 0;
        int failCount = 0;

        for (int i = 0; i < executeTime; ++i) {
            logMessage = "";
            try {
                test.run();
                passCount += 1;
            } catch (Exception e) {
                failCount += 1;
                errorCases.add(
                        "Fail Test id: " + String.valueOf(i + 1) + '\n' +
                        "Error Message: " + e.getMessage() + '\n' + logMessage
                );
            }
        }

        System.out.println("Testing completed.");
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);

        writeErrorsToFile(output);

        assert (failCount == 0);
    }

    private void writeErrorsToFile(String logFilePath) {
        try (FileWriter writer = new FileWriter(logFilePath)) {
            for (String errorMessage : errorCases) {
                writer.write(errorMessage);
                writer.write('\n');
            }
            System.out.println("Error cases saved to: " + logFilePath);
        } catch (IOException e) {
            System.err.println("Failed to write error cases to file: " + e.getMessage());
        }
    }

    public void log(String message) {
        logMessage += message + '\n';
    }
}
