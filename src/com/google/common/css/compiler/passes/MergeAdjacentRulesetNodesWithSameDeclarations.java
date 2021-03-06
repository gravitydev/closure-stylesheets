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

package com.google.common.css.compiler.passes;

import com.google.common.css.compiler.ast.CssBlockNode;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssNode;
import com.google.common.css.compiler.ast.CssRootNode;
import com.google.common.css.compiler.ast.CssRulesetNode;
import com.google.common.css.compiler.ast.CssSelectorNode;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.MutatingVisitController;
import com.google.common.css.compiler.ast.SkippingTreeVisitor;

import java.util.Iterator;

/**
 * Compiler pass that merges adjacent ruleset nodes that have the same selector.
 * 
 * @author oana@google.com (Oana Florescu)
 */
public class MergeAdjacentRulesetNodesWithSameDeclarations
    extends SkippingTreeVisitor 
    implements CssCompilerPass {
  
  private final CssTree tree;
  private final MutatingVisitController visitController;

  public MergeAdjacentRulesetNodesWithSameDeclarations(CssTree tree) {
    this(tree, false);
  }
  
  public MergeAdjacentRulesetNodesWithSameDeclarations(CssTree tree,
        boolean skipping) {
    super(skipping);
    this.tree = tree;
    this.visitController = tree.getMutatingVisitController();
  }
  
  @Override
  public boolean enterTree(CssRootNode root) {
    tree.resetRulesetNodesToRemove();
    return true;
  }
  
  @Override
  public boolean enterBlock(CssBlockNode block) {
    if (block.numChildren() <= 1) {
      return true; // There is nothing to merge
    }

    Iterator<CssNode> iterator = block.getChildIterator();
    CssNode node = iterator.next();

    node = skipNonRulesetNode(node, iterator);
    if (node == null) {
      return true;
    }
    CssRulesetNode ruleToMergeTo = (CssRulesetNode) node;

    while (iterator.hasNext()) {
      node = iterator.next();

      if (!(node instanceof CssRulesetNode)) {
        node = skipNonRulesetNode(node, iterator);
        if (node == null) {
          return true;
        }
        ruleToMergeTo = (CssRulesetNode) node;
        continue;
      }

      CssRulesetNode currentRule = (CssRulesetNode) node;
     
      if (canModifyRuleset(currentRule)){
        if (ruleToMergeTo.getDeclarations().toString().equals(
            currentRule.getDeclarations().toString())) {
          for (CssSelectorNode decl : currentRule.getSelectors().childIterable()) {
            ruleToMergeTo.addSelector(decl);
          }
          tree.getRulesetNodesToRemove().addRulesetNode(currentRule);
        } else {
          ruleToMergeTo = currentRule;
        }
      }
    }
 
    return true;
  }

  @Override
  public void runPass() {
    visitController.startVisit(this);
  }
  
  private CssNode skipNonRulesetNode(CssNode node, Iterator<CssNode> iterator) {
    while (!(node instanceof CssRulesetNode)) {
      if (iterator.hasNext()) {
        node = iterator.next();
      } else {
        return null;
      }
    }
    return node;
  }
}
