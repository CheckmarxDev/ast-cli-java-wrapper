package com.checkmarx.ast.results;


import com.checkmarx.ast.Scan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CxCommandOutput {
    private int exitCode;
    private List<Scan> scanObjectList;
}
