# Workshop `implementing DDD with hexagonal architecture`

[![Projektsetup](https://github.com/larmic/workshop_ddd_implementing_hexagonal_architecture/actions/workflows/project_setup.yml/badge.svg)](https://github.com/larmic/workshop_ddd_implementing_hexagonal_architecture/actions/workflows/project_setup.yml)
[![Story 1 - Anlegen von Räumen](https://github.com/larmic/workshop_ddd_implementing_hexagonal_architecture/actions/workflows/story_1.yml/badge.svg)](https://github.com/larmic/workshop_ddd_implementing_hexagonal_architecture/actions/workflows/story_1.yml)
[![Story 2 - Hinzufügen von Personen](https://github.com/larmic/workshop_ddd_implementing_hexagonal_architecture/actions/workflows/story_2.yml/badge.svg)](https://github.com/larmic/workshop_ddd_implementing_hexagonal_architecture/actions/workflows/story_2.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Dieses Projekt dient als Unterstützung und Musterlösung zu meinem [Workshop](misc/presentation/workshop_folien.pdf). 
In den einzelnen Branches sind die jeweiligen Umsetzungen zu den User Stories des Workshops dokumentiert. 
Hier im `main` befindet sich das erste Projektsetup.

#### Stories und Branches
* Projektsetup: ```main```
* Erfassen von Räumen: `story1_anlegen_von_räumen`

#### Story in diesem Branch ```story2_hinzufügen_von_personen```

![Story1 - Hinzufügen von Personen](misc/stories/story2_hinzufuegen_von_personen.png)

## Requirements

* Java 17
* Docker

## Build and run

Siehe [Makefile](Makefile) und [requests.http](misc/requests.http) 
([Jetbrains HTTP Client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html)) für genauere 
Informationen.

```shell
# java 
$ make java/build
$ make java/run

# docker
$ make docker/build
$ make docker/run
```