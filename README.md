**This is an spring security Authentication and Authorization example.**

##Authentication:
	1. User enter credentials.
	2. Spring security check and confirm the user is exist in the system then authenticate to the user using its credential/principle and returns success response.		
	3. Else, return error response back.
	4. After successful authentication user will store in Spring application context for futher request.

##Authorization:
	1. Create a table structure to fetch the user roles as per assigned roles. (Use .sql file in root folder of the project).
	2. After successful completion of authentication, access role(s) will be applied to the specific REST endpoint.
	3. If role/s are not found then Unauthentication access will be return back.
	4. User will perform the operations if role/s are found and user has the right do the operations.

##Permissions:
	1. Added permission base authorization to the REST endpoints
	2. User can access only those endpoints who have rights to do the operation/s. Other user request will be return as access **UNAUTHORIZED**.
	
