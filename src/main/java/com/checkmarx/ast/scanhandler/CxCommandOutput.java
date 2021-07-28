package com.checkmarx.ast.scanhandler;


import lombok.*;


import java.util.List;

@Getter
@Setter
public class CxCommandOutput {
    private int exitCode;
    private List<CxScan> scanObjectList;

}
