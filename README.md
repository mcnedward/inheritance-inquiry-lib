﻿# Inheritance Inquiry Library
## An open-source application for analyzing Java projects using established software code metrics

This is a library for inspecting Java projects to see how inheritance is being used throughout the application. There is support for three main code metrics ([read more info here](http://www.aivosto.com/project/help/pm-oo-ck.html)):

* Depth of Inheritance Tree (DIT)
* Number of Children (NOC)
* Weighted Method Count (WMC)

In addition to this, the library will do some analysis on methods are being overriden or extended. The full inheritance hierarchy structure for your classes will also be found. You can use this library to produce graphs, powered by [JUNG](http://jung.sourceforge.net/), or Excel readable files.

## 	Including in your project

To use the library in your project, you'll need to download it as a jar and include it in the project build path. You can get the library [here](http://edwardmcnealy.com/inheritance-inquiry), or you can clone the source from GitHub.

##Services

The library uses a few different [Services](https://github.com/mcnedward/inheritance-inquiry-lib/tree/master/src/main/java/com/mcnedward/ii/service) to work together to build a parsed JavaProject into a JavaSolution which can be analyzed and extracted into graphs or sheets. There is a ServiceFactory that can be used to get an instance of whatever service you want.

```java
ProjectService projectService = ServiceFactory.projectService();
IGraphService ditGraphService = ServiceFactory.ditGraphService();
```

### ProjectService

The main service that parsers your project and analyzes it, building a JavaSolution containing all the information about your project.

### AnalyzerService

A service that can analyze different aspects or code metrics for your project.

### GraphService

A service that builds graphs based on the JavaSolution. These are JungGraphs that can be used in a Swing application, or can be downloaded also using the GraphService.
There are 3 different graph services, all implementing the IGraphService interface:

* DitGraphServce - Builds graphs for DIT
* NocGraphService - Builds graphs for NOC
* FullGraphService - Builds graphs for full inheritance hierarchy

### MetricService

A service that will convert your JavaSolution into Excel readable files. These can be either CSV files, or files separated with a tab as the delimiter.

### GitService

A service for downloading a remote GitHub repository into a File that can be used for the ProjectService.

## Listeners

The services are setup to be runnable as tasks, so most require a [Listener](https://github.com/mcnedward/inheritance-inquiry-lib/tree/master/src/main/java/com/mcnedward/ii/listener) in order to send notifications about progress, success, and errors. The ProjectService and AnalyzerService do not require a listener, but it is recommended to include a listener. The other Services do require a listener.

## Builders

[Builders](https://github.com/mcnedward/inheritance-inquiry-lib/tree/master/src/main/java/com/mcnedward/ii/builder) can be used to convienently execute a process in the library. These all run on a separate thread, so that when the library is used with a GUI the application will not hang.

To use a Builder, you need to pass in an instance of the required listener interfaces in the constructor. You must first call the setup method and pass in the proper service and options, then call the build method. The setup and build methods can be chained together or called separately.

```java
SolutionBuildListener listener = new SolutionBuildListener() {
	// progress and error methods
	@Override
	public void finished(JavaSolution solution) {
		System.out.println(solution.getInheritanceCount());
	}
};
ProjectBuilder builder = new ProjectBuilder(listener);
builder.setup(ServiceFactory.projectService()).build();
```

### ProjectBuilder

Use this builder to parser and analyze your project and return the JavaSolution that can be used in other Builders.

### GraphBuilder

This builder has two purposes. The first is to load all the JungGraphs from a JavaSolution. The other use is to export those graphs as images. These build tasks require a GraphOptions parameter that will be used to customize the graphs.

### MetricBuilder

This builder is used for exporting your JavaSolution to Excel sheets. You will need to set the MetricOptions for this builder.

### GitBuilder

This builder is used to download a file from Gitub. There are two setup options for downloading here: username and password, or personal access token.

## Running the jar

If you would like to just quickly export graphs you can run the jar in the command line. There are 4 parameters, with all but the last being required:

* Project location
* Graph type, either dit, noc, or full
* Graph export location
* true if you want to keep your project's package structure, false if you want the images all in one directory

Here's an example of exporting DIT graphs for a project:

```
java -jar InheritanceInquiryLib.jar C:\Dev\MyProject dit C:\Dev\Graphs true
```

## Examples

For an example of how this library works, look at the [main class](https://github.com/mcnedward/inheritance-inquiry-lib/blob/master/src/main/java/com/mcnedward/ii/InheritanceInquiryMain.java) or the [Inheritance Inquiry App](https://github.com/mcnedward/inheritance-inquiry).
