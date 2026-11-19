# Tic-tac-toe

Scala version of Tic Tac Toe :)

## Requirements

For running and Compiling: JDK 17 and sbt.
Built using Scala 2.13.18 and sbt 1.12.15.

```sh
sbt compile
sbt run
```

## Development approach

Game rules are developed using TDD i.e. write a failing test, implement the behaviour, then refactor.

Run the tests:

```sh
sbt test
```

## Development Cycle

1. Started with branch feature/board
2. Finished TDD and concerns related to board so I'm merging back to develop
