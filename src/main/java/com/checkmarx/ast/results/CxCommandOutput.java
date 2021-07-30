package com.checkmarx.ast.results;


import com.checkmarx.ast.scans.CxScan;
import lombok.*;


import java.util.List;

@Getter
@Setter
public class CxCommandOutput extends CxOutput{
    private int exitCode;
    private List<CxScan> scanObjectList;

}
