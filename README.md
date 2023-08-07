# Monte Carlo Function Optimization

Monte Carlo methods use random selections in calculations to solve numerical or physical problems. In this project, we deal with a Monte Carlo algorithm for finding the minimum (the lowest point) of a two-dimensional mathematical function f(x,y) within a specified range; this is an optimization problem.

## Installation 

Its recommended to use Linux or Bash to run and download all packages necessary to run this project. Download [java](https://www.java.com/en/) to run this project. 

## Usage

Use the Makefile to compile and run the project. The Makefile will ensure all files are compiled in the correct order. 

```bash

make
make run $(all the inputs here)

```

## Inputs

The inputs for this program include
• rows, columns:  the number of rows and columns in the discrete grid 
representing the function; 
• xmin, xmax, ymin, ymax: the boundaries of the  rectangular area for the 
terrain; 
• searches density: number of searches per grid point


```bash
make run rows=## columns=## xmin=## xmax=## ymax=## ymin=## searchDensity=##

````

The '#' are the place holders for the actual numbers
