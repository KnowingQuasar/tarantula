package surefire.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "testsuite")
public class TestReport {
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    public String name;

    @JacksonXmlProperty(isAttribute = true, localName = "xsi")
    public String xsi;

    @JacksonXmlProperty(isAttribute = true, localName = "noNamespaceSchemaLocation")
    public String noNamespaceSchemaLocation;

    @JacksonXmlProperty(isAttribute = true, localName = "time")
    public String time;

    @JacksonXmlProperty(isAttribute = true, localName = "tests")
    public String testTotal;

    @JacksonXmlProperty(isAttribute = true, localName = "errors")
    public String errorTotal;

    @JacksonXmlProperty(isAttribute = true, localName = "skipped")
    public String skippedTotal;

    @JacksonXmlProperty(isAttribute = true, localName = "failures")
    public String failuresTotal;

    @JacksonXmlProperty(isAttribute = false, localName = "properties")
    public Object properties;

    @JacksonXmlProperty(isAttribute = false, localName = "testcase")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> testCases;
}
