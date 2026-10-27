package com.ui.cloud.rekyc.autopush;

import org.testng.SkipException;
import org.testng.annotations.Test;

/** Server-error contract requires an isolated, controlled UAT failure fixture. */
public final class AutoPushApiFeedbackTest extends AutoPushUatTestBase {
    @Test(description="TC_AP_062 - Server errors surface without false success",
            groups={"autopush","uat","integration"})
    public void tcAp062_serverFailureIsVisibleWithoutFalseSuccess() {
        requireMutationOptIn();
        throw new SkipException("No controlled UAT server-error fixture or expected API response contract supplied");
    }
}
