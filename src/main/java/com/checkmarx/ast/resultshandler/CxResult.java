package com.checkmarx.ast.resultshandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Data
@Builder
@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxResult {

	private String comments;

	private String type;

	private String id;

	private String similarityID;

	private String status;

	private String state;

	private String severity;

	private String firstFoundAt;

    private String foundAt;

    private String firstScan;

	@JsonCreator
	public CxResult(@JsonProperty("comments") String comments, @JsonProperty("type") String type,
			@JsonProperty("id") String id, @JsonProperty("similarityID") String similarityID,
			@JsonProperty("status") String status, @JsonProperty("state") String state,
			@JsonProperty("severity") String severity, @JsonProperty("firstFoundAt") String firstFoundAt, 
            @JsonProperty("foundAt") String foundAt, @JsonProperty("firstScan") String firstScan ) {
		this.comments = comments;
		this.type = type;
		this.id = id;
		this.similarityID = similarityID;
		this.status = status;
		this.state = state;
		this.severity = severity;
		this.firstFoundAt = firstFoundAt;
        this.foundAt = foundAt;
        this.firstScan = firstScan;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
