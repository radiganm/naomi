%% wumpus.m
%% Mac Radigan

function W = wumpus(productions, types, functors)

  % define index and flux direction constants
  in=-1; DP=1; DY=2; DX=3; 

  % checks if a datatype is in a list
  idx = @(s) regexprep(s,'[A-z]', '');
  tok = @(s) regexp(findsym(s),',','split');

  % create the oriented incident matrix, W
  W = zeros(length(types),length(functors));
  for pp=1:size(productions,1), p=str2num(idx(findsym(productions{pp,DP})));
    if length(productions{pp,DX})
      % out-degree
      for xx=cellfun(@(s) str2num(s), idx(tok(productions{pp,DX})), ...
          'UniformOutput', false); x=xx{:};
        for qq=1:size(productions,1)
          for qy=symToIdx(productions{qq,DY});
            if qy==x
              W(x,p) = W(x,p) -in;
            end
          end
        end
      end
    end
    % in-degree
    for yy=cellfun(@(s) str2num(s), idx(tok(productions{pp,DY})), ...
           'UniformOutput', false); y=yy{:};
      for qq=1:size(productions,1)
        if length(productions{qq,DX}) 
          for qx=symToIdx(productions{qq,DX});
            if qx==y
              W(y,p) = W(y,p) +in;
            end
          end
        end
      end
    end
  end

function y = symToIdx(s)
  idx = @(s) regexprep(s,'[A-z]', '');
  y = [];
  for n=1:length(s)
    y(n)= str2num(idx(findsym(s(n)))); 
  end

%% *EOF*
