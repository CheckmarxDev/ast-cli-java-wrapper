package com.checkmarx.ast;

import com.checkmarx.ast.tenant.TenantSetting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TenantTest extends BaseTest {

    @Test
    void testTenantSettings() throws Exception {
        List<TenantSetting> tenantSettings = wrapper.tenantSettings();
        Assertions.assertTrue(tenantSettings.size() > 0);
    }

    @Test
    void testIdeScansEnabled() {
        Assertions.assertDoesNotThrow(() -> wrapper.ideScansEnabled());
    }
}
