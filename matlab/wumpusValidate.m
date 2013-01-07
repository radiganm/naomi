%% wumpusValidate.m
%% Mac Radigan

function valid = wumpusValidate(W, foe)

  nassert = @(a,b) arrayfun(@(x,y)assert(x==y),a,b);
  sel = @(x,n) x(n);
  [N Wu A D L C lambda ac con S] = wumpusEval(W);

  % assert that the dependencies are satisified by the sources
  if(~con)
    msg = sprintf('connection error: ac=%d', ac);
    if(foe), warning(msg); else error(msg); end
  end

  % assert consistency between Floyd-Warshall and spectral graph theory
  for v=S
    M = Wu;
    M(setdiff(v,S),:) = [];            % skip other sources
    l2 = sel(flipud(sort(eig(L))),2);  % second eigenvalue of M
    if( ~(l2>0&C(N,v))|(~(l2>0)&~C(N,v)) )
      disp('C*(A)'); disp(C)
      msg = sprintf('tautology error: C*(A)[%d,%d]>0 <-> l2>0', N, v);
      if(foe), warning(msg); else error(msg); end
    end
  end

%% *EOF*
