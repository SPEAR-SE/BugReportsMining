package ca.concordia;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


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
        List<String> addedLinesMethods = new ArrayList<>();
        List<String> deletedLinesMethods = new ArrayList<>();

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data_file_path)) {
            // Convert JSON to Java object
            Map<String, Map<String, Object>> data = gson.fromJson(reader, Map.class);
            // Access data from the object
            for (String project : data.keySet()) {
                if (project.equals("Lang") || project.equals("Math")){ //Skipping them for now since I can not run git checkout -
                    continue; // TODO: see how to handle
                }
                System.out.println("Project: " + project);
                String path_to_repo = projectsList.get(project);
                Map<String, Object> bugs = data.get(project);
                for (String bugID : bugs.keySet()) {
                    System.out.println("  Bug ID: " + bugID);
                    Map<String, Object> bug = (Map<String, Object>) bugs.get(bugID);
                    String buggyCommit = (String) bug.get("buggy_commit");
                    String bugfixCommit = (String) bug.get("bugfix_commit");
                    Map<String, Map<String, Object>> modifiedCode = (Map<String, Map<String, Object>>) bug.get("modified_code");
                    for (String filePath : modifiedCode.keySet()) {
                        if (filePath.startsWith("b/")) {
                            filePath = filePath.substring(2);
                        }
                        String absolute_file_path = path_to_repo + filePath;
                        Map<String, Object> fileInfo = modifiedCode.get(filePath);
                        GitHelper.checkoutCommit(path_to_repo, buggyCommit);
                        List<Double> deletedLinesDouble = (List<Double>) fileInfo.get("deleted_lines");
                        if (deletedLinesDouble != null) {
                            List<Integer> deletedLines = new ArrayList<>();

                            for (Double d : deletedLinesDouble) {
                                deletedLines.add(d.intValue());
                            }
                            for (Integer line: deletedLines){
                                String methodName = MethodFinder.findMethodName(absolute_file_path, line);
                                if (!deletedLinesMethods.contains(filePath+ " - " +methodName)) {
                                    deletedLinesMethods.add(filePath+ " - " + methodName);
                                }
                            }
                        }
                        GitHelper.checkoutCommit(path_to_repo, bugfixCommit);
                        List<Double> addedLinesDouble = (List<Double>) fileInfo.get("added_lines");
                        if (addedLinesDouble != null) {
                            List<Integer> addedLines = new ArrayList<>();

                            for (Double d : addedLinesDouble) {
                                addedLines.add(d.intValue());
                            }
                            for (Integer line: addedLines){
                                String methodName = MethodFinder.findMethodName(absolute_file_path, line);
                                if (!addedLinesMethods.contains(filePath+ " - " +methodName)) {
                                    addedLinesMethods.add(filePath+ " - " + methodName);
                                }
                            }
                        }
                        System.out.println("Added: ");
                        System.out.println(addedLinesMethods);
                        System.out.println("Deleted: ");
                        System.out.println(deletedLinesMethods);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
