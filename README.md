# MoneyManager
 
A fairly simple kind of just "getting into" demo Kotlin application for school homework project.

This is a Ktor based REST API that uses HTTP to communicate with the client. You can try it with any HTTP app like postman.

Available requests:

-POST /expenses: Creates a new expense
-GET /expenses: Gets all expenses (filterable)
-Get /expenses/{id}: Gets one expense by its ID
-DELETE /expenses/{id}: Deletes one expense by its ID
-PUT /expenses/{id}: Updates a given expense by its ID
-PUT /expenses/merge Merges to expense entities to one
