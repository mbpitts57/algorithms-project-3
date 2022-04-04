import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.*;
import java.nio.*;

public class TradeStock {
    public static String binaryFile, algorithm, result;
    public static long seriesLength;
    public static int purchasePoint, sellPoint, start, end = 0;
    public static int testIndex, midpoint, purchaseTemp, sellTemp;
    public static double maxProfit, profitTemp, profit = 0;
    public static double lowestVal, highestVal;

        static class MinAndMax {
            int min;
            int max;

            MinAndMax(int min, int max) {
                this.min = min;
                this.max = max;
            }
        }

        // algorithm 0, Theta(n^2) Brute Force
        public static void BruteForce(double[] array) {
            if (array.length < 2) {
                return;
            }

            // loops through array and compares every possible pair of points. does not consider buying and selling on the same day, because profit would always be zero. j starts at end of array, i starts at beginning.
            for (int i = 0; i < array.length; i++) {
                for (int j = array.length - 1; j > i; j--) {
                    profit = array[j] - array[i];
                    if (profit > maxProfit) {
                        maxProfit = profit;
                        purchasePoint = i;
                        sellPoint = j;
                    }
                }
            }
        }

        // algorithm 1, Theta(nlog(n)) Divide and Conquer
        public static void DivAndConquer1(double[] array, int start, int end) {
            int midpoint;
            // start = position of first day of stockPrices array.
            if (start == end) {
                return;
            }

            // finding the midpoint
            midpoint = (start + end) / 2;
            // recursive dividing
            DivAndConquer1(array, start, midpoint);
            DivAndConquer1(array, midpoint + 1, end);
            // lowestVal is the pointer for the smallest value of each possible purchase point.
            lowestVal = array[start];
            // purchaseTemp is the pointer for testing purchase points. It ultimately leads to the optimal purchase point, and its value is assigned to purchasePoint at the end of the method.
            purchaseTemp = start;
            
            for (int i = start; i <= midpoint; i++) {
                if (lowestVal > array[i]) {
                    purchaseTemp = i;
                    lowestVal = array[i];
                }
            }

            // pointers for possible selling points and the values at these points. In the end, these values leads to the optimal selling point.   
            highestVal = array[midpoint + 1];
            sellTemp = midpoint + 1;
            
            for (int i = midpoint + 1; i <= end; i++) {
                if (highestVal < array[i]) {
                    sellTemp = i;
                    highestVal = array[i];
                }
            }
                
            profitTemp = highestVal - lowestVal;
            
            if(profitTemp > maxProfit) {
                purchasePoint = purchaseTemp;
                sellPoint = sellTemp;
                maxProfit = profitTemp;
            }
        }
        
        // algorithm 2, Theta(n) Divide and Conquer
        public static MinAndMax DivAndConquer2(double[] array, int start, int end) {
            MinAndMax profit2, profit3, profit4, profit5;
            int midpoint;
            profit2 = new MinAndMax(start, start);

            if (start == end) {
                return profit2;
            }

            // finding the midpoint
            midpoint = (start + end) / 2;
            // recursive dividing
            profit3 = DivAndConquer2(array, start, midpoint);
            profit4 = DivAndConquer2(array, midpoint + 1, end);
            // lowestVal is the pointer for the smallest value of each possible purchase point.
            lowestVal = array[profit3.min];
            // purchaseTemp is the pointer for testing purchase points. It ultimately leads to the optimal purchase point, and its value is assigned to purchasePoint at the end of the method.
            purchaseTemp = profit3.min;

            // pointers for possible selling points and the values at these points. In the end, these values leads to the optimal selling point.  
            highestVal = array[profit4.max];
            sellTemp = profit4.max;

            profitTemp = highestVal - lowestVal;

            if (profitTemp > maxProfit) {
                purchasePoint = purchaseTemp;
                sellPoint = sellTemp;
                maxProfit = profitTemp;
            }

            profit5 = new MinAndMax(profit3.min, profit3.max);

            if (array[profit4.min] < array[profit5.min]) {
                profit5.min = profit4.min;
            }

            if (array[profit4.max] > array[profit5.max]) {
                profit5.max = profit4.max;
            }

            return profit5;
        }
                
        // ******problems with mystock3, mystock4, mystock5, and mystock6 with this algo only. stack overflow error.
        // algorithm 3, Theta(n) Decrease and Conquer
        public static int DecAndConquer(double[] array, int end) {
                if (array.length < 1) {
                    return 0;
                }
                
                testIndex = DecAndConquer(array, end - 1);

                if (array[end] < array[testIndex]) {
                    return end;
                }
                
                profit = array[end] - array[testIndex];
                
                if (profit > maxProfit) {
                    maxProfit = profit;
                    purchasePoint = testIndex;
                    sellPoint = end;
                }

                return testIndex;
        }
        
        public static void main(String args[]) throws IOException {
            // evaluates the user-provided arguments.
            if (args.length == 2) {
                binaryFile = args[0];
                algorithm = args[1];
            } else {
                System.out.println("Two arguments expected. " + args.length + " received.\nPlease enter the name of the binary file followed by the desired search algorithm.\nAlgorithm Menu:\n0: Theta(n^2) Brute Force\n1: Theta(nlog(n)) Divide and Conquer\n2: Theta(n) Divide and Conquer\n3: Theta(n) Decrease and Conquer");
                return;
            }

            try (FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(binaryFile), StandardOpenOption.READ)) {
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
                fc.read(byteBuffer);
                byteBuffer.flip();

                // the series length is equal to the number of numbers in the bin file, minus one, which is the number that indicates the series length,
                long seriesLength = (fc.size() / 4 - 1);

                // creates float array from all values in bin file
                Buffer buffer = byteBuffer.asFloatBuffer();
                float[] floatArray = new float[(int) fc.size() / 4];
                ((FloatBuffer) buffer).get(floatArray);

                // copies all values from float array into double array, but excludes the series length number
                double[] stockPrices = new double[(int) seriesLength];
                for (int i = 1; i < floatArray.length; i++) {
                    stockPrices[i - 1] = floatArray[i];
                }

                // processes user input. Completes operation by feeding array into user-specified algorithm.
                if (algorithm.equals("0")) {
                    algorithm = "Theta(n^2) Brute Force";
                    BruteForce(stockPrices);
                } else if (algorithm.equals("1")) {
                    algorithm = "Theta(nlog(n)) Divide and Conquer";
                    DivAndConquer1(stockPrices, 0, stockPrices.length - 1);
                } else if (algorithm.equals("2")) {
                    algorithm = "Theta(n) Divide and Conquer";
                    DivAndConquer2(stockPrices, 0, stockPrices.length - 1);
                } else if (algorithm.equals("3")) {
                    algorithm = "Theta(n) Decrease and Conquer";
                    DecAndConquer(stockPrices, stockPrices.length - 1);
                } else {
                    System.out.println(
                            "Option must be 0, 1, 2, or 3.\n0: Theta(n^2) Brute Force\n1: Theta(nlog(n)) Divide and Conquer\n2: Theta(n) Divide and Conquer\n3: Theta(n) Decrease and Conquer");
                    return;
                }

                System.out.println("Input size = " + stockPrices.length);
                System.out.println("Molly Pitts\n" + binaryFile + "\n" + algorithm + "\n" + purchasePoint + ", "
                        + sellPoint + ", " + Math.round(maxProfit * 1000.0) / 1000.0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        
}


