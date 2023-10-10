package com.v2soft.productrating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class ProductRatingApplication {

	private static final Logger infoAndDebuglogger = LogManager.getLogger("InfoAndDebugLogger");

	public static void main(String[] args) {
		String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
		String logDirectoryPath = "logs/" + formattedDate;
		Path logDirectory = Path.of(logDirectoryPath);

		if (!Files.exists(logDirectory)) {
			try {
				infoAndDebuglogger.debug("Attempting to create file directory: " + logDirectoryPath);
				Files.createDirectories(logDirectory);
			} catch (IOException e) {
				infoAndDebuglogger.error("Error during log directory creation: " + e);
			}
		}

		SpringApplication.run(ProductRatingApplication.class, args);
	}
}