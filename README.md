[![Java CI with Maven](https://github.com/pfichtner/bundesliga-tabelle/actions/workflows/maven.yml/badge.svg)](https://github.com/pfichtner/bundesliga-tabelle/actions/workflows/maven.yml)

# bundesliga-tabelle

This repository contains code I use in my coachings and/or as showcase. 
It is intended to represent the constellation that a team develops a frontend and a backend, but the backend obtains the data from a third-party system that we have no control over or can hardly influence. 

One idea is that the structure of the tests follows the structure of the production code, which is structured according to [Ports & Adapters](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)). 
So it highlights the following aspects: 
- Ensuring the correct functional logic
- Ensure the primary adapters (in this case, the only existing http-adapter) works as expected (map the http-request to domain logic, call the domain logic accordingly and maps the result to the http-response)
- Ensure the secondary adapters (in this case, the only existing http-adapter) works as expected (map the domain-data to the http-request, call the http server accordingly and maps the http-response to the domain-data)
  - Test this in isolation using a mocked http service
  - Test this integrative if the real http service responds as expected. We can't do contract tests here, since we don't have organisational control over the provider (third-party system), see https://docs.pact.io/getting_started/what_is_pact_good_for#what-is-pact-good-for

Additionally it does: 
- [PACT](https://pact.io/) contract verification that this backend behaves as expected by the frontend(s)
- [Archunit](https://www.archunit.org/) architecture tests that the code base is structured as intended
