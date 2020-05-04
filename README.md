# Vitrail

![Build](https://img.shields.io/github/workflow/status/jlandic/vitrail/CI/master?style=flat-square&logo=github-actions)
![Coverage](https://img.shields.io/codecov/c/github/jlandic/vitrail?logo=codecov&style=flat-square)
![Version](https://img.shields.io/bintray/v/jlandic/vitrail/vitrail?style=flat-square&logo=gradle)

![License](https://img.shields.io/github/license/jlandic/vitrail?style=flat-square)

## Introduction

`Vitrail` is a text generator using context-free grammars, inspired by projects like [Tracery](https://github.com/galaxykate/tracery), or [Grammy](https://github.com/AlmasB/grammy).

It can be used on the spot by providing a grammar as a JSON file, and following the default syntax, and can be used as a library.

## Features

- Read grammar from JSON file
- Shared variables, available through all symbol expansions
- Customisable grammar syntax/operators (when used as a library)
- Potential for custom modifiers (when used as a library)

## [Documentation](https://jlandic.github.io/vitrail)

## Installation

### Requirements

- `JDK >= 1.13`

### Install the library

#### Gradle

```groovy
implementation "com.wilgig:vitrail:$VERSION"
```

#### Maven

```xml
<dependency>
  <groupId>com.wilgig</groupId>
  <artifactId>vitrail</artifactId>
  <version>VERSION</version>
  <type>pom</type>
</dependency>
```

## Usage

### Run the sample application

1. Create a JSON file with your grammar under `src/main/resources`. Name the file `test.json`.
2. Create a symbol named `root`. By convention, `vitrail` will expand the text starting with this `root` symbol
    ```
    {
        "root": "Some text to be expanded"
    }
    ```
3. Add the rules you want according to the [default grammar syntax](#grammar-features)
4. Run the sample application
    ```
    ./gradlew run
    ```

### Grammar features

#### Symbol expansion (default: `{ }`)

_Looks up the rules corresponding to the symbol, and picks one randomly as a replacement_

```json
{
  "root": "{subject} {verb} {object}",
  "subject": [
    "Alice",
    "Bob"
  ],
  "verb": [
    "shares",
    "eats",
    "sees"
  ],
  "object": [
    "the apple",
    "the banana"
  ]
}
```

#### Variable capture (default: `[symbol>variableName]`)

_Creates a `variableName` symbol with a static value. The rules corresponding to `symbol` determine the value of `variableName`.
This value never changes once it's initialized._

```json
{
  "root": "[person>subject]{subject} {verb}. {subject} also {verb}",
  "person": [
    "..."
  ],
  "...": []
}
```

In the example above, both `subject` symbols will expand with the value coming from `person`.

> Note:
> The placement of the variable capture matters!
>
> The algorithm expands symbols from left to right. This means that a symbol expansion captured inside a variable is only available for expressions on its right side.
>
> Otherwise, placement is free.

> Note (expanding on the previous note):
>
> While placement is free for now, future plans involve allowing for more granularity regarding the scope of variables.
>
> One possibility would be to declare captures inside a symbol to make it available only inside the scope of this symbol's expansion:
> ```json
> {
>   "root": "{[person>subject]part1}. {[person>subject]part2}"
> }
> ```
> In the example above, `subject` could have different values in the expansion of `part1` and `part2`.
>
> Do keep this in mind when writing your grammars.

#### Modifiers (default: `:modifierName`)

_Applies a modification on the expanded value of a symbol._

> Note: while a `Modifier` interface exists, and modifiers added to the `Grammar` at runtime are applied, the library does not provide any modifier yet.

```json
{
  "root": "{subject:capitalize}",
  "subject": [
    "john"
  ]
}
```

Considering that the `Grammar` has a modifier with the name `capitalize`, that capitalizes the first letter of the value passed to it, the result of the example above would be:
```
"John"
```

## License

`Vitrail` is released under [MIT License](https://opensource.org/licenses/MIT)

## Changelog

## Roadmap

- Proper test coverage
- Implement more granular scopes for captured variables
- Allow modifiers to receive parameters
- Allow specifying probabilities for different rules to be picked when expanding a symbol
