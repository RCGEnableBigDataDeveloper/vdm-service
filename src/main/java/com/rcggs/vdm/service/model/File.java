
package com.rcggs.vdm.service.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "Hostname",
    "SourcePath",
    "FileFormat",
    "Delimiter"
})
public class File {

    @JsonProperty("Hostname")
    private String hostname;
    @JsonProperty("SourcePath")
    private String sourcePath;
    @JsonProperty("FileFormat")
    private String fileFormat;
    @JsonProperty("Delimiter")
    private String delimiter;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Hostname")
    public String getHostname() {
        return hostname;
    }

    @JsonProperty("Hostname")
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty("SourcePath")
    public String getSourcePath() {
        return sourcePath;
    }

    @JsonProperty("SourcePath")
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @JsonProperty("FileFormat")
    public String getFileFormat() {
        return fileFormat;
    }

    @JsonProperty("FileFormat")
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @JsonProperty("Delimiter")
    public String getDelimiter() {
        return delimiter;
    }

    @JsonProperty("Delimiter")
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
