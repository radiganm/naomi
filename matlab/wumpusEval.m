%% wumpusEval.m
%% Mac Radigan

function [N Wu A D L C lambda ac con S] = wumpusEval(W)

  nassert = @(a,b) arrayfun(@(x,y)assert(x==y),a,b);

  in=-1;                                 % flux direction, in
  Wu = sign(W);                          % oriented incident matrix, Wu
  L = Wu'*Wu;                            % Laplacian matrix, L
  A = abs(L.*(L<0));                     % adjacency matrix, A
  D = L+A;                               % degree matrix, D
  [i,j]=find(D); N=i(end);               % determine number of nodes
  k=1:N;                                 % submatrix indices, k
  s=zeros(size(A)); s(k,k)=diag(k)>0;    % submatrix diagonal, s
  C=(A+s)^N;                             % transitive closure, C
  lambda = flipud(sort(eig(L)));         % eigenvalues, lambda
  ac = lambda(2);                        % algebraic connectivity, ac
  con = ac>0;                            % check connectivity, con
  SS = find((min(Wu)<0)&(max(Wu)<-in));  % source nodes, SS
  S = find(C(SS,N));                     % starting nodes

%% *EOF*
