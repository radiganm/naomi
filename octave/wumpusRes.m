%% wumpusRes.m
%% Mac Radigan

function st = wumpusRes(W)

  nassert = @(a,b) arrayfun(@(x,y)assert(x==y),a,b);
  [N Wu A D L C lambda ac con S] = wumpusEval(W);

  [st cl] = resolve(Wu, N);


function [vo cl] = resolve(Wu, vi)
  in=-1;                                 % flux direction, in
  cl = 0;
  vo = [];
% for v=vi
%   [i dc]  = find(Wu(:,v)==-in);
%   [vd dc] = find(Wu(i,:)==in);
%   if(any(vd))
%     vo=horzcat(vo,resolve(Wu,vd)); 
%   else
%     vo=[];
%   end
% end

  cx = [];
  vx = [];
  for v=vi
    fprintf(1, 'resolving... %d\n', v);
    [dc i]  = find(Wu(:,v)==-in);
    [dc vd] = find(Wu(:,i)==in);
    if(~any(vd))
      cl = 1;
      vo=horzcat(vo,vi)
    else
      [vx cxx] = resolve(Wu,vd); 
      cx(end+1) = cxx;
    end
  end
  if(any(cx)&all(cx))
    cl = 1;
  else
    vo=horzcat(vo,vx)
  end
    %keyboard
    %if(~all(vx))
    %  error 'dependency not found'
    %end
  fprintf(1, 'returning...\n');
  %vi
  %vo
  %cl

%% *EOF*
