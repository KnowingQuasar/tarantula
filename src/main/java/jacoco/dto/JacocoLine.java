package jacoco.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class JacocoLine {
    @JacksonXmlProperty(isAttribute = true, localName = "nr")
    public String lineNumber;
    @JacksonXmlProperty(isAttribute = true, localName = "mi")
    public String missedInstructions;
    @JacksonXmlProperty(isAttribute = true, localName = "ci")
    public String checkedInstructions;
    @JacksonXmlProperty(isAttribute = true, localName = "mb")
    public String missedBranches;
    @JacksonXmlProperty(isAttribute = true, localName = "cb")
    public String checkedBranches;
}
