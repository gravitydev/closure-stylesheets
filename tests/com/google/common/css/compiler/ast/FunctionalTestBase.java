/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.css.compiler.ast;

import com.google.common.css.compiler.ast.testing.FunctionalTestCommonBase;
import com.google.common.css.compiler.ast.testing.NewFunctionalTestBase;
import com.google.common.css.compiler.passes.CreateComponentNodes;
import com.google.common.css.compiler.passes.CreateConditionalNodes;
import com.google.common.css.compiler.passes.CreateDefinitionNodes;
import com.google.common.css.compiler.passes.PrettyPrinter;

/**
 * Utility methods for all of the functional tests.
 *
 * @author oana@google.com (Oana Florescu)
 */
public class FunctionalTestBase extends FunctionalTestCommonBase {

  protected CssTree newTree = null;
  protected NewFunctionalTestBase newTestBase;

  /**
   * Utility method that given an input string parses it using the CssParser and
   * then converts it into a CssTree object.
   *
   * @param sourceCode A string representing the css input for the parser
   */
  @Override
  protected void parseAndBuildTree(String sourceCode) {
    buildTreeWithNewParser(sourceCode);
    runPassesOnNewTree();
  }

  protected void runPassesOnNewTree() {
    CreateDefinitionNodes pass1 = new CreateDefinitionNodes(
        newTree.getMutatingVisitController(), newTestBase.getErrorManager());
    pass1.runPass();
    CreateConditionalNodes pass2 = new CreateConditionalNodes(
        newTree.getMutatingVisitController(), newTestBase.getErrorManager());
    pass2.runPass();
    CreateComponentNodes pass3 = new CreateComponentNodes(
        newTree.getMutatingVisitController(), newTestBase.getErrorManager());
    pass3.runPass();
  }


  protected String getPrettyPrintedTree(CssTree tree) {
    PrettyPrinter prettyPrinter = new PrettyPrinter(tree.getVisitController());
    prettyPrinter.setStripQuotes(true);
    prettyPrinter.runPass();
    return prettyPrinter.getPrettyPrintedString();
  }

  protected void buildTreeWithNewParser(String sourceCode) {
    newTestBase = new NewFunctionalTestBase();
    newTestBase.parseAndBuildTree(sourceCode);
    newTree = newTestBase.getTree();
    tree = newTestBase.getTree();
  }

}
