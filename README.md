
# Bug Reports Mining

This project contains the implementation and data of the course project intituled **"Production Bug Localization Using Logs and Test Coverage"**.

The scripts are organized as follows:
* [getBugReportsList_Jira](https://github.com/SPEAR-SE/BugReportsMining/blob/main/getBugReportsList_Jira.ipynb) - Extracts all the Jira tickets from a given project that obey these project's criteria, that is: 1- the task is closed, 2- have a bug issue type assigned to it, 3- have logs inside its content (log snippets and/or Stack traces), 4- have a traceable bugfix commit
* [getBugReportsList_GithubIssues](https://github.com/SPEAR-SE/BugReportsMining/blob/main/getBugReportsList_GithubIssues.ipynb) - Does the same, but for GitHub Issues instead of Jira
* [searchCommitOnTEvosRepo](https://github.com/SPEAR-SE/BugReportsMining/blob/main/searchCommitOnTEvosRepo.ipynb) - Given a JSON file with the bug reports and its correspondents buggy and bugfix commit, it searches the commits on T-Evos database and outputs the bug-reports in which both commits were found in the base.
* [extractCodeCoverageData](https://github.com/SPEAR-SE/BugReportsMining/blob/main/extractCodeCoverageData.ipynb) - Given a folder with T-Evos coverage data for a given project, it calculates details such as the covered lines and the coverage percentage for each file
* [analyseCoverage](https://github.com/SPEAR-SE/BugReportsMining/blob/main/analyseCoverage.ipynb) - Based on the coverage data, it makes a coverage analysis in a file-level
* [analyseCoverage-2.0.](https://github.com/SPEAR-SE/BugReportsMining/blob/main/analyseCoverage-2.0.ipynb) - Based on the coverage data, it makes a coverage analysis in a method-level and in general
* [modifiedOchiai](https://github.com/SPEAR-SE/BugReportsMining/blob/main/modifiedOchiai.ipynb) - Implements Approach 1 based on Ochiai presented in the report
*  [modifiedOchiai2](https://github.com/SPEAR-SE/BugReportsMining/blob/main/modifiedOchiai2.ipynb) - Implements the Approach 2 based on Ochiai presented in the report
* [analyseOchiaiOutputs](https://github.com/SPEAR-SE/BugReportsMining/blob/main/analyseOchiaiOutputs.ipynb) - Based on the outputs from previous scripts (suspiciousness score of each file) and the stack traces file, it generates a rank and calculate the metrics (Precision@N, Recall@N, F1@N, MAP and MRR) for each of them.

The data is structured as follows:

* [/commits](https://github.com/SPEAR-SE/BugReportsMining/tree/main/commits) - Contains the outputs from the scripts [getBugReportsList_Jira](https://github.com/SPEAR-SE/BugReportsMining/blob/main/getBugReportsList_Jira.ipynb) and [getBugReportsList_GithubIssues](https://github.com/SPEAR-SE/BugReportsMining/blob/main/getBugReportsList_GithubIssues.ipynb) for all the repos in T-Evos
* [/coverage_info](https://github.com/SPEAR-SE/BugReportsMining/tree/main/coverage_info) - Contains the outputs from the script [analyseCoverage](https://github.com/SPEAR-SE/BugReportsMining/blob/main/analyseCoverage.ipynb) 
* [/coverageMining](https://github.com/SPEAR-SE/BugReportsMining/tree/main/coverageMining) - Contains the outputs from the script [analyseCoverage-2.0.](https://github.com/SPEAR-SE/BugReportsMining/blob/main/analyseCoverage-2.0.ipynb)
* [/ochiaiScores](https://github.com/SPEAR-SE/BugReportsMining/tree/main/ochiaiScores) - Contains the outputs from the script [modifiedOchiai](https://github.com/SPEAR-SE/BugReportsMining/blob/main/modifiedOchiai.ipynb)
*  [/ochiaiScores2](https://github.com/SPEAR-SE/BugReportsMining/tree/main/ochiaiScores2) - Contains the outputs from the script [modifiedOchiai2](https://github.com/SPEAR-SE/BugReportsMining/blob/main/modifiedOchiai2.ipynb)
* [/Rankings](https://github.com/SPEAR-SE/BugReportsMining/tree/main/Rankings) - Contains the ranking information for Approach 1, Approach 2, and the Stack Traces entries


