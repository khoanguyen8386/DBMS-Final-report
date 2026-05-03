====================================================================
   INSTALLATION AND SOURCE CODE EXECUTION GUIDE: COURSE REGISTRATION SYSTEM
====================================================================
Project: Course Registration System
Course: Principles of Database Management - IU University

Dear Professor/Instructor, this is the source code for our project. To run the application directly on your personal computer environment, please follow these configuration steps sequentially:

--------------------------------------------------------------------
PART 1: DATABASE SETUP
--------------------------------------------------------------------
1. Open MySQL Workbench.
2. Open the provided script file named [database_script.sql] included in the submission folder.
3. Copy the entire content of the file, paste it into the Query window, and click "Execute" (the lightning bolt icon) to run it.
   -> This process will automatically create the Database, table structures, and insert the sample data.

--------------------------------------------------------------------
PART 2: DATABASE CONNECTION CONFIGURATION IN SOURCE CODE (IMPORTANT)
--------------------------------------------------------------------
Since the MySQL password varies on each machine, please change the connection configuration in the code so the application can successfully connect to the Database created in Part 1:

1. Open this entire Project folder using an IDE (IntelliJ IDEA, Eclipse, or VS Code).
2. Locate the Database connection configuration file at the following path:
   -> src / main / java / com / courseapp / util / DBConnection.java
      (Note: Please ensure this path matches your actual folder structure).
3. In the DBConnection.java file, please find the login credentials and update them to match your local MySQL credentials:
   - String USER = "root";  (Keep this unchanged if you use the root account)
   - String PASSWORD = "enter_your_mysql_password_here";

--------------------------------------------------------------------
PART 3: COMPILE AND RUN THE APPLICATION
--------------------------------------------------------------------
After successfully updating the Database password, you can run the application using one of the following methods:

Method: Run via Maven commands:
- Prerequisite: You must have Apache Maven installed on your computer.
  + If Maven is not installed, please download it from: https://maven.apache.org/download.cgi
  + Extract the downloaded file and add the Maven 'bin' folder path to your system's Environment Variables (PATH).
- Open Terminal/Command Prompt at the root folder of the project.
- Type the following command to compile and package the code: mvn clean package
- After it displays "BUILD SUCCESS"
- Navigate to the 'target' folder and run the .jar file
- java -jar target\CourseRegistrationSystem-1.0-SNAPSHOT.jar

--------------------------------------------------------------------
PART 4: SAMPLE TEST ACCOUNTS
--------------------------------------------------------------------
The system has been pre-loaded with sample data. 

* Admin Role: 
  - Email: admin@university.edu 
  - Password: admin123

* Instructor Role: 
  - Email: an.nv@university.edu 
  - Password: pass123
* Student Role: 
  - Email: lananh@student.edu 
  - Password: 1234

Thank you very much for your time reviewing and grading our project!
====================================================================