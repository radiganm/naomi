%% wumpusExec.m
%% Mac Radigan

function wumpusExec(W)

  nassert = @(a,b) arrayfun(@(x,y)assert(x==y),a,b);
  [N Wu A D L C lambda ac con S] = wumpusEval(W);

%% *EOF*
