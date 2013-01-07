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
% workflow: ${fname} (${namespace}) [${wfid}]

% storage
w = {};
% workflow id
w{end+1}.wfid = '${wfid}';

% products
w{end}.products = {'${fn.getProducts().join("','")}'};

% dependencies
w{end}.dependencies = {'${fn.getDependencies().join("','")}'};

% parameters
w{end}.parameters = {'${fn.getParameters().join("','")}'};

% algebraic connectivity
w{end}.ac = ${util.getAlgebraicConnectivity()};

% Oriented Incident Matrix
w{end}.W = ${getMatrix(util.getWumpus())};

% Unit Oriented Incident Matrix
w{end}.Wu = ${getMatrix(util.getWumpus())};

% Laplacian Matrix
w{end}.L = ${getMatrix(util.getLaplacian())};

% Degree Matrix
w{end}.D = ${getMatrix(util.getDegree())};

% Adjacency Matrix
w{end}.A = ${getMatrix(util.getAdjacency())};

% Positive Flux Adjacency Matrix
w{end}.Ap = ${getMatrix(util.getPositiveFlux())};

% Negative Flux Adjacency Matrix
w{end}.An = ${getMatrix(util.getNegativeFlux())};

% Transitive Closure Matrix
w{end}.C = ${getMatrix(util.getTransitiveClosure())};

% *EOF*
