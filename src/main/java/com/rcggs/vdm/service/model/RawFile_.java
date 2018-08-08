
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
    "Name",
    "Description",
    "Location",
    "FileFormat",
    "Delimiter",
    "Status",
    "SourceID"
})
public class RawFile_ {

    @JsonProperty("Name")
    private String name;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Location")
    private String location;
    @JsonProperty("FileFormat")
    private String fileFormat;
    @JsonProperty("Delimiter")
    private String delimiter;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("SourceID")
    private Integer sourceID;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("Location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("Location")
    public void setLocation(String location) {
        this.location = location;
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

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("Status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("SourceID")
    public Integer getSourceID() {
        return sourceID;
    }

    @JsonProperty("SourceID")
    public void setSourceID(Integer sourceID) {
        this.sourceID = sourceID;
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
