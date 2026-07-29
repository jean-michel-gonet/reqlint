package com.reqlint.core.surefire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SurefireTestReportItem(LocalDateTime timeStamp, String name, List<SurefireStageOutputItem> stageOutputs, String failure) {
    private static final Pattern STAGE_PATTERN = Pattern.compile("#\\s*Stage\\s+([0-9]+)\\s?-\\s(.*)$");

    public SurefireTestReportItem(LocalDateTime timeStamp, String name, String output, String failure) {
        this(timeStamp, name, stageOutputs(output), failure);
    }

    private static List<SurefireStageOutputItem> stageOutputs(String output) {
        List<SurefireStageOutputItem> stageOutputs = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(output))) {
            StringBuilder stageOutput = new StringBuilder();
            String line;
            int stageNumber = 0;
            String stageTitle = "Preparation";
            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = STAGE_PATTERN.matcher(line);
                if (matcher.find()) {
                    stageOutputs.add(new SurefireStageOutputItem(
                            stageNumber,
                            stageTitle,
                            stageOutput.toString()));
                    stageOutput = new StringBuilder();
                    stageNumber = Integer.parseInt(matcher.group(1));
                    stageTitle = matcher.group(2);
                } else {
                    stageOutput.append(line).append("\r\n");
                }
            }
            stageOutputs.add(new SurefireStageOutputItem(
                    stageNumber,
                    stageTitle,
                    stageOutput.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stageOutputs;
    }

    public boolean isFailed() {
        return failure != null && !failure.isEmpty();
    }
}
