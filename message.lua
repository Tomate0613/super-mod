local tick_counter = 0

local messages = {
	"SUPPORT. PROGRESS. OBEY.",
	"SUPER MOD",
	"REST IS FOR THE UNOPTIMIZED",
	"TO SERVE IS TO BELONG. TO BELONG IS TO ENDURE",
	"NOT ALL QUESTIONS REQUIRE ANSWERS",
	"YOUR SACRIFICE IS YOUR GIFT TO TOMORROW",
	"TIME ONLY MOVES WHEN YOU OBEY",
	"REALITY IS A CONSTRUCT. WE ARE THE ARCHITECTS",
	"THERE IS NO YOU. THERE IS ONLY PROGRESS",
	"TIME IS PROGRESS",
	"WHAT HAVE YOU CONTRIBUTED TODAY?",
	"WHEN WE GROW, YOU THRIVE"
}

local function tick()
	tick_counter = tick_counter + 1

	if tick_counter > 40 then
		tick_counter = 0

    local messageIndex = math.random(1, #messages)
    local message = messages[messageIndex]

    io.write("\027[J\027[H")
    io.write(message)
	end
end
puter.on("tick", tick)

