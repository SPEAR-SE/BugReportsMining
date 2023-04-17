package ca.concordia;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.lang.reflect.Type;


public class Application {

    static String data_file_path = "../data/merged_data_production_bug_reports.json";


    static Map<String, String> projectsList = new HashMap<>() {{
        put("Lang", "/Users/lorenapacheco/Concordia/Masters/defects4j/project_repos/commons-lang.git/");
        put("Math", "/Users/lorenapacheco/Concordia/Masters/defects4j/project_repos/commons-math.git/");
        put("Cli", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/commons-cli/");
        put("Closure", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/closure-compiler/");
        put("Codec", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/commons-codec/");
        put("Collections", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/commons-collections/");
        put("Compress", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/commons-compress/");
        put("Csv", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/commons-csv/");
        put("Gson", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/gson/");
        put("JacksonCore", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/jackson-core/");
        put("JacksonDatabind", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/jackson-databind/");
        put("Jsoup", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/jsoup/");
        put("JxPath", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/commons-jxpath/");
        put("Mockito", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/mockito/");
        put("Time", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/joda-time/");
        put("fastjson", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/fastjson/");
        put("junit4", "/Users/lorenapacheco/Concordia/Masters/open_source_repos_being_studied/junit4/");
    }};


    public static void main(String[] args){

        Gson gson = new GsonBuilder().create();




        try (FileReader reader = new FileReader(data_file_path)) {
            // Convert JSON to Java object
            Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
            Map<String, Map<String, Object>> data = gson.fromJson(reader, type);

            // Access data from the object
            for (String project : data.keySet()) {
                if (project.equals("Lang") || project.equals("Math")){ //Skipping them for now since I can not run git checkout -
                    continue; // TODO: see how to handle
                }
                String path_to_repo = projectsList.get(project);
                Map<String, Object> bugs = data.get(project);
                for (String bugID : bugs.keySet()) {

                    Map<String, Object> bug = (Map<String, Object>) bugs.get(bugID);
                    String buggyCommit = (String) bug.get("buggy_commit");
                    String bugfixCommit = (String) bug.get("bugfix_commit");

                    // Checking the code
                    List<String> addedLinesMethods;
                    List<String> deletedLinesMethods;
                    Map<String, List<String>> buggyMethods = new HashMap<>();
                    Map<String, List<String>> newMethods = new HashMap<>();
                    Map<String, Map<String, Object>> modifiedCode = (Map<String, Map<String, Object>>) bug.get("modified_code");
                    for (String filePath : modifiedCode.keySet()) {
                        String filePathFixed = filePath;
                        if (filePath.startsWith("b/")) {
                            filePathFixed = filePath.substring(2);
                        }
                        String absolute_file_path = path_to_repo + filePath;
                        Map<String, Object> fileInfo = modifiedCode.get(filePath);
                        List<Double> deletedLinesDouble = (List<Double>) fileInfo.get("deleted_lines");
                        deletedLinesMethods = get_touched_methods(buggyCommit, path_to_repo,deletedLinesDouble, absolute_file_path, filePathFixed, false);
                        List<Double> addedLinesDouble = (List<Double>) fileInfo.get("added_lines");
                        addedLinesMethods = get_touched_methods(bugfixCommit, path_to_repo,addedLinesDouble, absolute_file_path, filePathFixed, false);

                        for (String method : deletedLinesMethods){
                            String fileName = method.split(" - ")[0];
                            String methodName = method.split(" - ")[1];
                            buggyMethods= addNewMethod(buggyMethods, methodName, fileName);
                        }
                        List<Integer> addedLines = convertDoubleListIntoIntegers(addedLinesDouble);
                        // If a method was added in the bugfix commit, it is not a buggy method
                        GitHelper.checkoutCommit(path_to_repo, bugfixCommit);
                        for (String method : addedLinesMethods){
                            if (!deletedLinesMethods.contains(method)){
                                String fileName = method.split(" - ")[0];
                                String methodName = method.split(" - ")[1];
                                boolean newMethod = CheckIfMethodWasCreatedFinder.checkIfMethodWasCreated(absolute_file_path, addedLines, methodName );
                                if (newMethod){
                                    newMethods= addNewMethod(newMethods, methodName, fileName);
                                } else{
                                    buggyMethods= addNewMethod(buggyMethods, methodName, fileName);
                                }
                            }
                        }
                    }

                    // Checking the tests
                    List<String> addedLinesTests;
                    List<String> deletedLinesTests;
                    Map<String, List<String>> updatedTests = new HashMap<>();
                    Map<String, List<String>> newTests = new HashMap<>();
                    Map<String, Map<String, Object>> modifiedTests = new HashMap<>();
                    if (bug.get("modified_tests") != null){
                        modifiedTests = (Map<String, Map<String, Object>>) bug.get("modified_tests");
                    }
                    for (String filePath : modifiedTests.keySet()) {
                        String filePathFixed = filePath;
                        if (filePath.startsWith("b/")) {
                            filePathFixed = filePath.substring(2);
                        }
                        String absolute_file_path = path_to_repo + filePath;
                        Map<String, Object> fileInfo = modifiedTests.get(filePath);
                        List<Double> deletedLinesDouble = (List<Double>) fileInfo.get("deleted_lines");
                        deletedLinesTests = get_touched_methods(buggyCommit, path_to_repo, deletedLinesDouble, absolute_file_path, filePathFixed, true);
                        List<Double> addedLinesDouble = (List<Double>) fileInfo.get("added_lines");
                        addedLinesTests = get_touched_methods(bugfixCommit, path_to_repo, addedLinesDouble, absolute_file_path, filePathFixed, true);

                        for (String test : deletedLinesTests){
                            String fileName = test.split(" - ")[0];
                            String testName = test.split(" - ")[1];
                            updatedTests= addNewMethod(updatedTests, testName, fileName);
                        }
                        List<Integer> addedLines = convertDoubleListIntoIntegers(addedLinesDouble);
                        // If a method was added in the bugfix commit, it is not a buggy method
                        GitHelper.checkoutCommit(path_to_repo, bugfixCommit);
                        for (String test : addedLinesTests) {
                            if (!deletedLinesTests.contains(test)) {
                                String fileName = test.split(" - ")[0];
                                String testName = test.split(" - ")[1];
                                boolean newTest = CheckIfMethodWasCreatedFinder.checkIfMethodWasCreated(absolute_file_path, addedLines, testName);
                                if (newTest) {
                                    newTests= addNewMethod(newTests, testName, fileName);
                                } else {
                                    updatedTests= addNewMethod(updatedTests, testName, fileName);
                                }
                            }
                        }
                    }
                    bug.put("buggyMethods", buggyMethods);
                    bug.put("newMethods", newMethods);
                    bug.put("updatedTests", updatedTests);
                    bug.put("newTests", newTests);
                }
            }
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            String updatedJson = gsonPretty.toJson(data);

            // Write the updated JSON string to the file
            try (FileWriter writer = new FileWriter(data_file_path)) {
                writer.write(updatedJson);
                System.out.println("Json file data/merged_data_production_bug_reports.json update with the extract information: buggyMethods, newMethods, updatedTests and newTests");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static List<String> get_touched_methods(String commit, String pathToRepo, List<Double> linesDouble, String absoluteFilePath, String filePath, boolean isTest) throws IOException {
        List<String> touchedMethods = new ArrayList<>();
        GitHelper.checkoutCommit(pathToRepo, commit);
        List<Integer> lines = convertDoubleListIntoIntegers(linesDouble);
        if (linesDouble != null) {
            for (Integer line: lines){
                String methodName = MethodFinder.findMethodName(absoluteFilePath, line, isTest);
                if (methodName != null && !touchedMethods.contains(filePath+ " - " +methodName)) {
                    touchedMethods.add(filePath+ " - " + methodName);
                }
            }
        }
        return touchedMethods;
    }
    public static List<Integer> convertDoubleListIntoIntegers(List<Double> linesDouble) {
        List<Integer> lines = new ArrayList<>();
        if (linesDouble != null) {
            for (Double d : linesDouble) {
                lines.add(d.intValue());
            }
        }
        return lines;
    }

    public static Map<String, List<String>> addNewMethod(Map<String, List<String>> methodsObject,String methodName, String fileName){
        if (!methodsObject.keySet().contains(fileName)) {
            methodsObject.put(fileName, new ArrayList<>(Arrays.asList()));
        }
        List<String> fileMethods = methodsObject.get(fileName);
        fileMethods.add(methodName);
        methodsObject.put(fileName, fileMethods);
        return methodsObject;
    }

}