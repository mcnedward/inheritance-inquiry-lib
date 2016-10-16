package com.mcnedward.ii;

import com.mcnedward.ii.builder.GraphBuilder;
import com.mcnedward.ii.builder.ProjectBuilder;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.listener.GraphExportListener;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.listener.SolutionBuildListener;
import com.mcnedward.ii.service.graph.IGraphService;
import com.mcnedward.ii.service.graph.element.GraphOptions;
import com.mcnedward.ii.service.graph.jung.JungGraph;
import com.mcnedward.ii.service.metric.MetricType;
import com.mcnedward.ii.utils.ServiceFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Edward - Jun 16, 2016
 */
public final class InheritanceInquiryMain {

    private static GraphBuilder graphBuilder;
    private static IGraphService graphService;
    private static File graphDirectory = null;
    private static boolean usePackages;
    private static String projectName;

    private static void setupGraphService(String graphType) {
        if (graphType == null || (
                !graphType.equals(MetricType.DIT.name().toLowerCase()) &&
                !graphType.equals(MetricType.NOC.name().toLowerCase()) &&
                !graphType.equals("full"))) {
            error(String.format("The second parameter needs to be the graph type: %s, %s, or %s.",
                    MetricType.DIT.name().toLowerCase(),
                    MetricType.NOC.name().toLowerCase(),
                    "full"));
            return;
        }
        if (graphType.equals(MetricType.DIT.name().toLowerCase())) {
            graphService = ServiceFactory.ditGraphService();
        } else if (graphType.equals(MetricType.NOC.name().toLowerCase())) {
            graphService = ServiceFactory.nocGraphService();
        } else
            graphService = ServiceFactory.fullGraphService();
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            error("You need to include a file location...");
        }
        File projectFile = new File(args[0]);
        if (!projectFile.exists()) {
            error("The project file you entered does not exist...");
        }
        if (args.length >= 2) {
            setupGraphService(args[1]);
            if (args.length == 2) error("You need to enter the graph directory location as the third argument...");

            graphDirectory = new File(args[2]);
            if (!graphDirectory.exists()) {
                if (!graphDirectory.mkdirs()) {
                    error("Couldn't make the directory for your graphs...");
                }
            }
            if (graphDirectory.isFile()) {
                error("The graph directory cannot be a file...");
            }
            
            if (args.length == 4) {
                String arg = args[3];
                if (arg.equals("true") || arg.equals("false"))
                    usePackages = arg.equals("true");
                else
                    error("You need to enter \"true\" to use the package structure for graph export, or \"false\" to put all graphs in one directory");
            }
        }

        ProjectBuilder builder = new ProjectBuilder(solutionBuildListener());
        graphBuilder = new GraphBuilder(graphLoadListener(), graphExportListener());
        builder.setup(projectFile).build();
    }

    private static SolutionBuildListener solutionBuildListener() {
        return new SolutionBuildListener() {

            @Override
            public void onProgressChange(String message, int progress) {
                progress(message, progress);
            }

            @Override
            public void onBuildError(String message, Exception exception) {
                error(message, exception);
            }

            @Override
            public void finished(JavaSolution solution) {
                System.out.println("Finished building " + solution.toString());
                if (graphDirectory == null) System.exit(0);
                projectName = solution.getProjectName();
                graphBuilder.setupForBuild(graphService, new GraphOptions(solution)).build();
            }
        };
    }

    private static GraphLoadListener graphLoadListener() {
        return new GraphLoadListener() {
            @Override
            public void onGraphsLoaded(List<JungGraph> graphs) {
                if (graphs.size() == 0) {
                    System.out.println("No graphs were created.");
                    System.exit(0);
                }
                System.out.println(String.format("Built %s graphs.", graphs.size()));
                graphBuilder.setupForExport(graphService, graphs, new GraphOptions(graphDirectory, projectName, usePackages))
                        .build();
            }

            @Override
            public void onProgressChange(String message, int progress) {
                progress(message, progress);
            }

            @Override
            public void onBuildError(String message, Exception exception) {
                error(message, exception);
            }
        };
    }

    private static GraphExportListener graphExportListener() {
        return new GraphExportListener() {
            @Override
            public void onGraphsExport() {
                System.out.println("\nYour graphs were exported!");
                System.exit(0);
            }

            @Override
            public void onProgressChange(String message, int progress) {
                progress(message, progress);
            }

            @Override
            public void onBuildError(String message, Exception exception) {
                error(message, exception);
            }
        };
    }

    private static void progress(String message, int progress) {
        System.out.print(String.format("%s [%s]\r", message, progress));
    }

    private static void error(String message) {
        System.out.println(message);
        System.exit(0);
    }

    private static void error(String message, Exception exception) {
        System.out.println(message);
        exception.printStackTrace();
        System.exit(0);
    }

}
