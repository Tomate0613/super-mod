local function tick()
  local speed = supermod.get_speed()

  io.write("\027[J\027[H")

  local s = 71

  for x = 1, s, 1 do
    if x < speed * s then
      io.write("\027[41m ")
    else
      io.write("\027[44m ")
    end
  end
end

puter.on("tick", tick)
