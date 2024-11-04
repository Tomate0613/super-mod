io.write("> ")

local input_buffer = ""
local command_history = {}
local history_index = 0
local print_prompt = false

local KEYS = {
	ENTER = 257,
	BACKSPACE = 259,
	KEY_UP = 265,
	KEY_DOWN = 264,
}

-- Function to handle the execution of Lua scripts
local function run_lua_script(script_name, args)
	print_prompt = true

	if not script_name:match("%.lua$") then
		script_name = script_name .. ".lua"
	end

	local chunk, err = loadfile(script_name)
	if not chunk then
		print("Error: Cannot load script - " .. err)
		return
	end

	local function run_script()
		puter.run(script_name, args)
	end

	local success, result = pcall(run_script)
	if not success then
		print("Error: An error occurred while running the script - " .. result)
	end
end

local function tick()
	if print_prompt then
		print_prompt = false

		io.write("> ")
	end
end

-- Function to handle key press events, called by super mod
local function on_key_pressed(key, scancode, modifier)
	if key == KEYS.ENTER then
		io.write("\n")

		local command = {}
		for word in input_buffer:gmatch("%S+") do
			table.insert(command, word)
		end

		if #command > 0 then
			local script_name = command[1]
			local args = {}
			for i = 2, #command do
				table.insert(args, command[i])
			end

			if script_name == "exit" then
				print("Exiting shell.")
				puter.stop()
				return
			end

			run_lua_script(script_name, args)

			table.insert(command_history, input_buffer)
			history_index = #command_history + 1
		end

		input_buffer = ""
	elseif key == KEYS.BACKSPACE then
		if #input_buffer > 0 then
			input_buffer = input_buffer:sub(1, -2)
			io.write("\027[D\027[s \027[u")
		end
	elseif key == KEYS.KEY_UP then
		if history_index > 1 then
			history_index = history_index - 1
			input_buffer = command_history[history_index]
			io.write("\027[K\r> " .. input_buffer)
		end
	elseif key == KEYS.KEY_DOWN then
		if history_index < #command_history then
			history_index = history_index + 1
			input_buffer = command_history[history_index]
			io.write("\027[K\r> " .. input_buffer)
		else
			history_index = #command_history + 1
			input_buffer = ""
			io.write("\027[K\r> ")
		end
	end
end

local function on_char_typed(char)
	input_buffer = input_buffer .. char

	io.write(char)
end

puter.on("on_key_pressed", on_key_pressed)
puter.on("on_char_typed", on_char_typed)
puter.on("tick", tick)
