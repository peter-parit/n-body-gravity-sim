# Celestial Body Gravity Simulator (N-body simulation)
 
This n-body simulation will focus on simulating gravitational forces on n-objects on a 2D-plane. The purpose is to provide insights on the improved performance and run-time speed between the $O(n^2)$ naive approach to the $O(n\log n)$ Barnes-Hut approach. The latter further utilizes a QuadTree data structure, along with concurrency such as scala's own Futures.

## How to run simulation
This project is done using the SBT build tool, so you must import the build first. After that, you can run the simulation using `sbt run`.

**Note**: Currently, the main running class is set to the `BarnesHutSim.scala`, which includes all optimized methods. If you want to try to run other main classes, namely `BarnesHutWithoutParSim` or `NaiveSim`, you must remove the following line from the `build.sbt` file in the project:

`Compile / mainClass := Some("barneshut.BarnesHutSim"),`

Since I've imported dependencies for JavaFX, you shouldn't need to install them. However, if it doesn't work, then the version that is being used is the [SDK version 21](https://gluonhq.com/products/javafx/)

*Keep in mind* that this method of visualization is not the fastest, so you might need a more powerful machine if you wanted to try simulating (with good fps) higher number of bodies i.e. 100,000+
