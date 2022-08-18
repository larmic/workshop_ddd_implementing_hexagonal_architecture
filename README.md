# Musterlösung zum Workshop `implementing DDD with hexagonal architecture`

Dieses Projekt dient als Unterstützung und Musterlösung zu meinem Workshop. In den einzelnen Branches sind die
jeweiligen Umsetzungen zu den User Stories des Workshops dokumentiert. Hier im `main` befindet sich das erste 
Projektsetup.

## Requirements

* Java 17
* Maven >= 3.6.x
* Docker optional

## Build and run

Siehe [Makefile](Makefile) für genauere Informationen. 

```shell
# java 
$ make java/build
$ make java/run
$ make http-call

# docker
$ make docker/build
$ make docker/run
$ make http-call
```