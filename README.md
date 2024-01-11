# Project Mean - Computing Averages in Java
## Project Overview

The Mean project focuses on calculating the average value of elements in an array using concurrent thread processing. It utilizes the CompletableFuture class for asynchronous computation.
Project Structure

## The project consists of two main classes:

Mean.java
- This class implements the calculation of the average value of array elements using multiple threads.
- Method **parallelMean1** uses separate threads, and the results are collected and averaged.
- Method **parallelMean2** utilizes a results queue (**BlockingQueue**) for communication between threads.
- Method **parallelMean3** uses an ExecutorService to manage a thread pool.

AsyncMean.java
- The **AsyncMean** class introduces asynchronous computation using **CompletableFuture**.
- Method **asyncMeanv1** uses **CompletableFuture.supplyAsync** for asynchronous computation of the average in multiple chunks of the array.
- Method **asyncMeanv2** also uses **CompletableFuture.supplyAsync** but employs a results queue for collecting and averaging results.

# Summary
In my project, I leveraged threads and implemented various approaches to tackle this topic.
This hands-on experience has provided me with valuable insights into working in a multithreaded environment,
and I've gained knowledge about several popular approaches to concurrent programming.
This experience has deepened my understanding of the challenges associated with parallel programming and has
equipped me with valuable insights into effectively harnessing threads for optimizing program execution times.

