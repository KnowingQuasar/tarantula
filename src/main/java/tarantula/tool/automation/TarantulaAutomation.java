package tarantula.tool.automation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.maven.cli.MavenCli;
import org.apache.tools.ant.DirectoryScanner;

import jacoco.dto.JacocoLine;
import jacoco.dto.JacocoPackage;
import jacoco.dto.JacocoReport;
import jacoco.dto.JacocoSourceFile;
import surefire.dto.TestReport;

/**
 * Automated Tarantula Implementation. Runs mvn test with the provided test
 * cases and checks coverage
 *
 */
public class TarantulaAutomation {
    public static double totalPassedTests = 0;
    public static double totalFailedTests = 0;
    public static ArrayList<CoverageLine> coverageLines = new ArrayList<>();

    public static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static JacocoReport convertFromExec(String projDirectory) throws Exception {
        File jacocoReportExec = new File(projDirectory + "/target/jacoco.exec");

        if (!jacocoReportExec.exists()) {
            throw new Exception("Jacoco exec file could not be found");
        }

        String currentDirectory = System.getProperty("user.dir");
        System.out.println("The current working directory is " + currentDirectory);

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", currentDirectory + "/lib/jacococli.jar", "report",
                projDirectory + "/target/jacoco.exec", "--xml",
                currentDirectory + "/generated_jacoco_reports/report.xml", "--classfiles",
                projDirectory + "/target/classes/");
        pb.start().waitFor();

        File jacocoReport = new File(currentDirectory + "/generated_jacoco_reports/report.xml");

        try {
            XmlMapper xmlMapper = new XmlMapper();
            String xml = inputStreamToString(new FileInputStream(jacocoReport));
            JacocoReport obj = xmlMapper.readValue(xml, JacocoReport.class);
            jacocoReport.delete();
            return obj;
        } catch (Exception e) {
            System.err.println("Could not parse jacoco.xml. Exiting...");
            return null;
        }
    }

    public static TestReport getTestReport(String projDirectory) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(new String[] { "*.xml" });
        ds.setBasedir(projDirectory + "/target/surefire-reports/");
        ds.setCaseSensitive(false);
        ds.scan();
        String[] reportNames = ds.getIncludedFiles();

        if (reportNames.length != 1) {
            System.err.println("Could not find the test report");
            return null;
        }

        File testReport = new File(projDirectory + "/target/surefire-reports/" + reportNames[0]);

        try {
            XmlMapper xmlMapper = new XmlMapper();
            String xml = inputStreamToString(new FileInputStream(testReport));
            TestReport obj = xmlMapper.readValue(xml, TestReport.class);
            testReport.delete();
            return obj;
        } catch (Exception e) {
            System.err.println("Could not parse test results. Exiting...");
        }
        return null;
    }

    public static void doTarantula() {
        for (CoverageLine line : coverageLines) {
            try {
                line.suspiciousness = (line.failedTests / totalFailedTests)
                        / ((line.passedTests / totalPassedTests) + (line.failedTests / totalFailedTests));
            } catch (Exception e) {
                System.err.println("Invalid Coverage Line");
            }
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.out.println(
                    "Required arguments for this are: working directory, Java file to work on, Test class, Test methods to run separated by commas within the class");
            return;
        }

        String projDirectory = args[0];
        String javaPkg = args[1].replace(".", "/");
        String javaFile = args[2];
        String testClass = args[3];
        String[] tests = args[4].split(",");

        MavenCli cli = new MavenCli();
        System.setProperty("maven.multiModuleProjectDirectory", projDirectory);

        for (String test : tests) {
            cli.doMain(new String[] { "clean", "-Dtest=" + testClass + "#" + test, "test" }, projDirectory, System.out,
                    System.out);
            try {
                JacocoReport jacocoReport = convertFromExec(projDirectory);
                TestReport testReport = getTestReport(projDirectory);

                double numFailed = Integer.parseInt(testReport.failuresTotal);
                double totalRan = Integer.parseInt(testReport.testTotal);
                double totalSkipped = Integer.parseInt(testReport.skippedTotal);
                double numPassed = totalRan - numFailed - totalSkipped;

                totalPassedTests += numPassed;
                totalFailedTests += numFailed;

                for (JacocoPackage pkg : jacocoReport.pkg) {
                    if (pkg.name.equals(javaPkg)) {
                        for (JacocoSourceFile sourceFile : pkg.sourceFiles) {
                            if (javaFile.equals(sourceFile.name)) {
                                for (JacocoLine line : sourceFile.lines) {
                                    if (Integer.parseInt(line.checkedInstructions) != 0) {
                                        CoverageLine foundCoverageLine = null;
                                        for (CoverageLine coverageLine : coverageLines) {
                                            if (coverageLine.lineNumber == Integer.parseInt(line.lineNumber)) {
                                                coverageLine.failedTests += numFailed;
                                                coverageLine.passedTests += numPassed;
                                                foundCoverageLine = coverageLine;
                                                break;
                                            }
                                        }

                                        if (foundCoverageLine == null) {
                                            foundCoverageLine = new CoverageLine(Double.parseDouble(line.lineNumber),
                                                    numPassed, numFailed);
                                            coverageLines.add(foundCoverageLine);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                System.out.println("Ran test class: " + test);
            } catch (Exception e) {
                System.err.println("Could not parse jacoco exec file");
            }
        }

        doTarantula();

        Collections.sort(coverageLines, new Comparator<CoverageLine>() {
            public int compare(CoverageLine c1, CoverageLine c2) {
                if (c1.suspiciousness > c2.suspiciousness) {
                    return -1;
                } else if (c2.suspiciousness > c1.suspiciousness) {
                    return 1;
                } else {
                    if (c1.lineNumber > c2.lineNumber) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("Line Number,Suspiciousness\n");

        for (CoverageLine cl : coverageLines) {
            sb.append(cl.lineNumber).append(",").append(cl.suspiciousness).append("\n");
        }

        System.out.println("Tarantula Report for " + javaFile);
        System.out.println(sb.toString());

        try {
            FileWriter writer = new FileWriter("Tarantula Report for " + javaFile + ".csv");
            writer.write(sb.toString());
            writer.close();
        } catch (IOException ie) {
            System.err.println("Could not write Tarantula results to file.");
        }
    }
}
