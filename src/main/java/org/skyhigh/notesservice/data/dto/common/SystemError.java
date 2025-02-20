package org.skyhigh.notesservice.data.dto.common;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class SystemError {
    protected String code;
    protected String message;
    private ZonedDateTime timestamp;
    private String localizedMessage;
    private String exceptionMessage;
    private String stackTrace;

    private SystemError(SystemErrorBuilder systemErrorBuilder) {
        code = systemErrorBuilder.code;
        message = systemErrorBuilder.message;
        timestamp = systemErrorBuilder.timestamp;
        localizedMessage = systemErrorBuilder.localizedMessage;
        exceptionMessage = systemErrorBuilder.exceptionMessage;
        stackTrace = systemErrorBuilder.stackTrace;
    }

    public static class SystemErrorBuilder {
        private String code;
        private String message;
        private ZonedDateTime timestamp;
        private String localizedMessage;
        private String exceptionMessage;
        private String stackTrace;

        public SystemErrorBuilder() {}

        public SystemErrorBuilder(String code,
                                  String message,
                                  ZonedDateTime timestamp,
                                  String localizedMessage,
                                  String exceptionMessage,
                                  String stackTrace) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
            this.localizedMessage = localizedMessage;
            this.exceptionMessage = exceptionMessage;
            this.stackTrace = stackTrace;
        }

        public SystemErrorBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public SystemErrorBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public SystemErrorBuilder setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SystemErrorBuilder setLocalizedMessage(String localizedMessage) {
            this.localizedMessage = localizedMessage;
            return this;
        }

        public SystemErrorBuilder setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
            return this;
        }

        public SystemErrorBuilder setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public SystemError build() {
            return new SystemError(this);
        }
    }
}