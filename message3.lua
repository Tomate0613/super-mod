local tick_counter = 0
local glitch_counter = 0
local current_message = ""
local final_message = ""
local display_message = ""

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

-- Function to create a glitchy transformation of a message
local function glitchify(message)
    local glitchedMessage = ""
    for i = 1, #message do
        if math.random() < 0.2 then
            -- Add random glitch characters occasionally
            glitchedMessage = glitchedMessage .. string.char(math.random(33, 126))
        else
            -- Keep the actual character, occasionally randomizing case
            local char = message:sub(i, i)
            if math.random() < 0.2 then
                glitchedMessage = glitchedMessage .. (math.random() < 0.5 and char:upper() or char:lower())
            else
                glitchedMessage = glitchedMessage .. char
            end
        end
    end
    return glitchedMessage
end

local function tick()
    tick_counter = tick_counter + 1

    if tick_counter > 40 then
        tick_counter = 0

        -- Select a random message from the list
        local messageIndex = math.random(1, #messages)
        final_message = messages[messageIndex]

        display_message = glitchify(final_message)
    end

    if tick_counter % 5 == 0 then
        display_message = glitchify(final_message)
    end

    -- Show the glitched or clean message
    io.write("\027[J\027[H")
    io.write(display_message)
end

puter.on("tick", tick)