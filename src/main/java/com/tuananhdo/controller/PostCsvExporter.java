package com.tuananhdo.controller;

import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.exporter.AbstractExporter;
import org.springframework.scheduling.annotation.Async;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PostCsvExporter extends AbstractExporter {

    @Async("exportCsvThread")
    public CompletableFuture<Void> export(List<PostDTO> posts, HttpServletResponse response) throws IOException {
        Instant startTime = Instant.now();
        return CompletableFuture.runAsync(() -> {
            try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
                super.setResponseHeader(response, "text/csv", ".csv", "posts_");
                String[] csvHeader = {"Post ID", "Title", "Description", "Content", "Date"};
                String[] fieldMapping = {"id", "title", "shortDescription", "content", "createdOn"};

                csvBeanWriter.writeHeader(csvHeader);

                posts.forEach(postDTO -> {
                    try {
                        csvBeanWriter.write(postDTO, fieldMapping);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).thenAccept(result -> {
            Instant endTime = Instant.now();
            Duration elapsedTime = Duration.between(startTime, endTime);
            System.out.println("Time thread export CSV file : " + elapsedTime.toMillis() + " ms");
        });
    }
}
