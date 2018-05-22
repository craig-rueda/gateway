[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Reactive Api Gateway

A Spring Webflux-based API gateway loosely based on Netflix's Zuul

## Huh???

Netflix uses [Zuul](https://github.com/Netflix/zuul/wiki), the gatekeeper from Ghostbusters, to shuttle all traffic in/out of their APIs. Their original design
was based on J2EE Servlets (2.x) and was therefore fully blocking in nature. Blocking is fine when concurrency is relatively low,
however it starts to cause issues when loads increase. In the API gateway world, most of the actual clock time of a given request 
is typically spent waiting around for I/O operations to complete.

If only there was a way to make things asynchronous in order to do `other` work while we're waiting around for I/O to complete??

Enter [Reactive Streams](https://en.wikipedia.org/wiki/Reactive_Streams). Reactive streams give us a fairly straightforward 
way to write asynchronous, non-blocking code which is callback-free.

Netflix has recently released Zuul 2.0, which is non-blocking. It's based on [RxJava](https://github.com/ReactiveX/RxJava/wiki), 
and since Spring has chosen to go with [Reactor](https://projectreactor.io/) as their reactive implementation for 
Spring WebFlux, I decided to build this framework which borrows some of the basic ideas from Zuul - mainly their idea of 
handling request processing as a set of filters running as a chain of responsibility.

## Usage

TODO: Fill this out!

## Performance

TODO: Fill this out!