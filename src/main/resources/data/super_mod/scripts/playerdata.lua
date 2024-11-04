local function tick()
  local player = minecraft.get_local_player()
  local computer_pos = puter.get_pos()

  local d_x = player.position.x - computer_pos.x;
  local d_y = player.position.y - computer_pos.y;
  local d_z = player.position.z - computer_pos.z;

  print("\027[J\027[H(" .. player.position.x .. ", " .. player.position.y .. ", " .. player.position.z .. ")")

  local x = (d_x * d_x)
  local y = (d_y * d_y)
  local z = (d_z * d_z)

  print("Distance: " .. math.sqrt(x + y + z))
  print(player.name)
end

puter.on("tick", tick)
