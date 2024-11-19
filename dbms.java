import java.util.*;
import java.io.*;

class Data_read {
    static final String FILE_NAME = "dbms_data";

    void data_read() {
        try (BufferedReader B_read = new BufferedReader(new FileReader(FILE_NAME))) {
            String str;
            System.out.println("Reading data from file: " + FILE_NAME);

            while ((str = B_read.readLine()) != null) {
                System.out.println(str);
            }
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }
}

// HashMap class for formatting 
class Mapping_task {
    static final String FILE_NAME = "dbms_data";
    private HashMap<String, HashMap<String, String>> Main_mapping = new HashMap<>();

    Mapping_task() {
        // Load data from the file into Main_mapping
        loadExistingData();
    }

    void loadExistingData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            String currentMainKey = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("main Key:")) {
                    currentMainKey = line.substring(line.indexOf(":") + 1).trim();
                    Main_mapping.put(currentMainKey, new HashMap<>());
                } 
                else if (line.startsWith("    sub-Key:") && currentMainKey != null) {
                    String[] parts = line.split(",");
                    String subKey = parts[0].split(":")[1].trim();
                    String value = parts[1].split(":")[1].trim();
                    Main_mapping.get(currentMainKey).put(subKey, value);
                }
            }
            System.out.println("Loaded existing data from file.");
        } 
        catch (FileNotFoundException e) {
            System.out.println("File not found. Starting with an empty database.");
        } 
        catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
        }
    }

    void deleteMainKey(String mainKey) {
        if (Main_mapping.containsKey(mainKey)) {
            Main_mapping.remove(mainKey);
            System.out.println("Main key '" + mainKey + "' deleted.");
        } 
        else {
            System.out.println("Main key '" + mainKey + "' does not exist.");
        }
    }

    void deleteSubKey(String mainKey, List<String> subKeys) {
        if (Main_mapping.containsKey(mainKey)) {
            HashMap<String, String> subMap = Main_mapping.get(mainKey);
            for (String subKey : subKeys) {
                if (subMap.containsKey(subKey)) {
                    subMap.remove(subKey);
                    System.out.println("Sub-key '" + subKey + "' deleted from main key '" + mainKey + "'.");
                } else {
                    System.out.println("Sub-key '" + subKey + "' does not exist under main key '" + mainKey + "'.");
                }
            }
            if (subMap.isEmpty()) {
                Main_mapping.remove(mainKey);
                System.out.println("Main key '" + mainKey + "' deleted as it has no sub-keys left.");
            }
        } else {
            System.out.println("Main key '" + mainKey + "' does not exist.");
        }
    }

    void processQuery(String query) {
        query = query.trim();
        if (query.toLowerCase().startsWith("delete")) {
            if (query.toLowerCase().startsWith("delete from")) {
                String mainKeyPart = query.substring(query.indexOf(":") + 1, query.indexOf("{")).trim().replace("\"", "").replace("delete from ", "").trim();
                String valuesPart = query.substring(query.indexOf("{") + 1, query.indexOf("}")).trim();
                String[] subKeys = valuesPart.split(",");

                List<String> subKeysList = new ArrayList<>();
                for (String key : subKeys) {
                    String[] keyParts = key.split(":");
                    if (keyParts.length > 1) {
                        subKeysList.add(keyParts[1].trim());
                    } 
                    else {
                        System.out.println("Invalid sub-key format: " + key);
                    }
                }

                deleteSubKey(mainKeyPart, subKeysList);
            } 
            else {
                String mainKey = query.substring(query.indexOf(":") + 1).trim().replace("\"", "");
                deleteMainKey(mainKey);
            }
        } 
        else if (query.toLowerCase().startsWith("create")) {
            String mainKey = query.substring(query.indexOf(":") + 1).trim().replace("\"", "");
            Main_mapping.putIfAbsent(mainKey, new HashMap<>());
            System.out.println("Main key '" + mainKey + "' created.");
        } 
        else if (query.toLowerCase().startsWith("search")) {
            // Extract the main key from the search query
            String mainKey = query.substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("search", "").trim();
        
            // Check if the main key exists in the mapping
            if (Main_mapping.containsKey(mainKey)) {
                System.out.println("Main Key: " + mainKey);
                HashMap<String, String> subMap = Main_mapping.get(mainKey);
        
                // Print all sub-keys and their values
                for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                    System.out.println("    sub-Key: " + subEntry.getKey() + ", Value: " + subEntry.getValue());
                }
            } else {
                System.out.println("Main key '" + mainKey + "' does not exist.");
            }
        }
        else if (query.toLowerCase().startsWith("search where")) {
            try {
                // Extract the part after "search where" and trim spaces
                String conditionsPart = query.substring(query.indexOf("{"), query.lastIndexOf("}") + 1).trim();
        
                // Split multiple conditions if they exist
                String[] conditions = conditionsPart.split("\\},\\s*\\{");
        
                // Parse each condition and store them as pairs of subKey and value
                List<Map<String, String>> subKeyConditions = new ArrayList<>();
                for (String condition : conditions) {
                    // Ensure braces are correctly removed
                    condition = condition.replace("{", "").replace("}", "").trim();
                    String[] keyValuePairs = condition.split(",\\s*");
        
                    // Extract key-value pairs
                    Map<String, String> subKeyValue = new HashMap<>();
                    for (String keyValue : keyValuePairs) {
                        String[] keyValueSplit = keyValue.split(":");
                        if (keyValueSplit.length == 2) {
                            String subKey = keyValueSplit[0].trim();
                            String value = keyValueSplit[1].trim();
                            subKeyValue.put(subKey, value);
                        } else {
                            System.out.println("Invalid sub-key-value format: " + condition);
                            return;
                        }
                    }
                    subKeyConditions.add(subKeyValue);
                }
        
                // Now search through the Main_mapping for keys that match all conditions
                boolean found = false;
                for (Map.Entry<String, HashMap<String, String>> entry : Main_mapping.entrySet()) {
                    String mainKey = entry.getKey();
                    HashMap<String, String> subMap = entry.getValue();
        
                    // Check if this main key satisfies all conditions
                    boolean match = true;
                    for (Map<String, String> condition : subKeyConditions) {
                        for (Map.Entry<String, String> conditionEntry : condition.entrySet()) {
                            String subKey = conditionEntry.getKey();
                            String expectedValue = conditionEntry.getValue();
        
                            if (!subMap.containsKey(subKey) || !subMap.get(subKey).equals(expectedValue)) {
                                match = false;
                                break;
                            }
                        }
                        if (!match) break;
                    }
        
                    // If all conditions match, print the main key and its sub-keys
                    if (match) {
                        System.out.println("Main Key: " + mainKey);
                        for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                            System.out.println("    sub-Key: " + subEntry.getKey() + ", Value: " + subEntry.getValue());
                        }
                        found = true;
                    }
                }
        
                if (!found) {
                    System.out.println("No main key found matching the given sub-key conditions.");
                }
            } catch (Exception e) {
                System.out.println("Error processing query: " + e.getMessage());
            }
        }
        
        
        
        else if (query.toLowerCase().startsWith("insert into")) {
            String[] parts = query.split("values", 2);
            if (parts.length < 2) {
                System.out.println("Invalid query format for insert.");
                return;
            }
            String mainKeyPart = parts[0].substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("insert into ", "").trim();
            String valuesPart = parts[1].trim();

            if (!Main_mapping.containsKey(mainKeyPart)) {
                System.out.println("Main key '" + mainKeyPart + "' does not exist. Use CREATE first.");
                return;
            }

            valuesPart = valuesPart.substring(1, valuesPart.length() - 1);  
            String[] keyValuePairs = valuesPart.split("},\\s*\\{");  

            for (String pair : keyValuePairs) {
                pair = pair.replace("{", "").replace("}", "").trim();
                String[] keyValue = pair.split(",");
                if (keyValue.length != 2) {
                    System.out.println("Invalid key-value format: " + pair);
                    continue;
                }
                String[] subKeyParts = keyValue[0].split(":");
                String[] valueParts = keyValue[1].split(":");
                if (subKeyParts.length < 2 || valueParts.length < 2) {
                    System.out.println("Invalid key or value format in: " + pair);
                    continue;
                }
                String subKey = subKeyParts[1].trim();
                String value = valueParts[1].trim();
                Main_mapping.get(mainKeyPart).put(subKey, value);
            }
            System.out.println("\nInserted values into '" + mainKeyPart + "'.");
        }
        else if (query.toLowerCase().startsWith("update ")) {
            if (query.contains("to")) {
                // Update main key
                String[] parts = query.split("to", 2);
                if (parts.length < 2) {
                    System.out.println("Invalid update format for main key.");
                    return;
                }
                String oldMainKey = parts[0].substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("update ", "").trim();
                String newMainKey = parts[1].trim().replace("\"", "");
                
                if (Main_mapping.containsKey(oldMainKey)) {
                    if (!Main_mapping.containsKey(newMainKey)) {
                        Main_mapping.put(newMainKey, Main_mapping.remove(oldMainKey));
                        System.out.println("Main key '" + oldMainKey + "' updated to '" + newMainKey + "'.");
                    } 
                    else {
                        System.out.println("Main key '" + newMainKey + "' already exists. Merging entries.");
                        Main_mapping.get(newMainKey).putAll(Main_mapping.remove(oldMainKey));
                    }
                } 
                else {
                    System.out.println("Main key '" + oldMainKey + "' does not exist.");
                }
            } 
            else {
                // Update sub-keys
                String[] parts = query.split("values", 2);
                if (parts.length < 2) {
                    System.out.println("Invalid update format for sub-keys.");
                    return;
                }
                String mainKey = parts[0].substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("update ", "").trim();
                String valuesPart = parts[1].trim();
    
                if (!Main_mapping.containsKey(mainKey)) {
                    System.out.println("Main key '" + mainKey + "' does not exist. Use CREATE first.");
                    return;
                }
    
                valuesPart = valuesPart.substring(1, valuesPart.length() - 1);  
                String[] keyValuePairs = valuesPart.split("},\\s*\\{");  
    
                for (String pair : keyValuePairs) {
                    pair = pair.replace("{", "").replace("}", "").trim();
                    String[] keyValue = pair.split(",");
                    if (keyValue.length != 2) {
                        System.out.println("Invalid key-value format: " + pair);
                        continue;
                    }
                    String[] subKeyParts = keyValue[0].split(":");
                    String[] valueParts = keyValue[1].split(":");
                    if (subKeyParts.length < 2 || valueParts.length < 2) {
                        System.out.println("Invalid key or value format in: " + pair);
                        continue;
                    }
                    String subKey = subKeyParts[1].trim();
                    String value = valueParts[1].trim();
                    Main_mapping.get(mainKey).put(subKey, value);
                }
                System.out.println("\nUpdated values in '" + mainKey + "'.");
            }
        } 
        else {
            System.out.println("\nInvalid query.");
        }
        writeDataToFile();
    }

    void writeDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Map.Entry<String, HashMap<String, String>> entry : Main_mapping.entrySet()) {
                String mainKey = entry.getKey();
                writer.write("main Key: " + mainKey + "\n");

                HashMap<String, String> subMap = entry.getValue();
                for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                    writer.write("    sub-Key: " + subEntry.getKey() + ", Value: " + subEntry.getValue() + "\n");
                }
            }
            System.out.println("\nData successfully updated in " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("\nAn error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    void data_collection() {
        Scanner s1 = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter SQL-like query or type 'quit' to exit:");
            String query = s1.nextLine();

            if (query.equalsIgnoreCase("quit")) {
                writeDataToFile();
                break;
            }
            processQuery(query);
        }
    }
}

public class dbms {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Data_read dr = new Data_read(); 
        Mapping_task mt = new Mapping_task(); 

        while (true) {
            System.out.println("\n\n\t\t<<===== no-SQL dbms =====>> ");
            System.out.println("\n\n\t\t Press 1 for data reading");
            System.out.println("\t\t Press 2 for dbms tasks ");
            System.out.println("\t\t Press 3 for exit\n\n");

            int i = sc.nextInt();
            sc.nextLine();

            switch (i) {
                case 1: 
                    dr.data_read();
                    break;

                case 2:
                    mt.data_collection();
                    break;

                case 3: 
                    System.out.println("<<===== Thanks =====>>");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("\t\t<<===== Invalid choice =====>>\n\n");
            }
        }
    }
}
