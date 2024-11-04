local profile = arg[1]

if profile == nil then
  print("No session specified")
  return
end

local function listener(success, data)
  print(success)
  print(data)

  puter.stop()
end

supermod.request_session(profile, listener)