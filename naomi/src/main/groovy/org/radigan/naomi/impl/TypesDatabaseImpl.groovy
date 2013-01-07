// @file     TypesDatabaseImpl.groovy
// @author   Mac Radigan

package org.radigan.naomi.impl

import org.radigan.naomi.service.TypesDatabase
import javax.swing.tree.TreeNode
import javax.swing.tree.DefaultMutableTreeNode as Node
import groovy.util.slurpersupport.GPathResult
import org.apache.log4j.Logger

public class TypesDatabaseImpl implements TypesDatabase {

  public static final String BASE_OBJECT = "Object"
  private Node root = new Node(BASE_OBJECT)

  private TypesDatabaseImpl() {
    initialize()
  }
  private static TypesDatabaseImpl ref = null
  public static TypesDatabase getInstance() { 
    if(!ref) ref=new TypesDatabaseImpl()
    return ref
  }

  private void initialize() {
  }

  public TreeNode find(String type) {
    return find(root, type)
  }
  public TreeNode find(TreeNode node, String type) {
    if(node.getUserObject()==type) {
      return node
    } else {
      def rval = null
      node.children().each { n ->
        rval = find(n, type)
      }
      return rval
    }
  }

  public String qname(String type) {
    def a = []
    def n = find(root,type)
    a << n.getUserObject()
    while(n=n.getParent()) { a << n.getUserObject() }
    return a.reverse().join(':')
  }

  public boolean typeOf(String t, String b) {
    //def tn = find(t)
    //if(!tn) throw new IllegalArgumentException("Unknown type: $t")
    //return find(tn,t)&&1
    return find(find(t),t)&&1
  }

  public void parse(String text) {
    def xml = new XmlSlurper().parseText(text).declareNamespace(
      t:'http://org.radigan.naomi/datatypes'
      )
    xml.children().each { n -> 
      parse(n)
    }
  }
  public void parse(GPathResult node) {
    def name = node.'@name' as String
    def type = node.'@type' as String
    def p = find(type)
    if(p) p.add(new Node(name))
    node.children().each { parse(it) }
  }

  public String toString() {
    def sb = new StringBuilder() 
    sb << toString(root, 0)
    return sb.toString()
  }
  public String toString(TreeNode node, int ind) {
    def sb = new StringBuilder() 
    sb << " ".multiply(ind) << node.getUserObject() << "\n"
    node.children().each { n ->
      sb << toString(n, ind+2)
    }
    return sb.toString()
  }

}

// *EOF*
