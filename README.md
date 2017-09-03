# FastFuzzyStringMatcher
A BK tree implementation for fast in-memory string matching.

## Features
- Fast, fuzzy, string matching.
- Fuzzy search based on percentage and edit distance.
- Associate data with string keywords and return both. For example, search for a file name, and return associated file paths.

## Motivation
Although hash maps can be used for fast exact string matching, and tries can be used for fast prefix matching, there are few solutions out there for fast matching of strings based on edit distance or percentage difference. Of course, you can search through every string in your collection, comparing its edit distance to the keyword you're searching for, but this tends to be pretty inefficient.

FastFuzzyStringMatcher builds a [BK tree](https://en.wikipedia.org/wiki/BK-tree) to make searching based on edit distance a lot more efficient.

## Setup
The project was built using Eclipse and Java 8 and should build cleanly, assuming you have the latest JDK installed.

The main class can be found in `src/main/java` --> package: `com.gitub.pekoto.fastfuzzystringmatcher` --> `StringMatcher.java`.

### Usage
Usage is fairly simple:

1. Delcare a new instance of `StringMatcher`
2. Add your data by calling `yourStringMatcherInstance.add(...)`
3. Search for your data by calling `yourStringMatcherInstance.search(...)`

__Example:__

### Running the tests
__TODO__

### Running the example
__TODO__

## How Edit Distance is Calculated
__TODO (Levenshtein distance explanation)__

## How String Matching Works
__TODO (BK Tree explanation)__
