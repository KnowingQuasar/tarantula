package jacoco.dto;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class JacocoPackage {
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    public String name;

    @JacksonXmlProperty(isAttribute = false, localName = "class")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Object> jacocoClass;
    
    @JacksonXmlProperty(isAttribute = false, localName = "sourcefile")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<JacocoSourceFile> sourceFiles;

    @JacksonXmlProperty(isAttribute = false, localName = "counter")
    private Object counter;
}
