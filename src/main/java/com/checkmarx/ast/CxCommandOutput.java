package com.checkmarx.ast;


import lombok.*;


import java.util.List;

@Getter
@Setter
public class CxCommandOutput {
    private int exitCode;
    private List<CxScan> scanObjectList;

}
