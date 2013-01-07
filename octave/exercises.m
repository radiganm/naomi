%% exercises.m
%% Mac Radigan

clear all


types = {'d1','d2','d3','d4'};
functors = {'p1','p2','p3','p4'};

syms(types{:});
syms(functors{:});
productions = cell(length(functors),3);  % initialize production list

%------------------------------
%   p   y1...    x1...
%------------------------------
productions = {                ...
  [p1], [d1],    []          ; ...  % starting productions (sources)
  [p2], [d2 d3 d3], [d1]     ; ...  % intermediate productions
  [p3], [d4],    [d2 d3 d3]  ; ...  % final production (sink)
};

W = wumpus(productions, types, functors);
[N Wu A D L C lambda ac con S] = wumpusEval(W);
W
Wu
A
D
L
C
lambda
ac
con
S
wumpusValidate(W, true);
st = wumpusRes(W);
st

%% *EOF*
