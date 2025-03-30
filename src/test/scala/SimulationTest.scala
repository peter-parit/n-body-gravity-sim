import NaiveSimTest.*
import OptimizedSimTest.*


object SimulationTest {
    def main(args: Array[String]): Unit = {

        val N = 100000
        
        print(f"Simulation Performance Test on $N bodies with 10 iterations\n\nAverage time [Naive]: ")
        val avgNaive = NaiveSimTest.run(N)
        println(f"$avgNaive%1.3f seconds")

        // print("\nAverage time [Optimized]: ")
        // val avgOpt = OptimizedSimTest.run(N)
        // println(f"$avgOpt%1.3f seconds")

        println("\nAll done!")

    }
}
