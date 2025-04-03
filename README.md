# N-body Gravity Simulation

## Overview and Summary of Project Results

https://github.com/user-attachments/assets/3b6622fe-dfea-4c33-b69b-e2719b4a2426

This n-body simulation will focus on simulating gravitational forces on n-objects on a 2D-plane. The purpose is to provide insights on the improved performance and run-time speed between the $O(n^2)$ naive approach to the $O(n\log n)$ Barnes-Hut approach. The latter further utilizes a QuadTree data structure, along with concurrency such as scala's own Futures. ScalaFX is further used to visualize and simulate the programs.

### Barnes-Hut

Barnes-Hut Algorithm utilizes the ratio $\frac{s}{d} \leq \Theta$, where $s$ is the width of the region, $d$ is the distance between two objects, and $\Theta$ is the threshold used to determine whether Barnes-Hut or sequential is used. The Barnes-Hut approach is essentially obtaining a tree's center of mass and use it for force calculation instead of doing all individual bodies in that area, since the bodies are far away enough to not have any influence on the observed body.

### Using Futures

To speed the computation time, Future was wrapped around the outer `map` (where `calculateForce` was called on every body), so all calculations are done concurrently. This resulted in roughly `2x` time faster computation time with bodies $> 1,000,000$.

### Results (computation time for `10` iterations)

NUM_BODIES | NAIVE | BARNES-HUT | BARNES-HUT + FUTURE
-----------|-------|------------|--------------------
100 | 0.063 | 0.070 | 0.068
500 | 0.122 | 0.083 | 0.077
1,000 | 0.272 | 0.096 | 0.113
5,000 | 3.275 | 0.243 | 0.233
10,000 | 15.407 | 0.495 | 0.325
50,000 | - | 1.799 | 0.984
100,000 | - | 3.217 | 1.760
1,000,000 | - | 41.089 | 26.945

The naive approach took extremely long for bodies of 10,000 or more. So, I have decided to not record down the time, and will proceed to use the existing values and predict the expected run time for each number of bodies in the graph section. Both Barnes-Hut versions are comparable up to 5,000 bodies, where the Future version grew lesser than the one without Futures. To provide more information, I also added the 1,000,000 body runtime, and it can be seen clearly that the Future version grew much slower.

### Emperical Analysis

- `red - naive`

- `blue - barnes-hut`

- `green - barnes-hut + future`

![Image](https://github.com/user-attachments/assets/39df8a90-1243-4fe6-a46d-12c1031ba22f)

As expected, the naive approach grows in $O(n^2) time, while the other two grow in $O(n\log n)$ time. Furthermore, it can be seen that the use of concurrent computation allows the computation speed to be moderately improved over the version without it.

Read more about how results were taken in my report attached in the repository.

## How to run simulation
This project is done using the SBT build tool, so you must import the build first. After that, you can run the simulation using `sbt run`.

**Note**: Currently, the main running class is set to the `BarnesHutSim.scala`, which includes all optimized methods. If you want to try to run other main classes, namely `BarnesHutWithoutParSim` or `NaiveSim`, you must remove the following line from the `build.sbt` file in the project:

`Compile / mainClass := Some("barneshut.BarnesHutSim"),`

Since I've imported dependencies for JavaFX, you shouldn't need to install them. However, if it doesn't work, then the version that is being used is the [SDK version 21](https://gluonhq.com/products/javafx/)

*Keep in mind* that this method of visualization is not the fastest, so you might need a more powerful machine if you wanted to try simulating (with good fps) higher number of bodies i.e. 100,000+
