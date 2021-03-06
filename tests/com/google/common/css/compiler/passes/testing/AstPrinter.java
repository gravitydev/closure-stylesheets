/*
 * Copyright 2011 Google Inc.
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

package com.google.common.css.compiler.passes.testing;

import com.google.common.css.compiler.ast.CssBooleanExpressionNode;
import com.google.common.css.compiler.ast.CssCommentNode;
import com.google.common.css.compiler.ast.CssConditionalBlockNode;
import com.google.common.css.compiler.ast.CssConditionalRuleNode;
import com.google.common.css.compiler.ast.CssDeclarationBlockNode;
import com.google.common.css.compiler.ast.CssDeclarationNode;
import com.google.common.css.compiler.ast.CssDefinitionNode;
import com.google.common.css.compiler.ast.CssImportRuleNode;
import com.google.common.css.compiler.ast.CssMediaRuleNode;
import com.google.common.css.compiler.ast.CssNode;
import com.google.common.css.compiler.ast.CssPropertyValueNode;
import com.google.common.css.compiler.ast.CssRootNode;
import com.google.common.css.compiler.ast.CssRulesetNode;
import com.google.common.css.compiler.ast.CssSelectorListNode;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.CssValueNode;
import com.google.common.css.compiler.passes.CompactPrinter;

/**
 * A {@link CompactPrinter} extension that adds square brackets to make the
 * construction of the AST more obvious.
 *
 * <p> This printer is useful to ensure that the AST is constructed in the
 * expected way. Thus, it avoids that simply returning the source code is
 * regarded as valid.
 *
 * @author fbenz@google.com (Florian Benz)
 */
public class AstPrinter extends CompactPrinter {
  /**
   * Uses the AstPrinter to print the tree.
   */
  public static String print(CssTree tree) {
    AstPrinter astPrinter = new AstPrinter(tree);
    astPrinter.runPass();
    return astPrinter.getCompactPrintedString();
  }

  public AstPrinter(CssTree tree) {
    super(tree);
  }

  @Override
  public boolean enterTree(CssRootNode root) {
    sb.append("[");
    return super.enterTree(root);
  }

  @Override
  public void leaveTree(CssRootNode root) {
    super.leaveTree(root);
    sb.append("]");
  }

  @Override
  public boolean enterDeclarationBlock(CssDeclarationBlockNode block) {
    super.enterDeclarationBlock(block);
    sb.append("[");
    return true;
  }

  @Override
  public void leaveDeclarationBlock(CssDeclarationBlockNode block) {
    sb.append("]");
    super.leaveDeclarationBlock(block);
  }

  @Override
  public boolean enterSelectorBlock(CssSelectorListNode block) {
    sb.append("[");
    return super.enterSelectorBlock(block);
  }

  @Override
  public void leaveSelectorBlock(CssSelectorListNode block) {
    super.leaveSelectorBlock(block);
    sb.append("]");
  }

  @Override
  public boolean enterPropertyValue(CssPropertyValueNode propertyValue) {
    sb.append("[");
    return super.enterPropertyValue(propertyValue);
  }

  @Override
  public void leavePropertyValue(CssPropertyValueNode propertyValue) {
    super.leavePropertyValue(propertyValue);
    deleteLastCharIfCharIs(' ');
    sb.append("]");
  }

  @Override
  public boolean enterDefinition(CssDefinitionNode node) {
    sb.append("@def ");
    sb.append(node.getName());
    sb.append(" [");
    return true;
  }

  @Override
  public void leaveDefinition(CssDefinitionNode node) {
    deleteLastCharIfCharIs(' ');
    sb.append("];");
  }

  @Override
  public boolean enterConditionalBlock(CssConditionalBlockNode node) {
    return true;
  }

  @Override
  public boolean enterConditionalRule(CssConditionalRuleNode node) {
    sb.append(node.getType());
    sb.append("[");
    for (CssValueNode value : node.getParameters()) {
      appendValue(value);
      sb.append(" ");
    }
    deleteLastCharIfCharIs(' ');
    sb.append("]{");
    return true;
  }

  @Override
  public void leaveConditionalRule(CssConditionalRuleNode node) {
    sb.append("}");
  }

  @Override
  public boolean enterRuleset(CssRulesetNode node) {
    appendComments(node);
    return super.enterRuleset(node);
  }

  @Override
  public boolean enterDeclaration(CssDeclarationNode node) {
    appendComments(node);
    return super.enterDeclaration(node);
  }

  @Override
  public boolean enterMediaRule(CssMediaRuleNode node) {
    appendComments(node);
    return super.enterMediaRule(node);
  }

  @Override
  public boolean enterImportRule(CssImportRuleNode node) {
    appendComments(node);
    return super.enterImportRule(node);
  }

  /**
   * This method appends the representation of a value but does not cover
   * all cases at the moment.
   */
  private void appendValue(CssValueNode node) {
    if (node instanceof CssBooleanExpressionNode) {
      appendBooleanExpression((CssBooleanExpressionNode) node);
    } else {
      sb.append(node.getValue());
    }
  }

  private void appendBooleanExpression(CssBooleanExpressionNode node) {
    if (!node.getType().isOperator()) {
      sb.append(node.getValue());
    } else if (node.getType().isBinaryOperator()) {
      appendBooleanChildExpression(node, node.getLeft());
      sb.append(" ");
      sb.append(node.getType().getOperatorString());
      sb.append(" ");
      appendBooleanChildExpression(node, node.getRight());
    } else if (node.getType().isUnaryOperator()) {
      sb.append(node.getType().getOperatorString());
      appendBooleanChildExpression(node, node.getLeft());
    }
  }

  private void appendBooleanChildExpression(CssBooleanExpressionNode node,
      CssBooleanExpressionNode child) {
    if (child.getType().getPriority() >= node.getType().getPriority()) {
      appendBooleanExpression(child);
    } else {
      sb.append("(");
      appendBooleanExpression(child);
      sb.append(")");
    }
  }

  private void appendComments(CssNode node) {
    for (CssCommentNode c : node.getComments()) {
      sb.append("[");
      sb.append(c.getValue());
      sb.append("]");
    }
  }
}
