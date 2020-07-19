# Don’t Use @Transactional in Tests

This is the sample code used for the article below, where I talk about the problems of using @Transactional in Spring Boot integration tests:

- [Medium](https://medium.com/@henrickkakutalua/dont-use-transactional-in-tests-65b857f63a4a?sk=6f414d673e6f64511057acce4ef5f155)
- [dev.to](https://dev.to/henrykeys/don-t-use-transactional-in-tests-40eb)

## Installation

### Database

You need to have [PostgreSQL](https://www.postgresql.org/download/) installed in your system.

Then configure the environment variables below:

- **DATABASE_HOST**: localhost
- **DATABASE_NAME**: avoid-transactional
- **DATABASE_USERNAME**: postgres
- **DATABASE_PASSWORD**: yourpassword

You can of course provide different values depending on your local database configuration.

I recommend you to create two databases, one for production and another  for tests. So you can run the tests without interfering with manual  production testing using a tool like cURL or Postman. You’ll need to  change the **DATABASE_NAME** variable in-between.

### Development

Be sure to have JDK and Maven installed in your system, and then execute the project by running:

```bash
mvn spring-boot:run
```

To run the tests:
```bash
mvn test
```