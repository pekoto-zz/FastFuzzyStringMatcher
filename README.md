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

1. Delcare a new instance of `StringMatcher<T> myStringMatcher = new StringMatcher<T>();`
2. Add your data by calling `myStringMatcher.add(...)`
3. Search for your data by calling `myStringMatcher.search(...)`

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

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/bk-tree-1.png" width="76" height="100" />

Next we add __cat__. This has an edit distance of 1 (substitute h for c), so we add it as a child with a key of 1:

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/bk-tree-2.png" width="75" height="196" />

We do the same with __kate__ and __ball__ -- calculate their edit distances respective to the root (__hat__), and then add them as children with those keys:

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/bk-tree-4.png" width="322" height="196" />

Finally we add __bat__. But notice that the edit distance is 1, and we already have a child with edit distance 1 -- __cat__. No problem. We just move down to cat, calculate the edit distance between __cat__ and __bat__, and add the node as a child of __cat__.

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/bk-tree-5.png" width="322" height="315" />

Okay, now we're ready to search!

Imagine we accidentally typed in the word __zat__, and we want to get which potential corrections for our spelling mistake.
Let's say we want to search all of our nodes within an maximum edit distance of 1.

First, we compare __zat__ with our root, __hat__. Sure enough, the edit distance is 1, which is within our threshold, so we add __hat__ to our list of results to return.

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/search-1.png" width="322" height="355" />

Next, we examine all of the child nodes within the current edit distance +/- our threshold of 1.

1 (current edit distance) + 1 (our threshold) = 2 (max edit distance)

1 (current edit distance) - 1 (out threshold) = 0 (min edit distance)

So we'll examine all of the children that have an edit distance between 0-2.
First, let's check out __kate__.

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/search-2.png" width="354" height="316" />

Oh uh, the edit distance between __zat__ and __kate__ is 2, so we ignore this node, and there are no children, so let's back up.

__cat__ has an edit distance of 1, so let's check it out. The edit distance between __zat__ and __cat__ is also 1, which is within our threshold, so whoopee -- we have another results.

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/search-3.png" width="354" height="355" />

Oh yeah, cat has a child node. We repeat the step we did at the root but using our current node: work out the maximum and minimum threshold based on the edit distance between __zat__ and __cat__, and then examine children within that threshold.

This brings us down to __bat__. We check the edit distance, and again find it's within our threshold.

With that we're done, and return __hat__, __cat__, and __bat__!

<img src="https://github.com/pekoto/FastFuzzyStringMatcher/blob/master/images/search-4.png" width="354" height="355" />

Overall, we still ended up searching 80% of our tree, but even in a bad scenario like this, 80% can still be a significant saving if you have, for example, 500,000 strings in your collection.

__Exercise__
What would happen if __zap__ had been added to our BK tree?

## Thoughts
The BK tree is a simple data structure that can deliver significant performance increases when you need to search a large number of strings. They're also a quick way to implement fuzzy searching or spell checking.

Happy searching :)
