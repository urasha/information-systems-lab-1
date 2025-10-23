package ru.urasha.studygroup.util;

import com.fasterxml.jackson.databind.JsonMappingException;
import ru.urasha.studygroup.dto.ImportErrorDto;

import java.util.List;

public final class JsonErrorParser {

    private JsonErrorParser() {  }

    public static ImportErrorDto fromJsonMappingException(JsonMappingException jme) {
        List<JsonMappingException.Reference> path = jme.getPath();
        StringBuilder fieldBuilder = new StringBuilder();
        int recordIndex = -1;

        for (JsonMappingException.Reference ref : path) {
            if (ref.getFieldName() != null) {
                if (!fieldBuilder.isEmpty()) fieldBuilder.append('.');
                fieldBuilder.append(ref.getFieldName());
            }
            if (ref.getIndex() != -1 && recordIndex == -1) {
                recordIndex = ref.getIndex();
            }
        }

        String fieldPath = fieldBuilder.isEmpty() ? "file" : fieldBuilder.toString();

        String message = "Invalid value for field '" + fieldPath + "'";

        return new ImportErrorDto(recordIndex, fieldPath, message);
    }
}
