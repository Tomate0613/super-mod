local function on_key_pressed(key, scancode, modifier)
  print("key: " .. key)
  print("scancode: " .. scancode)
  print("modifier: " .. modifier)
end

puter.on("on_key_pressed", on_key_pressed)
