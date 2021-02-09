package com.cx.sdk;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class CxScan {

	public String scanID;

	public String ProjectID;

	public String status;

	public String createdAt;

	public String updatedAt;

	public String tags;

	public String initiator;

	public String origin;

}
