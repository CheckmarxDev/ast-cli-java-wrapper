package com.checkmarx.ast;

import com.checkmarx.ast.engine.Engine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EngineTest extends  BaseTest{

    @Test
    void testEngineList() throws Exception{
        List<Engine>engineList= wrapper.engineList(null,null);
        Assertions.assertTrue(engineList.size()<=10);
    }

   @Test
    void testSASTList() throws Exception{
        List<Engine>sastEngineList= wrapper.engineList("SAST",null);
        Assertions.assertTrue(sastEngineList.size()<=10);
    }
}
