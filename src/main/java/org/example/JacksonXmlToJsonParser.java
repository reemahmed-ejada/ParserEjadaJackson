package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JacksonXmlToJsonParser {

    // Store all paths to keys in a map
    private static Map<String, JsonNode> allNodes = new HashMap<>();
    // Store related paths for each key
    private static Map<String, String> relatedKeys = new HashMap<>();
    private static ObjectMapper jsonMapper = new ObjectMapper();


    public static void main(String[] args) {
        try {
            // First Create an instance of XmlMapper to handle XML
            XmlMapper xmlMapper = new XmlMapper();

            // Second Read XML file and convert it to a JsonNode (generic tree model for JSON data)
            File xmlFile = new File("src/main/resources/XMLResponseData-LEAD.xml");
            JsonNode jsonNode = xmlMapper.readTree(xmlFile);

            // third Now convert the JsonNode to a formatted JSON string
            String jsonString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

            // Fourth Print the resulting JSON to output file
            File file = new File("output.json");
            jsonMapper.writeValue(file, jsonNode);

            // Clear the map of nodes
            allNodes.clear();

            // Call the recursive function to populate allNodes map
            retrieveAllNodes(jsonNode, "");

            // Prompt the user to select and display the value
            promptUserToSelectNodeName(1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Recursive function to retrieve all nodes
    public static void retrieveAllNodes(JsonNode node, String parentKey) throws JsonProcessingException {

        if (node.isObject()) {
            // If the node is an object, iterate over its fields
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String currentKey = field.getKey();
                JsonNode currentValue = field.getValue();

                // Check if the current field has a "Name" and "Value" field
                if (currentKey.equals("ResultOutput") && currentValue.isObject()) {
                    // If the current node is ResultOutput (or any object), check its fields
                    Iterator<Map.Entry<String, JsonNode>> resultFields = currentValue.fields();

                    while (resultFields.hasNext()) {
                        Map.Entry<String, JsonNode> resultField = resultFields.next();
                        String resultFieldKey = resultField.getKey();
                        JsonNode resultFieldValue = resultField.getValue();
                        // Create a new key path for "Name" and "Value" fields
                        String newKey = parentKey + "." + currentKey + "." + resultFieldKey;
                        allNodes.put(newKey, resultFieldValue);

                        // Store the related key for "Name"
                        if (resultFieldKey.equals("Name")) {
                            relatedKeys.put(newKey, parentKey + "." + currentKey + ".Value");
                        }
                    }

                } else {

                    // Create a new key path for nested objects
                    String newPathKey = parentKey.isEmpty() ? currentKey : parentKey + "." + currentKey;

                    // Store this key path and value in the map
                    // System.out.println("System key value from NOT Name  is "+ currentKey+ "  and value is " +currentValue);
                    allNodes.put(currentKey, currentValue);

                    // Recursively call the function for nested object
                    retrieveAllNodes(currentValue, newPathKey);

                }
            }
        } else if (node.isArray()) {
            // If the node is an array, iterate over the array elements
            for (int i = 0; i < node.size(); i++) {
                JsonNode arrayElement = node.get(i);
                //System.out.println("Json node which is array " + jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayElement));

                // Create a new key path for array indices
                String newPathKey = parentKey + "[" + i + "]";
                //Recursively call the function for array elements but skip add it to the map
                retrieveAllNodes(arrayElement, newPathKey);
            }
        } else {
            // If the node is a value, print the key path and its value skip add it to the map to not dublicate the records
            //System.out.println("value to be added here to the map "+parentKey + ":   value is " + node.asText());
        }
    }


    // Function to prompt user to select a key and print its value
    public static void promptUserToSelectKey() {
        Scanner scanner = new Scanner(System.in);

        // Display all available key paths to the user
        System.out.println("Select one of the following key paths to extract the value:");
        List<String> keys = new ArrayList<>(allNodes.keySet());
        for (int i = 0; i < keys.size(); i++) {

            System.out.println((i + 1) + ". " + keys.get(i));
        }

        // Prompt the user to select a key
        System.out.print("Enter the number of your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume the newline

        // Ensure the choice is valid
        if (choice < 1 || choice > keys.size()) {
            System.out.println("Invalid choice. Please try again.");
        } else {
            String selectedKey = keys.get(choice - 1);
            JsonNode selectedNode = allNodes.get(selectedKey);

            // Print the selected key and its value
            // System.out.println("Selected Key: " + selectedKey);
            System.out.println("1: " + selectedNode.asText());

            // Check if the selected key has a related key
            if (relatedKeys.containsKey(selectedKey)) {
                String relatedKey = relatedKeys.get(selectedKey);
                JsonNode relatedNode = allNodes.get(relatedKey);

                // Display related key and its value
                // System.out.println("Related Key: " + relatedKey);
                System.out.println("Related Value: " + relatedNode.toString());
            }

        }
    }


    // Function to prompt user to select a Nodename and print its value
    public static void promptUserToSelectNodeName(Integer round) {
        Scanner scanner = new Scanner(System.in);
        List<String> keys = new ArrayList<>(allNodes.keySet());
        List<JsonNode> Values = new ArrayList<>(allNodes.values());

        if (round == 1) {
            // Display all available NodeNames to the user for first round
            System.out.println("Select one of the following Names to extract the value or 0 to exit :");
            for (int i = 0; i < keys.size(); i++) {

                String[] pathParts = keys.get(i).split("\\.");
                String lastString = pathParts[pathParts.length - 1];
           // Skip show the records for Value and if it is Name show it's value else show last nodename form keypath
                if (!lastString.equals("Value")) {
                    if (lastString.equals("Name")) {
                        System.out.println((i + 1) + ". " + Values.get(i).asText());
                    } else {
                        System.out.println((i + 1) + ". " + lastString);
                    }
                }
            }
        }
        // Prompt the user to select a number for the node
        System.out.print("Enter the number of your choice or 0 to exit: ");
        int choice = scanner.nextInt();

        // exit if the user enter zero
        if (choice == 0) {
            return;
        }
        // Ensure the choice is valid
        if (choice < 1 || choice > keys.size()) {
            System.out.println("Invalid choice. Please try again.");
        } else {
            // Return the Choice to the full path

            String selectedKey = keys.get(choice - 1);
            JsonNode selectedNode = allNodes.get(selectedKey);

            // Print the selected node value
            System.out.println("Value: " + selectedNode.asText());

            // Check if the selected Node has a related key
            if (relatedKeys.containsKey(selectedKey)) {
                String relatedKey = relatedKeys.get(selectedKey);
                JsonNode relatedNode = allNodes.get(relatedKey);
                // Display related key value
                System.out.println("Related Value: " + relatedNode.toString());
            }

        }

        // recursive call the function to handle multiple inputs from user
        promptUserToSelectNodeName(2);
    }


}