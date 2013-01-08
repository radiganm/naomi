// @file     WumpusUtil.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.utilities

import org.radigan.system.utilities.MatrixImpl
import org.radigan.system.utilities.Matrix
import org.radigan.naomi.wumpus.service.Functor
import org.radigan.naomi.wumpus.impl.SimulationFunctor
import org.radigan.system.utilities.Md5
import java.util.List

public class WumpusUtil {

  Map<Integer,List<Integer> > r = [:] // representatives
  List<Functor> f = []                // functors
  List<String> e = []                 // edges
  MatrixImpl w = null                 // oriented incident matrix
  MatrixImpl wu = null                // unit oriented incident matrix
  MatrixImpl l = null                 // Laplacian matrix
  MatrixImpl a = null                 // adjacency matrix
  MatrixImpl ap = null                // positive flux adjacency matrix
  MatrixImpl an = null                // negative flux adjacency matrix
  MatrixImpl d = null                 // degree matrix
  int n = 0                           // determine number of nodes
  MatrixImpl c = null                 // transitive closure
  double ac = 0.0                     // algebraic connectivity
  boolean con = false                 // connectivity
  List<Integer> ss = []               // source nodes
  List<Integer> s = []                // starting nodes
  List<Integer> t = []                // sink nodes

  public WumpusUtil(Matrix w, List<Functor> f, List<String> e) { 
    this.f = f
    this.e = e
    eval(w)
  }
  public WumpusUtil(List<Functor> f) { eval(f) }

  protected void eval(Matrix w) {
    this.w = w                 // oriented incident matrix, W
    wu = w.signum()            // unit oriented incident matrix, Wu = sign(W)
    l = wu.t()*wu              // Laplacian matrix, L = Wu'*Wu
    a = (l**(l<<0)).abs()      // adjacency matrix, A = abs(L.*(L<0))
    d = l+a                    // degree matrix, D
    n = d.lastNonzeroRow()     // determine number of nodes, N
    def sm = MatrixImpl.zeros(a).fill(MatrixImpl.eye(a))
    c = (a+sm)^n               // transitive closure, C=(A+s)^N
    ac = l.eigW(1)             // algebraic connectivity, ac = eigW(L, 2)
    con = ac>0                 // check connectivity, con = ac>0
    ss = ((wu.min()<<0)&(wu.max()<<1)).find() // source nodes, SS
    s = c.indToRows(c.find(ss,[n]))           // starting nodes, S
    ap = MatrixImpl.zeros(a)   // positive flux adjacency matrix
    an = MatrixImpl.zeros(a)   // negative flux adjacency matrix
    def edges = getEdges()
    [edges['in'],edges['out']].transpose().each { p ->
      ap.setEntry(p[0],p[1], ap.getEntry(p[0],p[1])+1)
      an.setEntry(p[1],p[0], an.getEntry(p[1],p[0])+1)
    }
    //ap = a**(wu>>0)               // positive flux adjacency matrix, Ap = A.*(Wu>0)
    //an = a**(wu.t()>>0)           // negative flux adjacency matrix, An = A.*(Wu<0)
    t = [traverse(s[0], +1).last()] // sink nodes, t
  }

  protected void eval(List<Functor> f) {
    this.f = f
    def edg = []
    for(fd in 0 .. f.size()-1) {
      for(fp in 0 .. f.size()-1) {
        //f[fd].getDependencies().unique().each { dn ->
        f[fd].getDependencies().each { dn ->
          def deg = f[fp].getProducts().count{it==dn}
          if(deg) edg << [fd, fp, deg, dn]
        }
      }
    }
    this.w = MatrixImpl.zeros(edg.size(),f.size())
    this.e = []
    edg.eachWithIndex { en, ind -> 
      e << en[3]
      w.setEntry(ind,en[1],-1*en[2]) 
      w.setEntry(ind,en[0],+1*en[2]) 
    }
    eval(w)
  }

  public WumpusUtil filter(int n) {
    def retF = []
    traverse(n,-1).each { ind -> retF << f[ind] }
    def tempF = f.clone()
    tempF.retainAll(retF)
    return new WumpusUtil(tempF)
    //def tempW = w.sub(traverse(n,-1).reverse(), w.getColRange())
    //return new WumpusUtil(tempW, tempF, e)
  }

  public List<Integer> traverse(int n, int flux) {
    def ax = (flux>0) ? ap : an
    return [n] + traverse(n, ax)
  }
  private List<Integer> traverse(int n, Matrix ax) {
    def rval = []
    def l = ax.find([n], ax.getColRange())
    if(l) ax.indToCols(l).each { ln -> rval += [ln] + traverse(ln, ax) }
    return rval
  }

  public List<Integer> step(int n, int flux) {
    def rval = []
    def ax = (flux>0) ? ap : an
    def l = ax.find([n], ax.getColRange())
    if(l) ax.indToCols(l).each { ln -> rval += [ln] }
    return rval
  }

  public List<Integer> getNodes() {
    def rval = []
    s.each { n -> rval += traverse(n, +1) }
    return rval
  }

