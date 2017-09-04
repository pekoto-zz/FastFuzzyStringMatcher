# FastFuzzyStringMatcher
A BK tree implementation for fast in-memory string matching.

## Features
- Fast, fuzzy, string matching.
- Fuzzy search based on percentage and edit distance.
- Associate data with string keywords and return both. For example, search for a file name, and return associated file paths.

## Motivation
Although hash maps can be used for fast exact string matching, and tries can be used for fast prefix matching, there are few solutions out there for fast matching of strings based on edit distance or percentage difference. Of course, you can search through every string in a collection, comparing its edit distance to the keyword you're searching for, but this tends to be pretty inefficient.

FastFuzzyStringMatcher builds a [BK tree](https://en.wikipedia.org/wiki/BK-tree) to make searching based on edit distance a lot more efficient.

## Setup
The project was built using Eclipse and Java 8 and should build cleanly, assuming you have the latest JDK installed.

The main class can be found in `src/main/java` --> package: `com.gitub.pekoto.fastfuzzystringmatcher` --> `StringMatcher.java`.

### Usage
Usage is fairly simple:

1. Delcare a new instance of `StringMatcher`
2. Add your data by calling `yourStringMatcherInstance.add(...)`
3. Search for your data by calling `yourStringMatcherInstance.search(...)`

`EditDistanceCalculator.java` is public,  so it can also be used independently to calculate the edit distance between two `CharSequence` objects.

### Running the tests
`src/test/java` contains unit tests which further demonstrate the functionality of the `StringMatcher` and `EditDistanceCalculator` classes.

These are just standard JUnit tests and can be run in Eclipse by right-clicking on the package --> Run As --> JUnit Test.

### Running the example
`src/example/java` shows how the `StringMatcher` can be used to implement a translation memory dictionary with fuzzy matching. `EnglishJapaneseDictionarySearcher.java` contains the implementation of this dictionary. It loads around 5000 entries from the [JMDict Project](http://www.edrdg.org/jmdict/j_jmdict.html). `SearchDriver.java` then shows how this dictionary can be searched.

`StringMatcher` should be able to handle a lot more than 5,000 translation pairs, but I wanted to keep the example size small, so kept it to this number.

## How Does It Work?

### 1. Edit Distance
Edit distance, better known as [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) is the minimum number of edits it takes to turn one string into another, using substitution, insertion, and deletion.

For example, to turn __cat__ into __hate__:
1. cat > hat (substitute c for h)
2. hat > hate (insert e)

Edit distance = 2

The algorithm to calculate this involves using dynamic programming to build a matrix of edit distances that can be used to work out the minimum edit distance between two strings. [Wiki](https://en.wikipedia.org/wiki/Levenshtein_distance) has a nicer explanation and better examples than I could give here.

`EditDistanceCalculator.java` uses the `iterative with two matrix rows`(https://en.wikipedia.org/wiki/Levenshtein_distance#Iterative_with_two_matrix_rows) approach. This seems to give the best performance based on some quick tests I did.

The `EditDistanceCalculator` also takes `CharSequence` objects as its arguments, so it can be used with `StringBuffer` and so on as well as just `String`s.

## 2. BK Tree
`StringMatcher` is essentially a [BK tree](https://en.wikipedia.org/wiki/BK-tree) implementation.

In a BK tree, every node is added based on its edit distance from the root.

For example, say we had a dictionary with the words __hat__, __cat__, __kate__, __ball__, and __bat__.

We start by adding __hat__:

