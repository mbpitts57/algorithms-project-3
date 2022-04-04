# algorithms-project-3

Project 3 for CS 433. This program takes a binary file containing a time series of stock prices as input and has 
multiple algorithms written to find the best point A at which to buy, and the best point B at which to sell in 
order to maximize profits.

This program implements a Theta(n^2) time complexity Brute Force approach, a Theta(nlogn) time complexity Divide 
and Conquer Approach, a Theta(n) time complexity Divide and Conquer Approach, and a Theta(n) time complexity 
Decrease and Conquer Approach.

The program takes two command-line arguments from the user: the binary file to be read, and the user's algorithm 
of choice. Each input file consists of the following in binary format: • n, the length of the stock price series. 
It has type int (4 bytes in big-endian). • n stock prices. Each price has type float (4 bytes in big-endian).

The output prints out my name, the name of the binary file, the size of the series, the optimal purchasing time, 
the optimal selling time, and the maximum profit.
