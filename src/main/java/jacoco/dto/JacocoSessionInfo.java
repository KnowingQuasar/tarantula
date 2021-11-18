package jacoco.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class JacocoSessionInfo {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    @JacksonXmlProperty(isAttribute = true, localName = "start")
    private String start;

    @JacksonXmlProperty(isAttribute = true, localName = "dump")
    private String dump;
}
