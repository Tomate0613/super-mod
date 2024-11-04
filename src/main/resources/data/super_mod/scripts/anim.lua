local i = 0
local dir = 0

local function tick()
  if dir == 0 then
    i = i + 1
  else
    i = i - 1
  end

  io.write("\027[J\027[H")


  for x = 1, i, 1 do
    io.write(" ")
  end

  io.write("#")

  if i == 0 then
    dir = 0
  end

  if i == 20 then
    dir = 1
  end
end

puter.on("tick", tick)
