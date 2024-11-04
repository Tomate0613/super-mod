local filename = arg[1]

if filename == nil then
  print("Missing argument")
	return
end

local file = io.open(filename, "rb")

if file == nil then
  print("No such file or directory")
	return
end

local content = file:read("*all")
print(content)

file:close()