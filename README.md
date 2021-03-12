**This is an spring security Authentication and Authorization example.**

**Authentication:**
```
1. User will enter user name and password.
2. Spring security will check  if user is exist if not return error message as UserNotFoundException and user handy message to the consumer.
3. If exist, then  authenticate to the user using its credential/principle.
4. After successful authentication user will store in Spring application context.
```

**Authorization:**
    1. Create a table structure to fetch the user roles as per assigned roles. (Use .sql file in root folder of the project).
    2. After successful completion of first phase is authentication, then role will be applied to the specific endpoint for users.
       if user have any role/s. If role/s are not found then Unauthentication access will be return the consumer. 
       If role/s are found then request will accomplish the task.

**Permissions:**
	1. I have added permission based authorization to the rest endpoints
	2. User can access only those endpoints who have access to them. Other user request will be return as access denied.
	
