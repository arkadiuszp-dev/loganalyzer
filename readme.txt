------------
Instructions
------------
1. Build using maven3 running command:
     mvn install
2. Run parallel processing running command:
     java -cp csassignment102019-1.0-SNAPSHOT.jar ap.cs.ParallelLogAnalyzer [path to file]
   Run sequential processing running command:
     java -cp csassignment102019-1.0-SNAPSHOT.jar ap.cs.LogAnalyzer [path to file]

   Basic file example can be found in src/main/resources

----------------
Design decisions
----------------

1.
Algorithm assumes that the amount of analyzed processes that live concurrently, overlapping each other in the file,
does not exceed the memory available for the matching process.
It uses HashMap to store unmatched entries for performance reasons.
If this condition was not satisfied or if it was important to ensure
that the process can be stopped at any point of reading the file and resumed later,
HashMap could be replaced by storing intermediate entries in the database.

2.
Input/output are the most time consuming operations in this assignment,
affecting total execution time more than the execution of business logic.
Sequential and parallel solutions are therefore both present for comparison.
