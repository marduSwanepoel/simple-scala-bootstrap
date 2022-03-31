---
<img src="https://www.scala-lang.org/resources/img/frontpage/scala-spiral.png" width="50" height="70">


# Simple Scala Bootstrap
___
The *Simple Scala Bootstrap* project is an open-source project created to provide developers with a quick and simple way to get a Scala service with the basic functionality required, running. 

The project consists of two parts, each contributing to the ultimate purpose of getting you running as soon as possible:
- *Simple Scala* Util Library
  - A Utility Library providing out-of-the-box functionality for HTTP API server and route setup, error-handling, service DTOs, validation, JSON utils, and implementations for some database and queueing technologies. See [The Simple Scala Utility](##the-simple-scala-utility-library) section for more details
- *Simple Scala* template service
  - This is a basic DDD and onion-architecture-based Scala service which demonstrates the use of the Simple Scala utility library. It also demonstrates the implementation of DDD and the onion-architecture in the service's folder structure - something that is essential to achieving a [screaming architecture](https://blog.cleancoder.com/uncle-bob/2011/09/30/Screaming-Architecture.html) repository.
  
##The Simple Scala Utility Library
___
- A Utility Library that provides you with essential functionality to bootstrap and easily implement additional features in your Scala service. 
Below are the utilities currently provided:
- Transport (`/transport`)
  - Internal application DTOs for passing around information in an async ([Monix-based](https://monix.io/docs/current/eval/task.html)), functional manner
  - Includes error handling utilities & failure metadata transport
- HTTP API (`/httpApi`)
  - Scaffolding for implementing an HTTP API
  - Scaffolding for implementing an [Akka-based](https://doc.akka.io/docs/akka-http/current/server-side/index.html) HTTP API 
- Application Infrastructure (`/application`)
  - Utilities to start-up and run your application
- JSON (`/json`)
  - Basic [spray-json](https://github.com/spray/spray-json) based JSON formats
- Logging (`/logging`)
- Time (`/time`)

## The Simple Scala Template Service
___
A ready-to-run Scala service serving three purposes:
1. Demonstrates the use of the Simple Scala utility library through implementation and examples.
2. Provides a ready-to-use folder structure based on DDD and the Onion-Architecture.
3. Gives you a ready-to-run Scala service that you can build further implementation on.

### Folder Structure
The folder structure convention used in this repo is based on conventions of DDD and Onion-Architecture design:
```
.
├── /application                                 # Application layer content, ... 
├── /domain                                      # Domain, Repo and Service layers content
│   ├── /<SomeEntity>                            # Directory per domain entity/area. We use case ex. SomeDomainEntity
│       ├── SomeEntity.scala                     # Class/Object defenition for the entity, if needed
│       ├── /repo OR SomeEntityRepo.scala        # Repository(s) for infrastructure access related to this entity
│       ├── /service OR SomeEntityService.scala  # Service(s) holding the domain-logic relating to this entity
│       ├── ...                                  # Whichever additional classes, logic, etc is required to implement the entity and its related logic
│   ├── /...                                     # Additional domain entities
├── /infrastructure                              # Infrastructure implementations for the domain repositories in /domain/<entity>/repo
├── Main.scala                                   # Main boot-class serving as the entry point for the service
└── README.md
```
For more details on the architecture style followed in this repo, see 
- [Domain-Driven Design: What is it and how do you use it?](https://airbrake.io/blog/software-design/domain-driven-design)
- [The Onion Architecture by Jeffrey Palermo](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)

###API Calls
To interact with the server when running it out of the box, you can use the following cURL commands to play around with the `/people` router.
####Health Check `v1/health`
Receives a status-code 200 if the service is up and all components of it are healthy
```
curl --location --request GET 'http://localhost:21001/health'
```
####Get Random Person `v1/people/random`
Retrieves a randomly generated person
```
curl --location --request GET 'http://localhost:21001/v1/people/random'
```
####Post Person `v1/people`
Add a new person
```
curl --location --request POST 'http://localhost:21001/v1/people/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "birthDate": "2002-03-31",
    "id": "d448f1f3-17b0-4b58-944d-d561dd0f87d1",
    "name": "Mark",
    "surname": "Barks"
}'
```
####Get Person By ID `v1/people/<PERSON_ID>`
Retrieves a person using that persons ID
```
curl --location --request GET 'http://localhost:21001/v1/people/d448f1f3-17b0-4b58-944d-d561dd0f87d1'
```


#Keen to Contribute?
___
We are always striving to grow the utility library to include more useful functionality. If you are interested in contributing, have a look at the Development Backlog below to see which tasks you want to give a stab at.

Also feel free to take on general improvements and refactoring as you see fit.

##Development Backlog
___
These are the aspects we still wish to incorporate into this library. Feel free to take on any of them. Below is a list RANKED (by priority) items we want to add to the project.
- Validation
  - Implement validation helper methods for data validation at service, repo and API levels using monad transformers, type classes and tagless final.
  - See [this article on validation in Scala](https://blog.softwaremill.com/38-lines-of-code-towards-better-data-validation-in-scala-c933e5a88f76) for a guideline as to what we are looking for.
- Porting Code to Scala 3
  - The project is running on Scala 3, although a fair bit of code is still written in a Scala 2 style, or uses Scala 2 syntax - all which are deprectaed in Scala 3. 
  - These needs to be ported and refactored to Scala 3 code.
- Porting of Monix-task to Cats Effects
- Error Handling Utilities
  - Adding additional useful error handling utility functions.
  - See [this article on error handling](https://softwaremill.com/practical-guide-to-error-handling-in-scala-cats-and-cats-effect/) as a guideline for what we are looking at.
- Unit Tests
  - We do not have 100% test coverage on the project. 
  - We would like to change this, aiming for 90% code coverage across the project.
- HTTP API Authentication
  - Providing a means of Akka directive-based authentication when hitting the service APIs
- Authorization & Access Control
  - Providing a generic means of authorization and access control to APIs 
- MongoDB Connection Implementation
  - MongoDB infrastructure and implementation utilities to allow the service to use MongoDB as a repository implementation.
- Postgres Connection Implementation
    - Postgres infrastructure and implementation utilities to allow the service to use Postgres as a repository implementation.
- Kafka Connection Implementation
    - Kafka infrastructure and implementation utilities to allow the service to use Kafka as a repository implementation.
- RabbitMQ Connection Implementation
    - RabbitMQ infrastructure and implementation utilities to allow the service to use RabbitMQ as a repository and API implementation.
- Prometheus HTTP Metrics Endpoint
    - Provide HTTP-level performance metrics that can be retrieved by [Prometheus](https://prometheus.io/) via an exposed endpoint.