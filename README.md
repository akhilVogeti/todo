# To-do List

This project is a SpringBoot application with MongoDB as the database backend, that helps registered users to perform basic CRUD operations on tasks and task lists along with advanced features like search, sort, filter their tasks. All the functionalities have been unit tested.

### Pre-requisites
* Docker to run the Dockerized application
* Postman for using the APIs

### Running the application
First, clone the repository and navigate to the project directory
```bash
git clone https://github.com/akhilVogeti/todo.git   
cd todo
```
To start the application, run the following command
```bash
docker compose up
```

### APIs
1. Register user
```bash
curl -X POST \
-H "Content-type: application/json" \
-d '{ "username": "user1", "password": "password1" }' \
'http://localhost:8080/register'
```
2. Create two task-lists : "Personal" , "Work". (A default list by name "inbox" will be created for every user to act as a list for tasks with no specific list assigned.)
```bash
curl -u "user1:password1" \
 -X POST \ 
-H "Content-Type: application/json" \
-d '{"listName":"Personal"}'  \
http://localhost:8080/tasks/lists
```
```bash
curl -u "user1:password1" \
 -X POST \ 
-H "Content-Type: application/json" \
-d '{"listName":"Work"}'  \
http://localhost:8080/tasks/lists
```

3. Add tasks to "Personal" and then "Work". 
```bash
curl -u "user1:password1" \
 -X POST \
-H "Content-Type: application/json" \
-d '{"title":"Task1",
"description":"Description for Task1",
"createdOn":"2024-05-01",
"dueDate":"2024-05-10",
"category":"Category1",
"completed":true,
"priority":"1"}' \
http://localhost:8080/tasks/lists/Personal
```
```bash
curl -u "user1:password1" \
-X POST\ 
-H "Content-Type: application/json" \
-d '{"title":"Task2",
"description":"Description for Task2 to be done after Task1",
"createdOn":"2024-05-01",
"dueDate":"2024-05-10",
"category":"Category1",
"completed":false,
"priority":"2"}' \
http://localhost:8080/tasks/lists/Personal
```
```bash
curl -u "user1:password1" \
-X POST \
-H "Content-Type: application/json" \
-d '{
  "title": "Task3",
  "description": "Description for Task3",
  "createdOn": "2024-05-01",
  "dueDate": "2024-05-11",
  "category": "Category2",
  "completed": false,
  "priority": "2"
}' \
http://localhost:8080/tasks/lists/Personal
```
```bash
curl -u "user1:password1" \
-X POST \
-H "Content-Type: application/json" \
-d '{
"title": "Task5",
"description": "Description for Task5",
"createdOn": "2024-05-01",
"dueDate": "2024-05-09",
"category": "Category3",
"completed": false,
"priority": "1"
}' \
http://localhost:8080/tasks/lists/Work
```
```bash
curl -u "user1:password1" \
-X POST \
-H "Content-Type: application/json" \
-d '{
  "title": "Task6",
  "description": "Description for Task6",
  "createdOn": "2024-05-01",
  "dueDate": "2024-05-03",
  "category": "Category3",
  "completed": false,
  "priority": "5"
}' \
http://localhost:8080/tasks/lists/Work
```
```bash
curl -u "user1:password1" \
-X POST \
-H "Content-Type: application/json" \
-d '{
  "title": "Task7",
  "description": "Description for Task7",
  "createdOn": "2024-05-01",
  "dueDate": "2024-05-18",
  "category": "Category3",
  "completed": true,
  "priority": "2"
}' \
http://localhost:8080/tasks/lists/Work
```
4. Add a task to default list called inbox.
```bash
curl -u "user1:password1" \
-X POST \
-H "Content-Type: application/json" \
-d '{
  "title": "Task4",
  "description": "Description for Task4",
  "createdOn": "2024-05-01",
  "dueDate": "2024-05-15",
  "category": "Category2",
  "completed": true,
  "priority": "4"
}' \
http://localhost:8080/tasks
```
5. Get all tasks of the user. (By default sorted by due date and incomplete tasks appear first)
```bash
curl -u "user1:password1" -X GET http://localhost:8080/tasks
```

6. Get tasks by listName. 
```bash
curl -u "user1:password1" -X GET http://localhost:8080/tasks/lists/Personal
```
```bash
curl -u "user1:password1" -X GET http://localhost:8080/tasks/lists/Personal
```bash
7. Get lists and tasks. ({listName:Tasks})
```bash
curl -u "user1:password1" -X GET http://localhost:8080/tasks/lists
```
8. Update listName of a list.
```bash
curl -u "user1:password1" \
-X PUT \
-H "Content-Type: application/json" \
-d '{
  "listName": "veryPersonal"
}' \
http://localhost:8080/tasks/lists/veryPersonal
```
9. Delete a list by {listName}. (All tasks in the list would get deleted too.)
```bash
curl -u "user1:password1" \
-X DELETE \
http://localhost:8080/tasks/lists/{listName}
```
10. Get Task by its Id. 
```bash
curl -u "user1:password1" \
-X GET \
http://localhost:8080/tasks/{id}
```
11. Update a task.
```bash
curl -u "user1:password1" \
-X PUT \
-H "Content-Type: application/json" \
-d '{
  "title": "updatedTitle",
  "description": "updated Description for Task",
  "createdOn": "2024-05-01",
  "dueDate": "2024-05-15",
  "category": "Category2",
  "completed": true,
  "priority": "4"
}' \
http://localhost:8080/tasks/{id}
```
12. Delete a task
```bash
curl -u "user1:password1" \
-X DELETE \
http://localhost:8080/tasks/{id}
```
13. Search in title or description. Will return tasks whose title or description has words matching your search text.
```bash
curl -u "user1:password1" \
-X GET \
http://localhost:8080/tasks/search?query={searchText}
```
14. Sort And Filter your tasks. Sorting can be done by dueDate or priority. Filtering by category or completion. Few combinations are given below.
- dueDate, category, Category1. 
```bash
curl -u "user1:password1" \
-X GET \
http://localhost:8080/tasks?sortBy=dueDate&filterBy=category&filterValue=Category1
```
- dueDate, completed, false. (Incomplete tasks sorted by due date)
```bash
curl -u "user1:password1" \
-X GET \
http://localhost:8080/tasks?sortBy=dueDate&filterBy=completed&filterValue=false
```
- priority, completed, false.
```bash
curl -u "user1:password1" \
-X GET \
http://localhost:8080/tasks?sortBy=priority&filterBy=completed&filterValue=false
```
- priority, category, Category2
```bash
curl -u "user1:password1" \
-X GET \
"http://localhost:8080/tasks?sortBy=priority&filterBy=category&filterValue=Category2"
```
### Contribute to the project
To contribute to the project, follow these steps:

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Commit and push your changes
5. Create a pull request

### License
Licensed under the MIT license.










