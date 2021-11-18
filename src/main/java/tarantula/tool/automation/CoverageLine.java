package tarantula.tool.automation;

public class CoverageLine {
    public double lineNumber;
    public double passedTests;
    public double failedTests;
    public double suspiciousness;

    public CoverageLine(double lineNumber, double passedTests, double failedTests) {
        this.lineNumber = lineNumber;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.suspiciousness = 0;
    }
}
