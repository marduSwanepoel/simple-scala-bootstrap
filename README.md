---

<p align="center"><a target="_blank" rel="noopener noreferrer"><img width="80" src="https://www.scala-lang.org/resources/img/frontpage/scala-spiral.png" alt="Vue logo"></a></p>


[![Pull Requests Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat)](http://makeapullrequest.com)
![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)
---
# The Simple Scala Bootstrap Project
The Simple Scala Bootstrap project is an open-source project created to provide developers with a quick and simple way to get a Scala service with the most essential functionality required, running. 

## Table of Contents
- [Overview](#overview)
- [The Simple Scala Utility Library](#the-simple-scala-utility-library)
- [The Simple Scala Template Service](#the-simple-scala-template-service)
- [Development Backlog](#development-backlog)
- [License](#license)

### Overview
The project consists of two parts, each contributing to the ultimate purpose of getting you running as soon as possible:
- *Simple Scala* Util Library
  - A Utility Library providing out-of-the-box functionality for HTTP API server and route setup, error-handling, service DTOs, validation, JSON utils, and implementations for some database and queueing technologies. See [The Simple Scala Utility](##the-simple-scala-utility-library) section for more details
- *Simple Scala* template service
  - This is a basic DDD and onion-architecture-based Scala service which demonstrates the use of the Simple Scala utility library. It also demonstrates the implementation of DDD and the onion-architecture in the service's folder structure - something that is essential to achieving a [screaming architecture](https://blog.cleancoder.com/uncle-bob/2011/09/30/Screaming-Architecture.html) repository.
  
## The Simple Scala Utility Library
A Utility Library that provides you with essential functionality to bootstrap and easily implement additional features in your Scala service. 
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

### API Calls
To interact with the server when running it out of the box, you can use the following cURL commands to play around with the `/people` router.
#### Health Check `v1/health`
Receives a status-code 200 if the service is up and all components of it are healthy
```
curl --location --request GET 'http://localhost:21001/health'
```
#### Get Random Person `v1/people/random`
Retrieves a randomly generated person
```
curl --location --request GET 'http://localhost:21001/v1/people/random'
```
#### Post Person `v1/people`
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
#### Get Person By ID `v1/people/<PERSON_ID>`
Retrieves a person using that persons ID
```
curl --location --request GET 'http://localhost:21001/v1/people/d448f1f3-17b0-4b58-944d-d561dd0f87d1'
```

# Keen to Contribute?
We are always striving to grow the utility library to include more useful functionality, and we really mainly on the community to do so. If you are interested in contributing, have a look at the Development Backlog below to see which tasks you want to give a stab at.

Also feel free to take on general improvements and refactoring as you see fit.

See our [contribution guideline](./CONTRIBUTE.md) on best practices for contributing.

## Development Backlog
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

## The Other Things
### License

Simple Scala Bootstrap is [MIT licensed](./LICENSE).

Copyright <YEAR> <COPYRIGHT HOLDER>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.