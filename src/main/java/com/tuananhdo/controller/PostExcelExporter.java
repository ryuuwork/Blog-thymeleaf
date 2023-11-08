package com.tuananhdo.controller;

import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.exporter.AbstractExporter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PostExcelExporter extends AbstractExporter {
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    public PostExcelExporter() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Posts");
    }

    private void writeHeaderLine() {
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "Post ID", cellStyle);
        createCell(row, 1, "Title", cellStyle);
        createCell(row, 2, "Content", cellStyle);
        createCell(row, 3, "Description", cellStyle);
        createCell(row, 4, "Date", cellStyle);
    }

    private void createCell(XSSFRow row, int column, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(column);
        sheet.autoSizeColumn(column);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    @Async("exportExcelThread")
    public CompletableFuture<Void> export(List<PostDTO> posts, HttpServletResponse response) throws IOException {
        Instant pastTime = Instant.now();
        return CompletableFuture.runAsync(() -> {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                super.setResponseHeader(response, "application/octet-stream", ".xlsx", "posts_");
                writeHeaderLine();
                writeDataLines(posts);
                workbook.write(outputStream);
                workbook.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).thenAccept(result ->
        {
            Instant nowTime = Instant.now();
            Duration duration = Duration.between(pastTime, nowTime);
            System.out.println("Time export excel file:" + duration.toMillis() + "ms");
        });

    }

    private void writeDataLines(List<PostDTO> posts) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        int row = 1;
        for (PostDTO postDTO : posts) {
            XSSFRow rows = sheet.createRow(row++);
            int column = 0;

            createCell(rows, column++, postDTO.getId(), cellStyle);
            createCell(rows, column++, postDTO.getTitle(), cellStyle);
            createCell(rows, column++, postDTO.getContent(), cellStyle);
            createCell(rows, column++, postDTO.getShortDescription(), cellStyle);
            createCell(rows, column, postDTO.getCreatedOn(), cellStyle);
        }
    }
}
