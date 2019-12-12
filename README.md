# mtransfer
Money Transfer - CQRS &amp; EventSourcing

Design and implement a RESTful API (including data model and the backing implementation) for
money transfers between accounts.

### Explicit requirements:
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

### Technology stack
1. Spark - routing
2. JUnit
3. Guice - dependency injection
4. Gson - json serializer

### Routes
- **PUT localhost:4567/api/account/create** Creates new account
- **GET localhost:4567/api/account/:id** Get account by id
- **POST localhost:4567/api/account/:accountId/deposit/:currency/:amount** Deposit money
- **POST localhost:4567/api/account/:accountId/withdraw/:currency/:amount** Withdraw money
- **POST localhost:4567/api/transaction?from=:acc1d&to=acc2&amount=:amount&currency=:currency** Initiate transaction
- **GET localhost:4567/api/transaction/:transactionId** Check transaction status
