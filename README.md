# secure-code-example
Sample web-app containing some labs for teaching secure coding

## How to run
After cloning repo to your machine, at the project root directory, run:

```docker build -t secure-code-v1 .```


This will build the docker container. Then run,

```docker run -dp 8080:8080 secure-code-v1```

This will run the app on your machine's port ```8080``` (assuming it's currently available).

Open ```localhost:8080``` on your browser and play around.

## TODOs
* Add some hints for the labs
* Some variable names aren't explanatory
* Some abstractions are possible to reduce the amount of code copy-paste
* The classname generation for test classes is also currently based on current-epoch (will not scale)
