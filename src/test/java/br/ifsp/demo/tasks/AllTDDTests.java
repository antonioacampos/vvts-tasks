package br.ifsp.demo.tasks;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages("br.ifsp.demo.tasks")
@SuiteDisplayName("All TDD tests")
@IncludeTags({"TDD"})
public class AllTDDTests {
}
