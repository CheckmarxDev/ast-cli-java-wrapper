package com.checkmarx.ast.results;

import lombok.Getter;

public enum ReportFormat {
    summaryHTML(".html"),
    json(".json"),
    sarif(".sarif"),
    ;

    @Getter
    private final String extension;

    ReportFormat(String extension) {
        this.extension = extension;
    }
}
