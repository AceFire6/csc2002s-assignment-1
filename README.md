# Assignment 1 - Parallelizing Satellite Data Correlation
#### Author: Jethro Muller
#### Student Number: MLLJET001
#### Date: 30 July 2014

## Description:
The project aimed to investigate the practical application of parallelism using the Java ForkJoinPool framework, and the speedups seen in the real world on different processors.

The data is meant to simulate working with radar signal data, with the `received.txt` files containing "noise" data.

## Instructions:

#### Running the code:
1. Make sure to have all the .java files in the same folder as the makefile.
2. Change to the source folder. `src/`
2. Run `make` to compile the java files.
3. Run `make run-*` where * is either `seq`, `parallel` or `full-test-suite`.
4. Run `make clean` to remove `.class` files from the directory.

#### Reading the report:
1. Go into the `Report/` directory.
2. Open `Report.html` in a web browser.

## File List:
* CSC2002S_Assignment1.pdf
* README.txt
* Report/
* src/SequentialCorrelate.java
* src/ParallelCorrelate.java
* src/CorrelateArray.java
* src/FindMax.java
* src/makefile
* src/text_files/small/
* src/text_files/medium/
* src/text_files/large/

