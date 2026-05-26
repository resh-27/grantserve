package com.cognizant.researcher_service.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocType {
    IDPROOF("IDPROOF"),
    BIRTHCERTIFICATE("BIRTHCERTIFICATE"),
    PUBLICATION("PUBLICATION");

    private final String value;

    DocType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DocType fromString(String value) {
        if (value != null) {
            for (DocType doc : DocType.values()) {
                if (doc.value.equalsIgnoreCase(value)) {
                    return doc;
                }
            }
        }
        throw new IllegalArgumentException("Unknown document type: " + value);
    }
}