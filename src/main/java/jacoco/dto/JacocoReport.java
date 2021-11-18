package jacoco.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "report")
public class JacocoReport {
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    @JacksonXmlProperty(isAttribute = false, localName = "sessioninfo")
    private JacocoSessionInfo sessionInfo;

    @JacksonXmlProperty(isAttribute = false, localName = "package")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<JacocoPackage> pkg;

    @JacksonXmlProperty(isAttribute = false, localName = "counter")
    private Object counter;
}
