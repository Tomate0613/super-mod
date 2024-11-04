local filename = arg[1]
local data = arg[2]

if filename == nil or data == nil then
	return
end

local file = io.open(filename, "w")

if file == nil then
	return
end


for i = 2, #arg do
  if i > 2 then
    file:write(" ")
  end
  file:write(arg[i])
end

file:close()
