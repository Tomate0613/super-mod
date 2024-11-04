local index = 0

local function tick()
  print("HELLO")

  index = index + 1
  if index == 10 then
    puter.stop()
  end
end

puter.on("tick", tick)
