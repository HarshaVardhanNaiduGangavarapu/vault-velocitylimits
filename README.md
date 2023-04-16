# vault-velocitylimits
This is an Engineering Home Task project for Vault's Senior Backend Engineer Role.

## Author
* Harshavardhan Naidu Gangavarapu
* 16-APRIL-2023

### Prerequisite
* Java 17 or later
* Maven
* IntelliJ (optional)

### Task Requirements
* Apply `Velocity Limits` to `vault` customers accounts and accept or decline fund loading transactions based on limits.
* Following are the Velocity Limits conditions:
   * A maximum of $5,000 can be loaded per day.
   * A maximum of $20,000 can be loaded per week.
   * A maximum of 3 loads can be performed per day, regardless of amount.

### Current Task Scope
* The Java Springboot project reads the `input.txt` file available under `resources` folder which contains the fund loading transaction's data.
* It processes the data by applying velocity limits and leveraging in-memory h2 database.
* The processed output is written to `output.txt` file under resources folder and also logged to standard output console.
* We have implemented Unit test cases for the service layer business logic with the help of Mocks.
* We have also implemented  End-to-End test cases to verify working business logic in all given scenarios.

### Future Task Scope
* We can add REST controllers and expose the business logic to outside world with the help of APIs.
* We can also implement APIs which can read transaction data from a URL or as a file input.
* We also store the processed data into a DB for future references.

### Steps to Run
* Import the project into IntelliJ IDE and run `VelocityLimitsApplication.java` class.