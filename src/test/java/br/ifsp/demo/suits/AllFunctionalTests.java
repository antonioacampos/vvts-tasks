package br.ifsp.demo.suits;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages("br.ifsp.demo.tasks")
@SuiteDisplayName("All Functional tests")
@IncludeTags({"Functional"})
public class AllFunctionalTests {
}
