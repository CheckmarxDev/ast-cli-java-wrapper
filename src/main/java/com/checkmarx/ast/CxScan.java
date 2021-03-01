package com.checkmarx.ast;

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

import java.util.Map;

@Data
@Builder
@Value
@JsonDeserialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class CxScan {

	private String ID;

	private String ProjectID;

	private String Status;

	private String CreatedAt;

	private String UpdatedAt;

	private Map<String,String> Tags;

	private String Initiator;

	private String Origin;

	@JsonCreator
	public CxScan(
			@JsonProperty("ID") String ID, @JsonProperty("ProjectID") String ProjectID, @JsonProperty("Status") String Status,
			@JsonProperty("CreatedAt") String CreatedAt, @JsonProperty("UpdatedAt") String UpdatedAt, @JsonProperty("Tags") Map<String,String> Tags,
			@JsonProperty("Initiator") String Initiator, @JsonProperty("Origin") String Origin
	) {
		this.ID = ID;
		this.ProjectID = ProjectID;
		this.Status = Status;
		this.CreatedAt = CreatedAt;
		this.UpdatedAt = UpdatedAt;
		this.Tags = Tags;
		this.Initiator =Initiator;
		this.Origin = Origin;
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
