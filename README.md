#### Git repository reset counter because of leaked passwords: #2
# **Description**
### This project's code contains the backend code of a system that handles tax declarations of property transactions

# **Setup**
### 1.Clone the project
### 2.Create a MySQL VM/Local/Hosted database and execute the database.sql file
### 3.Populate the environment variables that application.properties file uses. 
### 4.Run the application from the IDE

```shell
git clone https://github.com/Kostas-Xafis/DtstProject.git

cd DtstProject

// Execute database.sql file

// Either populate the environment variables from cli and open intellij from cli 
    //linux
export DatabaseURL="mysql://{ MYSQLUSER }:{ MYSQLPASSWORD }@{ MYSQLHOST }:{ MYSQLPORT }" 
export DatabasePassword="pwd"
export jwtSecret="jwtSecret"
export AdminSecret="anyRandomStrongKeyWillDo"

    //powershell
$env:DatabaseURL = "mysql://{ MYSQLUSER }:{ MYSQLPASSWORD }@{ MYSQLHOST }:{ MYSQLPORT }" 
$env:DatabasePassword="pwd"
$env:jwtSecret="jwtSecret"
$env:AdminSecret="anyRandomStrongKeyWillDo"
    
    // IDE configuration
// or open the project configuration that your IDE provides and populate them through that.

// Run the application from your IDE:

```

#### For convenience all dummy users from the database.sql have "password" as their password. 