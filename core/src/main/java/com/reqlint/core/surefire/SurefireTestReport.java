package com.reqlint.core.surefire;

import java.time.LocalDateTime;

public record SurefireTestReport(LocalDateTime timeStamp, String name, String output, String failure) {

}
