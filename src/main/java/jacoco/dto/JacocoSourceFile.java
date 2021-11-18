package jacoco.dto;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class JacocoSourceFile {
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    public String name;

    @JacksonXmlProperty(isAttribute = false, localName = "line")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<JacocoLine> lines;

    @JacksonXmlProperty(isAttribute = false, localName = "counter")
    private Object counter;
}
