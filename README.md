[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CircleCI](https://circleci.com/gh/craig-rueda/gateway/tree/master.svg?style=svg)](https://circleci.com/gh/craig-rueda/gateway/tree/master)

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

### JDK
```bash
echo "GET http://localhost:8081/local-https/sample2.json" | vegeta attack -duration=60s -rate=1000 | vegeta report

Requests      [total, rate]            60000, 1000.02
Duration      [total, attack, wait]    59.999981145s, 59.998998s, 983.145µs
Latencies     [mean, 50, 95, 99, max]  1.593255ms, 1.022032ms, 1.523621ms, 4.401885ms, 226.333704ms
Bytes In      [total, mean]            43740000, 729.00
Bytes Out     [total, mean]            0, 0.00
Success       [ratio]                  100.00%
Status Codes  [code:count]             200:60000
Error Set:
```

### OPENSSL
```bash
$ echo "GET http://localhost:8081/local-https/sample2.json" | vegeta attack -duration=60s -rate=1000 | vegeta report

Requests      [total, rate]            60000, 1000.02
Duration      [total, attack, wait]    1m0.000032302s, 59.998999s, 1.033302ms
Latencies     [mean, 50, 95, 99, max]  1.064278ms, 987.674µs, 1.351353ms, 1.780193ms, 66.463618ms
Bytes In      [total, mean]            43740000, 729.00
Bytes Out     [total, mean]            0, 0.00
Success       [ratio]                  100.00%
Status Codes  [code:count]             200:60000
Error Set:
```