  public void execute(Map<String,Integer> sv) {
    def svc = MatrixImpl.zeros(e.size(),1)
    e.eachWithIndex { en, ind ->
      if(sv[en]) {
        def val = svc.getEntry(ind,0)
        val+=sv[en]
        svc.setEntry(ind,0,val)
      }
    }
    def rho = w*svc
    println "w:\n${w}"
    println "svc:\n${svc}"
    println "rho:\n${rho}"
  }
  public void execute(int fn) {
    println f[fn].toString()
    ap.getRow(fn).eachWithIndex { v, n -> 
      if(v>0) execute(n) 
    }
  }
  public void execute() {
    s.each { n -> execute(n) }
  }

  public void connectedComponents() {
    f.eachWithIndex { fn, v -> makeSet(v) }
    e.eachWithIndex { en, n -> 
      def wp = w>>0
      def u = (wp.find([n], wp.getColRange()))[0]
      def wn = w<<0
      def v = (wn.find([n], wn.getColRange()))[0]
      if(findSet(u)==findSet(v)) union(u,v)
    }
  }
  public boolean sameComponent(int u, int v) {
    return findSet(u)==findSet(v)
  }
  public void makeSet(int x) {
    r[x] = [x]
  }
  public void clearSet() {
    r.clear()
  }
  public void union(int x, int y) {
    def sx = null
    def sy = null
    r.each { k, v -> 
      if(v.contains(x)) sx = k
      if(v.contains(y)) sy = k
    }
    if(!sx) throw new IllegalStateException("Representative not found for ${x}")
    if(!sy) throw new IllegalStateException("Representative not found for ${y}")
    r[sx] = r[sx]+r[sy]
    r.remove(sy)
  }
  public int findSet(int x) {
    r.each { k, v -> if(v.contains(x)) return k }
    throw new IllegalStateException("No such representative: ${x}")
  }

  public Map getEdges() { 
    def fi = []
    def fo = []
    w.getRowRange().each { row ->
      w.getColRange().each { col ->
        if(w.getEntry(row,col)<0) fi << col
        if(w.getEntry(row,col)>0) fo << col
      }
    }
    return ['name':e, 'in':fi, 'out':fo]
  }

  public Matrix getWumpus() { return w }
  public Matrix getUnitWumpus() { return wu }
  public Matrix getLaplacian() { return l }
  public Matrix getAdjacency() { return a }
  public Matrix getPositiveFlux() { return ap }
  public Matrix getNegativeFlux() { return an }
  public Matrix getDegree() { return d }
  public Matrix getTransitiveClosure() { return c }
  public double getAlgebraicConnectivity() { return ac }
  public boolean isConnected() { return con }
  public List<Integer> getSources() { return ss }
  public List<Integer> getSinks() { return t }
  public List<Integer> getStart() { return s }
  public String getEdge(int n) { return e[n] }
  public List<Functor> getFunctors(String namespace=SimulationFunctor.DEFAULT_NAMESPACE) { 
    def rval = []
    f.each { fn -> if(fn.getNamespace()==namespace) rval<<fn }
    return rval
  }
  public List<Functor> getFunctor(String name, String namespace=SimulationFunctor.DEFAULT_NAMESPACE) { 
    def rval = []
    f.each { fn -> if(fn.getName()==name && fn.getNamespace()==namespace) rval<<fn }
    return rval
  }
  public Integer getFunctorIndex(String name, String namespace=SimulationFunctor.DEFAULT_NAMESPACE) { 
    Integer index = null
    f.eachWithIndex { fn, ind ->
      if(fn.getName()==name && fn.getNamespace()==namespace) index = ind
    }
    return index
  }
  Map<Integer,List<Integer> > getRepresentatives() { return r }

  public String toString() {
    def sb = new StringBuilder() 
    sb << "f:\n"
    f.each { sb << "${it}\n" }
    sb << "e: ${e.join(',')}\n"
    sb << "W:\n${w}"
    sb << "Wu:\n${wu}"
    sb << "L:\n${l}"
    sb << "A:\n${a}"
    sb << "An:\n${an}"
    sb << "Ap:\n${ap}"
    sb << "D:\n${d}"
    sb << "C:\n${c}"
    sb << "N: ${n}\n"
    sb << "ac: ${ac}\n"
    sb << "con: ${con}\n"
    sb << "SS: ${ss}\n"
    sb << "S: ${s}\n"
    return sb.toString()
  }

  public String getId() {
    def sb = new StringBuffer()
    sb << 'f:=' << f*.getQname().join(',')
    sb << '&e:=' << e.join(',')
    sb << '&w:=' << '['
    w.getRowRange().eachWithIndex { row, rind ->
      w.getColRange().eachWithIndex { col, cind ->
        sb << "${w.getEntry(rind,cind)}"
        if(cind!=w.getCols()-1) sb << ','
      }
      if(rind!=w.getRows()-1) sb << ';'
    }
    sb << ']'
    return Md5.encode(sb.toString())
  }

}

// *EOF*
