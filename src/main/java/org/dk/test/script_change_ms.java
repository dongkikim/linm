package org.dk.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class script_change_ms {
    //script 시간조정
    private static final long MS_OFFSET = 18000;
    private static final String filePath = "src/main/resources/macro/changeChar_scrolldown.json";
    public static void main(String[] args) {
        modifyJsonFile();
    }

    public static void modifyJsonFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        // Enable pretty printing for output
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Read JSON from file
            JsonNode rootNode = mapper.readTree(file);

            // Navigate to "records"
            JsonNode recordsNode = rootNode.path("records");
            if (recordsNode.isMissingNode()) {
                System.out.println("'records' node not found in " + filePath);
                return;
            }

            // Iterate through each record in "records"
            Iterator<Map.Entry<String, JsonNode>> fields = recordsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> recordEntry = fields.next();
                JsonNode recordValue = recordEntry.getValue();

                // Navigate to "events" inside the record
                JsonNode eventsNode = recordValue.path("events");
                if (eventsNode.isArray()) {
                    ArrayNode eventsArray = (ArrayNode) eventsNode;
                    
                    // Iterate through events and modify "ms"
                    for (JsonNode event : eventsArray) {
                        if (event.has("ms")) {
                            String msStr = event.get("ms").asText();
                            try {
                                long currentMs = Long.parseLong(msStr);
                                long newMs = currentMs - MS_OFFSET;
                                
                                // Update the "ms" field
                                ((ObjectNode) event).put("ms", String.valueOf(newMs));
                            } catch (NumberFormatException e) {
                                System.out.println("Skipping invalid ms value: " + msStr);
                            }
                        }
                    }
                }
            }

            // Write the modified JSON back to the file
            mapper.writeValue(file, rootNode);
            System.out.println("Successfully processed " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
