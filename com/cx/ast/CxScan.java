package com.cx.ast;

import lombok.Data;

@Data

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
