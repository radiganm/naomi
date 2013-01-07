<% 
  sources = util.getStart()
  nodes = util.getNodes()
  sinks = util.getSinks()
  functors = nodes-sources-sinks
  f = util.getFunctors()
  edges = util.getEdges()
  ename = edges['name']
  ei = edges['in']
  eo = edges['out']
  fname = f[sinks[0]].getName()
  namespace = f[sinks[0]].getNamespace()
  qname = f[sinks[0]].getQname()
  fn = f[sinks[0]]
  wfid = util.getId()
  getMatrix = { m ->
    def sb = new StringBuffer()
    sb << "["
    m.getRowRange().eachWithIndex { row, rind ->
      m.getColRange().eachWithIndex { col, cind ->
        sb << "${(int)m.getEntry(rind,cind)}"
        if(cind!=m.getCols()-1) sb << ","
      }
      if(rind!=m.getRows()-1) sb << ";"
    }
    sb << "]"
    return sb.toString()
  }
%>
% ${fname}.m
% Mac Radigan

% algebraic connectivity
ac = ${util.getAlgebraicConnectivity()};

% Oriented Incident Matrix
W = ${getMatrix(util.getWumpus())};

% Unit Oriented Incident Matrix
Wu = ${getMatrix(util.getWumpus())};

% Laplacian
L = ${getMatrix(util.getLaplacian())};

% Degree
D = ${getMatrix(util.getDegree())};

% Adjacency
A = ${getMatrix(util.getAdjacency())};

% Positive Flux Adjacency
Ap = ${getMatrix(util.getPositiveFlux())};

% Negative Flux Adjacency
An = ${getMatrix(util.getNegativeFlux())};

% Transitive Closure
C = ${getMatrix(util.getTransitiveClosure())};

% *EOF*
